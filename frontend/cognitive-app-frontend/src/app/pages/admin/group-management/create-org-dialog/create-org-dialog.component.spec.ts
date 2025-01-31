import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateOrgDialogComponent } from './create-org-dialog.component';

describe('CreateOrgDialogComponent', () => {
  let component: CreateOrgDialogComponent;
  let fixture: ComponentFixture<CreateOrgDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateOrgDialogComponent]
    });
    fixture = TestBed.createComponent(CreateOrgDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
