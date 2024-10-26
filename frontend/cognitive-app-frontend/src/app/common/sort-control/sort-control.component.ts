import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
    selector: 'app-sort-control',
    templateUrl: './sort-control.component.html',
    styleUrls: ['./sort-control.component.scss']
})
export class SortControlComponent {

    /**
     * The elements according which data can be sorted. Each element will have ascending and descending sorting options.
     */
    @Input() sortElements: string[] = [];
    @Output() onSort = new EventEmitter<SortElement>();

    currentSortElement: string | undefined = undefined;
    currentSortDirection: 'ASC' | 'DESC' | undefined = undefined;

    sortChosen(sortElement: string) {
        this.currentSortDirection = this.getNextSortDirection(sortElement);
        this.currentSortElement = sortElement;
        if(this.currentSortDirection === undefined){
            this.currentSortElement = undefined;
        }
        this.onSort.emit({sortElement: this.currentSortElement, sortDirection: this.currentSortDirection});
    }

    getSortDirectionIconName(){
        switch(this.currentSortDirection){
            case 'ASC':
                return 'arrow_upward';
            case 'DESC':
                return 'arrow_downward';
            default:
                return undefined;
        }
    }

    private getNextSortDirection(chosenSortElement: string): 'ASC' | 'DESC' | undefined {
        if (this.currentSortElement === chosenSortElement) {
            switch (this.currentSortDirection) {
                case 'ASC':
                    return 'DESC';
                case 'DESC':
                    return undefined;
                default:
                   return 'ASC';
            }
        }
        return 'ASC';
    }
}

export interface SortElement {
    sortElement: string | undefined;
    sortDirection: 'ASC' | 'DESC' | undefined;
}
