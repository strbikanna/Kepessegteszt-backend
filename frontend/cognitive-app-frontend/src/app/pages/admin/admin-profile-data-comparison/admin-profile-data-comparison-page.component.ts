import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {
    ProfileDataComparisonPageComponent
} from "../../student/profile-data-comparison/profile-data-comparison-page.component";
import {ProfileDataComparisonService} from "../../../service/profile-data-comparison/profile-data-comparison.service";
import {FormBuilder} from "@angular/forms";
import {AbilityService} from "../../../service/ability/ability.service";
import {User} from "../../../model/user.model";
import {TEXTS} from "../../../text/app.text_messages";
import {ProfileStatistics} from "../../../model/profile-statistics.model";
import {BehaviorSubject, map, Observable} from "rxjs";
import {CandlestickChartDataModel} from "../../../charts/candlestick-chart/candlestick-chart-data.model";
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from "@angular/common";
import {ProfileDescription} from "../../../model/ProfileDescription";
import {imagePaths} from "../../../utils/app.image_resources";
import {UserInfo} from "../../../auth/userInfo";

@Component({
    selector: 'app-admin-profile-data-comparison-page',
    templateUrl: './admin-profile-data-comparison-page.component.html',
    styleUrls: ['./admin-profile-data-comparison-page.component.scss']
})
export class AdminProfileDataComparisonPageComponent extends ProfileDataComparisonPageComponent {
    constructor(service: ProfileDataComparisonService, formBuilder: FormBuilder, abilityService: AbilityService,
                private router: Router, private location: Location, private route: ActivatedRoute,
    ) {
        super(service, formBuilder, abilityService);
    }

    protected chosenUsername?: string;
    protected nameOfUser?: string;
    protected comparisonDescription?: ProfileDescription;
    protected descriptionLoading = true;
    protected prompt?: string;
    private profileStatisticsData: BehaviorSubject<ProfileStatistics[]> = new BehaviorSubject<ProfileStatistics[]>([]);

    userPickerText = TEXTS.cognitive_profile.user_picker
    llmText = TEXTS.cognitive_profile.llm

    override ngOnInit() {
        this.chosenUsername = this.route.snapshot.queryParams['username'];
        this.nameOfUser = this.route.snapshot.queryParams['name'];
        if (this.chosenUsername) {
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
        if(this.canSeeStatistics()) {
            this.service.getProfileStatisticsOfGroup(this.userFilter, this.chosenUsername!!).subscribe(data =>
                this.profileStatisticsData.next(data)
            );
        }
        this.service.getComparisonDescription(this.userFilter, this.prompt, this.chosenUsername).subscribe(description => {
            this.comparisonDescription = description;
            this.descriptionLoading = false;
        })
    }

    onUserChosen(user: User) {
        this.chosenUsername = user.username;
        this.nameOfUser = user.firstName + ' ' + user.lastName;
        this.initDataForUser(user.username);
        this.updateUrlParams();
    }

    initDataForUser(username: string) {
        this.groups = this.service.getGroupsOfOtherUser(username);
        this.userProfileData = this.service.getProfileData(username);
        if(this.canSeeStatistics()) {
            this.service.getProfileStatisticsOfGroup(undefined, username).subscribe(data => {
                this.profileStatisticsData.next(data);
            });
        }
        this.service.getComparisonDescription(undefined, undefined, username).subscribe(description => {
            this.comparisonDescription = description;
            this.descriptionLoading = false;
        });
        this.onSubmit();
    }

    canSeeStatistics(): boolean {
        return UserInfo.canSeeCognitiveProfileStatistics();
    }
    canPrompt(): boolean {
        return UserInfo.canSeePromptLlm();
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

    onSubmitPrompt() {
        this.service.getComparisonDescription(this.userFilter, this.prompt, this.chosenUsername).subscribe(description => {
            this.comparisonDescription = description;
            this.descriptionLoading = false;
        });
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
