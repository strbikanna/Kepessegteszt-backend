import { Component, HostListener, OnInit} from '@angular/core';
import {CognitiveProfileService} from "./service/cognitive-profile.service";
import {CognitiveProfile} from "../model/cognitive_profile.model";
import {BehaviorSubject, Observable} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TEXTS} from "../utils/app.text_messages";
import {UserInfo} from "../auth/userInfo";
import {Role} from "../utils/constants";
import {User} from "../model/user.model";

@Component({
    selector: 'app-cognitive-profile',
    templateUrl: './cognitive-profile.component.html',
    styleUrls: ['./cognitive-profile.component.scss']
})
export class CognitiveProfileComponent implements OnInit {

    currentProfileData!: CognitiveProfile
    profileDataHistory: BehaviorSubject<CognitiveProfile[]> = new BehaviorSubject<CognitiveProfile[]>([])
    errorText = TEXTS.error
    text = TEXTS.cognitive_profile
    dateForm = new FormGroup({
        start: new FormControl<Date | null>(null),
        end: new FormControl<Date | null>(null),
    })
    userContactForm = new FormControl<User | null>(null, Validators.required)
    contacts!: Observable<User[]>
    chosenUser: User | undefined = undefined
    isExpandMoreButtonVisible = true
    loading = true
    loadingProfile = true
    isInspectorUser = false

    constructor(private service: CognitiveProfileService) {
        UserInfo.currentUser.roles
            .some(role => role === Role.PARENT || role === Role.TEACHER || role === Role.SCIENTIST) ? this.isInspectorUser = true : this.isInspectorUser = false
    }

    ngOnInit(): void {
        if (this.isInspectorUser) {
            this.contacts = this.service.getContacts()
            return
        }
        this.service.getCurrentProfile().subscribe(profile => {
            this.currentProfileData = profile
            this.loadingProfile = false
        })
        this.service.getLatestProfiles().subscribe(profiles => {
            this.profileDataHistory.next(profiles)
            this.loading = false
        })
    }

    onDownClicked() {
        const chartPosition = document.getElementById('chart')?.offsetTop
        window.scroll({top: chartPosition, behavior: 'smooth'})
    }

    onTopClicked() {
        window.scroll({top: 0, behavior: 'smooth'})
    }

    @HostListener('window:scroll', ['$event'])
    onScroll($event: Event) {
        const chartPosition = document.getElementById('chart')?.offsetTop! - document.getElementById('chart')!.offsetHeight * 0.5
        this.isExpandMoreButtonVisible = chartPosition >= window.scrollY;
    }

    onUserChosen() {
        if (this.userContactForm.value == null) {
            return
        } else {
            this.chosenUser = this.userContactForm.value
            this.service.getCurrentProfileOfOtherUser(this.chosenUser.username).subscribe(profile => {
                this.currentProfileData = profile
                this.loadingProfile = false
            })
            this.service.getLatestProfilesOfOtherUser(this.chosenUser.username).subscribe(profiles => {
                this.profileDataHistory.next(profiles)
                this.loading = false
            })
        }
    }

    onDateChosen() {
        if (this.dateForm.controls.start.value == null || this.dateForm.controls.end.value == null) {
            return
        }
        const profilesBetween = this.isInspectorUser && this.chosenUser ?
            this.service.getProfilesBetweenOfOtherUser(this.dateForm.controls.start.value, this.dateForm.controls.end.value, this.chosenUser.username) :
            this.service.getProfilesBetween(this.dateForm.controls.start.value, this.dateForm.controls.end.value)

        profilesBetween.subscribe(profiles => {
                this.profileDataHistory.next(profiles)
                this.currentProfileData = profiles[profiles.length - 1]
            }
        )
    }

}
