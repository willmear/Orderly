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

describe('Businesses e2e test', () => {
  const businessesPageUrl = '/businesses';
  const businessesPageUrlPattern = new RegExp('/businesses(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const businessesSample = {
    name: 'parsing Rubber',
    summary: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
    type: 'Directives Sleek',
    image: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
    imageContentType: 'unknown',
  };

  let businesses;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/businesses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/businesses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/businesses/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (businesses) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/businesses/${businesses.id}`,
      }).then(() => {
        businesses = undefined;
      });
    }
  });

  it('Businesses menu should load Businesses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('businesses');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Businesses').should('exist');
    cy.url().should('match', businessesPageUrlPattern);
  });

  describe('Businesses page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(businessesPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Businesses page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/businesses/new$'));
        cy.getEntityCreateUpdateHeading('Businesses');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessesPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/businesses',
          body: businessesSample,
        }).then(({ body }) => {
          businesses = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/businesses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [businesses],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(businessesPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Businesses page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('businesses');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessesPageUrlPattern);
      });

      it('edit button click should load edit Businesses page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Businesses');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessesPageUrlPattern);
      });

      it('edit button click should load edit Businesses page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Businesses');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessesPageUrlPattern);
      });

      it('last delete button click should delete instance of Businesses', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('businesses').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', businessesPageUrlPattern);

        businesses = undefined;
      });
    });
  });

  describe('new Businesses page', () => {
    beforeEach(() => {
      cy.visit(`${businessesPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Businesses');
    });

    it('should create an instance of Businesses', () => {
      cy.get(`[data-cy="name"]`).type('Alabama Handmade').should('have.value', 'Alabama Handmade');

      cy.get(`[data-cy="summary"]`)
        .type('../fake-data/blob/hipster.txt')
        .invoke('val')
        .should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="rating"]`).type('86189').should('have.value', '86189');

      cy.get(`[data-cy="location"]`).type('Intelligent Global').should('have.value', 'Intelligent Global');

      cy.get(`[data-cy="type"]`).type('Music Personal').should('have.value', 'Music Personal');

      cy.setFieldImageAsBytesOfEntity('image', 'integration-test.png', 'image/png');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        businesses = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', businessesPageUrlPattern);
    });
  });
});
