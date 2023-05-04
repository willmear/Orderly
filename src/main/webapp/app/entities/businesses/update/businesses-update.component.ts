import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { BusinessesFormService, BusinessesFormGroup } from './businesses-form.service';
import { IBusinesses } from '../businesses.model';
import { BusinessesService } from '../service/businesses.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-businesses-update',
  templateUrl: './businesses-update.component.html',
})
export class BusinessesUpdateComponent implements OnInit {
  isSaving = false;
  businesses: IBusinesses | null = null;

  editForm: BusinessesFormGroup = this.businessesFormService.createBusinessesFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected businessesService: BusinessesService,
    protected businessesFormService: BusinessesFormService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    private accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ businesses }) => {
      this.businesses = businesses;
      if (businesses) {
        this.updateForm(businesses);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('teamprojectApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const businesses = this.businessesFormService.getBusinesses(this.editForm);
    let type = document.getElementById('type') as HTMLSelectElement;
    businesses.type = type.value;
    businesses.rating = 1;
    businesses.rateCount = 0;
    if (businesses.id !== null) {
      this.subscribeToSaveResponse(this.businessesService.update(businesses));
    } else {
      this.subscribeToSaveResponse(this.businessesService.create(businesses));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBusinesses>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(businesses: IBusinesses): void {
    this.businesses = businesses;
    this.businessesFormService.resetForm(this.editForm, businesses);
  }
}
