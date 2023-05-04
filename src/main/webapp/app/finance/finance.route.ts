import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FinanceComponent } from './finance.component';

export const FINANCE_ROUTE: Route = {
  path: 'finance',
  component: FinanceComponent,
  data: {
    authorities: [],
    pageTitle: 'finance.title',
  },
  canActivate: [UserRouteAccessService],
};
