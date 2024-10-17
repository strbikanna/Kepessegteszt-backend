import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileLineChartComponent } from './profile-line-chart.component';

describe('ProfileLineChartComponent', () => {
  let component: ProfileLineChartComponent;
  let fixture: ComponentFixture<ProfileLineChartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileLineChartComponent]
    });
    fixture = TestBed.createComponent(ProfileLineChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
