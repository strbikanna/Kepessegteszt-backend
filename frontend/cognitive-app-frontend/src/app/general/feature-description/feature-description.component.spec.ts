import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureDescriptionComponent } from './feature-description.component';

describe('FeatureDescriptionComponent', () => {
  let component: FeatureDescriptionComponent;
  let fixture: ComponentFixture<FeatureDescriptionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FeatureDescriptionComponent]
    });
    fixture = TestBed.createComponent(FeatureDescriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
