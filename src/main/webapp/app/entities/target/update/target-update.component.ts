import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { TargetFormService, TargetFormGroup } from './target-form.service';
import { ITarget } from '../target.model';
import { TargetService } from '../service/target.service';

@Component({
  selector: 'jhi-target-update',
  templateUrl: './target-update.component.html',
})
export class TargetUpdateComponent implements OnInit {
  isSaving = false;
  target: ITarget | null = null;

  editForm: TargetFormGroup = this.targetFormService.createTargetFormGroup();

  constructor(
    protected targetService: TargetService,
    protected targetFormService: TargetFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ target }) => {
      this.target = target;
      if (target) {
        this.updateForm(target);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const target = this.targetFormService.getTarget(this.editForm);
    if (target.id !== null) {
      this.subscribeToSaveResponse(this.targetService.update(target));
    } else {
      this.subscribeToSaveResponse(this.targetService.create(target));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITarget>>): void {
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

  protected updateForm(target: ITarget): void {
    this.target = target;
    this.targetFormService.resetForm(this.editForm, target);
  }
}
