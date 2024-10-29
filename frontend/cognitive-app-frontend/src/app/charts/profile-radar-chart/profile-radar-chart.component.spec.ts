import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileRadarChartComponent } from './profile-radar-chart.component';

describe('ProfileRadarChartComponent', () => {
  let component: ProfileRadarChartComponent;
  let fixture: ComponentFixture<ProfileRadarChartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileRadarChartComponent]
    });
    fixture = TestBed.createComponent(ProfileRadarChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
