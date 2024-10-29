import {Component, OnInit} from '@angular/core';
import {
  ProfileDataComparisonPageComponent
} from "../../student/profile-data-comparison/profile-data-comparison-page.component";
import {ProfileDataComparisonService} from "../../../service/profile-data-comparison/profile-data-comparison.service";
import {FormBuilder} from "@angular/forms";
import {AbilityService} from "../../../service/ability/ability.service";
import {User} from "../../../model/user.model";
import {TEXTS} from "../../../utils/app.text_messages";
import {ProfileStatistics} from "../../../model/profile-statistics.model";
import {BehaviorSubject, map, Observable} from "rxjs";
import {CandlestickChartDataModel} from "../../../charts/candlestick-chart/candlestick-chart-data.model";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";

@Component({
  selector: 'app-admin-profile-data-comparison-page',
  templateUrl: './admin-profile-data-comparison-page.component.html',
  styleUrls: ['./admin-profile-data-comparison-page.component.scss']
})
export class AdminProfileDataComparisonPageComponent extends ProfileDataComparisonPageComponent{
  constructor(service: ProfileDataComparisonService, formBuilder: FormBuilder, abilityService: AbilityService,
              private router: Router, private location: Location, private route: ActivatedRoute,
  ) {
    super(service, formBuilder, abilityService);
  }
  protected chosenUsername?: string;
  protected nameOfUser?: string;
  private profileStatisticsData: BehaviorSubject<ProfileStatistics[]> = new BehaviorSubject<ProfileStatistics[]>([]);

  userPickerText = TEXTS.cognitive_profile.user_picker

  override ngOnInit() {
    this.chosenUsername = this.route.snapshot.queryParams['username'];
    this.nameOfUser = this.route.snapshot.queryParams['name'];
    if(this.chosenUsername){
        this.initDataForUser(this.chosenUsername);
    }
    this.allAbilities = this.abilityService.getAllAbilities();
  }
  override onSubmit() {
    this.setComparisonTitle(this.profileQueryForm.get('calculationType')?.value ?? 'average')
    this.service.getProfileDataOfGroup(
        this.userFilter,
        this.profileQueryForm.get('calculationType')?.value ?? 'average',
        this.chosenUsername
    ).subscribe(
        (data) => {
          this.dataToCompare.next(data)
        }
    )
    this.service.getProfileStatisticsOfGroup(this.userFilter, this.chosenUsername!!).subscribe(data =>
        this.profileStatisticsData.next(data)
    );
  }

  onUserChosen(user: User) {
    this.chosenUsername = user.username;
    this.nameOfUser = user.firstName + ' ' + user.lastName;
    this.initDataForUser(user.username);
    this.updateUrlParams();
  }

  initDataForUser(username:string){
    this.groups = this.service.getGroupsOfOtherUser(username);
    this.userProfileData = this.service.getProfileData(username);
    this.service.getProfileStatisticsOfGroup(undefined, username).subscribe(data =>
        this.profileStatisticsData.next(data)
    );
  }

  getCandleStickData(): Observable<CandlestickChartDataModel[]> {
    return this.profileStatisticsData.pipe(
        map((data) => {
          return data.map((item) => {
            return {
              category: item.ability.name,
              mean: item.mean,
              deviation: item.deviation,
              individualValue: item.individualValue
            }
          })
        })
    );
  }

  private updateUrlParams() {
    const params = {
      username: this.chosenUsername,
      name: this.nameOfUser,
    };
    const urlTree = this.router.createUrlTree(['/profile-compare-admin'], {
      relativeTo: this.route,
      queryParams: params,
      queryParamsHandling: 'merge',
    });
    this.location.go(urlTree.toString());
  }


}
