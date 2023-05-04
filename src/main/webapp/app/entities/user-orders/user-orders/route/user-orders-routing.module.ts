import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserOrdersComponent } from '../list/user-orders.component';
import { UserOrdersDetailComponent } from '../detail/user-orders-detail.component';
import { UserOrdersUpdateComponent } from '../update/user-orders-update.component';
import { UserOrdersRoutingResolveService } from './user-orders-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const userOrdersRoute: Routes = [
  {
    path: '',
    component: UserOrdersComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserOrdersDetailComponent,
    resolve: {
      userOrders: UserOrdersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserOrdersUpdateComponent,
    resolve: {
      userOrders: UserOrdersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserOrdersUpdateComponent,
    resolve: {
      userOrders: UserOrdersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userOrdersRoute)],
  exports: [RouterModule],
})
export class UserOrdersRoutingModule {}
