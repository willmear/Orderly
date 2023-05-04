import {Component} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {Router} from "@angular/router";

@Component({
  templateUrl: './order-view-dialog.component.html',
})
export class OrderViewDialogComponent {

  //the order object passed into the modal (i.e. the one clicked on, and being viewed)
  order : any;

  dueDate : any;

  dateOrdered : any;


  constructor(
    protected activeModal: NgbActiveModal,
    private router: Router
  ) {
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

    if(!this.order) {
      //if the order object is not present, error and exit
      console.error("Order undefined");
      this.activeModal.dismiss("Order undefined");
    } else {
      this.dueDate = this.order.dueDate;
      this.dateOrdered = this.order.dateOrdered;
    }
  }

  redirectToOrder() {
    this.router.navigate(['/orders']);
    this.cancel();
  }
}
