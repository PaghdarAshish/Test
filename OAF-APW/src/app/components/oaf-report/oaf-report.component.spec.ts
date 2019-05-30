import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OAFReportComponent  } from './oaf-report.component';

describe('OAFReportComponent', () => {
  let component: OAFReportComponent;
  let fixture: ComponentFixture<OAFReportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OAFReportComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OAFReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
