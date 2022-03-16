import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ConteneurService } from '../service/conteneur.service';
import { IConteneur, Conteneur } from '../conteneur.model';

import { ConteneurUpdateComponent } from './conteneur-update.component';

describe('Conteneur Management Update Component', () => {
  let comp: ConteneurUpdateComponent;
  let fixture: ComponentFixture<ConteneurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let conteneurService: ConteneurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ConteneurUpdateComponent],
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
      .overrideTemplate(ConteneurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConteneurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    conteneurService = TestBed.inject(ConteneurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const conteneur: IConteneur = { id: 456 };

      activatedRoute.data = of({ conteneur });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(conteneur));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Conteneur>>();
      const conteneur = { id: 123 };
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
      expect(comp.previousState).toHaveBeenCalled();
      expect(conteneurService.update).toHaveBeenCalledWith(conteneur);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Conteneur>>();
      const conteneur = new Conteneur();
      jest.spyOn(conteneurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conteneur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conteneur }));
      saveSubject.complete();

      // THEN
      expect(conteneurService.create).toHaveBeenCalledWith(conteneur);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Conteneur>>();
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
      expect(conteneurService.update).toHaveBeenCalledWith(conteneur);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
