import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IConteneur, NewConteneur } from '../conteneur.model';

export type PartialUpdateConteneur = Partial<IConteneur> & Pick<IConteneur, 'id'>;

type RestOf<T extends IConteneur | NewConteneur> = Omit<T, 'dateEntree' | 'dateSortie'> & {
  dateEntree?: string | null;
  dateSortie?: string | null;
};

export type RestConteneur = RestOf<IConteneur>;

export type NewRestConteneur = RestOf<NewConteneur>;

export type PartialUpdateRestConteneur = RestOf<PartialUpdateConteneur>;

export type EntityResponseType = HttpResponse<IConteneur>;
export type EntityArrayResponseType = HttpResponse<IConteneur[]>;

@Injectable({ providedIn: 'root' })
export class ConteneurService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/conteneurs', 'jhipstersamplekeycloak');

  create(conteneur: NewConteneur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conteneur);
    return this.http
      .post<RestConteneur>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(conteneur: IConteneur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conteneur);
    return this.http
      .put<RestConteneur>(`${this.resourceUrl}/${this.getConteneurIdentifier(conteneur)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(conteneur: PartialUpdateConteneur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conteneur);
    return this.http
      .patch<RestConteneur>(`${this.resourceUrl}/${this.getConteneurIdentifier(conteneur)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestConteneur>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestConteneur[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getConteneurIdentifier(conteneur: Pick<IConteneur, 'id'>): number {
    return conteneur.id;
  }

  compareConteneur(o1: Pick<IConteneur, 'id'> | null, o2: Pick<IConteneur, 'id'> | null): boolean {
    return o1 && o2 ? this.getConteneurIdentifier(o1) === this.getConteneurIdentifier(o2) : o1 === o2;
  }

  addConteneurToCollectionIfMissing<Type extends Pick<IConteneur, 'id'>>(
    conteneurCollection: Type[],
    ...conteneursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const conteneurs: Type[] = conteneursToCheck.filter(isPresent);
    if (conteneurs.length > 0) {
      const conteneurCollectionIdentifiers = conteneurCollection.map(conteneurItem => this.getConteneurIdentifier(conteneurItem));
      const conteneursToAdd = conteneurs.filter(conteneurItem => {
        const conteneurIdentifier = this.getConteneurIdentifier(conteneurItem);
        if (conteneurCollectionIdentifiers.includes(conteneurIdentifier)) {
          return false;
        }
        conteneurCollectionIdentifiers.push(conteneurIdentifier);
        return true;
      });
      return [...conteneursToAdd, ...conteneurCollection];
    }
    return conteneurCollection;
  }

  protected convertDateFromClient<T extends IConteneur | NewConteneur | PartialUpdateConteneur>(conteneur: T): RestOf<T> {
    return {
      ...conteneur,
      dateEntree: conteneur.dateEntree?.toJSON() ?? null,
      dateSortie: conteneur.dateSortie?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restConteneur: RestConteneur): IConteneur {
    return {
      ...restConteneur,
      dateEntree: restConteneur.dateEntree ? dayjs(restConteneur.dateEntree) : undefined,
      dateSortie: restConteneur.dateSortie ? dayjs(restConteneur.dateSortie) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestConteneur>): HttpResponse<IConteneur> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestConteneur[]>): HttpResponse<IConteneur[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
