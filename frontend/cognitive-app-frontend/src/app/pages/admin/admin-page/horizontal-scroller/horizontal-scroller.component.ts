import {
  Component,
  EventEmitter, HostListener,
  Input, OnInit,
  Output,
} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatIconButton} from "@angular/material/button";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-horizontal-scroller',
  templateUrl: './horizontal-scroller.component.html',
  standalone: true,
  imports: [
    MatIcon,
    MatIconButton,
    MatButton,
    NgIf,
    NgForOf
  ],
  styleUrls: ['./horizontal-scroller.component.scss']
})
export class HorizontalScrollerComponent implements OnInit{
  @Input() items: string[] = [];
  @Output() itemSelected = new EventEmitter<string>();
  @Output() selectionCleared = new EventEmitter<string>();

  currentIndex = 0;
  visibleItems = 5;

  selectedItem: string = '';

  ngOnInit() {
    this.updateVisibleItems();
  }

  get visibleButtons(): string[] {
    return this.items.slice(this.currentIndex, this.currentIndex + this.visibleItems);
  }

  stepLeft() {
    if (this.currentIndex > 0) {
     this.currentIndex = Math.max(0, this.currentIndex - this.visibleItems);
    }
  }

  stepRight() {
    if (this.currentIndex + this.visibleItems < this.items.length) {
        this.currentIndex = Math.min(this.items.length - this.visibleItems, this.currentIndex + this.visibleItems);
    }
  }

  selectItem(item: string) {
    if(this.selectedItem === item) {
        this.selectedItem = '';
        this.selectionCleared.emit(item);
        return;
    }
    this.selectedItem = item;
    this.itemSelected.emit(item);
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event) {
    this.updateVisibleItems();
  }

  updateVisibleItems() {
    const screenWidth = window.innerWidth;

    // Adjust the number of items per page based on screen width
    if (screenWidth > 1200) {
      this.visibleItems = 10;
    } else {
      this.visibleItems = 5;
    }

  }
}
