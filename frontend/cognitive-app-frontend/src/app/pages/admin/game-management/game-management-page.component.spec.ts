import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameManagementPageComponent } from './game-management-page.component';

describe('GameManagementComponent', () => {
  let component: GameManagementPageComponent;
  let fixture: ComponentFixture<GameManagementPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameManagementPageComponent]
    });
    fixture = TestBed.createComponent(GameManagementPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
