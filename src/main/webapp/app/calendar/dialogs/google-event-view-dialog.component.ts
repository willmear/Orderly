import {Component} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  templateUrl: './google-event-view-dialog.component.html',
})
export class GoogleEventViewDialogComponent {

  //the Google event passed into the modal (i.e. the one clicked on, and being viewed/edited)
  googleEvent : any;

  googleAccount : any;

  start : any;

  end : any;

  allDay : boolean;


  constructor(
    protected activeModal: NgbActiveModal
  ) {
    this.allDay = false;
  }

  // Closes the modal
  cancel(): void {
    this.activeModal.close();
  }

  // Called when the modal is first loaded
  ngOnInit(): void {
    //check if the 'More' popover is present, if so, reduce it's z-index value so modal appears over it
    let morePopover = document.getElementsByClassName("fc-popover").item(0);
    if(morePopover != null) {
      //popover is present
      //get current style property value
      let existingSyle = morePopover.getAttribute("style");
      //add z-index: 999
      morePopover.setAttribute("style", existingSyle + " z-index: 999;");
    }

    if(!this.googleEvent) {
      //if the Google event is not present, error and exit
      console.error("Google event undefined");
      this.activeModal.dismiss("Google event undefined");
    } else {
      if(!this.allDay) {
        this.start = new Date(this.googleEvent.start.dateTime.value).toISOString().substring(0, 16);
        this.end = new Date(this.googleEvent.end.dateTime.value).toISOString().substring(0, 16);
      } else {
        this.start = new Date(this.googleEvent.start.date.value).toISOString().substring(0, 10);
        this.end = new Date(this.googleEvent.end.date.value).toISOString().substring(0, 10);
      }
    }
  }

  redirectToGoogleEvent() {
    window.open(this.googleEvent.htmlLink, '_blank');
  }
}
