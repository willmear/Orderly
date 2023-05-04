import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITarget, NewTarget } from '../target.model';

export type PartialUpdateTarget = Partial<ITarget> & Pick<ITarget, 'id'>;

export type EntityResponseType = HttpResponse<ITarget>;
export type EntityArrayResponseType = HttpResponse<ITarget[]>;


@Injectable({ providedIn: 'root' })
export class TargetService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/targets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(target: NewTarget): Observable<EntityResponseType> {
    return this.http.post<ITarget>(this.resourceUrl, target, { observe: 'response' });
  }

  update(target: ITarget): Observable<EntityResponseType> {
    return this.http.put<ITarget>(`${this.resourceUrl}/${this.getTargetIdentifier(target)}`, target, { observe: 'response' });
  }

  partialUpdate(target: PartialUpdateTarget): Observable<EntityResponseType> {
    return this.http.patch<ITarget>(`${this.resourceUrl}/${this.getTargetIdentifier(target)}`, target, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITarget>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITarget[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTargetIdentifier(target: Pick<ITarget, 'id'>): number {
    return target.id;
  }

  compareTarget(o1: Pick<ITarget, 'id'> | null, o2: Pick<ITarget, 'id'> | null): boolean {
    return o1 && o2 ? this.getTargetIdentifier(o1) === this.getTargetIdentifier(o2) : o1 === o2;
  }


  addTargetToCollectionIfMissing<Type extends Pick<ITarget, 'id'>>(
    targetCollection: Type[],
    ...targetsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const targets: Type[] = targetsToCheck.filter(isPresent);
    if (targets.length > 0) {
      const targetCollectionIdentifiers = targetCollection.map(targetItem => this.getTargetIdentifier(targetItem)!);
      const targetsToAdd = targets.filter(targetItem => {
        const targetIdentifier = this.getTargetIdentifier(targetItem);
        if (targetCollectionIdentifiers.includes(targetIdentifier)) {
          return false;
        }
        targetCollectionIdentifiers.push(targetIdentifier);
        return true;
      });
      return [...targetsToAdd, ...targetCollection];
    }
    return targetCollection;
  }
}
