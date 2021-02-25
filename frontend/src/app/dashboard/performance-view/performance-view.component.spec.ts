import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PerformanceViewComponent } from './performance-view.component';

describe('PerformanceViewComponent', () => {
  let component: PerformanceViewComponent;
  let fixture: ComponentFixture<PerformanceViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PerformanceViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PerformanceViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
