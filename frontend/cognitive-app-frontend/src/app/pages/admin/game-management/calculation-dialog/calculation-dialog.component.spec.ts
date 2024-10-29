import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalculationDialogComponent } from './calculation-dialog.component';

describe('CalculationDialogComponent', () => {
  let component: CalculationDialogComponent;
  let fixture: ComponentFixture<CalculationDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CalculationDialogComponent]
    });
    fixture = TestBed.createComponent(CalculationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
