import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RatioScaleComponent } from './ratio-scale.component';

describe('RatioScaleComponent', () => {
  let component: RatioScaleComponent;
  let fixture: ComponentFixture<RatioScaleComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RatioScaleComponent]
    });
    fixture = TestBed.createComponent(RatioScaleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
