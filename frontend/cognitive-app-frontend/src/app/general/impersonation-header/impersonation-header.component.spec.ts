import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpersonationHeaderComponent } from './impersonation-header.component';

describe('ImpersonationHeaderComponent', () => {
  let component: ImpersonationHeaderComponent;
  let fixture: ComponentFixture<ImpersonationHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImpersonationHeaderComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ImpersonationHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
