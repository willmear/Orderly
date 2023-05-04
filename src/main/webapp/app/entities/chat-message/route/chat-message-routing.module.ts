import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'app/shared/shared.module';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ChatMessageComponent } from '../list/chat-message.component';
import { ChatMessageDetailComponent } from '../detail/chat-message-detail.component';
import { ChatMessageRoutingResolveService } from './chat-message-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const chatMessageRoute: Routes = [
  {
    path: 'chatMessage',
    component: ChatMessageComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ChatMessageDetailComponent,
    resolve: {
      chatMessage: ChatMessageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [CommonModule, SharedModule, RouterModule.forChild(chatMessageRoute)],
  exports: [RouterModule],
})
export class ChatMessageRoutingModule {}
