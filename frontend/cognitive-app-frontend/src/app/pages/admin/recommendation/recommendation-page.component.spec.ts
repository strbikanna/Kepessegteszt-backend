import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecommendationPageComponent } from './recommendation-page.component';

describe('RecommendationComponent', () => {
  let component: RecommendationPageComponent;
  let fixture: ComponentFixture<RecommendationPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RecommendationPageComponent]
    });
    fixture = TestBed.createComponent(RecommendationPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
