import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBusinesses } from '../businesses.model';
import { BusinessesService } from '../service/businesses.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './businesses-delete-dialog.component.html',
})
export class BusinessesDeleteDialogComponent {
  businesses?: IBusinesses;

  constructor(protected businessesService: BusinessesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.businessesService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
