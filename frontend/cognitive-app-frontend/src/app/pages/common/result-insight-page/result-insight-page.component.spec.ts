import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResultInsightPageComponent } from './result-insight-page.component';

describe('ResultInsightPageComponent', () => {
  let component: ResultInsightPageComponent;
  let fixture: ComponentFixture<ResultInsightPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResultInsightPageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ResultInsightPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
