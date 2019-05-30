import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailApprovalComponent } from './email-approval.component';

describe('EmailApprovalComponent', () => {
  let component: EmailApprovalComponent;
  let fixture: ComponentFixture<EmailApprovalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EmailApprovalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
