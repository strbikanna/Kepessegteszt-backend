import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminCognitiveProfilePageComponent } from './admin-cognitive-profile-page.component';

describe('AdminCognitiveProfileComponent', () => {
  let component: AdminCognitiveProfilePageComponent;
  let fixture: ComponentFixture<AdminCognitiveProfilePageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminCognitiveProfilePageComponent]
    });
    fixture = TestBed.createComponent(AdminCognitiveProfilePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
