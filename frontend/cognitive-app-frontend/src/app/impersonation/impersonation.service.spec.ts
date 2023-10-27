import { TestBed } from '@angular/core/testing';

import { ImpersonationService } from './impersonation.service';

describe('ImpersonationService', () => {
  let service: ImpersonationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ImpersonationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
