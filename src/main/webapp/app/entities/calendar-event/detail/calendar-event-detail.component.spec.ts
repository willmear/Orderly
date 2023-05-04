import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CalendarEventDetailComponent } from './calendar-event-detail.component';

describe('CalendarEvent Management Detail Component', () => {
  let comp: CalendarEventDetailComponent;
  let fixture: ComponentFixture<CalendarEventDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CalendarEventDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ calendarEvent: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CalendarEventDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CalendarEventDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load calendarEvent on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.calendarEvent).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
