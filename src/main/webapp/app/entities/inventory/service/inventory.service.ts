import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInventory, NewInventory } from '../inventory.model';

export type PartialUpdateInventory = Partial<IInventory> & Pick<IInventory, 'id'>;

export type EntityResponseType = HttpResponse<IInventory>;
export type EntityArrayResponseType = HttpResponse<IInventory[]>;

@Injectable({ providedIn: 'root' })
export class InventoryService {
    protected resourceUrl = this.applicationConfigService.getEndpointFor('api/inventories');

    constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

    create(inventory: NewInventory): Observable<EntityResponseType> {
        return this.http.post<IInventory>(this.resourceUrl,
                 inventory ,
                { observe: 'response' })
        ;
    }

    update(inventory: IInventory): Observable<EntityResponseType> {
        return this.http.put<IInventory>(`${this.resourceUrl}/${this.getInventoryIdentifier(inventory)}`,
                 inventory ,
                { observe: 'response' })
        ;
    }

    partialUpdate(inventory: PartialUpdateInventory): Observable<EntityResponseType> {
        return this.http.patch<IInventory>(`${this.resourceUrl}/${this.getInventoryIdentifier(inventory)}`,
                 inventory ,
                { observe: 'response' })
        ;
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IInventory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            ;
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IInventory[]>(this.resourceUrl, { params: options, observe: 'response' })
            ;
    }

    delete(id: number): Observable<HttpResponse<{}>> {
        return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }


    getInventoryIdentifier(inventory: Pick<IInventory, 'id'>): number {
        return inventory.id;
    }

    compareInventory(o1: Pick<IInventory, 'id'> | null, o2: Pick<IInventory, 'id'> | null): boolean {
        return o1 && o2 ? this.getInventoryIdentifier(o1) === this.getInventoryIdentifier(o2) : o1 === o2;
    }

    addInventoryToCollectionIfMissing<Type extends Pick<IInventory, 'id'>>(inventoryCollection: Type[], ...inventoriesToCheck: (Type | null | undefined)[]): Type[] {
        const inventories: Type[] = inventoriesToCheck.filter(isPresent);
        if (inventories.length > 0) {
            const inventoryCollectionIdentifiers = inventoryCollection.map(inventoryItem => this.getInventoryIdentifier(inventoryItem)!);
            const inventoriesToAdd = inventories.filter(inventoryItem => {
                const inventoryIdentifier = this.getInventoryIdentifier(inventoryItem);
                if (inventoryCollectionIdentifiers.includes(inventoryIdentifier)) {
                    return false;
                }
                inventoryCollectionIdentifiers.push(inventoryIdentifier);
                return true;
            });
            return [...inventoriesToAdd, ...inventoryCollection];
        }
        return inventoryCollection;
    }

}
