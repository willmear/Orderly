import { Component } from '@angular/core';
import {NgbActiveModal, NgbCarouselConfig} from '@ng-bootstrap/ng-bootstrap';

import {CONFIRMED_EVENT} from 'app/config/navigation.constants';

@Component({
  templateUrl: './google-auth-signin-dialog.component.html',
  styleUrls: ['./google-auth-signin-dialog.component.css'],
})
export class GoogleAuthSigninDialogComponent {
  constructor(protected activeModal: NgbActiveModal, private config : NgbCarouselConfig) {
    config.interval = 10000; //10 seconds
  }

  // Close modal with no status message
  cancel(): void {
    this.activeModal.close();
  }

  // Close modal, confirming user wants to authenticate
  continueSignIn(): void {
   this.activeModal.close(CONFIRMED_EVENT);
  }
}
