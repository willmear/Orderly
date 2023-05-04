import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpClient } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AddItemDialogComponent } from './dialogs/add-item.component';
import { IInventory } from 'app/entities/inventory/inventory.model';
import { InventoryService } from 'app/entities/inventory/service/inventory.service';
import { ITEM_SAVED_EVENT } from '../config/navigation.constants';
import dayjs from 'dayjs/esm';

@Component({
  selector: 'jhi-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.css'],
})
export class inventoryComponent implements OnInit {
  message: string;
  items: IInventory[];
  item: any;
  currentTable: any;

  constructor(protected modalService: NgbModal, protected inventoryService: InventoryService) {
    this.message = '';
    this.items = [];
  }

  ngOnInit(): void {
    this.inventoryService.query().subscribe((res: HttpResponse<IInventory[]>) => this.onSuccess(res.body));
  }

  AddItem(): void {
    const modalRef = this.modalService.open(AddItemDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.result.then(response => {
      if (response === ITEM_SAVED_EVENT) {
        this.ngOnInit();
      }
    });
  }

  onSuccess(items: IInventory[] | null) {
    this.items = items || [];
  }

  delete(id: number) {
    this.inventoryService.delete(id).subscribe(res => this.ngOnInit());
  }

  initialisePage() {
    const currentTable = document.getElementById('current-table') as HTMLTableElement;

    if (currentTable == null) {
      alert('Could not find a table');
    }
    const indropDown = document.getElementById('dropdown') as HTMLSelectElement;

    sortTable(indropDown, currentTable);

    if (indropDown == null) {
      alert('Could not find a dropdown element');
    } else {
      indropDown.onchange = function () {
        sortTable(indropDown, currentTable);
      };
    }

    function sortTable(dropdown: HTMLSelectElement, table: HTMLTableElement) {
      const sortBy = dropdown.value;
      var switching = true;
      var shouldSwitch, x, y, rows, n;

      switch (sortBy) {
        case 'quantity':
          n = 0;
          break;
        case 'name':
          n = 1;
          break;
      }

      if (sortBy == 'current-sort' && dropdown == document.getElementById('current-sort')) {
        n == 2;
      }

      while (switching) {
        switching = false;
        rows = table.rows;

        for (var i = 1; i < rows.length - 1; i++) {
          shouldSwitch = false;
          // @ts-ignore
          x = rows[i].getElementsByTagName('TD')[n];
          // @ts-ignore
          y = rows[i + 1].getElementsByTagName('TD')[n];

          if (n == 0) {
            if (parseInt(x.innerHTML) > parseInt(y.innerHTML)) {
              shouldSwitch = true;
              break;
            }
          } else if (x.innerHTML.toString().toLowerCase() > y.innerHTML.toString().toLowerCase()) {
            shouldSwitch = true;
            break;
          }
        }

        while (switching) {
          switching = false;
          rows = table.rows;
          for (var i = 1; i < rows.length - 1; i++) {
            shouldSwitch = false;
            // @ts-ignore
            x = rows[i].getElementsByTagName('TD')[n];
            // @ts-ignore
            y = rows[i + 1].getElementsByTagName('TD')[n];

            if (n == 0) {
              if (parseInt(x.innerHTML) > parseInt(y.innerHTML)) {
                shouldSwitch = true;
                break;
              }
            } else if (x.innerHTML.toString().toLowerCase() > y.innerHTML.toString().toLowerCase()) {
              shouldSwitch = true;
              break;
            }

            if (shouldSwitch) {
              rows[i].parentNode!.insertBefore(rows[i + 1], rows[i]);
            }
          }
        }
      }
    }
    return 1;
  }
}
