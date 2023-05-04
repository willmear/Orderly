import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBusinesses } from '../businesses.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../businesses.test-samples';

import { BusinessesService } from './businesses.service';

const requireRestSample: IBusinesses = {
  ...sampleWithRequiredData,
};

describe('Businesses Service', () => {
  let service: BusinessesService;
  let httpMock: HttpTestingController;
  let expectedResult: IBusinesses | IBusinesses[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BusinessesService);
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

    it('should create a Businesses', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const businesses = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(businesses).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Businesses', () => {
      const businesses = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(businesses).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Businesses', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Businesses', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Businesses', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBusinessesToCollectionIfMissing', () => {
      it('should add a Businesses to an empty array', () => {
        const businesses: IBusinesses = sampleWithRequiredData;
        expectedResult = service.addBusinessesToCollectionIfMissing([], businesses);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(businesses);
      });

      it('should not add a Businesses to an array that contains it', () => {
        const businesses: IBusinesses = sampleWithRequiredData;
        const businessesCollection: IBusinesses[] = [
          {
            ...businesses,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBusinessesToCollectionIfMissing(businessesCollection, businesses);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Businesses to an array that doesn't contain it", () => {
        const businesses: IBusinesses = sampleWithRequiredData;
        const businessesCollection: IBusinesses[] = [sampleWithPartialData];
        expectedResult = service.addBusinessesToCollectionIfMissing(businessesCollection, businesses);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(businesses);
      });

      it('should add only unique Businesses to an array', () => {
        const businessesArray: IBusinesses[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const businessesCollection: IBusinesses[] = [sampleWithRequiredData];
        expectedResult = service.addBusinessesToCollectionIfMissing(businessesCollection, ...businessesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const businesses: IBusinesses = sampleWithRequiredData;
        const businesses2: IBusinesses = sampleWithPartialData;
        expectedResult = service.addBusinessesToCollectionIfMissing([], businesses, businesses2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(businesses);
        expect(expectedResult).toContain(businesses2);
      });

      it('should accept null and undefined values', () => {
        const businesses: IBusinesses = sampleWithRequiredData;
        expectedResult = service.addBusinessesToCollectionIfMissing([], null, businesses, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(businesses);
      });

      it('should return initial array if no Businesses is added', () => {
        const businessesCollection: IBusinesses[] = [sampleWithRequiredData];
        expectedResult = service.addBusinessesToCollectionIfMissing(businessesCollection, undefined, null);
        expect(expectedResult).toEqual(businessesCollection);
      });
    });

    describe('compareBusinesses', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBusinesses(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBusinesses(entity1, entity2);
        const compareResult2 = service.compareBusinesses(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBusinesses(entity1, entity2);
        const compareResult2 = service.compareBusinesses(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBusinesses(entity1, entity2);
        const compareResult2 = service.compareBusinesses(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
