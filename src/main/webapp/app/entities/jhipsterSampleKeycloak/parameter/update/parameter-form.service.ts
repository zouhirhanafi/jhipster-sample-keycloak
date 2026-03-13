import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IParameter, NewParameter } from '../parameter.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IParameter for edit and NewParameterFormGroupInput for create.
 */
type ParameterFormGroupInput = IParameter | PartialWithRequiredKeyOf<NewParameter>;

type ParameterFormDefaults = Pick<NewParameter, 'id' | 'activated'>;

type ParameterFormGroupContent = {
  id: FormControl<IParameter['id'] | NewParameter['id']>;
  label: FormControl<IParameter['label']>;
  activated: FormControl<IParameter['activated']>;
  lib2: FormControl<IParameter['lib2']>;
  lib3: FormControl<IParameter['lib3']>;
  refExterne: FormControl<IParameter['refExterne']>;
  val1: FormControl<IParameter['val1']>;
  val2: FormControl<IParameter['val2']>;
  val3: FormControl<IParameter['val3']>;
  ordre: FormControl<IParameter['ordre']>;
  type: FormControl<IParameter['type']>;
  paraent: FormControl<IParameter['paraent']>;
};

export type ParameterFormGroup = FormGroup<ParameterFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ParameterFormService {
  createParameterFormGroup(parameter: ParameterFormGroupInput = { id: null }): ParameterFormGroup {
    const parameterRawValue = {
      ...this.getFormDefaults(),
      ...parameter,
    };
    return new FormGroup<ParameterFormGroupContent>({
      id: new FormControl(
        { value: parameterRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      label: new FormControl(parameterRawValue.label),
      activated: new FormControl(parameterRawValue.activated),
      lib2: new FormControl(parameterRawValue.lib2),
      lib3: new FormControl(parameterRawValue.lib3),
      refExterne: new FormControl(parameterRawValue.refExterne),
      val1: new FormControl(parameterRawValue.val1),
      val2: new FormControl(parameterRawValue.val2),
      val3: new FormControl(parameterRawValue.val3),
      ordre: new FormControl(parameterRawValue.ordre),
      type: new FormControl(parameterRawValue.type),
      paraent: new FormControl(parameterRawValue.paraent),
    });
  }

  getParameter(form: ParameterFormGroup): IParameter | NewParameter {
    return form.getRawValue() as IParameter | NewParameter;
  }

  resetForm(form: ParameterFormGroup, parameter: ParameterFormGroupInput): void {
    const parameterRawValue = { ...this.getFormDefaults(), ...parameter };
    form.reset(
      {
        ...parameterRawValue,
        id: { value: parameterRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ParameterFormDefaults {
    return {
      id: null,
      activated: false,
    };
  }
}
