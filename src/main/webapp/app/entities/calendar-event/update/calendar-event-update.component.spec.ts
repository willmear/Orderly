import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CalendarEventFormService } from './calendar-event-form.service';
import { CalendarEventService } from '../service/calendar-event.service';
import { ICalendarEvent } from '../calendar-event.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { CalendarEventUpdateComponent } from './calendar-event-update.component';

describe('CalendarEvent Management Update Component', () => {
  let comp: CalendarEventUpdateComponent;
  let fixture: ComponentFixture<CalendarEventUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let calendarEventFormService: CalendarEventFormService;
  let calendarEventService: CalendarEventService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CalendarEventUpdateComponent],
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
      .overrideTemplate(CalendarEventUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CalendarEventUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    calendarEventFormService = TestBed.inject(CalendarEventFormService);
    calendarEventService = TestBed.inject(CalendarEventService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const calendarEvent: ICalendarEvent = { id: 456 };
      const user: IUser = { id: 78637 };
      calendarEvent.user = user;

      const userCollection: IUser[] = [{ id: 76389 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ calendarEvent });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const calendarEvent: ICalendarEvent = { id: 456 };
      const user: IUser = { id: 61719 };
      calendarEvent.user = user;

      activatedRoute.data = of({ calendarEvent });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.calendarEvent).toEqual(calendarEvent);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICalendarEvent>>();
      const calendarEvent = { id: 123 };
      jest.spyOn(calendarEventFormService, 'getCalendarEvent').mockReturnValue(calendarEvent);
      jest.spyOn(calendarEventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calendarEvent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: calendarEvent }));
      saveSubject.complete();

      // THEN
      expect(calendarEventFormService.getCalendarEvent).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(calendarEventService.update).toHaveBeenCalledWith(expect.objectContaining(calendarEvent));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICalendarEvent>>();
      const calendarEvent = { id: 123 };
      jest.spyOn(calendarEventFormService, 'getCalendarEvent').mockReturnValue({ id: null });
      jest.spyOn(calendarEventService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calendarEvent: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: calendarEvent }));
      saveSubject.complete();

      // THEN
      expect(calendarEventFormService.getCalendarEvent).toHaveBeenCalled();
      expect(calendarEventService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICalendarEvent>>();
      const calendarEvent = { id: 123 };
      jest.spyOn(calendarEventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calendarEvent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(calendarEventService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
