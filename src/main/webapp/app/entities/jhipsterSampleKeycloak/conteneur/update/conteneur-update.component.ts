import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IConteneur } from '../conteneur.model';
import { ConteneurService } from '../service/conteneur.service';
import { ConteneurFormGroup, ConteneurFormService } from './conteneur-form.service';

@Component({
  standalone: true,
  selector: 'jhi-conteneur-update',
  templateUrl: './conteneur-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ConteneurUpdateComponent implements OnInit {
  isSaving = false;
  conteneur: IConteneur | null = null;

  protected conteneurService = inject(ConteneurService);
  protected conteneurFormService = inject(ConteneurFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ConteneurFormGroup = this.conteneurFormService.createConteneurFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conteneur }) => {
      this.conteneur = conteneur;
      if (conteneur) {
        this.updateForm(conteneur);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const conteneur = this.conteneurFormService.getConteneur(this.editForm);
    if (conteneur.id !== null) {
      this.subscribeToSaveResponse(this.conteneurService.update(conteneur));
    } else {
      this.subscribeToSaveResponse(this.conteneurService.create(conteneur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConteneur>>): void {
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

  protected updateForm(conteneur: IConteneur): void {
    this.conteneur = conteneur;
    this.conteneurFormService.resetForm(this.editForm, conteneur);
  }
}
