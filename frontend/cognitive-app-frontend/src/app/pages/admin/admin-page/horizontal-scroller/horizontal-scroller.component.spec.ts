import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HorizontalScrollerComponent } from './horizontal-scroller.component';

describe('HorizontalScrollerComponent', () => {
  let component: HorizontalScrollerComponent;
  let fixture: ComponentFixture<HorizontalScrollerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HorizontalScrollerComponent]
    });
    fixture = TestBed.createComponent(HorizontalScrollerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
