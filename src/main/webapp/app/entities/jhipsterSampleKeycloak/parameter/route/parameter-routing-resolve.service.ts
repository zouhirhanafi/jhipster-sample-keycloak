import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IParameter } from '../parameter.model';
import { ParameterService } from '../service/parameter.service';

const parameterResolve = (route: ActivatedRouteSnapshot): Observable<null | IParameter> => {
  const id = route.params.id;
  if (id) {
    return inject(ParameterService)
      .find(id)
      .pipe(
        mergeMap((parameter: HttpResponse<IParameter>) => {
          if (parameter.body) {
            return of(parameter.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default parameterResolve;
