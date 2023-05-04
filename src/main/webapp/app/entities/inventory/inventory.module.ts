import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { InventoryComponent } from './list/inventory.component';
import { InventoryDetailComponent } from './detail/inventory-detail.component';
import { InventoryUpdateComponent } from './update/inventory-update.component';
import { InventoryDeleteDialogComponent } from './delete/inventory-delete-dialog.component';
import { InventoryRoutingModule } from './route/inventory-routing.module';

@NgModule({
  imports: [
    SharedModule,
    InventoryRoutingModule,
  ],
  declarations: [
    InventoryComponent,
    InventoryDetailComponent,
    InventoryUpdateComponent,
    InventoryDeleteDialogComponent,

  ],
})
export class InventoryModule {}
