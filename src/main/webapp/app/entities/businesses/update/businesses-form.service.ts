import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBusinesses, NewBusinesses } from '../businesses.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBusinesses for edit and NewBusinessesFormGroupInput for create.
 */
type BusinessesFormGroupInput = IBusinesses | PartialWithRequiredKeyOf<NewBusinesses>;

type BusinessesFormDefaults = Pick<NewBusinesses, 'id'>;

type BusinessesFormGroupContent = {
  id: FormControl<IBusinesses['id'] | NewBusinesses['id']>;
  name: FormControl<IBusinesses['name']>;
  summary: FormControl<IBusinesses['summary']>;
  rating: FormControl<IBusinesses['rating']>;
  location: FormControl<IBusinesses['location']>;
  type: FormControl<IBusinesses['type']>;
  image: FormControl<IBusinesses['image']>;
  imageContentType: FormControl<IBusinesses['imageContentType']>;
};

export type BusinessesFormGroup = FormGroup<BusinessesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BusinessesFormService {
  createBusinessesFormGroup(businesses: BusinessesFormGroupInput = { id: null }): BusinessesFormGroup {
    const businessesRawValue = {
      ...this.getFormDefaults(),
      ...businesses,
    };
    return new FormGroup<BusinessesFormGroupContent>({
      id: new FormControl(
        { value: businessesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(businessesRawValue.name, {
        validators: [Validators.required],
      }),
      summary: new FormControl(businessesRawValue.summary, {
        validators: [Validators.required],
      }),
      rating: new FormControl(businessesRawValue.rating),
      location: new FormControl(businessesRawValue.location),
      type: new FormControl(businessesRawValue.type, {
        validators: [Validators.required],
      }),
      image: new FormControl(businessesRawValue.image, {
        validators: [Validators.required],
      }),
      imageContentType: new FormControl(businessesRawValue.imageContentType),
    });
  }

  getBusinesses(form: BusinessesFormGroup): IBusinesses | NewBusinesses {
    return form.getRawValue() as IBusinesses | NewBusinesses;
  }

  resetForm(form: BusinessesFormGroup, businesses: BusinessesFormGroupInput): void {
    const businessesRawValue = { ...this.getFormDefaults(), ...businesses };
    form.reset(
      {
        ...businessesRawValue,
        id: { value: businessesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BusinessesFormDefaults {
    return {
      id: null,
    };
  }
}
