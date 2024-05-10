import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {map, Observable} from "rxjs";

@Component({
    selector: 'app-filter-control',
    templateUrl: './filter-control.component.html',
    styleUrls: ['./filter-control.component.scss']
})
export class FilterControlComponent implements OnInit {
    /**
     * The elements according which data can be sorted. Each element will have ascending and descending sorting options.
     */
    @Input({required: true}) filterElements!: Observable<string[]>
    @Output() onFilter = new EventEmitter<string[] | undefined>();

    protected readonly text = TEXTS.actions

    inputFilterValue: string | undefined = undefined;
    filterElementInputs: { name: string, checked: boolean }[] = [];
    actualFilterElementInputs: { name: string, checked: boolean }[] = [];
    dataArrived = false;

    ngOnInit() {
        this.filterElements.subscribe((elements) => {
            this.filterElementInputs = elements.map(element => {
                return {name: element, checked: false}
            });
            this.actualFilterElementInputs = this.filterElementInputs;
            console.log(elements)
            this.dataArrived = true;
        })
    }

    filterChosen(filterValue: string | undefined, checked: boolean) {
        this.actualFilterElementInputs = this.actualFilterElementInputs.map((element) => {
            if (element.name === filterValue) {
                element.checked = checked;
            }
            return element;
        })
        this.filterEvent()
    }

    allFilterChosen(checked: boolean) {
        this.actualFilterElementInputs = this.actualFilterElementInputs.map((element) => {
            element.checked = checked;
            return element;
        })
        this.filterEvent()
    }

    allChecked() {
        return this.actualFilterElementInputs.every((element) => element.checked);
    }

    someChecked() {
        return this.actualFilterElementInputs.some((element) => element.checked) && !this.allChecked();
    }

    filterFilterElementInputs() {
        if (this.inputFilterValue !== undefined) {
            this.actualFilterElementInputs = this.filterElementInputs.filter((element) => element.name.includes(this.inputFilterValue!!));
        } else {
            this.actualFilterElementInputs = this.filterElementInputs;
        }
    }

    private filterEvent() {
        this.onFilter.emit(this.actualFilterElementInputs
            .filter((element) => element.checked)
            .map((element) => element.name));
    }
}
