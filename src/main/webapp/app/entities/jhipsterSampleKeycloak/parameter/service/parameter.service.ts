import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IParameter, getParameterIdentifier } from '../parameter.model';

export type EntityResponseType = HttpResponse<IParameter>;
export type EntityArrayResponseType = HttpResponse<IParameter[]>;

@Injectable({ providedIn: 'root' })
export class ParameterService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/parameters', 'jhipstersamplekeycloak');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(parameter: IParameter): Observable<EntityResponseType> {
    return this.http.post<IParameter>(this.resourceUrl, parameter, { observe: 'response' });
  }

  update(parameter: IParameter): Observable<EntityResponseType> {
    return this.http.put<IParameter>(`${this.resourceUrl}/${getParameterIdentifier(parameter) as number}`, parameter, {
      observe: 'response',
    });
  }

  partialUpdate(parameter: IParameter): Observable<EntityResponseType> {
    return this.http.patch<IParameter>(`${this.resourceUrl}/${getParameterIdentifier(parameter) as number}`, parameter, {
      observe: 'response',
    });
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

  addParameterToCollectionIfMissing(
    parameterCollection: IParameter[],
    ...parametersToCheck: (IParameter | null | undefined)[]
  ): IParameter[] {
    const parameters: IParameter[] = parametersToCheck.filter(isPresent);
    if (parameters.length > 0) {
      const parameterCollectionIdentifiers = parameterCollection.map(parameterItem => getParameterIdentifier(parameterItem)!);
      const parametersToAdd = parameters.filter(parameterItem => {
        const parameterIdentifier = getParameterIdentifier(parameterItem);
        if (parameterIdentifier == null || parameterCollectionIdentifiers.includes(parameterIdentifier)) {
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
