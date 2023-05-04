import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Inventory e2e test', () => {

  const inventoryPageUrl = '/inventory';
  const inventoryPageUrlPattern = new RegExp('/inventory(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const inventorySample = {"name":"Account Savings","quantity":92210,"status":"Assimilated"};

  let inventory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/inventories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/inventories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/inventories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (inventory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/inventories/${inventory.id}`,
      }).then(() => {
        inventory = undefined;
      });
    }
  });


  it('Inventories menu should load Inventories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('inventory');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });;
    cy.getEntityHeading('Inventory').should('exist');
    cy.url().should('match', inventoryPageUrlPattern);
  });

  describe('Inventory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(inventoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Inventory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/inventory/new$'));
        cy.getEntityCreateUpdateHeading('Inventory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {

      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/inventories',
          body: inventorySample,
        }).then(({ body }) => {
          inventory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/inventories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [inventory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(inventoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Inventory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('inventory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryPageUrlPattern);
      });

      it('edit button click should load edit Inventory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Inventory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryPageUrlPattern);
      });

      it('edit button click should load edit Inventory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Inventory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryPageUrlPattern);
      });

      it('last delete button click should delete instance of Inventory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('inventory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryPageUrlPattern);
  
        inventory = undefined;
      });
    });
  });

  describe('new Inventory page', () => {
    beforeEach(() => {
      cy.visit(`${inventoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Inventory');
    });

    it('should create an instance of Inventory', () => {

      cy.get(`[data-cy="name"]`).type('copying Metal heuristic').should('have.value', 'copying Metal heuristic');


      cy.get(`[data-cy="quantity"]`).type('38383').should('have.value', '38383');


      cy.get(`[data-cy="status"]`).type('Taiwan Reduced').should('have.value', 'Taiwan Reduced');


      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        inventory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', inventoryPageUrlPattern);
    });
  });
});
