import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';


import { InventoryDetailComponent } from './inventory-detail.component';

describe('Inventory Management Detail Component', () => {
    let comp: InventoryDetailComponent;
    let fixture: ComponentFixture<InventoryDetailComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [InventoryDetailComponent],
            providers: [
                {
                    provide: ActivatedRoute,
                    useValue: { data: of({ inventory: {"id":123} }) }
                }
            ]
        })
        .overrideTemplate(InventoryDetailComponent, '')
        .compileComponents();
        fixture = TestBed.createComponent(InventoryDetailComponent);
        comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
        it('Should load inventory on init', () => {
            // WHEN
            comp.ngOnInit();

            // THEN
            expect(comp.inventory).toEqual(expect.objectContaining({"id":123}));
        });
    });

});
