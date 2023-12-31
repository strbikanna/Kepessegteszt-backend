import {Component, Input, OnInit} from '@angular/core';
import {EChartsOption} from "echarts";
import {CognitiveProfile} from "../../model/cognitive_profile.model";
import * as themeColors from "../../../assets/chart_theme/chart_colors";
import {Ability, AbilityType} from "../../model/ability.model";
import {TEXTS} from "../../utils/app.text_messages";
import {Observable} from "rxjs";

@Component({
    selector: 'app-profile-chart',
    templateUrl: './profile-chart.component.html',
    styleUrls: ['./profile-chart.component.scss']
})
export class ProfileChartComponent implements OnInit {

    loading = true;
    chartOptions!: EChartsOption
    /**
     * @param profileDataObservable: Observable<CognitiveProfile[]> - observable of the cognitive profile data
     */
    @Input({required: true}) profileDataObservable!: Observable<CognitiveProfile[]>;
    private profileData!: CognitiveProfile[];
    private filteredData: CognitiveProfile[] = [];
    private abilitiesForFiltering: Ability[] = [];
    hasHistory = false;
    text = TEXTS.cognitive_profile.chart;

    /**
     * Subscribe to input observable and initialize chart options
     */
    ngOnInit(): void {
        this.profileDataObservable.subscribe(profileData => {
            if (profileData.length > 0) {
                this.profileData = this.filterAndSortProfileData(profileData)
                this.hasHistory = true;
                this.initChartOptions(this.profileData);
                this.abilitiesForFiltering = this.abilityList(this.profileData);
            } else {
                this.hasHistory = false;
            }
            this.loading = false;
        })
    }

    /**
     * Generates filtered profile data based on the selected abilities
     * @param abilities
     */
    private filterDataByAbilities(abilities: Ability[]) {
        this.filteredData = [];
        this.profileData.forEach(profile => {
            let profileEntries = Array.from(profile.profileItems.entries())
            const constructedProfileData: CognitiveProfile = {
                timestamp: profile.timestamp,
                profileItems: new Map<Ability, number>()
            }
            profileEntries.forEach(([ability, value]) => {
                if (abilities.find(_ability => _ability.code === ability.code)) {
                    constructedProfileData.profileItems.set(ability, value)
                }
            })
            this.filteredData.push(constructedProfileData)
        })
    }

    allAbilities() {
        return this.abilityList(this.profileData)
    }

    /**
     * Handles the ability filter change event: filters data and updates chart options
     * @param ability
     */
    onAbilityFilterChange(ability: Ability) {
        if (this.abilitiesForFiltering.find(_ability => _ability.code === ability.code)) {
            this.abilitiesForFiltering = this.abilitiesForFiltering.filter(_ability => _ability.code !== ability.code)
        } else {
            this.abilitiesForFiltering.push(ability)
        }
        this.filterDataByAbilities(this.abilitiesForFiltering)
        this.initChartOptions(this.filteredData)
    }

    /**
     * Initializes chart options with profile data dates, abilities and values
     * @param data
     * @private
     */
    private initChartOptions(data: CognitiveProfile[]) {
        this.chartOptions = {
            title: {
                text: this.text.chart_title,
                left: 'center',
            },
            legend: {
                top: 'bottom',
            },
            tooltip: {},
            xAxis: {
                data: data
                    //.sort((a,b) => a.timestamp.getMilliseconds() - b.timestamp.getMilliseconds())
                    .map(profile => profile.timestamp.toLocaleString('hu-HU', {
                        year: 'numeric',
                        month: 'short',
                        day: 'numeric'
                    })),
            },
            yAxis: {},
            series: this.abilityList(data).map(ability => {
                return {
                    type: 'line',
                    name: ability.name,
                    data: this.valuesOfAbility(ability),
                }
            }),
            color: themeColors.colorSet,
        }
    }

    /**
     * Returns the profile values of the given ability in the profile data
     * @param ability
     * @private
     * @returns any[] all ability values of param ability
     */
    private valuesOfAbility(ability: Ability) {
        return this.profileData.map(profile => {
            const entry = Array.from(profile.profileItems.entries())
                .find(([_ability, value]) => _ability.code === ability.code)
            return entry ? entry[1] : null
        })
    }

    /**
     * Creates array of all distinct abilities in the profile data
     * @param profileData
     * @private
     */
    private abilityList(profileData: CognitiveProfile[]): Ability[] {
        let allAbilities: Ability[] = [];
        profileData.forEach(profile => {
            profile.profileItems.forEach((_, ability) => {
                if (!allAbilities.find(_ability => _ability.code === ability.code)) {
                    allAbilities.push(ability)
                }
            })
        })
        return allAbilities
    }

    /**
     * Filters out ENUM abilities from the profile data and sorts it by timestamp
     * @param profileData
     * @private
     */
    private filterAndSortProfileData(profileData: CognitiveProfile[]): CognitiveProfile[]{
        return profileData
            .map(profile => {
                let abilities = Array.from(profile.profileItems.keys())
                abilities.forEach(ability => {
                    if (ability.type === AbilityType.ENUM) {
                        profile.profileItems.delete(ability)
                    }
                })
                return profile
            })
            .sort((a, b) => a.timestamp.getTime() - b.timestamp.getTime());
    }

}
