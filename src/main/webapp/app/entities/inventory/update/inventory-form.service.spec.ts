import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../inventory.test-samples';

import { InventoryFormService } from './inventory-form.service';

describe('Inventory Form Service', () => {
  let service: InventoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InventoryFormService);
  });

  describe('Service methods', () => {
    describe('createInventoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInventoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            quantity: expect.any(Object),
            status: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IInventory should create a new form with FormGroup', () => {
        const formGroup = service.createInventoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(expect.objectContaining({
          id: expect.any(Object),
          name: expect.any(Object),
          quantity: expect.any(Object),
          status: expect.any(Object),
          user: expect.any(Object),
        }));
      });
    });

    describe('getInventory', () => {
      it('should return NewInventory for default Inventory initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createInventoryFormGroup(sampleWithNewData);

        const inventory = service.getInventory(formGroup) as any;

        expect(inventory).toMatchObject(sampleWithNewData);
      });

      it('should return NewInventory for empty Inventory initial value', () => {
        const formGroup = service.createInventoryFormGroup();

        const inventory = service.getInventory(formGroup) as any;

        expect(inventory).toMatchObject({});
      });

      it('should return IInventory', () => {
        const formGroup = service.createInventoryFormGroup(sampleWithRequiredData);

        const inventory = service.getInventory(formGroup) as any;

        expect(inventory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInventory should not enable id FormControl', () => {
        const formGroup = service.createInventoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInventory should disable id FormControl', () => {
        const formGroup = service.createInventoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
