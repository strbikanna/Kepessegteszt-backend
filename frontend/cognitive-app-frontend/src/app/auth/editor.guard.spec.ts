import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { gameManagementGuard } from './gameManagementGuard';

describe('editorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => gameManagementGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
