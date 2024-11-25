import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupTreeComponent } from './group-tree.component';

describe('GroupTreeComponent', () => {
  let component: GroupTreeComponent;
  let fixture: ComponentFixture<GroupTreeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GroupTreeComponent]
    });
    fixture = TestBed.createComponent(GroupTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
