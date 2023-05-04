import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICalendarEvent } from '../calendar-event.model';
import { CalendarEventService } from '../service/calendar-event.service';

@Injectable({ providedIn: 'root' })
export class CalendarEventRoutingResolveService implements Resolve<ICalendarEvent | null> {
  constructor(protected service: CalendarEventService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICalendarEvent | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((calendarEvent: HttpResponse<ICalendarEvent>) => {
          if (calendarEvent.body) {
            return of(calendarEvent.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
