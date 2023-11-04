import {Component, HostListener, OnInit} from '@angular/core';
import {CognitiveProfileService} from "./service/cognitive-profile.service";
import {CognitiveProfile} from "../model/cognitive_profile.model";
import {BehaviorSubject, Observable} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {TEXTS} from "../utils/app.text_messages";

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
    isExpandMoreButtonVisible = true
    loading = true

    constructor(private service: CognitiveProfileService) {
    }

    ngOnInit(): void {
        this.service.getCurrentProfile().subscribe(profile =>
            this.currentProfileData = profile
        )
        this.service.getLatestProfiles().subscribe(profiles => {
            this.profileDataHistory.next(profiles)
            this.loading = false
        })

    }

    onDownClicked() {
        const chartPosition = document.getElementById('chart')?.offsetTop
        window.scroll({top: chartPosition, behavior: 'smooth'})
    }
    onTopClicked(){
        window.scroll({top: 0, behavior: 'smooth'})
    }

    @HostListener('window:scroll', ['$event'])
    onScroll($event: Event) {
        const chartPosition = document.getElementById('chart')?.offsetTop! - document.getElementById('chart')!.offsetHeight * 0.9
        if (chartPosition < window.scrollY) {
            this.isExpandMoreButtonVisible = false
        } else {
            this.isExpandMoreButtonVisible = true
        }
    }

    onDateChosen() {
        if (this.dateForm.value.start == null || this.dateForm.value.end == null) {
            return
        }
        const profilesBetween = this.service.getProfilesBetween(this.dateForm.value.start, this.dateForm.value.end)
        profilesBetween.subscribe(profiles => {
                this.profileDataHistory.next(profiles)
                this.currentProfileData = profiles[profiles.length - 1]
            }
        )
    }

}
