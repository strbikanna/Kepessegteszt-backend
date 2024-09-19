import { Component } from '@angular/core';
import {of} from "rxjs";

@Component({
  selector: 'app-profile-line-chart',
  templateUrl: './profile-line-chart.component.html',
  styleUrls: ['./profile-line-chart.component.scss']
})
export class ProfileLineChartComponent {
    valueUser = of(1.7);
    valueGroup = of(1.3);
    valueMax = of(1.8);
}
