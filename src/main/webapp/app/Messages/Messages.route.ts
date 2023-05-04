import { Route } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MessagesComponent } from './Messages.component';

export const MESSAGES_ROUTE: Route = {
  path: 'Messages',
  component: MessagesComponent,
  data: {
    pageTitle: 'global.menu.Messages',
  },
};
