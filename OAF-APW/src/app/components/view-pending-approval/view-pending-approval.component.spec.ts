import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewPendingApprovalComponent } from './view-pending-approval.component';

describe('ViewPendingApprovalComponent', () => {
  let component: ViewPendingApprovalComponent;
  let fixture: ComponentFixture<ViewPendingApprovalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewPendingApprovalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewPendingApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
