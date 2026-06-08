import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LongWaitLoadingComponent } from './long-wait-loading.component';

describe('LongWaitLoadingComponent', () => {
  let component: LongWaitLoadingComponent;
  let fixture: ComponentFixture<LongWaitLoadingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LongWaitLoadingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LongWaitLoadingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
