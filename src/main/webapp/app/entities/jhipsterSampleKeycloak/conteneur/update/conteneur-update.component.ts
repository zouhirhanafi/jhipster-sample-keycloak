import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IConteneur, Conteneur } from '../conteneur.model';
import { ConteneurService } from '../service/conteneur.service';

@Component({
  selector: 'jhi-conteneur-update',
  templateUrl: './conteneur-update.component.html',
})
export class ConteneurUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    statut: [],
    dateEntree: [],
    dateSortie: [],
    zone: [],
    ligne: [],
    colonne: [],
    commentaire: [],
  });

  constructor(protected conteneurService: ConteneurService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conteneur }) => {
      if (conteneur.id === undefined) {
        const today = dayjs().startOf('day');
        conteneur.dateEntree = today;
        conteneur.dateSortie = today;
      }

      this.updateForm(conteneur);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const conteneur = this.createFromForm();
    if (conteneur.id !== undefined) {
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
    this.editForm.patchValue({
      id: conteneur.id,
      statut: conteneur.statut,
      dateEntree: conteneur.dateEntree ? conteneur.dateEntree.format(DATE_TIME_FORMAT) : null,
      dateSortie: conteneur.dateSortie ? conteneur.dateSortie.format(DATE_TIME_FORMAT) : null,
      zone: conteneur.zone,
      ligne: conteneur.ligne,
      colonne: conteneur.colonne,
      commentaire: conteneur.commentaire,
    });
  }

  protected createFromForm(): IConteneur {
    return {
      ...new Conteneur(),
      id: this.editForm.get(['id'])!.value,
      statut: this.editForm.get(['statut'])!.value,
      dateEntree: this.editForm.get(['dateEntree'])!.value ? dayjs(this.editForm.get(['dateEntree'])!.value, DATE_TIME_FORMAT) : undefined,
      dateSortie: this.editForm.get(['dateSortie'])!.value ? dayjs(this.editForm.get(['dateSortie'])!.value, DATE_TIME_FORMAT) : undefined,
      zone: this.editForm.get(['zone'])!.value,
      ligne: this.editForm.get(['ligne'])!.value,
      colonne: this.editForm.get(['colonne'])!.value,
      commentaire: this.editForm.get(['commentaire'])!.value,
    };
  }
}
