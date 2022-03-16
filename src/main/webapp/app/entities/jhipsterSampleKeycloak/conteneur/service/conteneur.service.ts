import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IConteneur, getConteneurIdentifier } from '../conteneur.model';

export type EntityResponseType = HttpResponse<IConteneur>;
export type EntityArrayResponseType = HttpResponse<IConteneur[]>;

@Injectable({ providedIn: 'root' })
export class ConteneurService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/conteneurs', 'jhipstersamplekeycloak');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(conteneur: IConteneur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conteneur);
    return this.http
      .post<IConteneur>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(conteneur: IConteneur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conteneur);
    return this.http
      .put<IConteneur>(`${this.resourceUrl}/${getConteneurIdentifier(conteneur) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(conteneur: IConteneur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conteneur);
    return this.http
      .patch<IConteneur>(`${this.resourceUrl}/${getConteneurIdentifier(conteneur) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IConteneur>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IConteneur[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addConteneurToCollectionIfMissing(
    conteneurCollection: IConteneur[],
    ...conteneursToCheck: (IConteneur | null | undefined)[]
  ): IConteneur[] {
    const conteneurs: IConteneur[] = conteneursToCheck.filter(isPresent);
    if (conteneurs.length > 0) {
      const conteneurCollectionIdentifiers = conteneurCollection.map(conteneurItem => getConteneurIdentifier(conteneurItem)!);
      const conteneursToAdd = conteneurs.filter(conteneurItem => {
        const conteneurIdentifier = getConteneurIdentifier(conteneurItem);
        if (conteneurIdentifier == null || conteneurCollectionIdentifiers.includes(conteneurIdentifier)) {
          return false;
        }
        conteneurCollectionIdentifiers.push(conteneurIdentifier);
        return true;
      });
      return [...conteneursToAdd, ...conteneurCollection];
    }
    return conteneurCollection;
  }

  protected convertDateFromClient(conteneur: IConteneur): IConteneur {
    return Object.assign({}, conteneur, {
      dateEntree: conteneur.dateEntree?.isValid() ? conteneur.dateEntree.toJSON() : undefined,
      dateSortie: conteneur.dateSortie?.isValid() ? conteneur.dateSortie.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateEntree = res.body.dateEntree ? dayjs(res.body.dateEntree) : undefined;
      res.body.dateSortie = res.body.dateSortie ? dayjs(res.body.dateSortie) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((conteneur: IConteneur) => {
        conteneur.dateEntree = conteneur.dateEntree ? dayjs(conteneur.dateEntree) : undefined;
        conteneur.dateSortie = conteneur.dateSortie ? dayjs(conteneur.dateSortie) : undefined;
      });
    }
    return res;
  }
}
