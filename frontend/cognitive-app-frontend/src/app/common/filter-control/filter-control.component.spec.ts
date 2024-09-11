import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterControlComponent } from './filter-control.component';

describe('FilterControlComponent', () => {
  let component: FilterControlComponent;
  let fixture: ComponentFixture<FilterControlComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FilterControlComponent]
    });
    fixture = TestBed.createComponent(FilterControlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
