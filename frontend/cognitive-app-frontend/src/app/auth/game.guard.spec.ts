import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { gameGuard } from './game.guard';

describe('gameGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => gameGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
