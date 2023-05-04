import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BusinessesComponent } from './list/businesses.component';
import { BusinessesDetailComponent } from './detail/businesses-detail.component';
import { BusinessesUpdateComponent } from './update/businesses-update.component';
import { BusinessesDeleteDialogComponent } from './delete/businesses-delete-dialog.component';
import { BusinessesRoutingModule } from './route/businesses-routing.module';

@NgModule({
  imports: [SharedModule, BusinessesRoutingModule],
  declarations: [BusinessesComponent, BusinessesDetailComponent, BusinessesUpdateComponent, BusinessesDeleteDialogComponent],
})
export class BusinessesModule {}
