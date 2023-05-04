import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICalendarEvent, NewCalendarEvent } from '../calendar-event.model';

export type PartialUpdateCalendarEvent = Partial<ICalendarEvent> & Pick<ICalendarEvent, 'id'>;

type RestOf<T extends ICalendarEvent | NewCalendarEvent> = Omit<T, 'start' | 'end'> & {
  start?: string | null;
  end?: string | null;
};

export type RestCalendarEvent = RestOf<ICalendarEvent>;

export type NewRestCalendarEvent = RestOf<NewCalendarEvent>;

export type PartialUpdateRestCalendarEvent = RestOf<PartialUpdateCalendarEvent>;

export type EntityResponseType = HttpResponse<ICalendarEvent>;
export type EntityArrayResponseType = HttpResponse<ICalendarEvent[]>;

@Injectable({ providedIn: 'root' })
export class CalendarEventService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/calendar-events');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(calendarEvent: NewCalendarEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(calendarEvent);
    return this.http
      .post<RestCalendarEvent>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(calendarEvent: ICalendarEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(calendarEvent);
    return this.http
      .put<RestCalendarEvent>(`${this.resourceUrl}/${this.getCalendarEventIdentifier(calendarEvent)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(calendarEvent: PartialUpdateCalendarEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(calendarEvent);
    return this.http
      .patch<RestCalendarEvent>(`${this.resourceUrl}/${this.getCalendarEventIdentifier(calendarEvent)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCalendarEvent>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCalendarEvent[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCalendarEventIdentifier(calendarEvent: Pick<ICalendarEvent, 'id'>): number {
    return calendarEvent.id;
  }

  compareCalendarEvent(o1: Pick<ICalendarEvent, 'id'> | null, o2: Pick<ICalendarEvent, 'id'> | null): boolean {
    return o1 && o2 ? this.getCalendarEventIdentifier(o1) === this.getCalendarEventIdentifier(o2) : o1 === o2;
  }

  addCalendarEventToCollectionIfMissing<Type extends Pick<ICalendarEvent, 'id'>>(
    calendarEventCollection: Type[],
    ...calendarEventsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const calendarEvents: Type[] = calendarEventsToCheck.filter(isPresent);
    if (calendarEvents.length > 0) {
      const calendarEventCollectionIdentifiers = calendarEventCollection.map(
        calendarEventItem => this.getCalendarEventIdentifier(calendarEventItem)!
      );
      const calendarEventsToAdd = calendarEvents.filter(calendarEventItem => {
        const calendarEventIdentifier = this.getCalendarEventIdentifier(calendarEventItem);
        if (calendarEventCollectionIdentifiers.includes(calendarEventIdentifier)) {
          return false;
        }
        calendarEventCollectionIdentifiers.push(calendarEventIdentifier);
        return true;
      });
      return [...calendarEventsToAdd, ...calendarEventCollection];
    }
    return calendarEventCollection;
  }

  protected convertDateFromClient<T extends ICalendarEvent | NewCalendarEvent | PartialUpdateCalendarEvent>(calendarEvent: T): RestOf<T> {
    return {
      ...calendarEvent,
      start: calendarEvent.start?.toJSON() ?? null,
      end: calendarEvent.end?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCalendarEvent: RestCalendarEvent): ICalendarEvent {
    return {
      ...restCalendarEvent,
      start: restCalendarEvent.start ? dayjs(restCalendarEvent.start) : undefined,
      end: restCalendarEvent.end ? dayjs(restCalendarEvent.end) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCalendarEvent>): HttpResponse<ICalendarEvent> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCalendarEvent[]>): HttpResponse<ICalendarEvent[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }

  //GET request to orders database to get current user's orders
  getOrders() : Observable<HttpResponse<any>> {
    return this.http.get('api/user-orders/', { observe: 'response' });
  }

  //GET request to get a summary of user's calendar events
  //returns 5 upcoming calendar events (and Google events if authenticated)
  //sorted by date
  //returned as JSON
  getEventSummary() : Observable<HttpResponse<any>> {
    return this.http.get('api/calendar-events/summary', { observe: 'response' });
  }
}
