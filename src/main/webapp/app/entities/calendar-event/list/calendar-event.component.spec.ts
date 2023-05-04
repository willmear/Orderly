import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CalendarEventService } from '../service/calendar-event.service';

import { CalendarEventComponent } from './calendar-event.component';

describe('CalendarEvent Management Component', () => {
  let comp: CalendarEventComponent;
  let fixture: ComponentFixture<CalendarEventComponent>;
  let service: CalendarEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'calendar-event', component: CalendarEventComponent }]), HttpClientTestingModule],
      declarations: [CalendarEventComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(CalendarEventComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CalendarEventComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CalendarEventService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.calendarEvents?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to calendarEventService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCalendarEventIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCalendarEventIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
