import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'app/shared/shared.module';
import { ChatMessageComponent } from './list/chat-message.component';
import { ChatMessageDetailComponent } from './detail/chat-message-detail.component';
import { ChatMessageRoutingModule } from './route/chat-message-routing.module';

@NgModule({
  imports: [CommonModule, SharedModule, ChatMessageRoutingModule],
  declarations: [ChatMessageComponent, ChatMessageDetailComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ChatMessageModule {}
