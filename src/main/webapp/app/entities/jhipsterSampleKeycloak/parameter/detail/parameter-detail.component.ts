import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IParameter } from '../parameter.model';

@Component({
  selector: 'jhi-parameter-detail',
  templateUrl: './parameter-detail.component.html',
})
export class ParameterDetailComponent implements OnInit {
  parameter: IParameter | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parameter }) => {
      this.parameter = parameter;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
