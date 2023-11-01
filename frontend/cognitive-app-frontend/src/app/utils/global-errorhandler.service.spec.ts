import { TestBed } from '@angular/core/testing';

import { GlobalErrorhandlerService } from './global-errorhandler.service';

describe('GlobalErrorhandlerService', () => {
  let service: GlobalErrorhandlerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GlobalErrorhandlerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
