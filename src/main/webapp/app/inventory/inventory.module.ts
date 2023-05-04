import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { inventory_ROUTE } from './inventory.route';
import { inventoryComponent } from './inventory.component';
import { AddItemDialogComponent } from './dialogs/add-item.component';

@NgModule({
  imports: [SharedModule, RouterModule.forRoot([inventory_ROUTE], { useHash: true })],
  declarations: [inventoryComponent, AddItemDialogComponent],
  entryComponents: [],
  providers: [],
  //schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class inventoryModule {}
