import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewBlankFormComponent } from './new-blank-form.component';

describe('NewBlankFormComponent', () => {
  let component: NewBlankFormComponent;
  let fixture: ComponentFixture<NewBlankFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewBlankFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewBlankFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
