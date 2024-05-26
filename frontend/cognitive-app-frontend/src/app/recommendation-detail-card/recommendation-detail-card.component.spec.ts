import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecommendationDetailCardComponent } from './recommendation-detail-card.component';

describe('RecommendationDetailCardComponent', () => {
  let component: RecommendationDetailCardComponent;
  let fixture: ComponentFixture<RecommendationDetailCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RecommendationDetailCardComponent]
    });
    fixture = TestBed.createComponent(RecommendationDetailCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
