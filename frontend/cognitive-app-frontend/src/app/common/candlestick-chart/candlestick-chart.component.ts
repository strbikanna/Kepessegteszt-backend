import {Component, Input, OnInit} from '@angular/core';
import {CandlestickChartDataModel} from "./candlestick-chart-data.model";
import {EChartsOption} from "echarts";
import {candleStickChartColors} from "../../../assets/chart_theme/chart_colors";

@Component({
    selector: 'app-candlestick-chart',
    templateUrl: './candlestick-chart.component.html',
    styleUrls: ['./candlestick-chart.component.scss']
})
export class CandlestickChartComponent implements OnInit{
    @Input({required: true}) data!: CandlestickChartDataModel[];
    protected chartOptions!: EChartsOption;
    private colors = candleStickChartColors;

    ngOnInit(): void {
        this.data = mockData;
        this.initChartOptions();
    }

    private initChartOptions(){
        this.chartOptions = {
            xAxis: {
                type: 'category',
                data: this.data.map(dataItem => dataItem.category)
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    type: 'candlestick',
                    data: this.data.map(dataItem => this.transformChartData(dataItem)),
                }
            ],
            itemStyle: {
                color: this.colors.colorGreen,
                color0: this.colors.colorRed,
                borderColor: this.colors.colorGreen,
                borderColor0: this.colors.colorRed,
            }
        }
    }

    private transformChartData(dataItem: CandlestickChartDataModel): number[]{
        return [dataItem.individualValue, dataItem.mean, dataItem.mean - dataItem.deviation, dataItem.mean + dataItem.deviation];
    }

}

const mockData: CandlestickChartDataModel[] = [
    {
        category: 'A',
        mean: 1.0,
        deviation: 0.2,
        individualValue: 1.1,
    },
    {
        category: 'B',
        mean: 1.13,
        deviation: 0.122,
        individualValue: 1.1,
    },
    {
        category: 'C',
        mean: 1.2,
        deviation: 0.3,
        individualValue: 1.22,
    },
    {
        category: 'D',
        mean: 1.0,
        deviation: 0.15,
        individualValue: 1.1,
    },
    {
        category: 'E',
        mean: 0.95,
        deviation: 0.2,
        individualValue: 1.1,
    },
];
