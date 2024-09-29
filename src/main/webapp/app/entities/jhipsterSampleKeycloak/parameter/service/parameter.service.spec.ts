import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IParameter } from '../parameter.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../parameter.test-samples';

import { ParameterService } from './parameter.service';

const requireRestSample: IParameter = {
  ...sampleWithRequiredData,
};

describe('Parameter Service', () => {
  let service: ParameterService;
  let httpMock: HttpTestingController;
  let expectedResult: IParameter | IParameter[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ParameterService);
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

    it('should create a Parameter', () => {
      const parameter = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(parameter).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Parameter', () => {
      const parameter = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(parameter).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Parameter', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Parameter', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Parameter', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addParameterToCollectionIfMissing', () => {
      it('should add a Parameter to an empty array', () => {
        const parameter: IParameter = sampleWithRequiredData;
        expectedResult = service.addParameterToCollectionIfMissing([], parameter);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(parameter);
      });

      it('should not add a Parameter to an array that contains it', () => {
        const parameter: IParameter = sampleWithRequiredData;
        const parameterCollection: IParameter[] = [
          {
            ...parameter,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addParameterToCollectionIfMissing(parameterCollection, parameter);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Parameter to an array that doesn't contain it", () => {
        const parameter: IParameter = sampleWithRequiredData;
        const parameterCollection: IParameter[] = [sampleWithPartialData];
        expectedResult = service.addParameterToCollectionIfMissing(parameterCollection, parameter);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(parameter);
      });

      it('should add only unique Parameter to an array', () => {
        const parameterArray: IParameter[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const parameterCollection: IParameter[] = [sampleWithRequiredData];
        expectedResult = service.addParameterToCollectionIfMissing(parameterCollection, ...parameterArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const parameter: IParameter = sampleWithRequiredData;
        const parameter2: IParameter = sampleWithPartialData;
        expectedResult = service.addParameterToCollectionIfMissing([], parameter, parameter2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(parameter);
        expect(expectedResult).toContain(parameter2);
      });

      it('should accept null and undefined values', () => {
        const parameter: IParameter = sampleWithRequiredData;
        expectedResult = service.addParameterToCollectionIfMissing([], null, parameter, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(parameter);
      });

      it('should return initial array if no Parameter is added', () => {
        const parameterCollection: IParameter[] = [sampleWithRequiredData];
        expectedResult = service.addParameterToCollectionIfMissing(parameterCollection, undefined, null);
        expect(expectedResult).toEqual(parameterCollection);
      });
    });

    describe('compareParameter', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareParameter(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareParameter(entity1, entity2);
        const compareResult2 = service.compareParameter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareParameter(entity1, entity2);
        const compareResult2 = service.compareParameter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareParameter(entity1, entity2);
        const compareResult2 = service.compareParameter(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
