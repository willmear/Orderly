import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrdersComponent } from './orders.component';

export const ORDERS_ROUTE: Route = {
  path: 'orders',
  component: OrdersComponent,
  data: {
    authorities: [],
    pageTitle: 'orders.title',
  },
  canActivate: [UserRouteAccessService],
};
