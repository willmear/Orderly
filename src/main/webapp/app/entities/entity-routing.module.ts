import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'calendar-event',
        data: { pageTitle: 'teamprojectApp.calendarEvent.home.title' },
        loadChildren: () => import('./calendar-event/calendar-event.module').then(m => m.CalendarEventModule),
      },
      {
        path: 'businesses',
        data: { pageTitle: 'teamprojectApp.businesses.home.title' },
        loadChildren: () => import('./businesses/businesses.module').then(m => m.BusinessesModule),
      },
      {
        path: 'target',
        data: { pageTitle: 'teamprojectApp.target.home.title' },
        loadChildren: () => import('./target/target.module').then(m => m.TargetModule),
      },
      {
        path: 'message-chat',
        data: { pageTitle: 'teamprojectApp.chatMessage.home.title' },
        loadChildren: () => import('./chat-message/chat-message.module').then(m => m.ChatMessageModule),
      },

      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
