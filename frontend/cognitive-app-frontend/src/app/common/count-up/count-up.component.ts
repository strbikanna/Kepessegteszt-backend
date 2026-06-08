import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-count-up',
  templateUrl: './count-up.component.html',
  styleUrl: './count-up.component.scss'
})
export class CountUpComponent implements OnInit{
  @Input({required : true}) target!: Observable<number>;
  @Input() duration = 2000;
  @Input() className = ""
  targetValue = 0;
  current = 0;

  ngOnInit() {
    this.target.subscribe(value => {
      this.targetValue = value;
      this.animateCount();
    })
  }

  animateCount() {
    const startTime = performance.now();
    const step = (now: number) => {
      const progress = Math.min((now - startTime) / this.duration, 1);
      this.current = Math.floor(progress * this.targetValue);

      if (progress < 1) {
        requestAnimationFrame(step);
      }
    };
    requestAnimationFrame(step);
  }
}

// kebab-case.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';
import {Observable} from "rxjs";
@Pipe({
  name: 'xpNumber',
})
export class NumberPipe implements PipeTransform {
  transform(value: number): string {
    return new Intl.NumberFormat('de-DE')
        .format(value).replaceAll('.', ' ');
  }
}