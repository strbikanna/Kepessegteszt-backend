import {Component, OnInit} from '@angular/core';
import {ProfileData} from "../model/profile_data.model";
import {BehaviorSubject, Observable, of} from "rxjs";
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
    dataToCompare: BehaviorSubject<ProfileData[]> = new BehaviorSubject<ProfileData[]>([]);
    calculationTypeOptions = ['average', 'min', 'max']

    profileQueryForm = this.formBuilder.group({
        groupId: new FormControl<number | undefined>(undefined, Validators.required),
        calculationType: new FormControl<'average' | 'min' | 'max' >('average', Validators.required)
    })
    ngOnInit(): void {
        this.groups = this.service.getGroupsOfUser()
        this.userProfileData = this.service.getProfileData()
    }

    onSubmit(){
        let selectedGroupId = this.profileQueryForm.get('groupId')?.value
        if(selectedGroupId){
            this.service.getProfileDataOfGroup(
                selectedGroupId,
                this.profileQueryForm.get('calculationType')?.value ?? 'average'
            ).subscribe(
                (data) => {
                    this.dataToCompare.next(data)
                }
            )
        }
    }
    comparisonTitle: 'avg' | 'max' | 'min' = 'avg';

}
