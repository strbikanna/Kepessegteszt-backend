import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileDataComparisonPageComponent } from './profile-data-comparison-page.component';

describe('ProfileDataComparisonComponent', () => {
  let component: ProfileDataComparisonPageComponent;
  let fixture: ComponentFixture<ProfileDataComparisonPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileDataComparisonPageComponent]
    });
    fixture = TestBed.createComponent(ProfileDataComparisonPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
