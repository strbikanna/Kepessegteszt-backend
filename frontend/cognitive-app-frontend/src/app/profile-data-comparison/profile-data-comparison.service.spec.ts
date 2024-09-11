import { TestBed } from '@angular/core/testing';

import { ProfileDataComparisonService } from './profile-data-comparison.service';

describe('ProfileDataComparisonService', () => {
  let service: ProfileDataComparisonService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfileDataComparisonService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
