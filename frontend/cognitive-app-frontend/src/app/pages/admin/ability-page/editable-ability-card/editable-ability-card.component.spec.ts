import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditableAbilityCardComponent } from './editable-ability-card.component';

describe('EditableAbilityCardComponent', () => {
  let component: EditableAbilityCardComponent;
  let fixture: ComponentFixture<EditableAbilityCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditableAbilityCardComponent]
    });
    fixture = TestBed.createComponent(EditableAbilityCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
