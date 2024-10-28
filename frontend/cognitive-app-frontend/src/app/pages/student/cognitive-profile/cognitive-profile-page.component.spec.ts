import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CognitiveProfilePageComponent } from './cognitive-profile-page.component';

describe('CognitiveProfileComponent', () => {
  let component: CognitiveProfilePageComponent;
  let fixture: ComponentFixture<CognitiveProfilePageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CognitiveProfilePageComponent]
    });
    fixture = TestBed.createComponent(CognitiveProfilePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
