import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BusinessesComponent } from '../list/businesses.component';
import { BusinessesDetailComponent } from '../detail/businesses-detail.component';
import { BusinessesUpdateComponent } from '../update/businesses-update.component';
import { BusinessesRoutingResolveService } from './businesses-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const businessesRoute: Routes = [
  {
    path: '',
    component: BusinessesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
  },
  {
    path: ':id/view',
    component: BusinessesDetailComponent,
    resolve: {
      businesses: BusinessesRoutingResolveService,
    },
  },
  {
    path: 'new',
    component: BusinessesUpdateComponent,
    resolve: {
      businesses: BusinessesRoutingResolveService,
    },
  },
  {
    path: ':id/edit',
    component: BusinessesUpdateComponent,
    resolve: {
      businesses: BusinessesRoutingResolveService,
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(businessesRoute)],
  exports: [RouterModule],
})
export class BusinessesRoutingModule {}
