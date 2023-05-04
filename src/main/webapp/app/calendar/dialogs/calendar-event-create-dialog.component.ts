import {Component} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICalendarEvent } from '../../entities/calendar-event/calendar-event.model';
import {CalendarEventService} from '../../entities/calendar-event/service/calendar-event.service';
import {finalize} from "rxjs/operators";
import {IUser} from "../../entities/user/user.model";
import {Observable} from "rxjs";
import {HttpResponse} from "@angular/common/http";
import { CalendarEventFormGroup, CalendarEventFormService } from "../../entities/calendar-event/update/calendar-event-form.service";
import {UserService} from "../../entities/user/user.service";
import {ActivatedRoute} from "@angular/router";
import {ITEM_SAVED_EVENT} from "../../config/navigation.constants";

@Component({
  templateUrl: './calendar-event-create-dialog.component.html',
})
export class CalendarEventCreateDialogComponent {

  //boolean flag to determine whether the modal is saving an event
  isSaving : boolean;

  //the calendar event being created
  calendarEvent?: ICalendarEvent;

  //boolean flag to determine whether this event is an all-day event
  allDay : boolean;

  //if the event is an all-day event, this stores its start date
  allDayStartDate : any;

  //if the event is an all-day event, this stores its end date
  allDayEndDate: any;

  //if the event is an all-day event, this stores the start time before the user toggles all day
  //so that if the user disables the all-day flag, the last inputted time is restored
  savedStartTime: any;

  //if the event is an all-day event, this stores the end time before the user toggles all day
  //so that if the user disables the all-day flag, the last inputted time is restored
  savedEndTime: any;

  //boolean marker to signify if the value in the allDayStartDate/allDayEndDate fields are valid
  allDayDateValid : boolean;

  //the form displayed on the modal
  editForm: CalendarEventFormGroup = this.calendarEventFormService.createCalendarEventFormGroup();

  constructor(
    protected calendarEventService: CalendarEventService,
    protected calendarEventFormService: CalendarEventFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected activeModal: NgbActiveModal
  ) {
    this.isSaving = false;
    this.allDay = false;
    this.allDayDateValid = false;
  }

  // Closes the modal
  cancel(): void {
    this.activeModal.close();
  }

  // Called when an event is saved successfully
  protected onSaveSuccess(): void {
    this.activeModal.close(ITEM_SAVED_EVENT);
  }

  // Called when an error occurs when trying to save an event
  protected onSaveError(): void {
    console.error("Unable to save event");
  }

  // Called to finalise the save by disabling boolean flag
  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  // Called when the modal is first loaded
  ngOnInit(): void {
    //check if the 'More' popover is present, if so, reduce it's z-index value so modal appears over it
    let morePopover = document.getElementsByClassName("fc-popover").item(0);
    if(morePopover != null) {
      //popover is present
      //get current style property value
      let existingSyle = morePopover.getAttribute("style");
      //add z-index: 999
      morePopover.setAttribute("style", existingSyle + " z-index: 999;");
    }
  }

  // Called when the user wishes to save a new event
  save(): void {
    this.isSaving = true;

    //parse the event from the form
    const calendarEvent = this.calendarEventFormService.getCalendarEvent(this.editForm);

    if (calendarEvent.id === null) {
      this.subscribeToSaveResponse(this.calendarEventService.create(calendarEvent));
    } else {
      //if the ID has been set, error
      //as this is a new event, it cannot have an ID
      console.error("Unable to save calendar event: new calendar event cannot already have an ID");
    }
  }

  // Called on a service call: waits for response and acts appropriately
  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICalendarEvent>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  // Called when a user toggles the all-day flag
  public toggleAllDay() {
    let dates;
    let newStart;
    let newEnd;
    let newStartArr;
    let newEndArr;

    // get the current start and end dates on the form as an array
    dates = this.getDateArr();
    if(dates === null) {
      //just return as error already printed to console
      return;
    }
    // separate arrays to start and end respectively
    newStartArr = dates.start;
    newEndArr = dates.end;

    // Disables the all-day marker
    if (!this.allDay) {
      // Set the start and end fields to be the current start/end dates from array,
      // and then the time that was saved in the original event
      this.editForm.controls['start'].setValue(newStartArr[0] + "T" + this.savedStartTime);
      this.editForm.controls['end'].setValue(newEndArr[0] + "T" + this.savedEndTime);
    }

    // Enables all-day marker
    if(this.allDay) {
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
      this.handleAllDayEventValidation();
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
      console.error("newStart is null or undefined");
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
    this.validateDateFields(true, false);
    this.handleAllDayEventValidation();
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
      console.error("newEnd is null or undefined");
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
    this.validateDateFields(false, true);
    this.handleAllDayEventValidation();
  }

  // Gets the current start and end date/time values
  // splits date/time into arrays and returns them
  private getDateArr() : any {
    let rawEvent;
    let start;
    let end;
    let startArr;
    let endArr;

    // Get the current form state and parse date/time for start and end
    // Date/Time Format for raw events: yyyy-mm-ddThh:mm - split by 'T'
    rawEvent = this.editForm.getRawValue();
    start = rawEvent.start;
    end = rawEvent.end;

    // If either newStart or newEnd are null or undefined, parsing failed, so print error and return
    if (start === null || start === undefined) {
      console.error("start is null or undefined");
      return null;
    }
    if (end === null || end === undefined) {
      console.error("end is null or undefined");
      return null;
    }

    // Split the start and end by 'T', so ...Arr[0] is date and ...Arr[1] is time
    startArr = start.split('T');
    endArr = end.split('T');

    return {
      start: startArr,
      end: endArr
    }
  }

  //checks whether the end date & time field is invalid. If so, set the end date (all-day field) to invalid too
  handleAllDayEventValidation() {
    // setTimeout(() => {
    //   let startFieldAllDay = document.getElementById('field_start_date');
    //   if(startFieldAllDay) {
    //     if (this.editForm.get('start')?.invalid) {
    //       startFieldAllDay.classList.remove('ng-valid');
    //       startFieldAllDay.classList.add('ng-invalid');
    //     } else {
    //       startFieldAllDay.classList.remove('ng-invalid');
    //       startFieldAllDay.classList.add('ng-valid')
    //     }
    //   } else {
    //     console.error("Can't get element field_start_date");
    //   }
    // }, 100);
    // setTimeout(() => {
    //   let endFieldAllDay = document.getElementById('field_end_date');
    //   if(endFieldAllDay) {
    //     if (this.editForm.get('end')?.invalid) {
    //       endFieldAllDay.classList.remove('ng-valid');
    //       endFieldAllDay.classList.add('ng-invalid');
    //     } else {
    //       endFieldAllDay.classList.remove('ng-invalid');
    //       endFieldAllDay.classList.add('ng-valid')
    //     }
    //   } else {
    //     console.error("Can't get element field_end_date");
    //   }
    // }, 100);

    setTimeout(() => {
      let startFieldAllDay = document.getElementById('field_start_date');
      let endFieldAllDay = document.getElementById('field_end_date');
      if(this.allDayStartDate >= this.allDayEndDate) {
        //invalid
        this.allDayDateValid = false;
        if(startFieldAllDay) {
          startFieldAllDay.classList.remove('ng-valid');
          startFieldAllDay.classList.add('ng-invalid');
        } else {
          console.error("Cannot get element field_start_date");
        }
        if(endFieldAllDay) {
          endFieldAllDay.classList.remove('ng-valid');
          endFieldAllDay.classList.add('ng-invalid');
        } else {
          console.error("Cannot get element field_end_date");
        }
      } else {
        //valid
        this.allDayDateValid = true;
        if(startFieldAllDay) {
          startFieldAllDay.classList.remove('ng-invalid');
          startFieldAllDay.classList.add('ng-valid');
        } else {
          console.error("Cannot get element field_start_date");
        }
        if(endFieldAllDay) {
          endFieldAllDay.classList.remove('ng-invalid');
          endFieldAllDay.classList.add('ng-valid');
        } else {
          console.error("Cannot get element field_end_date");
        }
      }
    }, 100);
  }

  //Manually calls the validators of the specified field(s)
  //If the start field updates for instance, ensure the end date is also valid
  // (i.e. comes after start date)
  async validateDateFields(start : boolean, end : boolean) {
    //validate the opposite field
    if(start) {
      this.editForm.get('end')?.updateValueAndValidity();
    }
    if(end) {
      this.editForm.get('start')?.updateValueAndValidity();
    }
  }
}
