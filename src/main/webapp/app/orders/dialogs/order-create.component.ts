import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { UserOrdersService } from '../../entities/user-orders/user-orders/service/user-orders.service';
import { IUserOrders } from '../../entities/user-orders/user-orders/user-orders.model';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ITEM_SAVED_EVENT } from '../../config/navigation.constants';
import dayjs from 'dayjs/esm';

@Component({
  selector: 'jhi-order-create',
  templateUrl: './order-create.component.html',
  styleUrls: ['./order-create.component.css'],
})
export class CreateOrderDialogComponent implements OnInit {
  orders: IUserOrders[];
  order: any;

  constructor(protected activeModal: NgbActiveModal, protected ordersService: UserOrdersService, private http: HttpClient) {
    this.orders = [];
  }

  ngOnInit(): void {
    this.ordersService.query().subscribe((res: HttpResponse<IUserOrders[]>) => this.onSuccess(res.body));
  }

  close(): void {
    this.activeModal.close(ITEM_SAVED_EVENT);
  }

  save(orders: {
    id: null;
    orderNum: number;
    orderDescription: string;
    dateOrdered: dayjs.Dayjs;
    dueDate: dayjs.Dayjs;
    customerID: number;
    productionCost: number;
    price: number;
  }): void {
    orders.dateOrdered = dayjs();
    this.ordersService.create(orders).subscribe(res => this.close());
  }

  onSuccess(orders: IUserOrders[] | null): void {
    this.orders = orders || [];
  }
}
