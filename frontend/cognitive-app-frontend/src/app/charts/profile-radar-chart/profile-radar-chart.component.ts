import {Component, Input, OnInit} from '@angular/core';
import {EChartsOption} from "echarts";
import {combineLatestWith, Observable} from "rxjs";
import {TEXTS} from "../../text/app.text_messages";
import {ProfileData} from "../../model/profile_data.model";
import * as themeColors from "../../../assets/chart_theme/chart_colors";

@Component({
    selector: 'app-profile-radar-chart',
    templateUrl: './profile-radar-chart.component.html',
    styleUrls: ['./profile-radar-chart.component.scss']
})
export class ProfileRadarChartComponent implements OnInit {
    chartOptions!: EChartsOption
    @Input({required: true}) profileDataObservable!: Observable<ProfileData[]>;
    @Input({required: true}) comparisonDataObservable!: Observable<ProfileData[]>;
    @Input({required: true}) comparisonTitle!: 'avg' | 'max' | 'min';
    text = TEXTS.cognitive_profile.radar_chart;
    loading = true;
    private profileData!: ProfileData[];
    private comparisonData!: ProfileData[];

    ngOnInit() {
        this.comparisonDataObservable.subscribe(data => {
            this.comparisonData = data.sort((a, b) => a.ability.code.localeCompare(b.ability.code))
            if (this.profileData) {
                this.comparisonData = data.filter(data => this.profileData.find(_data => _data.ability.code === data.ability.code))
                this.initChartOptions(this.profileData, this.comparisonData)
            }
        })
        this.profileDataObservable.subscribe((profileData) => {
            this.profileData = profileData.sort((a, b) => a.ability.code.localeCompare(b.ability.code))
            let cmpData : ProfileData[] = [];
            if(this.comparisonData && this.comparisonData.length > 0){
                cmpData = this.comparisonData.filter(data => profileData.find(_data => _data.ability.code === data.ability.code))
            }
            this.initChartOptions(this.profileData, cmpData)
        })

    }

    private initChartOptions(profileData: ProfileData[], comparisonData: ProfileData[]) {
        this.chartOptions = {
            legend: {
                data: [this.text.profile, this.getComparisonTitle()],
            },
            tooltip: {},
            radar: {
                indicator: this.getRadarIndicator(profileData),
            },
            series: [
                {
                    type: 'radar',

                    data: [
                        {
                            value: this.getRadarData(comparisonData),
                            name: this.getComparisonTitle(),
                            symbolSize: 15,
                        },
                        {
                            value: this.getRadarData(profileData),
                            name: this.text.profile,
                            areaStyle: {opacity: 0.5},
                            symbol: 'diamond',
                            symbolSize: 15,
                        },
                    ]
                }
            ],
            color: themeColors.colorSet
        }
        this.loading = false;
    }

    private getRadarIndicator(profileData: ProfileData[]) {
        const maxValue = Math.max(...profileData.map(data => data.value))
        return profileData.map(data => {
            return {name: data.ability.name, max: maxValue + 0.2}
        })
    }

    private getComparisonTitle() {
        switch (this.comparisonTitle) {
            case 'avg':
                return this.text.avg_in_group;
            case 'max':
                return this.text.max_in_group;
            case 'min':
                return this.text.min_in_group;
        }
    }

    private getRadarData(profileData: ProfileData[]) {
        return profileData.map(data => data.value)
    }
}
