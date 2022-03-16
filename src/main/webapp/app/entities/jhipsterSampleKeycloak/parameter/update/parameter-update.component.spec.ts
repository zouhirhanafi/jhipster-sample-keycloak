import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ParameterService } from '../service/parameter.service';
import { IParameter, Parameter } from '../parameter.model';

import { ParameterUpdateComponent } from './parameter-update.component';

describe('Parameter Management Update Component', () => {
  let comp: ParameterUpdateComponent;
  let fixture: ComponentFixture<ParameterUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let parameterService: ParameterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ParameterUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ParameterUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParameterUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    parameterService = TestBed.inject(ParameterService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Parameter query and add missing value', () => {
      const parameter: IParameter = { id: 456 };
      const type: IParameter = { id: 9449 };
      parameter.type = type;
      const paraent: IParameter = { id: 74076 };
      parameter.paraent = paraent;

      const parameterCollection: IParameter[] = [{ id: 4945 }];
      jest.spyOn(parameterService, 'query').mockReturnValue(of(new HttpResponse({ body: parameterCollection })));
      const additionalParameters = [type, paraent];
      const expectedCollection: IParameter[] = [...additionalParameters, ...parameterCollection];
      jest.spyOn(parameterService, 'addParameterToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ parameter });
      comp.ngOnInit();

      expect(parameterService.query).toHaveBeenCalled();
      expect(parameterService.addParameterToCollectionIfMissing).toHaveBeenCalledWith(parameterCollection, ...additionalParameters);
      expect(comp.parametersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const parameter: IParameter = { id: 456 };
      const type: IParameter = { id: 64802 };
      parameter.type = type;
      const paraent: IParameter = { id: 14513 };
      parameter.paraent = paraent;

      activatedRoute.data = of({ parameter });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(parameter));
      expect(comp.parametersSharedCollection).toContain(type);
      expect(comp.parametersSharedCollection).toContain(paraent);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Parameter>>();
      const parameter = { id: 123 };
      jest.spyOn(parameterService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parameter });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parameter }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(parameterService.update).toHaveBeenCalledWith(parameter);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Parameter>>();
      const parameter = new Parameter();
      jest.spyOn(parameterService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parameter });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parameter }));
      saveSubject.complete();

      // THEN
      expect(parameterService.create).toHaveBeenCalledWith(parameter);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Parameter>>();
      const parameter = { id: 123 };
      jest.spyOn(parameterService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parameter });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(parameterService.update).toHaveBeenCalledWith(parameter);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackParameterById', () => {
      it('Should return tracked Parameter primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackParameterById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
