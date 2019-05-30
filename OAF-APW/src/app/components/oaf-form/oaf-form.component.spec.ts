import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OAFFormComponent } from './oaf-form.component';

describe('OAFFormComponent', () => {
  let component: OAFFormComponent;
  let fixture: ComponentFixture<OAFFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OAFFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OAFFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
