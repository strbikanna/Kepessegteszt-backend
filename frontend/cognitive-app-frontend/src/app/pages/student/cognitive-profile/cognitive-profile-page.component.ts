import { Component, HostListener, OnInit} from '@angular/core';
import {CognitiveProfileService} from "../../../service/cognitive-profile/cognitive-profile.service";
import {CognitiveProfile} from "../../../model/cognitive_profile.model";
import {BehaviorSubject, Observable} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TEXTS} from "../../../utils/app.text_messages";
import {UserInfo} from "../../../auth/userInfo";
import {Role} from "../../../utils/constants";
import {User} from "../../../model/user.model";
import {DateRange} from "../../../common/date-picker/date-picker.component";

@Component({
    selector: 'app-cognitive-profile',
    templateUrl: './cognitive-profile-page.component.html',
    styleUrls: ['./cognitive-profile-page.component.scss']
})
export class CognitiveProfilePageComponent implements OnInit {

    currentProfileData!: CognitiveProfile
    profileDescription: string = ''
    profileDataHistory: BehaviorSubject<CognitiveProfile[]> = new BehaviorSubject<CognitiveProfile[]>([])
    text = TEXTS.cognitive_profile
    contacts!: Observable<User[]>
    loading = true
    loadingProfile = true
    loadingDescription = true

    constructor(private service: CognitiveProfileService) {}

    ngOnInit(): void {
        this.service.getCurrentProfile().subscribe(profile => {
            this.currentProfileData = profile
            this.loadingProfile = false
        })
        this.service.getProfileDescription().subscribe(description => {
            this.profileDescription = description.abilitiesAsText
            this.loadingDescription = false
        })
        this.service.getLatestProfiles().subscribe(profiles => {
            this.profileDataHistory.next(profiles)
            this.loading = false
        })
    }

    onDateChosen(dateRange: DateRange) {
        const profilesBetween = this.service.getProfilesBetween(dateRange.start, dateRange.end)

        profilesBetween.subscribe(profiles => {
                this.profileDataHistory.next(profiles)
                this.currentProfileData = profiles[profiles.length - 1]
            }
        )
    }

}
