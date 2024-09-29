import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IConteneur } from '../conteneur.model';
import { ConteneurService } from '../service/conteneur.service';

const conteneurResolve = (route: ActivatedRouteSnapshot): Observable<null | IConteneur> => {
  const id = route.params.id;
  if (id) {
    return inject(ConteneurService)
      .find(id)
      .pipe(
        mergeMap((conteneur: HttpResponse<IConteneur>) => {
          if (conteneur.body) {
            return of(conteneur.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default conteneurResolve;
