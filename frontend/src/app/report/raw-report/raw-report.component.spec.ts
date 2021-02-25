import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RawReportComponent } from './raw-report.component';

describe('RawReportComponent', () => {
  let component: RawReportComponent;
  let fixture: ComponentFixture<RawReportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RawReportComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RawReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
