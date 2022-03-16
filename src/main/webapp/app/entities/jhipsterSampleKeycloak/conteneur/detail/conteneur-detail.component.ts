import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IConteneur } from '../conteneur.model';

@Component({
  selector: 'jhi-conteneur-detail',
  templateUrl: './conteneur-detail.component.html',
})
export class ConteneurDetailComponent implements OnInit {
  conteneur: IConteneur | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conteneur }) => {
      this.conteneur = conteneur;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
