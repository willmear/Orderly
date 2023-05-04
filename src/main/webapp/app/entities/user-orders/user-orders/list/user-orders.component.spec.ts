import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserOrdersService } from '../service/user-orders.service';

import { UserOrdersComponent } from './user-orders.component';

describe('UserOrders Management Component', () => {
  let comp: UserOrdersComponent;
  let fixture: ComponentFixture<UserOrdersComponent>;
  let service: UserOrdersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'user-orders', component: UserOrdersComponent }]), HttpClientTestingModule],
      declarations: [UserOrdersComponent],
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
          },
        },
      ],
    })
      .overrideTemplate(UserOrdersComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserOrdersComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UserOrdersService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
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
    expect(comp.userOrders?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to userOrdersService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getUserOrdersIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getUserOrdersIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
