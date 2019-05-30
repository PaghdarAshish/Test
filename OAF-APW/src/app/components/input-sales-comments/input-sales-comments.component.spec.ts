import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InputSalesCommentsComponent } from './input-sales-comments.component';

describe('InputSalesCommentsComponent', () => {
  let component: InputSalesCommentsComponent;
  let fixture: ComponentFixture<InputSalesCommentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InputSalesCommentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InputSalesCommentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
