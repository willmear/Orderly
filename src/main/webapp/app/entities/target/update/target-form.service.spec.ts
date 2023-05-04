import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../target.test-samples';

import { TargetFormService } from './target-form.service';

describe('Target Form Service', () => {
  let service: TargetFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TargetFormService);
  });

  describe('Service methods', () => {
    describe('createTargetFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTargetFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            text: expect.any(Object),
          })
        );
      });

      it('passing ITarget should create a new form with FormGroup', () => {
        const formGroup = service.createTargetFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            text: expect.any(Object),
          })
        );
      });
    });

    describe('getTarget', () => {
      it('should return NewTarget for default Target initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTargetFormGroup(sampleWithNewData);

        const target = service.getTarget(formGroup) as any;

        expect(target).toMatchObject(sampleWithNewData);
      });

      it('should return NewTarget for empty Target initial value', () => {
        const formGroup = service.createTargetFormGroup();

        const target = service.getTarget(formGroup) as any;

        expect(target).toMatchObject({});
      });

      it('should return ITarget', () => {
        const formGroup = service.createTargetFormGroup(sampleWithRequiredData);

        const target = service.getTarget(formGroup) as any;

        expect(target).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITarget should not enable id FormControl', () => {
        const formGroup = service.createTargetFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTarget should disable id FormControl', () => {
        const formGroup = service.createTargetFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
