import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IParameter } from '../parameter.model';
import { ParameterService } from '../service/parameter.service';
import { ParameterFormGroup, ParameterFormService } from './parameter-form.service';

@Component({
  standalone: true,
  selector: 'jhi-parameter-update',
  templateUrl: './parameter-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ParameterUpdateComponent implements OnInit {
  isSaving = false;
  parameter: IParameter | null = null;

  parametersSharedCollection: IParameter[] = [];

  protected parameterService = inject(ParameterService);
  protected parameterFormService = inject(ParameterFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ParameterFormGroup = this.parameterFormService.createParameterFormGroup();

  compareParameter = (o1: IParameter | null, o2: IParameter | null): boolean => this.parameterService.compareParameter(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parameter }) => {
      this.parameter = parameter;
      if (parameter) {
        this.updateForm(parameter);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const parameter = this.parameterFormService.getParameter(this.editForm);
    if (parameter.id !== null) {
      this.subscribeToSaveResponse(this.parameterService.update(parameter));
    } else {
      this.subscribeToSaveResponse(this.parameterService.create(parameter));
    }
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
    this.parameter = parameter;
    this.parameterFormService.resetForm(this.editForm, parameter);

    this.parametersSharedCollection = this.parameterService.addParameterToCollectionIfMissing<IParameter>(
      this.parametersSharedCollection,
      parameter.type,
      parameter.paraent,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.parameterService
      .query()
      .pipe(map((res: HttpResponse<IParameter[]>) => res.body ?? []))
      .pipe(
        map((parameters: IParameter[]) =>
          this.parameterService.addParameterToCollectionIfMissing<IParameter>(parameters, this.parameter?.type, this.parameter?.paraent),
        ),
      )
      .subscribe((parameters: IParameter[]) => (this.parametersSharedCollection = parameters));
  }
}
