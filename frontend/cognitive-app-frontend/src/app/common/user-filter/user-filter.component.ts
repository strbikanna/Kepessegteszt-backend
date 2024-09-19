import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {UserFilter} from "./user.filter";
import {FormArray, FormBuilder, FormControl} from "@angular/forms";

@Component({
    selector: 'app-user-filter',
    templateUrl: './user-filter.component.html',
    styleUrls: ['./user-filter.component.scss']
})
export class UserFilterComponent implements OnInit {
    @Output() filterChange: EventEmitter<UserFilter> = new EventEmitter;

    constructor(private fb: FormBuilder) {
    }

    ngOnInit() {
        this.addAbilityFilter();
    }

    protected userFilterForm = this.fb.group({
        ageMin: [new FormControl<number | undefined>(undefined)],
        ageMax: [new FormControl<number | undefined>(undefined)],
        addressCity: [new FormControl<string | undefined>(undefined)],
        addressZip: [new FormControl<string | undefined>(undefined)],
        userGroupId: [new FormControl<number | undefined>(undefined)],
        abilityFilter: this.fb.array<UserFilter>([])
    });

    addAbilityFilter() {
        let abilityFilters = this.userFilterForm.get('abilityFilter') as FormArray;
        abilityFilters.push(this.fb.group({
            code: [new FormControl<string | undefined>(undefined)],
            valueMin: [new FormControl<number | undefined>(undefined)],
            valueMax: [new FormControl<number | undefined>(undefined)]
        }));
    }

    removeAbilityFilter(index: number) {
        let abilityFilters = this.userFilterForm.get('abilityFilter') as FormArray;
        abilityFilters.removeAt(index);
    }

    applyFilters() {
        let abilityFilters = this.userFilterForm.get('abilityFilter') as FormArray;
        const filter: UserFilter = {
            ageMin: this.userFilterForm.get('ageMin')?.value ?? undefined,
            ageMax: this.userFilterForm.get('ageMax')?.value ?? undefined,
            addressCity: this.userFilterForm.get('addressCity')?.value ?? undefined,
            addressZip: this.userFilterForm.get('addressZip')?.value ?? undefined,
            userGroupId: this.userFilterForm.get('userGroupId')?.value ?? undefined,
            abilityFilter: abilityFilters.value
        }
        this.filterChange.emit(filter);
    }

}
