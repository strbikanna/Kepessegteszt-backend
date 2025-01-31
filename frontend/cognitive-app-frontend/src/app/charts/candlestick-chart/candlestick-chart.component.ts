import {Component, Input, OnInit} from '@angular/core';
import {CandlestickChartDataModel} from "./candlestick-chart-data.model";
import {EChartsOption} from "echarts";
import {candleStickChartColors} from "../../../assets/chart_theme/chart_colors";
import {Observable} from "rxjs";

@Component({
    selector: 'app-candlestick-chart',
    templateUrl: './candlestick-chart.component.html',
    styleUrls: ['./candlestick-chart.component.scss']
})
export class CandlestickChartComponent implements OnInit {
    @Input({required: true}) data!: Observable<CandlestickChartDataModel[]>;
    protected chartOptions!: EChartsOption;
    private colors = candleStickChartColors;
    private chartData: CandlestickChartDataModel[] = [];

    ngOnInit(): void {
        this.data.subscribe((data) => {
            this.chartData = data;
            this.initChartOptions();
        });
    }

    private initChartOptions() {
        this.chartOptions = {
            xAxis: {
                type: 'category',
                data: this.chartData.map(dataItem => dataItem.category),
                axisLabel: {
                    formatter: (value: string) => {
                        return value.split(' ').join('\n');
                    }
                }
            },
            yAxis: {
                type: 'value',
                max: this.getYAxisMax(),
            },
            series: [
                {
                    type: 'candlestick',
                    data: this.chartData.map(dataItem => this.transformChartData(dataItem)),
                }
            ],
            itemStyle: {
                color: this.colors.colorGreen,
                color0: this.colors.colorRed,
                borderColor: this.colors.colorGreen,
                borderColor0: this.colors.colorRed,
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: (params: any) => {
                    const category = params[0].name as string;
                    const data = params[0].data as number[];

                    return `Képesség kód: ${category}<br/>
                            Egyéni érték: ${data[1]}<br/>
                            Átlag: ${data[2]}<br/>
                            Átlag-szórás: ${data[3]}<br/>
                            Átlag+szórás: ${data[4]}`;
                }
            }
        }
    }

    private transformChartData(dataItem: CandlestickChartDataModel): number[] {
        return [dataItem.individualValue, dataItem.mean, dataItem.mean - dataItem.deviation, dataItem.mean + dataItem.deviation];
    }

    private getYAxisMax(){
        const allValues = this.chartData.map(dataItem => this.transformChartData(dataItem)).flat();
        return (Math.max(...allValues)*1.3).toFixed(1);
    }

}