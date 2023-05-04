import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NgChartsModule } from 'ng2-charts';

import { SharedModule } from 'app/shared/shared.module';

import { FINANCE_ROUTE } from './finance.route';
import { FinanceComponent } from './finance.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TargetEditComponent } from './dialogs/target-edit/target-edit.component';

@NgModule({
  imports: [SharedModule, RouterModule.forRoot([FINANCE_ROUTE], { useHash: true }), NgChartsModule, FormsModule, HttpClientModule],
  declarations: [FinanceComponent, TargetEditComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class TeamprojectAppFinanceModule {}
