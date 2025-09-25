import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecialSettingsFormComponent } from './special-settings-form.component';

describe('SpecialSettingsFormComponent', () => {
  let component: SpecialSettingsFormComponent;
  let fixture: ComponentFixture<SpecialSettingsFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpecialSettingsFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SpecialSettingsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
