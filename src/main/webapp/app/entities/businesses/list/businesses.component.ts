import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { combineLatest, filter, Observable, switchMap, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBusinesses } from '../businesses.model';
import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { EntityArrayResponseType, BusinessesService } from '../service/businesses.service';
import { BusinessesDeleteDialogComponent } from '../delete/businesses-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';
import { SortService } from 'app/shared/sort/sort.service';
import { getSharedSetup } from '@angular/compiler-cli/ngcc/src/ngcc_options';
import { UserService } from '../../user/user.service';

let userService: UserService;

@Component({
  selector: 'jhi-businesses',
  templateUrl: './businesses.component.html',
})
export class BusinessesComponent implements OnInit {
  businesses?: IBusinesses[];
  business: IBusinesses | null = null;
  isLoading = false;

  predicate = 'id';
  ascending = true;

  constructor(
    protected businessesService: BusinessesService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected sortService: SortService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected userService: UserService
  ) {}

  trackId = (_index: number, item: IBusinesses): number => this.businessesService.getBusinessesIdentifier(item);

  ngOnInit(): void {
    this.load();
    this.clearFilter();
    this.homeSearch();
    this.homeFilter();
  }

  homeSearch(): void {
    let search = this.dataUtils.getSearch();
    if (search != '' && search != undefined) {
      let input = document.getElementById('searchBar') as HTMLInputElement;
      input.value = this.dataUtils.getSearch();
      this.dataUtils.setSearch('');
    }
  }

  homeFilter(): void {
    let filter = this.dataUtils.getFilter();
    if (filter != '' && filter != undefined) {
      let input = document.getElementById('type') as HTMLSelectElement;
      input.value = this.dataUtils.getFilter();
      this.dataUtils.setFilter('');
    }
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(businesses: IBusinesses): void {
    const modalRef = this.modalService.open(BusinessesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.businesses = businesses;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations())
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.predicate, this.ascending);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.predicate, this.ascending))
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.businesses = this.refineData(dataFromBody);
  }

  protected refineData(data: IBusinesses[]): IBusinesses[] {
    return data.sort(this.sortService.startSort(this.predicate, this.ascending ? 1 : -1));
  }

  protected fillComponentAttributesFromResponseBody(data: IBusinesses[] | null): IBusinesses[] {
    return data ?? [];
  }

  protected queryBackend(predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject = {
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.businessesService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(predicate?: string, ascending?: boolean): void {
    const queryParamsObj = {
      sort: this.getSortQueryParam(predicate, ascending),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }

  search(): string {
    let input = document.getElementById('searchBar') as HTMLInputElement;
    return input.value;
  }

  clearSearch(): void {
    let input = document.getElementById('searchBar') as HTMLInputElement;
    input.value = '';
    this.search();
  }

  filter(): string {
    let input = document.getElementById('type') as HTMLSelectElement;
    return input.value;
  }

  clearFilter(): void {
    let input = document.getElementById('type') as HTMLSelectElement;
    input.value = '';
    this.filter();
  }

  roundRating(rating: number | null | undefined): number {
    if (rating) {
      return rating;
      //return Math.round(rating);
    }
    return 0;
  }
}
