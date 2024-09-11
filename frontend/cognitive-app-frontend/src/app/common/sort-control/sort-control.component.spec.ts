import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SortControlComponent } from './sort-control.component';

describe('FilterAndSortToolboxComponent', () => {
  let component: SortControlComponent;
  let fixture: ComponentFixture<SortControlComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SortControlComponent]
    });
    fixture = TestBed.createComponent(SortControlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
