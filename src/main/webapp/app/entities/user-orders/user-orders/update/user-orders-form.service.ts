import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IUserOrders, NewUserOrders } from '../user-orders.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserOrders for edit and NewUserOrdersFormGroupInput for create.
 */
type UserOrdersFormGroupInput = IUserOrders | PartialWithRequiredKeyOf<NewUserOrders>;

type UserOrdersFormDefaults = Pick<NewUserOrders, 'id'>;

type UserOrdersFormGroupContent = {
  id: FormControl<IUserOrders['id'] | NewUserOrders['id']>;
  orderNum: FormControl<IUserOrders['orderNum']>;
  orderDescription: FormControl<IUserOrders['orderDescription']>;
  deliveryAddress: FormControl<IUserOrders['deliveryAddress']>;
  dateOrdered: FormControl<IUserOrders['dateOrdered']>;
  dueDate: FormControl<IUserOrders['dueDate']>;
  customerID: FormControl<IUserOrders['customerID']>;
  productionTime: FormControl<IUserOrders['productionTime']>;
  productionCost: FormControl<IUserOrders['productionCost']>;
  price: FormControl<IUserOrders['price']>;
  user: FormControl<IUserOrders['user']>;
};

export type UserOrdersFormGroup = FormGroup<UserOrdersFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserOrdersFormService {
  createUserOrdersFormGroup(userOrders: UserOrdersFormGroupInput = { id: null }): UserOrdersFormGroup {
    const userOrdersRawValue = {
      ...this.getFormDefaults(),
      ...userOrders,
    };
    return new FormGroup<UserOrdersFormGroupContent>({
      id: new FormControl(
        { value: userOrdersRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      orderNum: new FormControl(userOrdersRawValue.orderNum, {
        validators: [Validators.required],
      }),
      orderDescription: new FormControl(userOrdersRawValue.orderDescription, {
        validators: [Validators.required],
      }),
      deliveryAddress: new FormControl(userOrdersRawValue.deliveryAddress),
      dateOrdered: new FormControl(userOrdersRawValue.dateOrdered),
      dueDate: new FormControl(userOrdersRawValue.dueDate, {
        validators: [Validators.required],
      }),
      customerID: new FormControl(userOrdersRawValue.customerID, {
        validators: [Validators.required],
      }),
      productionTime: new FormControl(userOrdersRawValue.productionTime),
      productionCost: new FormControl(userOrdersRawValue.productionCost),
      price: new FormControl(userOrdersRawValue.price, {
        validators: [Validators.required],
      }),
      user: new FormControl(userOrdersRawValue.user),
    });
  }

  getUserOrders(form: UserOrdersFormGroup): IUserOrders | NewUserOrders {
    return form.getRawValue() as IUserOrders | NewUserOrders;
  }

  resetForm(form: UserOrdersFormGroup, userOrders: UserOrdersFormGroupInput): void {
    const userOrdersRawValue = { ...this.getFormDefaults(), ...userOrders };
    form.reset(
      {
        ...userOrdersRawValue,
        id: { value: userOrdersRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): UserOrdersFormDefaults {
    return {
      id: null,
    };
  }
}
