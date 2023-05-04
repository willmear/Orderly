import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TargetComponent } from './list/target.component';
import { TargetDetailComponent } from './detail/target-detail.component';
import { TargetUpdateComponent } from './update/target-update.component';
import { TargetDeleteDialogComponent } from './delete/target-delete-dialog.component';
import { TargetRoutingModule } from './route/target-routing.module';

@NgModule({
  imports: [SharedModule, TargetRoutingModule],
  declarations: [TargetComponent, TargetDetailComponent, TargetUpdateComponent, TargetDeleteDialogComponent],
})
export class TargetModule {}
