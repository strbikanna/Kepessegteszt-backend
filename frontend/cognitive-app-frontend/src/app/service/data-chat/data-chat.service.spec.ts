import { TestBed } from '@angular/core/testing';

import { DataChatService } from './data-chat.service';

describe('DataChatService', () => {
  let service: DataChatService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataChatService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
