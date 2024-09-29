import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../conteneur.test-samples';

import { ConteneurFormService } from './conteneur-form.service';

describe('Conteneur Form Service', () => {
  let service: ConteneurFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConteneurFormService);
  });

  describe('Service methods', () => {
    describe('createConteneurFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createConteneurFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            statut: expect.any(Object),
            dateEntree: expect.any(Object),
            dateSortie: expect.any(Object),
            zone: expect.any(Object),
            ligne: expect.any(Object),
            colonne: expect.any(Object),
            commentaire: expect.any(Object),
          }),
        );
      });

      it('passing IConteneur should create a new form with FormGroup', () => {
        const formGroup = service.createConteneurFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            statut: expect.any(Object),
            dateEntree: expect.any(Object),
            dateSortie: expect.any(Object),
            zone: expect.any(Object),
            ligne: expect.any(Object),
            colonne: expect.any(Object),
            commentaire: expect.any(Object),
          }),
        );
      });
    });

    describe('getConteneur', () => {
      it('should return NewConteneur for default Conteneur initial value', () => {
        const formGroup = service.createConteneurFormGroup(sampleWithNewData);

        const conteneur = service.getConteneur(formGroup) as any;

        expect(conteneur).toMatchObject(sampleWithNewData);
      });

      it('should return NewConteneur for empty Conteneur initial value', () => {
        const formGroup = service.createConteneurFormGroup();

        const conteneur = service.getConteneur(formGroup) as any;

        expect(conteneur).toMatchObject({});
      });

      it('should return IConteneur', () => {
        const formGroup = service.createConteneurFormGroup(sampleWithRequiredData);

        const conteneur = service.getConteneur(formGroup) as any;

        expect(conteneur).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IConteneur should not enable id FormControl', () => {
        const formGroup = service.createConteneurFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewConteneur should disable id FormControl', () => {
        const formGroup = service.createConteneurFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
