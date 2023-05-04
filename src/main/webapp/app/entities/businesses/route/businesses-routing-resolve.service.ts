import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBusinesses } from '../businesses.model';
import { BusinessesService } from '../service/businesses.service';

@Injectable({ providedIn: 'root' })
export class BusinessesRoutingResolveService implements Resolve<IBusinesses | null> {
  constructor(protected service: BusinessesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBusinesses | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((businesses: HttpResponse<IBusinesses>) => {
          if (businesses.body) {
            return of(businesses.body);
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
