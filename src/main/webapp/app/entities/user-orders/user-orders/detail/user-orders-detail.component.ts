import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserOrders } from '../user-orders.model';

@Component({
  selector: 'jhi-user-orders-detail',
  templateUrl: './user-orders-detail.component.html',
})
export class UserOrdersDetailComponent implements OnInit {
  userOrders: IUserOrders | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userOrders }) => {
      this.userOrders = userOrders;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
