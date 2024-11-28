import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserSearchByGroupComponent } from './user-search-by-group.component';

describe('UserSearchByGroupComponent', () => {
  let component: UserSearchByGroupComponent;
  let fixture: ComponentFixture<UserSearchByGroupComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserSearchByGroupComponent]
    });
    fixture = TestBed.createComponent(UserSearchByGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
