import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ConteneurDetailComponent } from './conteneur-detail.component';

describe('Conteneur Management Detail Component', () => {
  let comp: ConteneurDetailComponent;
  let fixture: ComponentFixture<ConteneurDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConteneurDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ conteneur: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ConteneurDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ConteneurDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load conteneur on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.conteneur).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
