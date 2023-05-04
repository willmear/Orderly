import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UserOrdersFormService, UserOrdersFormGroup } from './user-orders-form.service';
import { IUserOrders } from '../user-orders.model';
import { UserOrdersService } from '../service/user-orders.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-user-orders-update',
  templateUrl: './user-orders-update.component.html',
})
export class UserOrdersUpdateComponent implements OnInit {
  isSaving = false;
  userOrders: IUserOrders | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: UserOrdersFormGroup = this.userOrdersFormService.createUserOrdersFormGroup();

  constructor(
    protected userOrdersService: UserOrdersService,
    protected userOrdersFormService: UserOrdersFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userOrders }) => {
      this.userOrders = userOrders;
      if (userOrders) {
        this.updateForm(userOrders);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userOrders = this.userOrdersFormService.getUserOrders(this.editForm);
    if (userOrders.id !== null) {
      this.subscribeToSaveResponse(this.userOrdersService.update(userOrders));
    } else {
      this.subscribeToSaveResponse(this.userOrdersService.create(userOrders));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserOrders>>): void {
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

  protected updateForm(userOrders: IUserOrders): void {
    this.userOrders = userOrders;
    this.userOrdersFormService.resetForm(this.editForm, userOrders);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, userOrders.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userOrders?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
