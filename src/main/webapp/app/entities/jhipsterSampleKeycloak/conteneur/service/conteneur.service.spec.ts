import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IConteneur } from '../conteneur.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../conteneur.test-samples';

import { ConteneurService, RestConteneur } from './conteneur.service';

const requireRestSample: RestConteneur = {
  ...sampleWithRequiredData,
  dateEntree: sampleWithRequiredData.dateEntree?.toJSON(),
  dateSortie: sampleWithRequiredData.dateSortie?.toJSON(),
};

describe('Conteneur Service', () => {
  let service: ConteneurService;
  let httpMock: HttpTestingController;
  let expectedResult: IConteneur | IConteneur[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ConteneurService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Conteneur', () => {
      const conteneur = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(conteneur).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Conteneur', () => {
      const conteneur = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(conteneur).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Conteneur', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Conteneur', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Conteneur', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addConteneurToCollectionIfMissing', () => {
      it('should add a Conteneur to an empty array', () => {
        const conteneur: IConteneur = sampleWithRequiredData;
        expectedResult = service.addConteneurToCollectionIfMissing([], conteneur);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conteneur);
      });

      it('should not add a Conteneur to an array that contains it', () => {
        const conteneur: IConteneur = sampleWithRequiredData;
        const conteneurCollection: IConteneur[] = [
          {
            ...conteneur,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addConteneurToCollectionIfMissing(conteneurCollection, conteneur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Conteneur to an array that doesn't contain it", () => {
        const conteneur: IConteneur = sampleWithRequiredData;
        const conteneurCollection: IConteneur[] = [sampleWithPartialData];
        expectedResult = service.addConteneurToCollectionIfMissing(conteneurCollection, conteneur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conteneur);
      });

      it('should add only unique Conteneur to an array', () => {
        const conteneurArray: IConteneur[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const conteneurCollection: IConteneur[] = [sampleWithRequiredData];
        expectedResult = service.addConteneurToCollectionIfMissing(conteneurCollection, ...conteneurArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const conteneur: IConteneur = sampleWithRequiredData;
        const conteneur2: IConteneur = sampleWithPartialData;
        expectedResult = service.addConteneurToCollectionIfMissing([], conteneur, conteneur2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conteneur);
        expect(expectedResult).toContain(conteneur2);
      });

      it('should accept null and undefined values', () => {
        const conteneur: IConteneur = sampleWithRequiredData;
        expectedResult = service.addConteneurToCollectionIfMissing([], null, conteneur, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conteneur);
      });

      it('should return initial array if no Conteneur is added', () => {
        const conteneurCollection: IConteneur[] = [sampleWithRequiredData];
        expectedResult = service.addConteneurToCollectionIfMissing(conteneurCollection, undefined, null);
        expect(expectedResult).toEqual(conteneurCollection);
      });
    });

    describe('compareConteneur', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareConteneur(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareConteneur(entity1, entity2);
        const compareResult2 = service.compareConteneur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareConteneur(entity1, entity2);
        const compareResult2 = service.compareConteneur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareConteneur(entity1, entity2);
        const compareResult2 = service.compareConteneur(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
