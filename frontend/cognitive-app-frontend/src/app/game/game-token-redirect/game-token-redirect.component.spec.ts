import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameTokenRedirectComponent } from './game-token-redirect.component';

describe('GameTokenRedirectComponent', () => {
  let component: GameTokenRedirectComponent;
  let fixture: ComponentFixture<GameTokenRedirectComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameTokenRedirectComponent]
    });
    fixture = TestBed.createComponent(GameTokenRedirectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
