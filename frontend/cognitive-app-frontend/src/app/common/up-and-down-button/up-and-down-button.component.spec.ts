import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpAndDownButtonComponent } from './up-and-down-button.component';

describe('UpAndDownButtonComponent', () => {
  let component: UpAndDownButtonComponent;
  let fixture: ComponentFixture<UpAndDownButtonComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpAndDownButtonComponent]
    });
    fixture = TestBed.createComponent(UpAndDownButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
