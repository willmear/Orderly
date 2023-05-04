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

describe('CalendarEvent e2e test', () => {
  const calendarEventPageUrl = '/calendar-event';
  const calendarEventPageUrlPattern = new RegExp('/calendar-event(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const calendarEventSample = { name: 'demand-driven', start: '2023-03-11T17:02:21.530Z', end: '2023-03-11T11:53:37.682Z' };

  let calendarEvent;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/calendar-events+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/calendar-events').as('postEntityRequest');
    cy.intercept('DELETE', '/api/calendar-events/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (calendarEvent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/calendar-events/${calendarEvent.id}`,
      }).then(() => {
        calendarEvent = undefined;
      });
    }
  });

  it('CalendarEvents menu should load CalendarEvents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('calendar-event');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CalendarEvent').should('exist');
    cy.url().should('match', calendarEventPageUrlPattern);
  });

  describe('CalendarEvent page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(calendarEventPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CalendarEvent page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/calendar-event/new$'));
        cy.getEntityCreateUpdateHeading('CalendarEvent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', calendarEventPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/calendar-events',
          body: calendarEventSample,
        }).then(({ body }) => {
          calendarEvent = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/calendar-events+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [calendarEvent],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(calendarEventPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CalendarEvent page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('calendarEvent');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', calendarEventPageUrlPattern);
      });

      it('edit button click should load edit CalendarEvent page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CalendarEvent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', calendarEventPageUrlPattern);
      });

      it('edit button click should load edit CalendarEvent page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CalendarEvent');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', calendarEventPageUrlPattern);
      });

      it('last delete button click should delete instance of CalendarEvent', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('calendarEvent').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', calendarEventPageUrlPattern);

        calendarEvent = undefined;
      });
    });
  });

  describe('new CalendarEvent page', () => {
    beforeEach(() => {
      cy.visit(`${calendarEventPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CalendarEvent');
    });

    it('should create an instance of CalendarEvent', () => {
      cy.get(`[data-cy="name"]`).type('IB Account Account').should('have.value', 'IB Account Account');

      cy.get(`[data-cy="description"]`).type('Indiana').should('have.value', 'Indiana');

      cy.get(`[data-cy="start"]`).type('2023-03-11T11:18').blur().should('have.value', '2023-03-11T11:18');

      cy.get(`[data-cy="end"]`).type('2023-03-11T03:49').blur().should('have.value', '2023-03-11T03:49');

      cy.get(`[data-cy="location"]`).type('Concrete Democratic').should('have.value', 'Concrete Democratic');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        calendarEvent = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', calendarEventPageUrlPattern);
    });
  });
});
