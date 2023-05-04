import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { ORDERS_ROUTE } from './orders.route';
import { OrdersComponent } from './orders.component';
import { CreateOrderDialogComponent } from './dialogs/order-create.component';

@NgModule({
  imports: [SharedModule, RouterModule.forRoot([ORDERS_ROUTE], { useHash: true })],
  declarations: [OrdersComponent, CreateOrderDialogComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class TeamprojectAppOrdersModule {}
