import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OafDocumentDownloaderComponent } from './oaf-document-downloader.component';

describe('OafDocumentDownloaderComponent', () => {
  let component: OafDocumentDownloaderComponent;
  let fixture: ComponentFixture<OafDocumentDownloaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OafDocumentDownloaderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OafDocumentDownloaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
