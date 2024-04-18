import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileDataComparisonComponent } from './profile-data-comparison.component';

describe('ProfileDataComparisonComponent', () => {
  let component: ProfileDataComparisonComponent;
  let fixture: ComponentFixture<ProfileDataComparisonComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileDataComparisonComponent]
    });
    fixture = TestBed.createComponent(ProfileDataComparisonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
