import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { OrmvahTestModule } from '../../../test.module';
import { ExpeditorTypeUpdateComponent } from 'app/entities/expeditor-type/expeditor-type-update.component';
import { ExpeditorTypeService } from 'app/entities/expeditor-type/expeditor-type.service';
import { ExpeditorType } from 'app/shared/model/expeditor-type.model';

describe('Component Tests', () => {
  describe('ExpeditorType Management Update Component', () => {
    let comp: ExpeditorTypeUpdateComponent;
    let fixture: ComponentFixture<ExpeditorTypeUpdateComponent>;
    let service: ExpeditorTypeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OrmvahTestModule],
        declarations: [ExpeditorTypeUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(ExpeditorTypeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ExpeditorTypeUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ExpeditorTypeService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new ExpeditorType(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new ExpeditorType();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
