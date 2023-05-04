import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { CALENDAR_ROUTE } from './calendar.route';
import { CalendarComponent } from './calendar.component';
import {FullCalendarModule} from "@fullcalendar/angular";
import {CalendarEventViewDialogComponent} from "./dialogs/calendar-event-view-dialog.component";
import { CalendarEventComponent } from "../entities/calendar-event/list/calendar-event.component";
import {GoogleAuthSignoutDialogComponent} from "./google-calendar/google-auth-signout-dialog.component";
import {GoogleEventViewDialogComponent} from "./dialogs/google-event-view-dialog.component";
import {GoogleAuthUnauthorisedDialogComponent} from "./google-calendar/google-auth-unauthorised-dialog.component";
import {GoogleAuthSigninDialogComponent} from "./google-calendar/google-auth-signin-dialog.component";
import {OrderViewDialogComponent} from "./dialogs/order-view-dialog.component";
import {DateNavDialogComponent} from "./dialogs/date-nav-dialog.component";
import {CalendarEventCreateDialogComponent} from "./dialogs/calendar-event-create-dialog.component";

@NgModule({
  imports: [SharedModule, RouterModule.forRoot([CALENDAR_ROUTE], {useHash: true}), FullCalendarModule],
  declarations: [CalendarComponent, CalendarEventViewDialogComponent, GoogleAuthSignoutDialogComponent, GoogleEventViewDialogComponent, GoogleAuthUnauthorisedDialogComponent, GoogleAuthSigninDialogComponent, OrderViewDialogComponent, DateNavDialogComponent, CalendarEventCreateDialogComponent],
  entryComponents: [],
  providers: [CalendarEventComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class TeamprojectAppCalendarModule {}
