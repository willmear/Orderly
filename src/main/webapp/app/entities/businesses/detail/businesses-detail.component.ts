import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { IBusinesses } from '../businesses.model';
import { DataUtils } from 'app/core/util/data-util.service';
import { BusinessesService } from '../service/businesses.service';
import { BusinessesFormService } from '../update/businesses-form.service';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'jhi-businesses-detail',
  templateUrl: './businesses-detail.component.html',
})
export class BusinessesDetailComponent implements OnInit {
  isSaving = false;
  businesses: IBusinesses | null = null;

  constructor(
    protected dataUtils: DataUtils,
    protected activatedRoute: ActivatedRoute,
    protected businessesService: BusinessesService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ businesses }) => {
      this.businesses = businesses;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }

  updateRating(): void {
    if (this.businesses) {
      if (!this.businesses?.rateCount || this.businesses.rateCount < 1 || this.businesses.rateCount == undefined) {
        this.businesses.rateCount = 1;
      }
      if (!this.businesses?.rating || this.businesses.rating < 0 || this.businesses.rating == undefined) {
        this.businesses.rating = 1;
      }

      let input = document.getElementById('rate') as HTMLSelectElement;
      this.businesses.rating = Math.round(
        this.businesses.rating + (parseInt(input.value) - this.businesses.rating) / this.businesses.rateCount
      );
      this.businesses.rateCount = this.businesses.rateCount + 1;
      this.subscribeToSaveResponse(this.businessesService.update(this.businesses));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBusinesses>>): void {
    result.pipe(finalize(() => (this.isSaving = false))).subscribe({});
  }

  roundRating(): number {
    if (this.businesses && this.businesses.rating) {
      //return Math.round(this.businesses.rating);
      return this.businesses.rating;
    } else {
      return 0;
    }
  }

  message(): void {
    this.router.navigate(['/Messages']);
  }
}
