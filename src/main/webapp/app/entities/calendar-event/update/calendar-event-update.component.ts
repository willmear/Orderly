import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import {Observable} from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CalendarEventFormService, CalendarEventFormGroup } from './calendar-event-form.service';
import { ICalendarEvent } from '../calendar-event.model';
import { CalendarEventService } from '../service/calendar-event.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-calendar-event-update',
  templateUrl: './calendar-event-update.component.html',
})
export class CalendarEventUpdateComponent implements OnInit {
  isSaving : boolean;

  allDay : boolean;

  allDayStartDate : any;

  allDayEndDate : any;

  savedStartTime : any;

  savedEndTime : any;

  calendarEvent: ICalendarEvent | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: CalendarEventFormGroup = this.calendarEventFormService.createCalendarEventFormGroup();

  constructor(
    protected calendarEventService: CalendarEventService,
    protected calendarEventFormService: CalendarEventFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
  ) {
    this.isSaving = false;
    this.allDay = false;
  }

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ calendarEvent }) => {
      this.calendarEvent = calendarEvent;
      if (calendarEvent) {
        this.updateForm(calendarEvent);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const calendarEvent = this.calendarEventFormService.getCalendarEvent(this.editForm);
    if (calendarEvent.id !== null) {
      this.subscribeToSaveResponse(this.calendarEventService.update(calendarEvent));
    } else {
      this.subscribeToSaveResponse(this.calendarEventService.create(calendarEvent));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICalendarEvent>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(calendarEvent: ICalendarEvent): void {
    this.calendarEvent = calendarEvent;
    this.calendarEventFormService.resetForm(this.editForm, calendarEvent);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, calendarEvent.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.calendarEvent?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  public toggleAllDay() {
    let rawEvent;
    let newStart;
    let newEnd;
    let newStartArr;
    let newEndArr;

    // Get the current form state and parse date/time for start and end
    // Date/Time Format for raw events: yyyy-mm-ddThh:mm - split by 'T'
    rawEvent = this.editForm.getRawValue();
    newStart = rawEvent.start;
    newEnd = rawEvent.end;

    // If either newStart or newEnd are null or undefined, parsing failed, so print error and return
    if(newStart === null || newStart === undefined) {
      console.error("newStart is null or undefined. Exiting toggleAllDay()");
      return;
    }
    if(newEnd === null || newEnd === undefined) {
      console.error("newEnd is null or undefined. Exiting toggleAllDay()");
      return;
    }

    // Split the start and end by 'T', so ...Arr[0] is date and ...Arr[1] is time
    newStartArr = newStart.split('T');
    newEndArr = newEnd.split('T');

    // Disables the all-day marker
    if (!this.allDay) {
      // Set the start and end fields to be the current start/end dates from array,
      // and then the time that was saved
      this.editForm.controls['start'].setValue(newStartArr[0] + "T" + this.savedStartTime);
      this.editForm.controls['end'].setValue(newEndArr[0] + "T" + this.savedEndTime);
    } else {
      //Enables all-day marker

      // Save the time currently in the start/end fields for future use
      // (if user disables all-day)
      this.savedStartTime = newStartArr[1];
      this.savedEndTime = newEndArr[1];

      // Set the times to midnight, and update the full date/time accordingly
      newStartArr[1] = "00:00";
      newStart = newStartArr[0] + "T" + newStartArr[1];

      newEndArr[1] = "00:00";
      newEnd = newEndArr[0] + "T" + newEndArr[1];

      // Set the values in the "allDay...Date" field to the last known dates
      this.allDayStartDate = newStartArr[0];
      this.allDayEndDate = newEndArr[0];

      // Set the values in the standard start/end fields to these new dates
      this.editForm.controls['start'].setValue(newStart);
      this.editForm.controls['end'].setValue(newEnd);
    }
  }

  // Called when the "allDayStartDate" field is changed
  // Updates the standard 'start' field to mirror the other field
  // so when event is saved, it contains the date that the user
  // put into the allDayStart field
  public updateAllDayStart() {
    // get the current value in start field
    // if null or undefined, error and exit
    let newStart = this.editForm.controls['start'].getRawValue();
    if(newStart === null || newStart === undefined) {
      console.error("newStart is null or undefined. Exiting updateAllDayStart()");
      return;
    }

    // Split the start date/time
    // newStartArr[0] = Date
    // newStartArr[1] = Time
    let newStartArr = newStart.split('T');

    // Update the start date with the date of the allDayStartDate field
    // and set standard start field accordingly
    newStart = this.allDayStartDate + "T" + newStartArr[1];
    this.editForm.controls['start'].setValue(newStart);
  }


  // Called when the "allDayEndDate" field is changed
  // Updates the standard 'end' field to mirror the other field
  // so when event is saved, it contains the date that the user
  // put into the allDayEnd field
  public updateAllDayEnd() {
    // get the current value in end field
    // if null or undefined, error and exit
    let newEnd = this.editForm.controls['end'].getRawValue();
    if(newEnd === null || newEnd === undefined) {
      console.error("newEnd is null or undefined. Exiting updateAllDayEnd()");
      return;
    }

    // Split the end date/time
    // newEndArr[0] = Date
    // newEndArr[1] = Time
    let newEndArr = newEnd.split('T');

    // Update the end date with the date of the allDayEndDate field
    // and set standard end field accordingly
    newEnd = this.allDayEndDate + "T" + newEndArr[1];
    this.editForm.controls['end'].setValue(newEnd);
  }
}
