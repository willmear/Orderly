import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../businesses.test-samples';

import { BusinessesFormService } from './businesses-form.service';

describe('Businesses Form Service', () => {
  let service: BusinessesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BusinessesFormService);
  });

  describe('Service methods', () => {
    describe('createBusinessesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBusinessesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            summary: expect.any(Object),
            rating: expect.any(Object),
            location: expect.any(Object),
            type: expect.any(Object),
            image: expect.any(Object),
          })
        );
      });

      it('passing IBusinesses should create a new form with FormGroup', () => {
        const formGroup = service.createBusinessesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            summary: expect.any(Object),
            rating: expect.any(Object),
            location: expect.any(Object),
            type: expect.any(Object),
            image: expect.any(Object),
          })
        );
      });
    });

    describe('getBusinesses', () => {
      it('should return NewBusinesses for default Businesses initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBusinessesFormGroup(sampleWithNewData);

        const businesses = service.getBusinesses(formGroup) as any;

        expect(businesses).toMatchObject(sampleWithNewData);
      });

      it('should return NewBusinesses for empty Businesses initial value', () => {
        const formGroup = service.createBusinessesFormGroup();

        const businesses = service.getBusinesses(formGroup) as any;

        expect(businesses).toMatchObject({});
      });

      it('should return IBusinesses', () => {
        const formGroup = service.createBusinessesFormGroup(sampleWithRequiredData);

        const businesses = service.getBusinesses(formGroup) as any;

        expect(businesses).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBusinesses should not enable id FormControl', () => {
        const formGroup = service.createBusinessesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBusinesses should disable id FormControl', () => {
        const formGroup = service.createBusinessesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
