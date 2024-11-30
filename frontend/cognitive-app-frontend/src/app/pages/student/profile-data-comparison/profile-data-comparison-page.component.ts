import {Component, OnInit} from '@angular/core';
import {ProfileData} from "../../../model/profile_data.model";
import {BehaviorSubject, Observable, of} from "rxjs";
import {ProfileDataComparisonService} from "../../../service/profile-data-comparison/profile-data-comparison.service";
import {UserGroup} from "../../../model/user_group.model";
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {TEXTS} from "../../../utils/app.text_messages";
import {AbilityService} from "../../../service/ability/ability.service";
import {Ability} from "../../../model/ability.model";
import {UserFilter} from "../../../common/user-filter/user-filter.model";
import {imagePaths} from "../../../utils/app.image_resources";

@Component({
    selector: 'app-profile-data-comparison',
    templateUrl: './profile-data-comparison-page.component.html',
    styleUrls: ['./profile-data-comparison-page.component.scss']
})
export class ProfileDataComparisonPageComponent implements OnInit {
    constructor(protected service: ProfileDataComparisonService, protected formBuilder: FormBuilder, protected abilityService: AbilityService) {
    }

    text = TEXTS.cognitive_profile.comparison
    groups: Observable<UserGroup[]> = of([]);
    userProfileData: Observable<ProfileData[]> = of([]);
    allAbilities: Observable<Ability[]> = of([]);
    dataToCompare: BehaviorSubject<ProfileData[]> = new BehaviorSubject<ProfileData[]>([]);
    calculationTypeOptions = ['average', 'min', 'max']

    profileQueryForm = this.formBuilder.group({
        calculationType: new FormControl<'average' | 'min' | 'max'>('average', Validators.required)
    })

    protected userFilter?: UserFilter
    filterExpanded: boolean = false

    ngOnInit(): void {
        this.groups = this.service.getGroupsOfUser()
        this.userProfileData = this.service.getProfileData()
        this.allAbilities = this.abilityService.getAllAbilities()
    }

    setUserFilter(userFilter: UserFilter) {
        this.filterExpanded = false
        this.userFilter = userFilter
        this.onSubmit()
    }

    onSubmit() {
        this.setComparisonTitle(this.profileQueryForm.get('calculationType')?.value ?? 'average')
        this.service.getProfileDataOfGroup(
            this.userFilter,
            this.profileQueryForm.get('calculationType')?.value ?? 'average'
        ).subscribe(
            (data) => {
                this.dataToCompare.next(data)
            }
        )
    }

    protected setComparisonTitle(title: 'average' | 'max' | 'min') {
        if(title==='average')  {
            this.comparisonTitle = 'avg'
        }else{
            this.comparisonTitle = title
        }
    }

    comparisonTitle: 'avg' | 'max' | 'min' = 'avg';

    protected readonly imagePaths = imagePaths;
}
