import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TargetComponent } from '../list/target.component';
import { TargetDetailComponent } from '../detail/target-detail.component';
import { TargetUpdateComponent } from '../update/target-update.component';
import { TargetRoutingResolveService } from './target-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const targetRoute: Routes = [
  {
    path: '',
    component: TargetComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TargetDetailComponent,
    resolve: {
      target: TargetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TargetUpdateComponent,
    resolve: {
      target: TargetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TargetUpdateComponent,
    resolve: {
      target: TargetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(targetRoute)],
  exports: [RouterModule],
})
export class TargetRoutingModule {}
