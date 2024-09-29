import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IParameter, NewParameter } from '../parameter.model';

export type PartialUpdateParameter = Partial<IParameter> & Pick<IParameter, 'id'>;

export type EntityResponseType = HttpResponse<IParameter>;
export type EntityArrayResponseType = HttpResponse<IParameter[]>;

@Injectable({ providedIn: 'root' })
export class ParameterService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/parameters', 'jhipstersamplekeycloak');

  create(parameter: NewParameter): Observable<EntityResponseType> {
    return this.http.post<IParameter>(this.resourceUrl, parameter, { observe: 'response' });
  }

  update(parameter: IParameter): Observable<EntityResponseType> {
    return this.http.put<IParameter>(`${this.resourceUrl}/${this.getParameterIdentifier(parameter)}`, parameter, { observe: 'response' });
  }

  partialUpdate(parameter: PartialUpdateParameter): Observable<EntityResponseType> {
    return this.http.patch<IParameter>(`${this.resourceUrl}/${this.getParameterIdentifier(parameter)}`, parameter, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IParameter>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IParameter[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getParameterIdentifier(parameter: Pick<IParameter, 'id'>): number {
    return parameter.id;
  }

  compareParameter(o1: Pick<IParameter, 'id'> | null, o2: Pick<IParameter, 'id'> | null): boolean {
    return o1 && o2 ? this.getParameterIdentifier(o1) === this.getParameterIdentifier(o2) : o1 === o2;
  }

  addParameterToCollectionIfMissing<Type extends Pick<IParameter, 'id'>>(
    parameterCollection: Type[],
    ...parametersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const parameters: Type[] = parametersToCheck.filter(isPresent);
    if (parameters.length > 0) {
      const parameterCollectionIdentifiers = parameterCollection.map(parameterItem => this.getParameterIdentifier(parameterItem));
      const parametersToAdd = parameters.filter(parameterItem => {
        const parameterIdentifier = this.getParameterIdentifier(parameterItem);
        if (parameterCollectionIdentifiers.includes(parameterIdentifier)) {
          return false;
        }
        parameterCollectionIdentifiers.push(parameterIdentifier);
        return true;
      });
      return [...parametersToAdd, ...parameterCollection];
    }
    return parameterCollection;
  }
}
