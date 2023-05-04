import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICalendarEvent } from '../calendar-event.model';
import { CalendarEventService } from '../service/calendar-event.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './calendar-event-delete-dialog.component.html',
})
export class CalendarEventDeleteDialogComponent {
  calendarEvent?: ICalendarEvent;

  constructor(protected calendarEventService: CalendarEventService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.close();
  }

  confirmDelete(id: number): void {
    this.calendarEventService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
