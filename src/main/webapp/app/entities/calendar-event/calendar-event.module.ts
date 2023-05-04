import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CalendarEventComponent } from './list/calendar-event.component';
import { CalendarEventDetailComponent } from './detail/calendar-event-detail.component';
import { CalendarEventUpdateComponent } from './update/calendar-event-update.component';
import { CalendarEventDeleteDialogComponent } from './delete/calendar-event-delete-dialog.component';
import { CalendarEventRoutingModule } from './route/calendar-event-routing.module';

@NgModule({
  imports: [SharedModule, CalendarEventRoutingModule],
  declarations: [CalendarEventComponent, CalendarEventDetailComponent, CalendarEventUpdateComponent, CalendarEventDeleteDialogComponent],
})
export class CalendarEventModule {}
