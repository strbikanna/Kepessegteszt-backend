import {Component, Input, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {ProfileData} from "../../model/profile_data.model";
import {Ability} from "../../model/ability.model";
import {TEXTS} from "../../text/app.text_messages";

@Component({
  selector: 'app-profile-line-chart',
  templateUrl: './profile-line-chart.component.html',
  styleUrls: ['./profile-line-chart.component.scss']
})
export class ProfileLineChartComponent implements OnInit{
    @Input({required: true}) userProfileData!: Observable<ProfileData[]>
    @Input({required: true}) groupProfileData!: Observable<ProfileData[]>

    valueMax = 1.2;
    loading = true;
    userData: ProfileData[] = [];
    groupData: ProfileData[] = [];
    text= TEXTS.cognitive_profile.comparison

    ngOnInit() {
        this.userProfileData.subscribe(data => {
            this.userData = data;
            this.setMaxValue(data);
            if(this.groupData.length > 0) this.loading = false;
        });
        this.groupProfileData.subscribe(data => {
            this.groupData = data;
            this.setMaxValue(data);
            if(this.userData.length > 0) this.loading = false;
        });
    }

    setMaxValue(data: ProfileData[]){
        this.valueMax = Math.max(this.valueMax, ...data.map(d => d.value));
    }


    getGroupDataToAbility(ability: Ability){
        return this.groupData.find(d => d.ability.code === ability.code);
    }

}
