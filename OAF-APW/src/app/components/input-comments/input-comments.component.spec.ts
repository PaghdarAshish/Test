import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InputCommentsComponent } from './input-comments.component';

describe('InputCommentsComponent', () => {
  let component: InputCommentsComponent;
  let fixture: ComponentFixture<InputCommentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InputCommentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InputCommentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
