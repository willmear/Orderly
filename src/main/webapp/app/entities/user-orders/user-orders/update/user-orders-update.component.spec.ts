import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserOrdersFormService } from './user-orders-form.service';
import { UserOrdersService } from '../service/user-orders.service';
import { IUserOrders } from '../user-orders.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { UserOrdersUpdateComponent } from './user-orders-update.component';

describe('UserOrders Management Update Component', () => {
  let comp: UserOrdersUpdateComponent;
  let fixture: ComponentFixture<UserOrdersUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userOrdersFormService: UserOrdersFormService;
  let userOrdersService: UserOrdersService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserOrdersUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(UserOrdersUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserOrdersUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userOrdersFormService = TestBed.inject(UserOrdersFormService);
    userOrdersService = TestBed.inject(UserOrdersService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const userOrders: IUserOrders = { id: 456 };
      const user: IUser = { id: 97895 };
      userOrders.user = user;

      const userCollection: IUser[] = [{ id: 54006 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userOrders });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userOrders: IUserOrders = { id: 456 };
      const user: IUser = { id: 87117 };
      userOrders.user = user;

      activatedRoute.data = of({ userOrders });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.userOrders).toEqual(userOrders);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserOrders>>();
      const userOrders = { id: 123 };
      jest.spyOn(userOrdersFormService, 'getUserOrders').mockReturnValue(userOrders);
      jest.spyOn(userOrdersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userOrders });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userOrders }));
      saveSubject.complete();

      // THEN
      expect(userOrdersFormService.getUserOrders).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userOrdersService.update).toHaveBeenCalledWith(expect.objectContaining(userOrders));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserOrders>>();
      const userOrders = { id: 123 };
      jest.spyOn(userOrdersFormService, 'getUserOrders').mockReturnValue({ id: null });
      jest.spyOn(userOrdersService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userOrders: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userOrders }));
      saveSubject.complete();

      // THEN
      expect(userOrdersFormService.getUserOrders).toHaveBeenCalled();
      expect(userOrdersService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserOrders>>();
      const userOrders = { id: 123 };
      jest.spyOn(userOrdersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userOrders });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userOrdersService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
