import {Component, HostListener, Input} from '@angular/core';

@Component({
  selector: 'app-up-and-down-button',
  templateUrl: './up-and-down-button.component.html',
  styleUrls: ['./up-and-down-button.component.scss']
})
export class UpAndDownButtonComponent {
  @Input({required: true}) elementId!: string
  isExpandMoreButtonVisible = true

  onDownClicked() {
    const chartPosition = document.getElementById(this.elementId)?.offsetTop
    window.scroll({top: chartPosition, behavior: 'smooth'})
  }

  onTopClicked() {
    window.scroll({top: 0, behavior: 'smooth'})
  }

  @HostListener('window:scroll', ['$event'])
  onScroll($event: Event) {
    const chartPosition = document.getElementById(this.elementId)?.offsetTop! - document.getElementById(this.elementId)!.offsetHeight * 0.5
    this.isExpandMoreButtonVisible = chartPosition >= window.scrollY;
  }
}
