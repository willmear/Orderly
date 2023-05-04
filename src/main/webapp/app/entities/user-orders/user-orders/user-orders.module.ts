import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UserOrdersComponent } from './list/user-orders.component';
import { UserOrdersDetailComponent } from './detail/user-orders-detail.component';
import { UserOrdersUpdateComponent } from './update/user-orders-update.component';
import { UserOrdersDeleteDialogComponent } from './delete/user-orders-delete-dialog.component';
import { UserOrdersRoutingModule } from './route/user-orders-routing.module';

@NgModule({
  imports: [SharedModule, UserOrdersRoutingModule],
  declarations: [UserOrdersComponent, UserOrdersDetailComponent, UserOrdersUpdateComponent, UserOrdersDeleteDialogComponent],
})
export class UserOrdersModule {}
