import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigItemFormComponent } from './config-item-form.component';

describe('ConfigItemFormComponent', () => {
  let component: ConfigItemFormComponent;
  let fixture: ComponentFixture<ConfigItemFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfigItemFormComponent]
    });
    fixture = TestBed.createComponent(ConfigItemFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
