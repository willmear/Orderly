
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { InventoryService } from '../service/inventory.service';

import { InventoryComponent } from './inventory.component';

describe('Inventory Management Component', () => {
    let comp: InventoryComponent;
    let fixture: ComponentFixture<InventoryComponent>;
    let service: InventoryService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
              RouterTestingModule.withRoutes([{ path: 'inventory', component: InventoryComponent }]),
              HttpClientTestingModule
            ],
            declarations: [InventoryComponent],
            providers: [
                {
                    provide: ActivatedRoute,
                    useValue: {
                        data: of({
                            defaultSort: 'id,asc',
                        }),
                        queryParamMap: of(
                            jest.requireActual('@angular/router').convertToParamMap({
                                page: '1',
                                size: '1',
                                sort: 'id,desc',
                            })
                        ),
                        snapshot: { queryParams: {} },
                    }
                }
            ]
        })
        .overrideTemplate(InventoryComponent, '')
        .compileComponents();

        fixture = TestBed.createComponent(InventoryComponent);
        comp = fixture.componentInstance;
        service = TestBed.inject(InventoryService);

        const headers = new HttpHeaders();
        jest.spyOn(service, 'query').mockReturnValue(
            of(
                new HttpResponse({
                    body: [{"id":123}],
                    headers,
                })
            )
        );
    });

    it('Should call load all on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(service.query).toHaveBeenCalled();
        expect(comp.inventories?.[0]).toEqual(expect.objectContaining({"id":123}));
    });

    describe('trackId', () => {
        it('Should forward to inventoryService', () => {
            const entity = {"id":123};
            jest.spyOn(service, 'getInventoryIdentifier');
            const id = comp.trackId(0, entity);
            expect(service.getInventoryIdentifier).toHaveBeenCalledWith(entity);
            expect(id).toBe(entity.id);
        });
    });
});
