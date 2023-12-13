import { TestBed } from '@angular/core/testing';

import { GameAuthService } from './game-auth.service';

describe('GameAuthService', () => {
  let service: GameAuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GameAuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
