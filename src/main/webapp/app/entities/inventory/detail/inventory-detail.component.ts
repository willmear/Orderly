import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IInventory } from '../inventory.model';

@Component({
    selector: 'jhi-inventory-detail',
    templateUrl: './inventory-detail.component.html'
})
export class InventoryDetailComponent implements OnInit {
    inventory: IInventory | null = null;

    constructor(
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit(): void {
        this.activatedRoute.data.subscribe(({ inventory }) => {
            this.inventory = inventory;
        });
    }


    previousState(): void {
        window.history.back();
    }

}
