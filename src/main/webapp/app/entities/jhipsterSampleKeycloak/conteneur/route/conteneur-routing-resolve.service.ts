import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IConteneur, Conteneur } from '../conteneur.model';
import { ConteneurService } from '../service/conteneur.service';

@Injectable({ providedIn: 'root' })
export class ConteneurRoutingResolveService implements Resolve<IConteneur> {
  constructor(protected service: ConteneurService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IConteneur> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((conteneur: HttpResponse<Conteneur>) => {
          if (conteneur.body) {
            return of(conteneur.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Conteneur());
  }
}
