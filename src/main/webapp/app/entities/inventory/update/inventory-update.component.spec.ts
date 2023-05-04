import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { InventoryFormService } from './inventory-form.service';
import { InventoryService } from '../service/inventory.service';
import { IInventory } from '../inventory.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { InventoryUpdateComponent } from './inventory-update.component';

describe('Inventory Management Update Component', () => {
    let comp: InventoryUpdateComponent;
    let fixture: ComponentFixture<InventoryUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let inventoryFormService: InventoryFormService;
    let inventoryService: InventoryService;
    let userService: UserService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
            declarations: [InventoryUpdateComponent],
            providers: [
              FormBuilder,
              {
                provide: ActivatedRoute,
                useValue: {
                  params: from([{}]),
                },
              },
            ]
        })
        .overrideTemplate(InventoryUpdateComponent, '')
        .compileComponents();

        fixture = TestBed.createComponent(InventoryUpdateComponent);
        activatedRoute = TestBed.inject(ActivatedRoute);
        inventoryFormService = TestBed.inject(InventoryFormService);
        inventoryService = TestBed.inject(InventoryService);
        userService = TestBed.inject(UserService);

        comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
        it('Should call User query and add missing value', () => {
                const inventory : IInventory = {"id":456};
                const user : IUser = {"id":49631};
                inventory.user = user;

                const userCollection: IUser[] = [{"id":53831}];
                jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
                const additionalUsers = [
                    user,
                ];
                const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
                jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

                activatedRoute.data = of({ inventory });
                comp.ngOnInit();

                expect(userService.query).toHaveBeenCalled();
                expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
                  userCollection,
                  ...additionalUsers.map(expect.objectContaining)
                );
                expect(comp.usersSharedCollection).toEqual(expectedCollection);
        });

        it('Should update editForm', () => {
            const inventory: IInventory = {"id":456};
            const user: IUser = {"id":16974};
            inventory.user = user;

            activatedRoute.data = of({ inventory });
            comp.ngOnInit();

            expect(comp.usersSharedCollection).toContain(user);
            expect(comp.inventory).toEqual(inventory);
        });
    });

    describe('save', () => {
        it('Should call update service on save for existing entity', () => {
            // GIVEN
            const saveSubject = new Subject<HttpResponse<IInventory>>();
            const inventory = {"id":123};
            jest.spyOn(inventoryFormService, 'getInventory').mockReturnValue(inventory);
            jest.spyOn(inventoryService, 'update').mockReturnValue(saveSubject);
            jest.spyOn(comp, 'previousState');
            activatedRoute.data = of({ inventory });
            comp.ngOnInit();

            // WHEN
            comp.save();
            expect(comp.isSaving).toEqual(true);
            saveSubject.next(new HttpResponse({ body: inventory }));
            saveSubject.complete();

            // THEN
            expect(inventoryFormService.getInventory).toHaveBeenCalled();
            expect(comp.previousState).toHaveBeenCalled();
            expect(inventoryService.update).toHaveBeenCalledWith(expect.objectContaining(inventory));
            expect(comp.isSaving).toEqual(false);
        });

        it('Should call create service on save for new entity', () => {
            // GIVEN
            const saveSubject = new Subject<HttpResponse<IInventory>>();
            const inventory = {"id":123};
            jest.spyOn(inventoryFormService, 'getInventory').mockReturnValue({ id: null });
            jest.spyOn(inventoryService, 'create').mockReturnValue(saveSubject);
            jest.spyOn(comp, 'previousState');
            activatedRoute.data = of({ inventory: null });
            comp.ngOnInit();

            // WHEN
            comp.save();
            expect(comp.isSaving).toEqual(true);
            saveSubject.next(new HttpResponse({ body: inventory }));
            saveSubject.complete();

            // THEN
            expect(inventoryFormService.getInventory).toHaveBeenCalled();
            expect(inventoryService.create).toHaveBeenCalled();
            expect(comp.isSaving).toEqual(false);
            expect(comp.previousState).toHaveBeenCalled();
        });

        it('Should set isSaving to false on error', () => {
            // GIVEN
            const saveSubject = new Subject<HttpResponse<IInventory>>();
            const inventory = {"id":123};
            jest.spyOn(inventoryService, 'update').mockReturnValue(saveSubject);
            jest.spyOn(comp, 'previousState');
            activatedRoute.data = of({ inventory });
            comp.ngOnInit();

            // WHEN
            comp.save();
            expect(comp.isSaving).toEqual(true);
            saveSubject.error('This is an error!');

            // THEN
            expect(inventoryService.update).toHaveBeenCalled();
            expect(comp.isSaving).toEqual(false);
            expect(comp.previousState).not.toHaveBeenCalled();
        });
    });


    describe('Compare relationships', () => {
        describe('compareUser', () => {
            it('Should forward to userService', () => {
                const entity = {"id":123};
                const entity2 = {"id":456};
                jest.spyOn(userService, 'compareUser');
                comp.compareUser(entity, entity2);
                expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
            });
        });

    });
});
