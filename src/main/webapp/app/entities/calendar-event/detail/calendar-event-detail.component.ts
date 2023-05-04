import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICalendarEvent } from '../calendar-event.model';

@Component({
  selector: 'jhi-calendar-event-detail',
  templateUrl: './calendar-event-detail.component.html',
})
export class CalendarEventDetailComponent implements OnInit {
  calendarEvent: ICalendarEvent | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ calendarEvent }) => {
      this.calendarEvent = calendarEvent;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
