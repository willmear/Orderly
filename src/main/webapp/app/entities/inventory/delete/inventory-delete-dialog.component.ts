import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IInventory } from '../inventory.model';
import { InventoryService } from '../service/inventory.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
    templateUrl: './inventory-delete-dialog.component.html'
})
export class InventoryDeleteDialogComponent {
    inventory?: IInventory;

    constructor(protected inventoryService: InventoryService, protected activeModal: NgbActiveModal) {}

    cancel(): void {
        this.activeModal.dismiss();
    }

    confirmDelete(id: number): void {
        this.inventoryService.delete(id).subscribe(() => {
            this.activeModal.close(ITEM_DELETED_EVENT);
        });
    }
}
