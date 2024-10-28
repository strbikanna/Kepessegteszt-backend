import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbilityFilter, UserFilter} from "./user-filter.model";
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {ProfileData} from "../../model/profile_data.model";
import {UserGroup} from "../../model/user_group.model";
import {Observable} from "rxjs";
import {TEXTS} from "../../utils/app.text_messages";
import {Ability} from "../../model/ability.model";

@Component({
    selector: 'app-user-filter',
    templateUrl: './user-filter.component.html',
    styleUrls: ['./user-filter.component.scss']
})
export class UserFilterComponent implements OnInit {
    @Input() ageMin: number = 0;
    @Input() ageMax: number = 60;
    @Input() abilityValueMin: number = 0.0;
    @Input() abilityValueMax: number = 2.0;
    @Input({required: true}) availableGroups: Observable<UserGroup[]> = new Observable<UserGroup[]>();
    @Input({required: true}) availableAbilities: Observable<Ability[]> = new Observable<Ability[]>();

    @Output() filterChange: EventEmitter<UserFilter> = new EventEmitter;

    constructor(private fb: FormBuilder) {
    }

    ngOnInit() {
        this.availableAbilities.subscribe(
            (abilities) => {
                this.allAbilities = abilities;
            });
    }

    protected text = TEXTS.cognitive_profile.user_filter
    private allAbilities: Ability[] = [];

    protected userFilterForm = this.fb.group({
        ageMin: [undefined],
        ageMax: [undefined],
        addressCity: [''],
        addressZip: ['', Validators.pattern('^[0-9]{4}$')],
        userGroupId: [undefined],
        abilityFilter: this.fb.array<AbilityFilter>([])
    });

    isAbilityInForm(ability: Ability){
        let abilityFilters = this.userFilterForm.get('abilityFilter') as FormArray;
        return abilityFilters?.value.find((filter: AbilityFilter) => filter.code === ability.code);
    }

    addAbilityFilter(ability: Ability) {
        let abilityFilters = this.userFilterForm.get('abilityFilter') as FormArray;
        if(this.isAbilityInForm(ability)){
            return;
        }
        abilityFilters.push(this.fb.group({
            code: [ability.code],
            valueMin: [undefined],
            valueMax: [undefined]
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
        if(filter.addressCity === ''){
            filter.addressCity = undefined;
        }
        if(filter.addressZip === ''){
            filter.addressZip = undefined;
        }
        this.filterChange.emit(filter);
    }

    getAbilityNameOfControl(formControl: AbstractControl){
        let formGroup = formControl as FormGroup;
        let abilityCode = formGroup.controls['code'].value;
        return this.allAbilities.find((ability) => ability.code === abilityCode)?.name;
    }
    getAbilityFilterGroup(formControl: AbstractControl){
        return formControl as FormGroup;
    }


}
