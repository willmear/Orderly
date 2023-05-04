import {Injectable} from "@angular/core";
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({ providedIn: 'root' })
export class GoogleAuthService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/gauth');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  // Sends GET request to endpoint to trigger Google Authentication
  authenticate() : Observable<HttpResponse<any>> {
    return this.http.get(this.resourceUrl, { observe: 'response', withCredentials: true});
  }

  // Sends GET request to endpoint to query whether the current user is signed in with their Google Account
  isAuthenticated() : Observable<HttpResponse<any>> {
    return this.http.get('/api/gauth/authcheck', {observe: 'response', withCredentials: true});
  }

  // Sends POST request to endpoint to trigger Google sign out
  signOut() : Observable<HttpResponse<any>> {
    return this.http.post<any>('api/gauth/signout', null,  {observe: 'response', withCredentials: true});
  }
}
