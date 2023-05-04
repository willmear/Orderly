import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInventory } from '../inventory.model';
import { InventoryService } from '../service/inventory.service';

@Injectable({ providedIn: 'root' })
export class InventoryRoutingResolveService implements Resolve<IInventory | null> {
    constructor(protected service: InventoryService, protected router: Router) {}

    resolve(route: ActivatedRouteSnapshot): Observable<IInventory | null | never> {
        const id = route.params['id'];
        if (id) {
            return this.service.find(id).pipe(
                mergeMap((inventory: HttpResponse<IInventory>) => {
                    if (inventory.body) {
                        return of(inventory.body);
                    } else {
                        this.router.navigate(['404']);
                        return EMPTY;
                    }
                })
            );
        }
        return of(null);
    }
}
