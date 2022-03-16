import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IParameter, Parameter } from '../parameter.model';

import { ParameterService } from './parameter.service';

describe('Parameter Service', () => {
  let service: ParameterService;
  let httpMock: HttpTestingController;
  let elemDefault: IParameter;
  let expectedResult: IParameter | IParameter[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ParameterService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      label: 'AAAAAAA',
      activated: false,
      lib2: 'AAAAAAA',
      lib3: 'AAAAAAA',
      refExterne: 'AAAAAAA',
      val1: 'AAAAAAA',
      val2: 'AAAAAAA',
      val3: 'AAAAAAA',
      ordre: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Parameter', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Parameter()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Parameter', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          label: 'BBBBBB',
          activated: true,
          lib2: 'BBBBBB',
          lib3: 'BBBBBB',
          refExterne: 'BBBBBB',
          val1: 'BBBBBB',
          val2: 'BBBBBB',
          val3: 'BBBBBB',
          ordre: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Parameter', () => {
      const patchObject = Object.assign(
        {
          label: 'BBBBBB',
          activated: true,
          refExterne: 'BBBBBB',
          ordre: 1,
        },
        new Parameter()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Parameter', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          label: 'BBBBBB',
          activated: true,
          lib2: 'BBBBBB',
          lib3: 'BBBBBB',
          refExterne: 'BBBBBB',
          val1: 'BBBBBB',
          val2: 'BBBBBB',
          val3: 'BBBBBB',
          ordre: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Parameter', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addParameterToCollectionIfMissing', () => {
      it('should add a Parameter to an empty array', () => {
        const parameter: IParameter = { id: 123 };
        expectedResult = service.addParameterToCollectionIfMissing([], parameter);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(parameter);
      });

      it('should not add a Parameter to an array that contains it', () => {
        const parameter: IParameter = { id: 123 };
        const parameterCollection: IParameter[] = [
          {
            ...parameter,
          },
          { id: 456 },
        ];
        expectedResult = service.addParameterToCollectionIfMissing(parameterCollection, parameter);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Parameter to an array that doesn't contain it", () => {
        const parameter: IParameter = { id: 123 };
        const parameterCollection: IParameter[] = [{ id: 456 }];
        expectedResult = service.addParameterToCollectionIfMissing(parameterCollection, parameter);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(parameter);
      });

      it('should add only unique Parameter to an array', () => {
        const parameterArray: IParameter[] = [{ id: 123 }, { id: 456 }, { id: 34044 }];
        const parameterCollection: IParameter[] = [{ id: 123 }];
        expectedResult = service.addParameterToCollectionIfMissing(parameterCollection, ...parameterArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const parameter: IParameter = { id: 123 };
        const parameter2: IParameter = { id: 456 };
        expectedResult = service.addParameterToCollectionIfMissing([], parameter, parameter2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(parameter);
        expect(expectedResult).toContain(parameter2);
      });

      it('should accept null and undefined values', () => {
        const parameter: IParameter = { id: 123 };
        expectedResult = service.addParameterToCollectionIfMissing([], null, parameter, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(parameter);
      });

      it('should return initial array if no Parameter is added', () => {
        const parameterCollection: IParameter[] = [{ id: 123 }];
        expectedResult = service.addParameterToCollectionIfMissing(parameterCollection, undefined, null);
        expect(expectedResult).toEqual(parameterCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
