import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IConteneur, NewConteneur } from '../conteneur.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IConteneur for edit and NewConteneurFormGroupInput for create.
 */
type ConteneurFormGroupInput = IConteneur | PartialWithRequiredKeyOf<NewConteneur>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IConteneur | NewConteneur> = Omit<T, 'dateEntree' | 'dateSortie'> & {
  dateEntree?: string | null;
  dateSortie?: string | null;
};

type ConteneurFormRawValue = FormValueOf<IConteneur>;

type NewConteneurFormRawValue = FormValueOf<NewConteneur>;

type ConteneurFormDefaults = Pick<NewConteneur, 'id' | 'dateEntree' | 'dateSortie'>;

type ConteneurFormGroupContent = {
  id: FormControl<ConteneurFormRawValue['id'] | NewConteneur['id']>;
  statut: FormControl<ConteneurFormRawValue['statut']>;
  dateEntree: FormControl<ConteneurFormRawValue['dateEntree']>;
  dateSortie: FormControl<ConteneurFormRawValue['dateSortie']>;
  zone: FormControl<ConteneurFormRawValue['zone']>;
  ligne: FormControl<ConteneurFormRawValue['ligne']>;
  colonne: FormControl<ConteneurFormRawValue['colonne']>;
  commentaire: FormControl<ConteneurFormRawValue['commentaire']>;
};

export type ConteneurFormGroup = FormGroup<ConteneurFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ConteneurFormService {
  createConteneurFormGroup(conteneur: ConteneurFormGroupInput = { id: null }): ConteneurFormGroup {
    const conteneurRawValue = this.convertConteneurToConteneurRawValue({
      ...this.getFormDefaults(),
      ...conteneur,
    });
    return new FormGroup<ConteneurFormGroupContent>({
      id: new FormControl(
        { value: conteneurRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      statut: new FormControl(conteneurRawValue.statut),
      dateEntree: new FormControl(conteneurRawValue.dateEntree),
      dateSortie: new FormControl(conteneurRawValue.dateSortie),
      zone: new FormControl(conteneurRawValue.zone),
      ligne: new FormControl(conteneurRawValue.ligne),
      colonne: new FormControl(conteneurRawValue.colonne),
      commentaire: new FormControl(conteneurRawValue.commentaire),
    });
  }

  getConteneur(form: ConteneurFormGroup): IConteneur | NewConteneur {
    return this.convertConteneurRawValueToConteneur(form.getRawValue() as ConteneurFormRawValue | NewConteneurFormRawValue);
  }

  resetForm(form: ConteneurFormGroup, conteneur: ConteneurFormGroupInput): void {
    const conteneurRawValue = this.convertConteneurToConteneurRawValue({ ...this.getFormDefaults(), ...conteneur });
    form.reset(
      {
        ...conteneurRawValue,
        id: { value: conteneurRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ConteneurFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateEntree: currentTime,
      dateSortie: currentTime,
    };
  }

  private convertConteneurRawValueToConteneur(rawConteneur: ConteneurFormRawValue | NewConteneurFormRawValue): IConteneur | NewConteneur {
    return {
      ...rawConteneur,
      dateEntree: dayjs(rawConteneur.dateEntree, DATE_TIME_FORMAT),
      dateSortie: dayjs(rawConteneur.dateSortie, DATE_TIME_FORMAT),
    };
  }

  private convertConteneurToConteneurRawValue(
    conteneur: IConteneur | (Partial<NewConteneur> & ConteneurFormDefaults),
  ): ConteneurFormRawValue | PartialWithRequiredKeyOf<NewConteneurFormRawValue> {
    return {
      ...conteneur,
      dateEntree: conteneur.dateEntree ? conteneur.dateEntree.format(DATE_TIME_FORMAT) : undefined,
      dateSortie: conteneur.dateSortie ? conteneur.dateSortie.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
