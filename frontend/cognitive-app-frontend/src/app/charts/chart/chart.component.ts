import {Component, OnInit} from '@angular/core';
import {EChartsOption} from "echarts";

@Component({
  selector: 'app-chart',
  standalone: true,
  imports: [],
  templateUrl: './chart.component.html',
  styleUrl: './chart.component.scss'
})
export class ChartComponent implements OnInit{
  loading = true;
  chartOptions!: EChartsOption
  ngOnInit(): void {
    this.initChartOptions()
  }

  protected initChartOptions(): void {

  }
}
