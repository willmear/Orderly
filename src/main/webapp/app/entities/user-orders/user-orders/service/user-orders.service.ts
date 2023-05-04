import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserOrders, NewUserOrders } from '../user-orders.model';

export type PartialUpdateUserOrders = Partial<IUserOrders> & Pick<IUserOrders, 'id'>;

type RestOf<T extends IUserOrders | NewUserOrders> = Omit<T, 'dateOrdered' | 'dueDate'> & {
  dateOrdered?: string | null;
  dueDate?: string | null;
};

export type RestUserOrders = RestOf<IUserOrders>;

export type NewRestUserOrders = RestOf<NewUserOrders>;

export type PartialUpdateRestUserOrders = RestOf<PartialUpdateUserOrders>;

export type EntityResponseType = HttpResponse<IUserOrders>;
export type EntityArrayResponseType = HttpResponse<IUserOrders[]>;

@Injectable({ providedIn: 'root' })
export class UserOrdersService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-orders');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(userOrders: NewUserOrders): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userOrders);
    return this.http
      .post<RestUserOrders>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(userOrders: IUserOrders): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userOrders);
    return this.http
      .put<RestUserOrders>(`${this.resourceUrl}/${this.getUserOrdersIdentifier(userOrders)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(userOrders: PartialUpdateUserOrders): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userOrders);
    return this.http
      .patch<RestUserOrders>(`${this.resourceUrl}/${this.getUserOrdersIdentifier(userOrders)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestUserOrders>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestUserOrders[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUserOrdersIdentifier(userOrders: Pick<IUserOrders, 'id'>): number {
    return userOrders.id;
  }

  compareUserOrders(o1: Pick<IUserOrders, 'id'> | null, o2: Pick<IUserOrders, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserOrdersIdentifier(o1) === this.getUserOrdersIdentifier(o2) : o1 === o2;
  }

  // FOR FINANCES PAGE
  // DON'T EDIT
  // -----------------------------------------------------------

  chart1OneMonth(req?: any): Observable<HttpResponse<Map<String, number>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, number>>(`${this.resourceUrl}/count-by-product-one-month`, { params: options, observe: 'response' });
  }

  chart1SixMonths(req?: any): Observable<HttpResponse<Map<String, number>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, number>>(`${this.resourceUrl}/count-by-product-six-months`, { params: options, observe: 'response' });
  }

  chart1OneYear(req?: any): Observable<HttpResponse<Map<String, number>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, number>>(`${this.resourceUrl}/count-by-product-one-year`, { params: options, observe: 'response' });
  }

  chart1(req?: any): Observable<HttpResponse<Map<String, number>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, number>>(`${this.resourceUrl}/count-by-product`, { params: options, observe: 'response' });
  }

  chart2(req?: any): Observable<HttpResponse<Map<String, number>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, number>>(`${this.resourceUrl}/revenue-by-product`, { params: options, observe: 'response' });
  }

  chart2OneMonth(req?: any): Observable<HttpResponse<Map<String, number>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, number>>(`${this.resourceUrl}/revenue-by-product-one-month`, { params: options, observe: 'response' });
  }

  chart2SixMonths(req?: any): Observable<HttpResponse<Map<String, number>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, number>>(`${this.resourceUrl}/revenue-by-product-six-months`, {
      params: options,
      observe: 'response',
    });
  }

  chart2OneYear(req?: any): Observable<HttpResponse<Map<String, number>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, number>>(`${this.resourceUrl}/revenue-by-product-one-year`, { params: options, observe: 'response' });
  }

  chart3(req?: any): Observable<HttpResponse<Map<String, number>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, number>>(`${this.resourceUrl}/revenue-by-month`, { params: options, observe: 'response' });
  }

  chart4(req?: any): Observable<HttpResponse<Map<String, Array<number>>>> {
    const options = createRequestOption(req);
    return this.http.get<Map<String, Array<number>>>(`${this.resourceUrl}/revenue-and-loss`, { params: options, observe: 'response' });
  }

  // -----------------------------------------------------------

  addUserOrdersToCollectionIfMissing<Type extends Pick<IUserOrders, 'id'>>(
    userOrdersCollection: Type[],
    ...userOrdersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userOrders: Type[] = userOrdersToCheck.filter(isPresent);
    if (userOrders.length > 0) {
      const userOrdersCollectionIdentifiers = userOrdersCollection.map(userOrdersItem => this.getUserOrdersIdentifier(userOrdersItem)!);
      const userOrdersToAdd = userOrders.filter(userOrdersItem => {
        const userOrdersIdentifier = this.getUserOrdersIdentifier(userOrdersItem);
        if (userOrdersCollectionIdentifiers.includes(userOrdersIdentifier)) {
          return false;
        }
        userOrdersCollectionIdentifiers.push(userOrdersIdentifier);
        return true;
      });
      return [...userOrdersToAdd, ...userOrdersCollection];
    }
    return userOrdersCollection;
  }

  protected convertDateFromClient<T extends IUserOrders | NewUserOrders | PartialUpdateUserOrders>(userOrders: T): RestOf<T> {
    return {
      ...userOrders,
      dateOrdered: dayjs(userOrders.dateOrdered)?.format(DATE_FORMAT) ?? null,
      dueDate: dayjs(userOrders.dueDate)?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restUserOrders: RestUserOrders): IUserOrders {
    return {
      ...restUserOrders,
      dateOrdered: restUserOrders.dateOrdered ? dayjs(restUserOrders.dateOrdered) : undefined,
      dueDate: restUserOrders.dueDate ? dayjs(restUserOrders.dueDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestUserOrders>): HttpResponse<IUserOrders> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestUserOrders[]>): HttpResponse<IUserOrders[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
