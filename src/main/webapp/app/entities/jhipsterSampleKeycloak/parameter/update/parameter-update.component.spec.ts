import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ParameterService } from '../service/parameter.service';
import { IParameter } from '../parameter.model';
import { ParameterFormService } from './parameter-form.service';

import { ParameterUpdateComponent } from './parameter-update.component';

describe('Parameter Management Update Component', () => {
  let comp: ParameterUpdateComponent;
  let fixture: ComponentFixture<ParameterUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let parameterFormService: ParameterFormService;
  let parameterService: ParameterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ParameterUpdateComponent],
      providers: [
        provideHttpClient(),
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
    parameterFormService = TestBed.inject(ParameterFormService);
    parameterService = TestBed.inject(ParameterService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Parameter query and add missing value', () => {
      const parameter: IParameter = { id: 456 };
      const type: IParameter = { id: 13735 };
      parameter.type = type;
      const paraent: IParameter = { id: 13676 };
      parameter.paraent = paraent;

      const parameterCollection: IParameter[] = [{ id: 2996 }];
      jest.spyOn(parameterService, 'query').mockReturnValue(of(new HttpResponse({ body: parameterCollection })));
      const additionalParameters = [type, paraent];
      const expectedCollection: IParameter[] = [...additionalParameters, ...parameterCollection];
      jest.spyOn(parameterService, 'addParameterToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ parameter });
      comp.ngOnInit();

      expect(parameterService.query).toHaveBeenCalled();
      expect(parameterService.addParameterToCollectionIfMissing).toHaveBeenCalledWith(
        parameterCollection,
        ...additionalParameters.map(expect.objectContaining),
      );
      expect(comp.parametersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const parameter: IParameter = { id: 456 };
      const type: IParameter = { id: 19635 };
      parameter.type = type;
      const paraent: IParameter = { id: 32179 };
      parameter.paraent = paraent;

      activatedRoute.data = of({ parameter });
      comp.ngOnInit();

      expect(comp.parametersSharedCollection).toContain(type);
      expect(comp.parametersSharedCollection).toContain(paraent);
      expect(comp.parameter).toEqual(parameter);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParameter>>();
      const parameter = { id: 123 };
      jest.spyOn(parameterFormService, 'getParameter').mockReturnValue(parameter);
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
      expect(parameterFormService.getParameter).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(parameterService.update).toHaveBeenCalledWith(expect.objectContaining(parameter));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParameter>>();
      const parameter = { id: 123 };
      jest.spyOn(parameterFormService, 'getParameter').mockReturnValue({ id: null });
      jest.spyOn(parameterService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parameter: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parameter }));
      saveSubject.complete();

      // THEN
      expect(parameterFormService.getParameter).toHaveBeenCalled();
      expect(parameterService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParameter>>();
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
      expect(parameterService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareParameter', () => {
      it('Should forward to parameterService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(parameterService, 'compareParameter');
        comp.compareParameter(entity, entity2);
        expect(parameterService.compareParameter).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
