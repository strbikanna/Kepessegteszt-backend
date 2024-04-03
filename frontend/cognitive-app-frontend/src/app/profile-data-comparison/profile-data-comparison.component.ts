import {Component, OnInit} from '@angular/core';
import {ProfileData} from "../model/profile_data.model";
import {Observable, of} from "rxjs";
import {ProfileDataComparisonService} from "./profile-data-comparison.service";
import {UserGroup} from "../model/user_group.model";
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {TEXTS} from "../utils/app.text_messages";

@Component({
    selector: 'app-profile-data-comparison',
    templateUrl: './profile-data-comparison.component.html',
    styleUrls: ['./profile-data-comparison.component.scss']
})
export class ProfileDataComparisonComponent implements OnInit{
    constructor(private service: ProfileDataComparisonService, private formBuilder: FormBuilder) { }
    text = TEXTS.cognitive_profile.comparison
    groups: Observable<UserGroup[]> = of([]);
    userProfileData: Observable<ProfileData[]> = of([]);
    dataToCompare: Observable<ProfileData[]> = of([]);
    calculationTypeOptions = ['average', 'min', 'max']

    profileQueryForm = this.formBuilder.group({
        groupId: new FormControl<number | undefined>(undefined, Validators.required),
        calculationType: new FormControl<'average' | 'min' | 'max' >('average', Validators.required)
    })
    ngOnInit(): void {
        this.groups = this.service.getGroupsOfUser()
        this.userProfileData = this.service.getProfileData()
        this.dataToCompare = this.service.getProfileDataOfGroup(2, 'average')
    }

    onSubmit(){
        let selectedGroupId = this.profileQueryForm.get('groupId')?.value
        if(selectedGroupId){
            this.dataToCompare = this.service.getProfileDataOfGroup(
                selectedGroupId,
                this.profileQueryForm.get('calculationType')?.value ?? 'average'
            )
        }
    }
    comparisonTitle: 'avg' | 'max' | 'min' = 'avg';

}
