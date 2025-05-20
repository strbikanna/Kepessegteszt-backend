import {Component, Input} from '@angular/core';
import {ChartComponent} from "../chart/chart.component";
import {Observable} from "rxjs";
import {Result} from "../../model/result.model";
import {ConfigItem} from "../../model/config_item.model";
import * as themeColors from "../../../assets/chart_theme/chart_colors";


@Component({
    selector: 'app-result-chart',
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
        const chartData = this.sortData(this.resultData);
        chartData.forEach(data => this.scaleData(data, this.configItems));
        const labels = this.getLabelsForData(chartData);

        this.chartOptions = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                },
            },
            toolbox: {
                right: 20,
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
                    type: 'value',
                    min: 0,
                    max: 100,
                }
            ],
            series: labels.map((label: string, index: number) => {
                if(index === labels.length / 2 || index === (labels.length -1) / 2 ){
                    return {
                        name: label,
                        type: 'bar',
                        emphasis: {
                            focus: 'series'
                        },
                        data: chartData.map((data: Result) => data.config[label]),
                        maxBarWidth: 40,
                        markPoint: {
                            data: chartData.map((data, index) => {
                                return {
                                    name: 'Success or Failure',
                                    coord: [index, 100],
                                    symbol: data.passed ? 'image://assets/icons/check_circle.svg' : 'image://assets/icons/cancel.svg',
                                    symbolSize: 40,
                                }
                            })
                        }
                    }
                }
                return {
                    name: label,
                    type: 'bar',
                    barGap: 0,
                    emphasis: {
                        focus: 'series'
                    },
                    data: chartData.map((data: Result) => data.config[label]),
                    barWidth: 40,
                }
            }),
            color: themeColors.colorSet,

        };
        this.loading = false;
    }

    private sortData(data: Result[]): Result[] {
        return data.sort((a, b) => a.timestamp.getTime() - b.timestamp.getTime());
    }

    private scaleData(data: Result, configItems: ConfigItem[]): void {
        Object.keys(data.config).forEach(configName => {
            const configItem = configItems.find(item => item.paramName === configName);
            if (configItem) {
                const value = data.config[configName];
                data.config[configName] = this.resultItemToDifficultyPercent(value, configItem.hardestValue, configItem.easiestValue);
            }
        })
        if(data.config instanceof Map) {
            data.config.forEach((value, key) => {
                const configItem = configItems.find(item => item.paramName === key);
                if (configItem) {
                    const scaledValue = this.resultItemToDifficultyPercent(value, configItem.hardestValue, configItem.easiestValue);
                    data.config.set(key, scaledValue);
                }
            })
        }
    }

    private resultItemToDifficultyPercent(value: number, hardestValue: number, easiestValue: number): number {
        if(hardestValue < easiestValue) {
            return (1 - (Math.abs(value - hardestValue)) / Math.abs(hardestValue - easiestValue)) * 100;
        }
        return (Math.abs(value - easiestValue) / Math.abs(hardestValue - easiestValue)) * 100;
    }

    private getLabelsForData(data: Result[]): string[] {
        const configs = data.map(item => Object.keys(item.config));
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
