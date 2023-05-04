import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import {CONFIRMED_EVENT} from 'app/config/navigation.constants';

@Component({
  templateUrl: './google-auth-signout-dialog.component.html',
})
export class GoogleAuthSignoutDialogComponent {

  googleAccount : any

  constructor(protected activeModal: NgbActiveModal) {}

  // Close modal with no status message
  cancel(): void {
    this.activeModal.close();
  }

  // Close modal, confirming sign out
  confirmSignout(): void {
   this.activeModal.close(CONFIRMED_EVENT);
  }
}
