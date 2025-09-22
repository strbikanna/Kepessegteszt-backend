import {Component, Input, OnInit} from '@angular/core';
import {CognitiveProfile} from "../../model/cognitive_profile.model";
import {TEXTS} from "../../text/app.text_messages";
import {GenericProfileData} from "../../model/profile/profile_data.model";

@Component({
    selector: 'app-profile-card',
    templateUrl: './profile-card.component.html',
    styleUrls: ['./profile-card.component.scss']
})
export class ProfileCardComponent implements OnInit {
    /**
     * User's cognitive profile data to be displayed
     */
    @Input({required: true}) profileData!: GenericProfileData[];
    @Input({required: false}) timestamp: Date = new Date()
    text = TEXTS.cognitive_profile.card
    hasData = false

    ngOnInit(): void {
        if (this.profileData && this.profileData.length > 0) {
            this.hasData = true
        }
    }

    currentDisplayDate(): string {
        if (this.profileData) {
            return this.timestamp.toLocaleDateString(('hu-HU'), {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            })
        }
        return ''
    }

}
