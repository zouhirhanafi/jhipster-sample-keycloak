import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IParameter, Parameter } from '../parameter.model';
import { ParameterService } from '../service/parameter.service';

@Injectable({ providedIn: 'root' })
export class ParameterRoutingResolveService implements Resolve<IParameter> {
  constructor(protected service: ParameterService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IParameter> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((parameter: HttpResponse<Parameter>) => {
          if (parameter.body) {
            return of(parameter.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Parameter());
  }
}
