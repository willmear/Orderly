import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { GDPR_ROUTE } from './gdpr.route';
import { GdprComponent } from './gdpr.component';

@NgModule({
  imports: [SharedModule, RouterModule.forRoot([GDPR_ROUTE], { useHash: true })],
  declarations: [GdprComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class TeamprojectAppGdprModule {}
