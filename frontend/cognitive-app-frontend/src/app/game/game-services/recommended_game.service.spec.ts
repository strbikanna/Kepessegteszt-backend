import { TestBed } from '@angular/core/testing';

import { RecommendedGameService } from './recommended-game.service';

describe('GameServiceService', () => {
  let service: RecommendedGameService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RecommendedGameService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
