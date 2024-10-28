import {Component, OnInit} from '@angular/core';
import {
  ProfileDataComparisonPageComponent
} from "../../student/profile-data-comparison/profile-data-comparison-page.component";
import {ProfileDataComparisonService} from "../../../service/profile-data-comparison/profile-data-comparison.service";
import {FormBuilder} from "@angular/forms";
import {AbilityService} from "../../../service/ability/ability.service";
import {User} from "../../../model/user.model";

@Component({
  selector: 'app-admin-profile-data-comparison-page',
  templateUrl: './admin-profile-data-comparison-page.component.html',
  styleUrls: ['./admin-profile-data-comparison-page.component.scss']
})
export class AdminProfileDataComparisonPageComponent extends ProfileDataComparisonPageComponent{
  constructor(service: ProfileDataComparisonService, formBuilder: FormBuilder, abilityService: AbilityService) {
    super(service, formBuilder, abilityService);
  }
  protected chosenUser?: User;

  override ngOnInit() {
    this.allAbilities = this.abilityService.getAllAbilities();
  }
  override onSubmit() {
    this.setComparisonTitle(this.profileQueryForm.get('calculationType')?.value ?? 'average')
    this.service.getProfileDataOfGroup(
        this.userFilter,
        this.profileQueryForm.get('calculationType')?.value ?? 'average',
        this.chosenUser?.username
    ).subscribe(
        (data) => {
          this.dataToCompare.next(data)
        }
    )
  }

  onUserChosen(user: User) {
    this.chosenUser = user;
    this.initDataForUser(user.username);
  }

  initDataForUser(username:string){
    this.groups = this.service.getGroupsOfOtherUser(username);
    this.userProfileData = this.service.getProfileData(username);
  }


}
