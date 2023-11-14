import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecommendedGamesComponent } from './recommended-games.component';

describe('GamesComponent', () => {
  let component: RecommendedGamesComponent;
  let fixture: ComponentFixture<RecommendedGamesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RecommendedGamesComponent]
    });
    fixture = TestBed.createComponent(RecommendedGamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
