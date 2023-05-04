import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { InventoryService } from '../../entities/inventory/service/inventory.service';
import { IInventory } from '../../entities/inventory/inventory.model';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ITEM_SAVED_EVENT } from '../../config/navigation.constants';

@Component({
  templateUrl: './add-item.component.html',
})
export class AddItemDialogComponent implements OnInit {
  items: IInventory[];
  item: any;

  constructor(protected activeModal: NgbActiveModal, protected InventoryService: InventoryService, private http: HttpClient) {
    this.items = [];
  }

  ngOnInit(): void {
    this.InventoryService.query().subscribe((res: HttpResponse<IInventory[]>) => this.onSuccess(res.body));
  }

  close(): void {
    this.activeModal.close(ITEM_SAVED_EVENT);
  }

  save(items: { id: null; name: string; quantity: number; status: string }): void {
    this.InventoryService.create(items).subscribe(res => this.close());
  }

  onSuccess(items: IInventory[] | null): void {
    this.items = items || [];
  }
}
