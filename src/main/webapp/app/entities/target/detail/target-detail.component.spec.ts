import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TargetDetailComponent } from './target-detail.component';

describe('Target Management Detail Component', () => {
  let comp: TargetDetailComponent;
  let fixture: ComponentFixture<TargetDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TargetDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ target: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TargetDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TargetDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load target on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.target).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
