import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CalendarComponent } from './calendar.component';

export const CALENDAR_ROUTE: Route = {
  path: 'calendar',
  component: CalendarComponent,
  data: {
    authorities: [],
    pageTitle: 'calendar.title',
  },
  canActivate: [UserRouteAccessService],
};
