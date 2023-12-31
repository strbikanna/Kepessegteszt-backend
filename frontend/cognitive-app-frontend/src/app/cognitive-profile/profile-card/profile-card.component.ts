import {Component, Input, OnInit} from '@angular/core';
import {CognitiveProfile} from "../../model/cognitive_profile.model";
import {TEXTS} from "../../utils/app.text_messages";

@Component({
    selector: 'app-profile-card',
    templateUrl: './profile-card.component.html',
    styleUrls: ['./profile-card.component.scss']
})
export class ProfileCardComponent implements OnInit {
    /**
     * User's cognitive profile data to be displayed
     */
    @Input({required: true}) profileData!: CognitiveProfile;
    text = TEXTS.cognitive_profile.card
    hasData = false

    ngOnInit(): void {
        if (this.profileData && this.profileData.profileItems && this.profileData.profileItems.size > 0) {
            this.hasData = true
        }
    }

    currentDisplayDate(): string {
        if (this.profileData && this.profileData.timestamp) {
            return this.profileData.timestamp.toLocaleDateString(('hu-HU'), {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            })
        }
        return ''
    }

    /**
     * Map profile data entries to an array for easier iteration
     */
    profileDataEntries() {
        if (this.profileData && this.profileData.profileItems) {
            return Array.from(this.profileData.profileItems.entries())
        }
        return []
    }

}
