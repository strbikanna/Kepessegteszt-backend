import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResultInfoCardComponent } from './result-info-card.component';

describe('ResultInfoCardComponent', () => {
  let component: ResultInfoCardComponent;
  let fixture: ComponentFixture<ResultInfoCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResultInfoCardComponent]
    });
    fixture = TestBed.createComponent(ResultInfoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
