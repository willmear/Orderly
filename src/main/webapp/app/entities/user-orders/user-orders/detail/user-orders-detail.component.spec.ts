import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserOrdersDetailComponent } from './user-orders-detail.component';

describe('UserOrders Management Detail Component', () => {
  let comp: UserOrdersDetailComponent;
  let fixture: ComponentFixture<UserOrdersDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserOrdersDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userOrders: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserOrdersDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserOrdersDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userOrders on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userOrders).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
