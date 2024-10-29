import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {CognitiveProfileService} from "../../../service/cognitive-profile/cognitive-profile.service";
import {CognitiveProfile} from "../../../model/cognitive_profile.model";
import {DateRange} from "../../../common/date-picker/date-picker.component";
import {TEXTS} from "../../../utils/app.text_messages";
import {User} from "../../../model/user.model";
import {BehaviorSubject} from "rxjs";
import {Location} from "@angular/common";

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

  constructor(
      private router: Router,
      private location: Location,
      private route: ActivatedRoute,
      private service: CognitiveProfileService,
  ) {}

  ngOnInit() {
    this.chosenUsername = this.route.snapshot.queryParams['username'];
    this.loadProfileData();
  }

  userSelected(user: User) {
    this.chosenUsername = user.username;
    this.loadingProfile = true;
    this.loadProfileData();
    this.updateUrlParams();
  }

  loadProfileData() {
    if (this.chosenUsername !== undefined) {
      this.service.getCurrentProfileOfOtherUser(this.chosenUsername).subscribe(profile => {
        this.currProfileData = profile;
        this.loadingProfile = false;
      });
      this.service.getLatestProfilesOfOtherUser(this.chosenUsername).subscribe(profiles => {
        this.profileHistoryData?.next(profiles);
        this.loadingHistory = false
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

  updateUrlParams() {
    const params = {
        username: this.chosenUsername
    };
    const urlTree = this.router.createUrlTree(['/cognitive-profile-admin'], {
      relativeTo: this.route,
      queryParams: params,
      queryParamsHandling: 'merge',
    });
    this.location.go(urlTree.toString());
  }


}
