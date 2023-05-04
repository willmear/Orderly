import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBusinesses, NewBusinesses } from '../businesses.model';

export type PartialUpdateBusinesses = Partial<IBusinesses> & Pick<IBusinesses, 'id'>;

export type EntityResponseType = HttpResponse<IBusinesses>;
export type EntityArrayResponseType = HttpResponse<IBusinesses[]>;

@Injectable({ providedIn: 'root' })
export class BusinessesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/businesses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(businesses: NewBusinesses): Observable<EntityResponseType> {
    return this.http.post<IBusinesses>(this.resourceUrl, businesses, { observe: 'response' });
  }

  update(businesses: IBusinesses): Observable<EntityResponseType> {
    return this.http.put<IBusinesses>(`${this.resourceUrl}/${this.getBusinessesIdentifier(businesses)}`, businesses, {
      observe: 'response',
    });
  }

  partialUpdate(businesses: PartialUpdateBusinesses): Observable<EntityResponseType> {
    return this.http.patch<IBusinesses>(`${this.resourceUrl}/${this.getBusinessesIdentifier(businesses)}`, businesses, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBusinesses>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBusinesses[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBusinessesIdentifier(businesses: Pick<IBusinesses, 'id'>): number {
    return businesses.id;
  }

  compareBusinesses(o1: Pick<IBusinesses, 'id'> | null, o2: Pick<IBusinesses, 'id'> | null): boolean {
    return o1 && o2 ? this.getBusinessesIdentifier(o1) === this.getBusinessesIdentifier(o2) : o1 === o2;
  }

  addBusinessesToCollectionIfMissing<Type extends Pick<IBusinesses, 'id'>>(
    businessesCollection: Type[],
    ...businessesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const businesses: Type[] = businessesToCheck.filter(isPresent);
    if (businesses.length > 0) {
      const businessesCollectionIdentifiers = businessesCollection.map(businessesItem => this.getBusinessesIdentifier(businessesItem)!);
      const businessesToAdd = businesses.filter(businessesItem => {
        const businessesIdentifier = this.getBusinessesIdentifier(businessesItem);
        if (businessesCollectionIdentifiers.includes(businessesIdentifier)) {
          return false;
        }
        businessesCollectionIdentifiers.push(businessesIdentifier);
        return true;
      });
      return [...businessesToAdd, ...businessesCollection];
    }
    return businessesCollection;
  }
}
