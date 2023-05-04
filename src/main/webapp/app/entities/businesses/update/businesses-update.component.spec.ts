import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BusinessesFormService } from './businesses-form.service';
import { BusinessesService } from '../service/businesses.service';
import { IBusinesses } from '../businesses.model';

import { BusinessesUpdateComponent } from './businesses-update.component';

describe('Businesses Management Update Component', () => {
  let comp: BusinessesUpdateComponent;
  let fixture: ComponentFixture<BusinessesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let businessesFormService: BusinessesFormService;
  let businessesService: BusinessesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BusinessesUpdateComponent],
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
      .overrideTemplate(BusinessesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BusinessesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    businessesFormService = TestBed.inject(BusinessesFormService);
    businessesService = TestBed.inject(BusinessesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const businesses: IBusinesses = { id: 456 };

      activatedRoute.data = of({ businesses });
      comp.ngOnInit();

      expect(comp.businesses).toEqual(businesses);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBusinesses>>();
      const businesses = { id: 123 };
      jest.spyOn(businessesFormService, 'getBusinesses').mockReturnValue(businesses);
      jest.spyOn(businessesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ businesses });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: businesses }));
      saveSubject.complete();

      // THEN
      expect(businessesFormService.getBusinesses).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(businessesService.update).toHaveBeenCalledWith(expect.objectContaining(businesses));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBusinesses>>();
      const businesses = { id: 123 };
      jest.spyOn(businessesFormService, 'getBusinesses').mockReturnValue({ id: null });
      jest.spyOn(businessesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ businesses: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: businesses }));
      saveSubject.complete();

      // THEN
      expect(businessesFormService.getBusinesses).toHaveBeenCalled();
      expect(businessesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBusinesses>>();
      const businesses = { id: 123 };
      jest.spyOn(businessesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ businesses });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(businessesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
