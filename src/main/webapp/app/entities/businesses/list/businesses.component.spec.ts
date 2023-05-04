import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { BusinessesService } from '../service/businesses.service';

import { BusinessesComponent } from './businesses.component';

describe('Businesses Management Component', () => {
  let comp: BusinessesComponent;
  let fixture: ComponentFixture<BusinessesComponent>;
  let service: BusinessesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'businesses', component: BusinessesComponent }]), HttpClientTestingModule],
      declarations: [BusinessesComponent],
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
      .overrideTemplate(BusinessesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BusinessesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BusinessesService);

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
    // @ts-ignore
    expect(service.query).toHaveBeenCalled();
    // @ts-ignore
    expect(comp.businesses?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to businessesService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getBusinessesIdentifier');
      const id = comp.trackId(0, entity);
      // @ts-ignore
      expect(service.getBusinessesIdentifier).toHaveBeenCalledWith(entity);
      // @ts-ignore
      expect(id).toBe(entity.id);
    });
  });
});
