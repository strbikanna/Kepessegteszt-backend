import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminProfileDataComparisonPageComponent } from './admin-profile-data-comparison-page.component';

describe('AdminProfileDataComparisonPageComponent', () => {
  let component: AdminProfileDataComparisonPageComponent;
  let fixture: ComponentFixture<AdminProfileDataComparisonPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminProfileDataComparisonPageComponent]
    });
    fixture = TestBed.createComponent(AdminProfileDataComparisonPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
