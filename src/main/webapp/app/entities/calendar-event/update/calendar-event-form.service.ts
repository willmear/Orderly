import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICalendarEvent, NewCalendarEvent } from '../calendar-event.model';
import { calendarDateValidator } from "./calendar-event-validators";

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICalendarEvent for edit and NewCalendarEventFormGroupInput for create.
 */
type CalendarEventFormGroupInput = ICalendarEvent | PartialWithRequiredKeyOf<NewCalendarEvent>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICalendarEvent | NewCalendarEvent> = Omit<T, 'start' | 'end'> & {
  start?: string | null;
  end?: string | null;
};

type CalendarEventFormRawValue = FormValueOf<ICalendarEvent>;

type NewCalendarEventFormRawValue = FormValueOf<NewCalendarEvent>;

type CalendarEventFormDefaults = Pick<NewCalendarEvent, 'id' | 'start' | 'end'>;

type CalendarEventFormGroupContent = {
  id: FormControl<CalendarEventFormRawValue['id'] | NewCalendarEvent['id']>;
  name: FormControl<CalendarEventFormRawValue['name']>;
  description: FormControl<CalendarEventFormRawValue['description']>;
  start: FormControl<CalendarEventFormRawValue['start']>;
  end: FormControl<CalendarEventFormRawValue['end']>;
  location: FormControl<CalendarEventFormRawValue['location']>;
  user: FormControl<CalendarEventFormRawValue['user']>;
};

export type CalendarEventFormGroup = FormGroup<CalendarEventFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CalendarEventFormService {
  createCalendarEventFormGroup(calendarEvent: CalendarEventFormGroupInput = { id: null }): CalendarEventFormGroup {
    const calendarEventRawValue = this.convertCalendarEventToCalendarEventRawValue({
      ...this.getFormDefaults(),
      ...calendarEvent,
    });
    return new FormGroup<CalendarEventFormGroupContent>({
      id: new FormControl(
        { value: calendarEventRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(calendarEventRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(calendarEventRawValue.description),
      start: new FormControl(calendarEventRawValue.start, {
        validators: [Validators.required, calendarDateValidator],
      }),
      end: new FormControl(calendarEventRawValue.end, {
        validators: [Validators.required, calendarDateValidator],
      }),
      location: new FormControl(calendarEventRawValue.location),
      user: new FormControl(calendarEventRawValue.user),
    });
  }

  getCalendarEvent(form: CalendarEventFormGroup): ICalendarEvent | NewCalendarEvent {
    return this.convertCalendarEventRawValueToCalendarEvent(form.getRawValue() as CalendarEventFormRawValue | NewCalendarEventFormRawValue);
  }

  resetForm(form: CalendarEventFormGroup, calendarEvent: CalendarEventFormGroupInput): void {
    const calendarEventRawValue = this.convertCalendarEventToCalendarEventRawValue({ ...this.getFormDefaults(), ...calendarEvent });
    form.reset(
      {
        ...calendarEventRawValue,
        id: { value: calendarEventRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CalendarEventFormDefaults {
    //get the current time
    let startTime = dayjs().set('seconds', 0).set('milliseconds', 0);
    let startTimeMinutes = startTime.get('minutes');
    //if the current time is either XX:00 or XX:30, leave it as it is
    //else, go to the next half hour
    if(startTimeMinutes !== 0 && startTimeMinutes !== 30) {
      if(startTimeMinutes < 30) {
        startTime = dayjs(startTime).set('minutes', 30);
      } else {
        startTime = dayjs(startTime).set('minutes', 0).add(1, 'hour');
      }
    }
    //end time should always be 1 hour after start time
    const endTime = dayjs(startTime).add(1, 'hour')

    return {
      id: null,
      start: startTime,
      end: endTime,
    };
  }

  private convertCalendarEventRawValueToCalendarEvent(
    rawCalendarEvent: CalendarEventFormRawValue | NewCalendarEventFormRawValue
  ): ICalendarEvent | NewCalendarEvent {
    return {
      ...rawCalendarEvent,
      start: dayjs(rawCalendarEvent.start, DATE_TIME_FORMAT),
      end: dayjs(rawCalendarEvent.end, DATE_TIME_FORMAT),
    };
  }

  private convertCalendarEventToCalendarEventRawValue(
    calendarEvent: ICalendarEvent | (Partial<NewCalendarEvent> & CalendarEventFormDefaults)
  ): CalendarEventFormRawValue | PartialWithRequiredKeyOf<NewCalendarEventFormRawValue> {
    return {
      ...calendarEvent,
      start: calendarEvent.start ? calendarEvent.start.format(DATE_TIME_FORMAT) : undefined,
      end: calendarEvent.end ? calendarEvent.end.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
