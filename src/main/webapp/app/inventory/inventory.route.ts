import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { inventoryComponent } from './inventory.component';

export const inventory_ROUTE: Route = {
  path: 'inventory',
  component: inventoryComponent,
  data: {
    authorities: [],
    pageTitle: 'global.menu.inventory',
  },
  canActivate: [UserRouteAccessService],
};
