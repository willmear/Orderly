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

describe('UserOrders e2e test', () => {
  const userOrdersPageUrl = '/user-orders';
  const userOrdersPageUrlPattern = new RegExp('/user-orders(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const userOrdersSample = {
    orderNum: 6594,
    orderDescription: 'Principal Borders maroon',
    dueDate: '2023-04-25',
    customerID: 83170,
    price: 7376,
  };

  let userOrders;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/user-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/user-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/user-orders/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (userOrders) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/user-orders/${userOrders.id}`,
      }).then(() => {
        userOrders = undefined;
      });
    }
  });

  it('UserOrders menu should load UserOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-orders');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserOrders').should('exist');
    cy.url().should('match', userOrdersPageUrlPattern);
  });

  describe('UserOrders page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userOrdersPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserOrders page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-orders/new$'));
        cy.getEntityCreateUpdateHeading('UserOrders');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userOrdersPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/user-orders',
          body: userOrdersSample,
        }).then(({ body }) => {
          userOrders = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/user-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [userOrders],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(userOrdersPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details UserOrders page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userOrders');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userOrdersPageUrlPattern);
      });

      it('edit button click should load edit UserOrders page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserOrders');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userOrdersPageUrlPattern);
      });

      it('edit button click should load edit UserOrders page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserOrders');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userOrdersPageUrlPattern);
      });

      it('last delete button click should delete instance of UserOrders', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('userOrders').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', userOrdersPageUrlPattern);

        userOrders = undefined;
      });
    });
  });

  describe('new UserOrders page', () => {
    beforeEach(() => {
      cy.visit(`${userOrdersPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserOrders');
    });

    it('should create an instance of UserOrders', () => {
      cy.get(`[data-cy="orderNum"]`).type('23376').should('have.value', '23376');

      cy.get(`[data-cy="orderDescription"]`).type('mobile Loan Product').should('have.value', 'mobile Loan Product');

      cy.get(`[data-cy="deliveryAddress"]`).type('withdrawal Rubber').should('have.value', 'withdrawal Rubber');

      cy.get(`[data-cy="dateOrdered"]`).type('2023-04-25').blur().should('have.value', '2023-04-25');

      cy.get(`[data-cy="dueDate"]`).type('2023-04-25').blur().should('have.value', '2023-04-25');

      cy.get(`[data-cy="customerID"]`).type('50746').should('have.value', '50746');

      cy.get(`[data-cy="productionTime"]`).type('91101').should('have.value', '91101');

      cy.get(`[data-cy="productionCost"]`).type('38652').should('have.value', '38652');

      cy.get(`[data-cy="price"]`).type('83203').should('have.value', '83203');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        userOrders = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', userOrdersPageUrlPattern);
    });
  });
});
