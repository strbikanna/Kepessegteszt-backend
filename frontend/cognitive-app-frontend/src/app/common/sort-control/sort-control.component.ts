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

    sortModeForm = new FormGroup({
        sortElement: new FormControl(''),
        sortDirection: new FormControl<'ASC' | 'DESC' | undefined>(undefined)
    });

    sortChosen() {
        let sortElement = this.sortModeForm.get('sortElement')?.value;
        let sortDirection = this.sortModeForm.get('sortDirection')?.value;
        if (sortElement == null || sortDirection == null) {
            sortDirection = undefined
            sortElement = undefined
        }
        this.onSort.emit({sortElement, sortDirection});
    }
}

export interface SortElement {
    sortElement: string | undefined;
    sortDirection: 'ASC' | 'DESC' | undefined;
}
