import {Component, Input, OnInit} from '@angular/core';
import {CognitiveProfile} from "../../model/cognitive_profile.model";
import {TEXTS} from "../../utils/app.text_messages";
import {Observable, tap} from "rxjs";

@Component({
    selector: 'app-profile-card',
    templateUrl: './profile-card.component.html',
    styleUrls: ['./profile-card.component.scss']
})
export class ProfileCardComponent implements OnInit {
    @Input({required: true}) profileData!: CognitiveProfile;
    text = TEXTS.cognitive_profile
    hasData = false
    ngOnInit(): void {
            if(this.profileData && this.profileData.profileItems.size > 0) {
                this.hasData = true
            }
    }
    currentDisplayDate(): string{
        return this.profileData.timestamp.toLocaleDateString(('hu-HU'), {year: 'numeric', month: 'short', day: 'numeric'})
    }
    profileDataEntries(){
        return Array.from(this.profileData.profileItems.entries())
    }

}
