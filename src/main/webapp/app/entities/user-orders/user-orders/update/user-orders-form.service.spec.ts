import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../user-orders.test-samples';

import { UserOrdersFormService } from './user-orders-form.service';

describe('UserOrders Form Service', () => {
  let service: UserOrdersFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserOrdersFormService);
  });

  describe('Service methods', () => {
    describe('createUserOrdersFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserOrdersFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orderNum: expect.any(Object),
            orderDescription: expect.any(Object),
            deliveryAddress: expect.any(Object),
            dateOrdered: expect.any(Object),
            dueDate: expect.any(Object),
            customerID: expect.any(Object),
            productionTime: expect.any(Object),
            productionCost: expect.any(Object),
            price: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IUserOrders should create a new form with FormGroup', () => {
        const formGroup = service.createUserOrdersFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            orderNum: expect.any(Object),
            orderDescription: expect.any(Object),
            deliveryAddress: expect.any(Object),
            dateOrdered: expect.any(Object),
            dueDate: expect.any(Object),
            customerID: expect.any(Object),
            productionTime: expect.any(Object),
            productionCost: expect.any(Object),
            price: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getUserOrders', () => {
      it('should return NewUserOrders for default UserOrders initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createUserOrdersFormGroup(sampleWithNewData);

        const userOrders = service.getUserOrders(formGroup) as any;

        expect(userOrders).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserOrders for empty UserOrders initial value', () => {
        const formGroup = service.createUserOrdersFormGroup();

        const userOrders = service.getUserOrders(formGroup) as any;

        expect(userOrders).toMatchObject({});
      });

      it('should return IUserOrders', () => {
        const formGroup = service.createUserOrdersFormGroup(sampleWithRequiredData);

        const userOrders = service.getUserOrders(formGroup) as any;

        expect(userOrders).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserOrders should not enable id FormControl', () => {
        const formGroup = service.createUserOrdersFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserOrders should disable id FormControl', () => {
        const formGroup = service.createUserOrdersFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
