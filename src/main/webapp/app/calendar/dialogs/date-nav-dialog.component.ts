import {Component} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {CANCELLED_EVENT, CONFIRMED_EVENT} from "../../config/navigation.constants";

@Component({
  templateUrl: './date-nav-dialog.component.html',
})
export class DateNavDialogComponent {

  //the date to navigate to
  date : any;

  switchView : boolean;

  responseObject : {
    status: string,
    date?: string,
    switchView?: boolean
  }



  constructor(
    protected activeModal: NgbActiveModal
  ) {
    this.responseObject = {
      status: CANCELLED_EVENT
    }
    this.switchView = false;
  }

  // Closes the modal
  cancel(): void {
    this.activeModal.close(this.responseObject);
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

    //set the navigation date to the current date
    this.date = new Date().toISOString().substring(0, 10);
  }

  // Called when the user presses 'go'
  navigate() {
    //prepare the response object
    this.responseObject = {
      status: CONFIRMED_EVENT,
      date: this.date,
      switchView: this.switchView
    }
    //return the date in the input field
    this.activeModal.close(this.responseObject);
  }
}
