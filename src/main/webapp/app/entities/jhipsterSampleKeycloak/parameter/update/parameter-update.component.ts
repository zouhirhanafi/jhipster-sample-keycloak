import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IParameter, Parameter } from '../parameter.model';
import { ParameterService } from '../service/parameter.service';

@Component({
  selector: 'jhi-parameter-update',
  templateUrl: './parameter-update.component.html',
})
export class ParameterUpdateComponent implements OnInit {
  isSaving = false;

  parametersSharedCollection: IParameter[] = [];

  editForm = this.fb.group({
    id: [],
    label: [],
    activated: [],
    lib2: [],
    lib3: [],
    refExterne: [],
    val1: [],
    val2: [],
    val3: [],
    ordre: [],
    type: [],
    paraent: [],
  });

  constructor(protected parameterService: ParameterService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parameter }) => {
      this.updateForm(parameter);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const parameter = this.createFromForm();
    if (parameter.id !== undefined) {
      this.subscribeToSaveResponse(this.parameterService.update(parameter));
    } else {
      this.subscribeToSaveResponse(this.parameterService.create(parameter));
    }
  }

  trackParameterById(index: number, item: IParameter): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IParameter>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(parameter: IParameter): void {
    this.editForm.patchValue({
      id: parameter.id,
      label: parameter.label,
      activated: parameter.activated,
      lib2: parameter.lib2,
      lib3: parameter.lib3,
      refExterne: parameter.refExterne,
      val1: parameter.val1,
      val2: parameter.val2,
      val3: parameter.val3,
      ordre: parameter.ordre,
      type: parameter.type,
      paraent: parameter.paraent,
    });

    this.parametersSharedCollection = this.parameterService.addParameterToCollectionIfMissing(
      this.parametersSharedCollection,
      parameter.type,
      parameter.paraent
    );
  }

  protected loadRelationshipsOptions(): void {
    this.parameterService
      .query()
      .pipe(map((res: HttpResponse<IParameter[]>) => res.body ?? []))
      .pipe(
        map((parameters: IParameter[]) =>
          this.parameterService.addParameterToCollectionIfMissing(
            parameters,
            this.editForm.get('type')!.value,
            this.editForm.get('paraent')!.value
          )
        )
      )
      .subscribe((parameters: IParameter[]) => (this.parametersSharedCollection = parameters));
  }

  protected createFromForm(): IParameter {
    return {
      ...new Parameter(),
      id: this.editForm.get(['id'])!.value,
      label: this.editForm.get(['label'])!.value,
      activated: this.editForm.get(['activated'])!.value,
      lib2: this.editForm.get(['lib2'])!.value,
      lib3: this.editForm.get(['lib3'])!.value,
      refExterne: this.editForm.get(['refExterne'])!.value,
      val1: this.editForm.get(['val1'])!.value,
      val2: this.editForm.get(['val2'])!.value,
      val3: this.editForm.get(['val3'])!.value,
      ordre: this.editForm.get(['ordre'])!.value,
      type: this.editForm.get(['type'])!.value,
      paraent: this.editForm.get(['paraent'])!.value,
    };
  }
}
