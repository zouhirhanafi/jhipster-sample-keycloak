import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ParameterDetailComponent } from './parameter-detail.component';

describe('Parameter Management Detail Component', () => {
  let comp: ParameterDetailComponent;
  let fixture: ComponentFixture<ParameterDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ParameterDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ parameter: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ParameterDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ParameterDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load parameter on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.parameter).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
