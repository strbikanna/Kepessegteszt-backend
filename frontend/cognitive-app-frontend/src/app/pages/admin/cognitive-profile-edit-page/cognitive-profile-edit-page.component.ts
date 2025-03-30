import {Component, OnInit} from '@angular/core';
import {ProfileData} from "../../../model/profile/profile_data.model";
import {Ability, AbilityType} from "../../../model/ability.model";
import {AbilityService} from "../../../service/ability/ability.service";
import {CognitiveProfileService} from "../../../service/cognitive-profile/cognitive-profile.service";
import {FormArray, FormBuilder, Validators} from "@angular/forms";
import {AuthUser} from "../../../model/user/user-contacts.model";
import {TEXTS} from "../../../text/app.text_messages";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";

@Component({
    selector: 'app-cognitive-profile-edit-page',
    templateUrl: './cognitive-profile-edit-page.component.html',
    styleUrls: ['./cognitive-profile-edit-page.component.scss']
})
export class CognitiveProfileEditPageComponent implements OnInit {

    currentFloatProfile: ProfileData[] = []
    currentEnumProfile: ProfileData[] = []
    allAbilities: Ability[] = []
    selectedUser: {username: string, firstName: string, lastName: string} | undefined
    text = TEXTS.cognitive_profile.profile_edit

    constructor(
        private abilityService: AbilityService,
        private profileService: CognitiveProfileService,
        private fb: FormBuilder,
        private router: Router,
        private location: Location,
        private route: ActivatedRoute,
    ) { }

    ngOnInit() {
        this.abilityService.getAllAbilities().subscribe(data => {
            this.allAbilities = data
        });
        const username = this.route.snapshot.queryParams['username'];
        if(username){
            this.selectedUser = {
                username: username,
                firstName: this.route.snapshot.queryParams['firstName'],
                lastName: this.route.snapshot.queryParams['lastName']
            }
            this.onUserSelected(this.selectedUser)
        }

    }

    floatProfileFormItems: FormArray = this.fb.array([])
    floatProfileForm = this.fb.group({
        items: this.floatProfileFormItems
    })
    enumProfileFormItems: FormArray = this.fb.array([])
    enumProfileForm = this.fb.group({
        items: this.enumProfileFormItems
    })

    onUserSelected(user: {username: string, firstName: string, lastName: string}) {
        this.selectedUser = user
        this.updateUrlParams()
        this.profileService.getCurrentProfileOfOtherUser(user.username).subscribe(data => {
            this.setProfileData(data)
            this.initProfileForms()
        });
    }

    private initProfileForms() {
        this.currentFloatProfile.forEach(profile => {
            this.floatProfileFormItems.push(this.fb.group({
                ability: [profile.ability, Validators.required],
                value: profile.value,
                accuracy: [profile.accuracy, Validators.compose([Validators.min(0.0), Validators.max(1.0)])]
            }))
        })
        this.currentEnumProfile.forEach(profile => {
            this.enumProfileFormItems.push(this.fb.group({
                ability: [profile.ability, Validators.required],
                value: profile.value,
                accuracy: [profile.accuracy, Validators.compose([Validators.min(0.0), Validators.max(1.0)])]
            }))
        })
        this.allAbilities.forEach(ability => {
           if(ability.type === AbilityType.FLOAT){
               if(!this.currentFloatProfile.find(profile => profile.ability.code === ability.code)){
                   this.floatProfileFormItems.push(this.fb.group({
                       ability: [ability, Validators.required],
                       value: undefined,
                       accuracy: [0, Validators.compose([Validators.min(0.0), Validators.max(1.0)])]
                   }))
               }
              } else if(ability.type === AbilityType.ENUM){
                    if(!this.currentEnumProfile.find(profile => profile.ability.code === ability.code)){
                        this.enumProfileFormItems.push(this.fb.group({
                            ability: [ability, Validators.required],
                            value: undefined,
                            accuracy: [0, Validators.compose([Validators.min(0.0), Validators.max(1.0)])]
                        }))
                    }
                }
        });
    }

    private setProfileData(data: ProfileData[]){
        this.currentFloatProfile = data.filter(profile => profile.ability.type === AbilityType.FLOAT)
        this.currentEnumProfile = data.filter(profile => profile.ability.type === AbilityType.ENUM)
    }

    getAbilityName(ability: Ability): string {
        return ability.name
    }

    saveProfile() {
        if(!this.selectedUser) return;
        this.profileService.updateCurrentProfile([...this.floatProfileFormItems.value, ...this.enumProfileFormItems.value], this.selectedUser?.username).subscribe(data => {
            this.setProfileData(data)
            this.initProfileForms()
        })
    }

    updateUrlParams() {
        const params = {
            username: this.selectedUser?.username,
            firstName: this.selectedUser?.firstName,
            lastName: this.selectedUser?.lastName
        };
        const urlTree = this.router.createUrlTree(['/cognitive-profile-edit'], {
            relativeTo: this.route,
            queryParams: params,
            queryParamsHandling: 'merge',
        });
        this.location.go(urlTree.toString());
    }


}
