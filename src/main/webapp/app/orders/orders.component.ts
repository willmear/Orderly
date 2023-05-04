import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpClient } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CreateOrderDialogComponent } from './dialogs/order-create.component';
import { IUserOrders } from 'app/entities/user-orders/user-orders/user-orders.model';
import { UserOrdersService } from 'app/entities/user-orders/user-orders/service/user-orders.service';
import { ITEM_SAVED_EVENT } from '../config/navigation.constants';
import dayjs from 'dayjs/esm';

@Component({
  selector: 'jhi-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit {
  message: string;
  orders: IUserOrders[];
  order: any;
  today: Date;

  constructor(protected modalService: NgbModal, protected ordersService: UserOrdersService) {
    this.message = '';
    this.orders = [];
    this.today = new Date();
  }

  ngOnInit(): void {
    this.ordersService.query().subscribe((res: HttpResponse<IUserOrders[]>) => this.onSuccess(res.body));
  }

  CreateNewOrder(): void {
    const modalRef = this.modalService.open(CreateOrderDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.result.then(response => {
      if (response === ITEM_SAVED_EVENT) {
        this.ngOnInit();
      }
    });
  }

  onSuccess(orders: IUserOrders[] | null): void {
    this.orders = orders || [];
  }

  delete(id: number) {
    this.ordersService.delete(id).subscribe(res => this.ngOnInit());
  }

  initialisePage() {
    //Display today's date
    const dateElement = document.getElementById('date');
    //Null check
    if (dateElement === null) {
      alert('Could not find date element');
    } else {
      const months = [
        'January',
        'February',
        'March',
        'April',
        'May',
        'June',
        'July',
        'August',
        'September',
        'October',
        'November',
        'December',
      ];
      const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
      //Grab the current date and separate into components
      var year = this.today.getFullYear();
      var month = this.today.getMonth();
      var date = this.today.getDate();
      var day = this.today.getDay();
      dateElement.innerHTML = "Today's Date: " + days[day] + ' ' + date + ' ' + months[month] + ' ' + year!;
    }

    const currentTable = document.getElementById('current-table') as HTMLTableElement; //Must be casted to TableElement type to read rows
    this.colourCells();

    //Sorting Functionality
    //currentTable has already been assigned
    const dueTable = document.getElementById('due-table') as HTMLTableElement;
    const pastTable = document.getElementById('past-table') as HTMLTableElement;
    if (currentTable == null || pastTable == null || dueTable == null) {
      alert('Could not find a table');
    }
    const currentDropdown = document.getElementById('current-sort') as HTMLSelectElement;
    const dueDropdown = document.getElementById('due-sort') as HTMLSelectElement;
    const pastDropdown = document.getElementById('past-sort') as HTMLSelectElement;

    //Calls functions once to make sure tables are sorted by default
    sortTable(currentDropdown, currentTable);
    sortTable(dueDropdown, dueTable);
    sortTable(pastDropdown, pastTable);

    if (currentDropdown == null || dueDropdown == null || pastDropdown == null) {
      alert('Could not find a dropdown element');
    } else {
      /*Listeners for dropdowns (must create a new function as onchange does not accept
      functions with parameters so this is a workaround)*/
      currentDropdown.onchange = function () {
        sortTable(currentDropdown, currentTable);
      };
      dueDropdown.onchange = function () {
        sortTable(dueDropdown, dueTable);
      };
      pastDropdown.onchange = function () {
        sortTable(pastDropdown, pastTable);
      };
    }

    function sortTable(dropdown: HTMLSelectElement, table: HTMLTableElement) {
      const sortBy = dropdown.value;
      var switching = true;
      var shouldSwitch, x, y, rows, n;

      //Determine which row to sort by
      /*This is very over-fit to the tables used, changing the columns in any way will cause sorting to break.
      Could use table id in future*/
      switch (sortBy) {
        case 'order-number':
          n = 0;
          break;
        case 'description':
          n = 1;
          break;
        case 'stock-level':
        case 'completed-date':
          n = 2;
          break;
        case 'due-date':
          n = 3;
          break;
        default:
          n = -1;
      }

      //Like I said, over-fit
      if (sortBy == 'due-date' && dropdown == document.getElementById('due-sort')) {
        n == 2;
      }

      //Iterates through the rows until no rows are sorted, bubble sort
      while (switching) {
        switching = false;
        rows = table.rows;
        for (var i = 1; i < rows.length - 1; i++) {
          shouldSwitch = false;
          //n is column, selected above
          x = rows[i].getElementsByTagName('TD')[n];
          y = rows[i + 1].getElementsByTagName('TD')[n];

          //n = 0 is order number
          if (n == 0) {
            if (parseInt(x.innerHTML) > parseInt(y.innerHTML)) {
              shouldSwitch = true;
              break;
            }
            /*Sorting by date, very over-fit to particular length strings,
          will have to change when backend is done*/
          } else if (sortBy == 'due-date') {
            //Sort by year first, haven't actually tested this oops
            if (x.innerHTML.substr(7, 2) > y.innerHTML.substr(7, 2)) {
              shouldSwitch = true;
              break;
              //Sort by month if years are equal
            } else if (MonthToNum(x.innerHTML) > MonthToNum(y.innerHTML) && x.innerHTML.substr(7, 2) == y.innerHTML.substr(7, 2)) {
              shouldSwitch = true;
              break;
              //Sort by day if months and years are equal
            } else if (
              x.innerHTML.substr(0, 2) > y.innerHTML.substr(0, 2) &&
              MonthToNum(x.innerHTML) == MonthToNum(y.innerHTML) &&
              x.innerHTML.substr(7, 2) == y.innerHTML.substr(7, 2)
            ) {
              shouldSwitch = true;
              break;
            }
          } else if (sortBy == 'completed-date') {
            //Sort by year first, haven't actually tested this oops
            if (x.innerHTML.substr(7, 2) < y.innerHTML.substr(7, 2)) {
              shouldSwitch = true;
              break;
              //Sort by month if years are equal
            } else if (MonthToNum(x.innerHTML) < MonthToNum(y.innerHTML) && x.innerHTML.substr(7, 2) == y.innerHTML.substr(7, 2)) {
              shouldSwitch = true;
              break;
              //Sort by day if months and years are equal
            } else if (
              x.innerHTML.substr(0, 2) < y.innerHTML.substr(0, 2) &&
              MonthToNum(x.innerHTML) == MonthToNum(y.innerHTML) &&
              x.innerHTML.substr(7, 2) == y.innerHTML.substr(7, 2)
            ) {
              shouldSwitch = true;
              break;
            }
            //Sort by any string columns, e.g. inventory and stock level
          } else if (x.innerHTML.toString().toLowerCase() > y.innerHTML.toString().toLowerCase()) {
            shouldSwitch = true;
            break;
          }
        }

        if (shouldSwitch) {
          rows[i].parentNode!.insertBefore(rows[i + 1], rows[i]);
          switching = true;
        }
      }
      /*Function must return something for onchange,
    should probably check the output is 1 to see if it works,
    though it'll likely never get here anyway if there's an issue*/
      return 1;
    }

    /*Over-fit function to extract month from the date, and convert to a sortable integer,
    there's probably already a function for this and table sorting as a whole knowing my luck*/
    function MonthToNum(date: string) {
      switch (date.substr(3, 3)) {
        case 'Jan':
          return 1;
          break;
        case 'Feb':
          return 2;
          break;
        case 'Mar':
          return 3;
          break;
        case 'Apr':
          return 4;
          break;
        case 'May':
          return 5;
          break;
        case 'Jun':
          return 6;
          break;
        case 'Jul':
          return 7;
          break;
        case 'Aug':
          return 8;
          break;
        case 'Sep':
          return 9;
          break;
        case 'Oct':
          return 10;
          break;
        case 'Nov':
          return 11;
          break;
        case 'Dec':
          return 12;
          break;
        default:
          return -1;
      }
    }
  }

  //Colour inventory stock cells appropriately
  colourCells() {
    const currentTable = document.getElementById('current-table') as HTMLTableElement;
    if (currentTable === null) {
      alert('Could not find current orders table');
    } else {
      for (var i = 0, row; (row = currentTable.rows[i]); i++) {
        if (row.cells[2].innerText == 'LOW') {
          //cells[2] is the inventory column
          row.cells[2].style.backgroundColor = 'rgba(255, 213, 128, 0.77)';
          row.cells[2].style.color = '#000000';
        } else if (row.cells[2].innerText == 'IN STOCK') {
          row.cells[2].style.backgroundColor = 'rgba(98, 141, 87, 0.56)';
          row.cells[2].style.color = '#000000';
        } else if (row.cells[2].innerText == 'UNAVAILABLE') {
          row.cells[2].style.backgroundColor = 'rgba(176, 42, 42, 0.73)';
          row.cells[2].style.color = '#FFFFFF';
        }
      }
    }
  }
}
