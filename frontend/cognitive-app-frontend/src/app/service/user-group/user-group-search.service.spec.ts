import { TestBed } from '@angular/core/testing';

import { UserGroupSearchService } from './user-group-search.service';

describe('UserGroupSearchService', () => {
  let service: UserGroupSearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserGroupSearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
