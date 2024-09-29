import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ConteneurService } from '../service/conteneur.service';
import { IConteneur } from '../conteneur.model';
import { ConteneurFormService } from './conteneur-form.service';

import { ConteneurUpdateComponent } from './conteneur-update.component';

describe('Conteneur Management Update Component', () => {
  let comp: ConteneurUpdateComponent;
  let fixture: ComponentFixture<ConteneurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let conteneurFormService: ConteneurFormService;
  let conteneurService: ConteneurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ConteneurUpdateComponent],
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
      .overrideTemplate(ConteneurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConteneurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    conteneurFormService = TestBed.inject(ConteneurFormService);
    conteneurService = TestBed.inject(ConteneurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const conteneur: IConteneur = { id: 456 };

      activatedRoute.data = of({ conteneur });
      comp.ngOnInit();

      expect(comp.conteneur).toEqual(conteneur);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConteneur>>();
      const conteneur = { id: 123 };
      jest.spyOn(conteneurFormService, 'getConteneur').mockReturnValue(conteneur);
      jest.spyOn(conteneurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conteneur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conteneur }));
      saveSubject.complete();

      // THEN
      expect(conteneurFormService.getConteneur).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(conteneurService.update).toHaveBeenCalledWith(expect.objectContaining(conteneur));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConteneur>>();
      const conteneur = { id: 123 };
      jest.spyOn(conteneurFormService, 'getConteneur').mockReturnValue({ id: null });
      jest.spyOn(conteneurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conteneur: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conteneur }));
      saveSubject.complete();

      // THEN
      expect(conteneurFormService.getConteneur).toHaveBeenCalled();
      expect(conteneurService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConteneur>>();
      const conteneur = { id: 123 };
      jest.spyOn(conteneurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conteneur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(conteneurService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
