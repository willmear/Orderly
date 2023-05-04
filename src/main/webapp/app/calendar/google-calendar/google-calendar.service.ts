import {Injectable} from "@angular/core";
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({ providedIn: 'root' })
export class GoogleCalendarService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/gcal');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  // Sends GET request to endpoint to get events from user's Google Calendar
  getEvents() : Observable<HttpResponse<any>> {
    return this.http.get('/api/gcal', { observe: 'response', withCredentials: true});
  }
}
