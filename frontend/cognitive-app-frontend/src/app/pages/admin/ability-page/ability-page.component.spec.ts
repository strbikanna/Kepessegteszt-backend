import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AbilityPageComponent } from './ability-page.component';

describe('AbilityPageComponent', () => {
  let component: AbilityPageComponent;
  let fixture: ComponentFixture<AbilityPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AbilityPageComponent]
    });
    fixture = TestBed.createComponent(AbilityPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
