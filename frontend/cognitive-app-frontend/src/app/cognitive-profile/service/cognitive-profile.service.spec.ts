import { TestBed } from '@angular/core/testing';

import { CognitiveProfileService } from './cognitive-profile.service';

describe('CognitiveProfileService', () => {
  let service: CognitiveProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CognitiveProfileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
