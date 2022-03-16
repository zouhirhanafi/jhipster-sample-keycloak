import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IConteneur, Conteneur } from '../conteneur.model';

import { ConteneurService } from './conteneur.service';

describe('Conteneur Service', () => {
  let service: ConteneurService;
  let httpMock: HttpTestingController;
  let elemDefault: IConteneur;
  let expectedResult: IConteneur | IConteneur[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ConteneurService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      statut: 0,
      dateEntree: currentDate,
      dateSortie: currentDate,
      zone: 0,
      ligne: 0,
      colonne: 0,
      commentaire: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateEntree: currentDate.format(DATE_TIME_FORMAT),
          dateSortie: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Conteneur', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dateEntree: currentDate.format(DATE_TIME_FORMAT),
          dateSortie: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateEntree: currentDate,
          dateSortie: currentDate,
        },
        returnedFromService
      );

      service.create(new Conteneur()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Conteneur', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          statut: 1,
          dateEntree: currentDate.format(DATE_TIME_FORMAT),
          dateSortie: currentDate.format(DATE_TIME_FORMAT),
          zone: 1,
          ligne: 1,
          colonne: 1,
          commentaire: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateEntree: currentDate,
          dateSortie: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Conteneur', () => {
      const patchObject = Object.assign(
        {
          statut: 1,
          ligne: 1,
          commentaire: 'BBBBBB',
        },
        new Conteneur()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dateEntree: currentDate,
          dateSortie: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Conteneur', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          statut: 1,
          dateEntree: currentDate.format(DATE_TIME_FORMAT),
          dateSortie: currentDate.format(DATE_TIME_FORMAT),
          zone: 1,
          ligne: 1,
          colonne: 1,
          commentaire: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateEntree: currentDate,
          dateSortie: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Conteneur', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addConteneurToCollectionIfMissing', () => {
      it('should add a Conteneur to an empty array', () => {
        const conteneur: IConteneur = { id: 123 };
        expectedResult = service.addConteneurToCollectionIfMissing([], conteneur);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conteneur);
      });

      it('should not add a Conteneur to an array that contains it', () => {
        const conteneur: IConteneur = { id: 123 };
        const conteneurCollection: IConteneur[] = [
          {
            ...conteneur,
          },
          { id: 456 },
        ];
        expectedResult = service.addConteneurToCollectionIfMissing(conteneurCollection, conteneur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Conteneur to an array that doesn't contain it", () => {
        const conteneur: IConteneur = { id: 123 };
        const conteneurCollection: IConteneur[] = [{ id: 456 }];
        expectedResult = service.addConteneurToCollectionIfMissing(conteneurCollection, conteneur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conteneur);
      });

      it('should add only unique Conteneur to an array', () => {
        const conteneurArray: IConteneur[] = [{ id: 123 }, { id: 456 }, { id: 5813 }];
        const conteneurCollection: IConteneur[] = [{ id: 123 }];
        expectedResult = service.addConteneurToCollectionIfMissing(conteneurCollection, ...conteneurArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const conteneur: IConteneur = { id: 123 };
        const conteneur2: IConteneur = { id: 456 };
        expectedResult = service.addConteneurToCollectionIfMissing([], conteneur, conteneur2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conteneur);
        expect(expectedResult).toContain(conteneur2);
      });

      it('should accept null and undefined values', () => {
        const conteneur: IConteneur = { id: 123 };
        expectedResult = service.addConteneurToCollectionIfMissing([], null, conteneur, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conteneur);
      });

      it('should return initial array if no Conteneur is added', () => {
        const conteneurCollection: IConteneur[] = [{ id: 123 }];
        expectedResult = service.addConteneurToCollectionIfMissing(conteneurCollection, undefined, null);
        expect(expectedResult).toEqual(conteneurCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
