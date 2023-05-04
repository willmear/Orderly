import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserOrders } from '../user-orders.model';
import { UserOrdersService } from '../service/user-orders.service';

@Injectable({ providedIn: 'root' })
export class UserOrdersRoutingResolveService implements Resolve<IUserOrders | null> {
  constructor(protected service: UserOrdersService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserOrders | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userOrders: HttpResponse<IUserOrders>) => {
          if (userOrders.body) {
            return of(userOrders.body);
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
