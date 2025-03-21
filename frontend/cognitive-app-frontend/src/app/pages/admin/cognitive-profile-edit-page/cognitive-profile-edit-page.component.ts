import {Component, OnInit} from '@angular/core';
import {ProfileData} from "../../../model/profile/profile_data.model";
import {Ability, AbilityType} from "../../../model/ability.model";
import {AbilityService} from "../../../service/ability/ability.service";
import {CognitiveProfileService} from "../../../service/cognitive-profile/cognitive-profile.service";
import {FormArray, FormBuilder, Validators} from "@angular/forms";
import {AuthUser} from "../../../model/user/user-contacts.model";
import {TEXTS} from "../../../text/app.text_messages";

@Component({
    selector: 'app-cognitive-profile-edit-page',
    templateUrl: './cognitive-profile-edit-page.component.html',
    styleUrls: ['./cognitive-profile-edit-page.component.scss']
})
export class CognitiveProfileEditPageComponent implements OnInit {

    currentFloatProfile: ProfileData[] = []
    currentEnumProfile: ProfileData[] = []
    allAbilities: Ability[] = []
    selectedUser: AuthUser|undefined
    text = TEXTS.cognitive_profile.profile_edit

    constructor(private abilityService: AbilityService, private profileService: CognitiveProfileService, private fb: FormBuilder) { }

    ngOnInit() {
        this.abilityService.getAllAbilities().subscribe(data => {
            this.allAbilities = data
        });

    }

    floatProfileFormItems: FormArray = this.fb.array([])
    floatProfileForm = this.fb.group({
        items: this.floatProfileFormItems
    })
    enumProfileFormItems: FormArray = this.fb.array([])
    enumProfileForm = this.fb.group({
        items: this.enumProfileFormItems
    })

    onUserSelected(user: AuthUser){
        this.selectedUser = user
        this.profileService.getCurrentProfileOfOtherUser(user.username).subscribe(data => {
            this.currentFloatProfile = data.filter(profile => profile.ability.type === AbilityType.FLOAT)
            this.currentEnumProfile = data.filter(profile => profile.ability.type === AbilityType.ENUM)
            this.initProfileForms()
        });
    }

    private initProfileForms() {
        this.currentFloatProfile.forEach(profile => {
            this.floatProfileFormItems.push(this.fb.group({
                ability: [profile.ability.name, Validators.required],
                value: profile.value,
                accuracy: [profile.accuracy, Validators.compose([Validators.min(0.0), Validators.max(1.0)])]
            }))
        })
        this.currentEnumProfile.forEach(profile => {
            this.enumProfileFormItems.push(this.fb.group({
                ability: [profile.ability.name, Validators.required],
                value: profile.value,
                accuracy: [profile.accuracy, Validators.compose([Validators.min(0.0), Validators.max(1.0)])]
            }))
        })
        this.allAbilities.forEach(ability => {
           if(ability.type === AbilityType.FLOAT){
               if(!this.currentFloatProfile.find(profile => profile.ability.code === ability.code)){
                   this.floatProfileFormItems.push(this.fb.group({
                       ability: [ability.name, Validators.required],
                       value: undefined,
                       accuracy: [0, Validators.compose([Validators.min(0.0), Validators.max(1.0)])]
                   }))
               }
              } else if(ability.type === AbilityType.ENUM){
                    if(!this.currentEnumProfile.find(profile => profile.ability.code === ability.code)){
                        this.enumProfileFormItems.push(this.fb.group({
                            ability: [ability.name, Validators.required],
                            value: undefined,
                            accuracy: [0, Validators.compose([Validators.min(0.0), Validators.max(1.0)])]
                        }))
                    }
                }
        });
    }

    saveProfile() {
        this.profileService.updateCurrentProfile([...this.floatProfileFormItems.value, ...this.enumProfileFormItems.value]).subscribe(data => {
            console.log(data)
        })
    }


}
