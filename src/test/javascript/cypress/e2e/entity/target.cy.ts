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

describe('Target e2e test', () => {
  const targetPageUrl = '/target';
  const targetPageUrlPattern = new RegExp('/target(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const targetSample = {};

  let target;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/targets+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/targets').as('postEntityRequest');
    cy.intercept('DELETE', '/api/targets/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (target) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/targets/${target.id}`,
      }).then(() => {
        target = undefined;
      });
    }
  });

  it('Targets menu should load Targets page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('target');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Target').should('exist');
    cy.url().should('match', targetPageUrlPattern);
  });

  describe('Target page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(targetPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Target page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/target/new$'));
        cy.getEntityCreateUpdateHeading('Target');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', targetPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/targets',
          body: targetSample,
        }).then(({ body }) => {
          target = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/targets+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [target],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(targetPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Target page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('target');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', targetPageUrlPattern);
      });

      it('edit button click should load edit Target page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Target');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', targetPageUrlPattern);
      });

      it('edit button click should load edit Target page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Target');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', targetPageUrlPattern);
      });

      it('last delete button click should delete instance of Target', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('target').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', targetPageUrlPattern);

        target = undefined;
      });
    });
  });

  describe('new Target page', () => {
    beforeEach(() => {
      cy.visit(`${targetPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Target');
    });

    it('should create an instance of Target', () => {
      cy.get(`[data-cy="text"]`).type('Berkshire Producer').should('have.value', 'Berkshire Producer');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        target = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', targetPageUrlPattern);
    });
  });
});
