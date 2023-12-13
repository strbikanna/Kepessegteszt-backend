import { TestBed } from '@angular/core/testing';

import { SimpleHttpService } from './simple-http.service';

describe('HttpService', () => {
  let service: SimpleHttpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SimpleHttpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
