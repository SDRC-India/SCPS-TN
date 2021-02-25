import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditFacilityDetailsComponent } from './edit-facility-details.component';

describe('EditFacilityDetailsComponent', () => {
  let component: EditFacilityDetailsComponent;
  let fixture: ComponentFixture<EditFacilityDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditFacilityDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditFacilityDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
