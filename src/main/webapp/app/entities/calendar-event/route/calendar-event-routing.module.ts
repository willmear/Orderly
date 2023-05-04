import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CalendarEventComponent } from '../list/calendar-event.component';
import { CalendarEventDetailComponent } from '../detail/calendar-event-detail.component';
import { CalendarEventUpdateComponent } from '../update/calendar-event-update.component';
import { CalendarEventRoutingResolveService } from './calendar-event-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const calendarEventRoute: Routes = [
  {
    path: '',
    component: CalendarEventComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CalendarEventDetailComponent,
    resolve: {
      calendarEvent: CalendarEventRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CalendarEventUpdateComponent,
    resolve: {
      calendarEvent: CalendarEventRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CalendarEventUpdateComponent,
    resolve: {
      calendarEvent: CalendarEventRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(calendarEventRoute)],
  exports: [RouterModule],
})
export class CalendarEventRoutingModule {}
