import {Component, OnInit} from '@angular/core';
import {ProfileData} from "../model/profile_data.model";
import {Observable, of} from "rxjs";
import {AbilityType} from "../model/ability.model";
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
        console.log('init')
        this.groups = this.service.getGroupsOfUser()
        this.userProfileData = this.service.getProfileData()
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


    profileData: Observable<ProfileData[]> = of(
        [
            {
                ability: {
                    code: '1',
                    name: 'Ability 1',
                    description: 'Description 1',
                    type: AbilityType.FLOAT
                },
                value: 1.0
            },
            {
                ability: {
                    code: '2',
                    name: 'Ability 2',
                    description: 'Description 2',
                    type: AbilityType.FLOAT                },
                value: 0.8
            },
            {
                ability: {
                    code: '3',
                    name: 'Ability 3',
                    description: 'Description 3',
                    type: AbilityType.FLOAT
                },
                value: 1.6
            }
        ]
    )
    comparisonData: Observable<ProfileData[]> = of(
        [
            {
                ability: {
                    code: '1',
                    name: 'Ability 1',
                    description: 'Description 1',
                    type: AbilityType.FLOAT
                },
                value: 0.8
            },
            {
                ability: {
                    code: '2',
                    name: 'Ability 2',
                    description: 'Description 2',
                    type: AbilityType.FLOAT
                },
                value: 0.7
            },
            {
                ability: {
                    code: '3',
                    name: 'Ability 3',
                    description: 'Description 3',
                    type: AbilityType.FLOAT
                },
                value: 1.5
            }
        ]
    )
    comparisonTitle: 'avg' | 'max' | 'min' = 'avg';

}
