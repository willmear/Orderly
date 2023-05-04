import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { MESSAGES_ROUTE } from './Messages.route';
import { MessagesComponent } from './Messages.component';

@NgModule({
  imports: [SharedModule, RouterModule.forRoot([MESSAGES_ROUTE], { useHash: true })],
  declarations: [MessagesComponent],
  entryComponents: [],
  providers: [],
  //schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MessagesModule {}
