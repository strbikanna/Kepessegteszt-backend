import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CognitiveProfileEditPageComponent } from './cognitive-profile-edit-page.component';

describe('CognitiveProfileEditPageComponent', () => {
  let component: CognitiveProfileEditPageComponent;
  let fixture: ComponentFixture<CognitiveProfileEditPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CognitiveProfileEditPageComponent]
    });
    fixture = TestBed.createComponent(CognitiveProfileEditPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
