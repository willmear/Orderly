import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICalendarEvent } from '../calendar-event.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../calendar-event.test-samples';

import { CalendarEventService, RestCalendarEvent } from './calendar-event.service';

const requireRestSample: RestCalendarEvent = {
  ...sampleWithRequiredData,
  start: sampleWithRequiredData.start?.toJSON(),
  end: sampleWithRequiredData.end?.toJSON(),
};

describe('CalendarEvent Service', () => {
  let service: CalendarEventService;
  let httpMock: HttpTestingController;
  let expectedResult: ICalendarEvent | ICalendarEvent[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CalendarEventService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a CalendarEvent', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const calendarEvent = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(calendarEvent).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CalendarEvent', () => {
      const calendarEvent = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(calendarEvent).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CalendarEvent', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CalendarEvent', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CalendarEvent', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCalendarEventToCollectionIfMissing', () => {
      it('should add a CalendarEvent to an empty array', () => {
        const calendarEvent: ICalendarEvent = sampleWithRequiredData;
        expectedResult = service.addCalendarEventToCollectionIfMissing([], calendarEvent);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(calendarEvent);
      });

      it('should not add a CalendarEvent to an array that contains it', () => {
        const calendarEvent: ICalendarEvent = sampleWithRequiredData;
        const calendarEventCollection: ICalendarEvent[] = [
          {
            ...calendarEvent,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCalendarEventToCollectionIfMissing(calendarEventCollection, calendarEvent);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CalendarEvent to an array that doesn't contain it", () => {
        const calendarEvent: ICalendarEvent = sampleWithRequiredData;
        const calendarEventCollection: ICalendarEvent[] = [sampleWithPartialData];
        expectedResult = service.addCalendarEventToCollectionIfMissing(calendarEventCollection, calendarEvent);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(calendarEvent);
      });

      it('should add only unique CalendarEvent to an array', () => {
        const calendarEventArray: ICalendarEvent[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const calendarEventCollection: ICalendarEvent[] = [sampleWithRequiredData];
        expectedResult = service.addCalendarEventToCollectionIfMissing(calendarEventCollection, ...calendarEventArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const calendarEvent: ICalendarEvent = sampleWithRequiredData;
        const calendarEvent2: ICalendarEvent = sampleWithPartialData;
        expectedResult = service.addCalendarEventToCollectionIfMissing([], calendarEvent, calendarEvent2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(calendarEvent);
        expect(expectedResult).toContain(calendarEvent2);
      });

      it('should accept null and undefined values', () => {
        const calendarEvent: ICalendarEvent = sampleWithRequiredData;
        expectedResult = service.addCalendarEventToCollectionIfMissing([], null, calendarEvent, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(calendarEvent);
      });

      it('should return initial array if no CalendarEvent is added', () => {
        const calendarEventCollection: ICalendarEvent[] = [sampleWithRequiredData];
        expectedResult = service.addCalendarEventToCollectionIfMissing(calendarEventCollection, undefined, null);
        expect(expectedResult).toEqual(calendarEventCollection);
      });
    });

    describe('compareCalendarEvent', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCalendarEvent(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCalendarEvent(entity1, entity2);
        const compareResult2 = service.compareCalendarEvent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCalendarEvent(entity1, entity2);
        const compareResult2 = service.compareCalendarEvent(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCalendarEvent(entity1, entity2);
        const compareResult2 = service.compareCalendarEvent(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
