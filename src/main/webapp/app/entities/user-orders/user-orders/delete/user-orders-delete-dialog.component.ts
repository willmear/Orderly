import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserOrders } from '../user-orders.model';
import { UserOrdersService } from '../service/user-orders.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './user-orders-delete-dialog.component.html',
})
export class UserOrdersDeleteDialogComponent {
  userOrders?: IUserOrders;

  constructor(protected userOrdersService: UserOrdersService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userOrdersService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
