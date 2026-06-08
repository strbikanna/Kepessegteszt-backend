import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CountUpComponent } from './count-up.component';

describe('CountUpComponent', () => {
  let component: CountUpComponent;
  let fixture: ComponentFixture<CountUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CountUpComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CountUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
