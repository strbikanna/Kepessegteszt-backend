import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {CognitiveProfileService} from "../../../service/cognitive-profile/cognitive-profile.service";
import {CognitiveProfile} from "../../../model/cognitive_profile.model";
import {DateRange} from "../../../common/date-picker/date-picker.component";
import {TEXTS} from "../../../utils/app.text_messages";
import {User} from "../../../model/user.model";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-admin-cognitive-profile',
  templateUrl: './admin-cognitive-profile-page.component.html',
  styleUrls: ['./admin-cognitive-profile-page.component.scss']
})
export class AdminCognitiveProfilePageComponent implements  OnInit{
  text = TEXTS.cognitive_profile
  protected chosenUsername?: string;
  protected currProfileData?: CognitiveProfile;
  protected profileHistoryData: BehaviorSubject<CognitiveProfile[]> = new BehaviorSubject<CognitiveProfile[]>([]);
  protected loadingProfile = true;
  protected loadingHistory = true;

  constructor(private route: ActivatedRoute, private service: CognitiveProfileService) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
        this.chosenUsername = params.get('username') ?? undefined;
    });
    this.loadProfileData();
  }

  userSelected(user: User) {
    this.chosenUsername = user.username;
    this.loadingProfile = true;
    this.loadProfileData();
  }

  loadProfileData() {
    if (this.chosenUsername !== undefined) {
      this.service.getCurrentProfileOfOtherUser(this.chosenUsername).subscribe(profile => {
        this.currProfileData = profile;
        this.loadingProfile = false;
      });
    }
  }

  onDatesChosen(dateRange: DateRange) {
    this.loadingHistory = true;
    if (this.chosenUsername !== undefined) {
      this.service.getProfilesBetweenOfOtherUser(dateRange.start, dateRange.end, this.chosenUsername).subscribe(profiles => {
        this.profileHistoryData?.next(profiles);
        this.loadingHistory = false;
      });
    }
  }


}
