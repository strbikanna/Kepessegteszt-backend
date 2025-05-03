import {Component, Input, OnInit} from '@angular/core';
import {ChartComponent} from "../chart/chart.component";
import {Observable} from "rxjs";
import {Result} from "../../model/result.model";
import {ConfigItem} from "../../model/config_item.model";
import {NgxEchartsModule} from "ngx-echarts";

@Component({
    selector: 'app-result-chart',
    standalone: true,
    imports: [
        NgxEchartsModule
    ],
    templateUrl: './result-chart.component.html',
    styleUrl: './result-chart.component.scss'
})
export class ResultChartComponent extends ChartComponent {

    @Input({required: true}) resultDataObservable!: Observable<Result[]>;
    @Input({required: true}) configItemsObservable!: Observable<ConfigItem[]>;

    private resultData!: Result[];
    private configItems!: ConfigItem[];

    override initChartOptions(): void {
        this.observeConfigItems()
        this.observeResultData()
    }

    private setChartOptions(): void {
        console.log("Result data: ", this.resultData);
        const chartData = this.sortData(this.resultData);
        chartData.forEach(data => data.config = this.scaleData(data, this.configItems));
        console.log("Chart data: ", chartData);
        const labels = this.getLabelsForData(chartData);
        console.log("Labels: ", labels);

        this.chartOptions = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            legend: {
                data: labels,
            },
            xAxis: [
                {
                    type: 'category',
                    data: chartData.map(data => data.timestamp.toLocaleDateString()),
                }
            ],
            yAxis: [
                {
                    type: 'value'
                }
            ],
            series: labels.map((label: string) => {
                return {
                    name: label,
                    type: 'bar',
                    barGap: 0,
                    emphasis: {
                        focus: 'series'
                    },
                    data: chartData.map((data: Result) => data.config.get(label)),
                }
            }),

        };
        this.loading = false;
    }

    private sortData(data: Result[]): Result[] {
        return data.sort((a, b) => a.timestamp.getTime() - b.timestamp.getTime());
    }

    private scaleData(data: Result, configItems: ConfigItem[]): Map<string, number> {
        const scaledConfig: Map<string, number> = new Map<string, number>();
        Object.keys(data.config).forEach(configName => {
            const configItem = configItems.find(item => item.paramName === configName);
            if (configItem) {
                const value = data.config[configName];
                const scaledValue = this.resultItemToDifficultyPercent(value, configItem.hardestValue, configItem.easiestValue);
                scaledConfig.set(configName, scaledValue);
            }
        })
        return scaledConfig;
    }

    private resultItemToDifficultyPercent(value: number, hardestValue: number, easiestValue: number): number {
        return Math.abs(value - easiestValue) / Math.abs(hardestValue - easiestValue) * 100;
    }

    private getLabelsForData(data: Result[]): string[] {
        const configs = data.map(item => Array.from((item.config as Map<string, number>).keys()));
        const labels: string[] = [];
        configs.forEach(configKeys => {
            configKeys.forEach(label => {
                if (!labels.includes(label)) {
                    labels.push(label);
                }
            })
        })
        return labels;
    }

    private observeConfigItems(): void {
        this.configItemsObservable.subscribe(configItems => {
            this.loading = true;
            this.configItems = configItems;
            if (this.resultData) {
                this.setChartOptions()
            }
        })
    }

    private observeResultData(): void {
        this.resultDataObservable.subscribe(resultData => {
            this.loading = true;
            this.resultData = resultData;
            if (this.configItems) {
                this.setChartOptions()
            }
        })
    }

}
