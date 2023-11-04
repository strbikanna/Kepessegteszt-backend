import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CognitiveProfileComponent } from './cognitive-profile.component';

describe('CognitiveProfileComponent', () => {
  let component: CognitiveProfileComponent;
  let fixture: ComponentFixture<CognitiveProfileComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CognitiveProfileComponent]
    });
    fixture = TestBed.createComponent(CognitiveProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
