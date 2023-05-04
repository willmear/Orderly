import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITarget, NewTarget } from '../target.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITarget for edit and NewTargetFormGroupInput for create.
 */
type TargetFormGroupInput = ITarget | PartialWithRequiredKeyOf<NewTarget>;

type TargetFormDefaults = Pick<NewTarget, 'id'>;

type TargetFormGroupContent = {
  id: FormControl<ITarget['id'] | NewTarget['id']>;
  text: FormControl<ITarget['text']>;
};

export type TargetFormGroup = FormGroup<TargetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TargetFormService {
  createTargetFormGroup(target: TargetFormGroupInput = { id: null }): TargetFormGroup {
    const targetRawValue = {
      ...this.getFormDefaults(),
      ...target,
    };
    return new FormGroup<TargetFormGroupContent>({
      id: new FormControl(
        { value: targetRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      text: new FormControl(targetRawValue.text),
    });
  }

  getTarget(form: TargetFormGroup): ITarget | NewTarget {
    return form.getRawValue() as ITarget | NewTarget;
  }

  resetForm(form: TargetFormGroup, target: TargetFormGroupInput): void {
    const targetRawValue = { ...this.getFormDefaults(), ...target };
    form.reset(
      {
        ...targetRawValue,
        id: { value: targetRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TargetFormDefaults {
    return {
      id: null,
    };
  }
}
