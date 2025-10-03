import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-count-up',
  templateUrl: './count-up.component.html',
  styleUrl: './count-up.component.scss'
})
export class CountUpComponent implements OnInit{
  @Input() target = 1000;
  @Input() duration = 2000;
  @Input() className = ""

  current = 0;

  ngOnInit() {
    this.animateCount();
  }

  animateCount() {
    const startTime = performance.now();
    const step = (now: number) => {
      const progress = Math.min((now - startTime) / this.duration, 1);
      this.current = Math.floor(progress * this.target);

      if (progress < 1) {
        requestAnimationFrame(step);
      }
    };
    requestAnimationFrame(step);
  }
}

// kebab-case.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';
@Pipe({
  name: 'xpNumber',
})
export class NumberPipe implements PipeTransform {
  transform(value: number): string {
    const millions = Math.floor(value / 1_000_000);
    const thousands = Math.floor((value % 1_000_000) / 1_000);
    const rest = value % 1_000;
    if(millions){
      return `${millions} ${thousands} ${rest}`
    }
    if(thousands){
      return `${thousands} ${rest}`
    }
    else return `${rest}`
  }
}