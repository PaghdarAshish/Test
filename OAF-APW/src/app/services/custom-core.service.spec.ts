import { TestBed, inject } from '@angular/core/testing';

import { CustomCoreService } from './custom-core.service';

describe('CustomCoreService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CustomCoreService]
    });
  });

  it('should be created', inject([CustomCoreService], (service: CustomCoreService) => {
    expect(service).toBeTruthy();
  }));
});
