import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Ability} from "../../../../model/ability.model";
import {FormBuilder, Validators} from "@angular/forms";
import {TEXTS} from "../../../../text/app.text_messages";

@Component({
    selector: 'app-editable-ability-card',
    templateUrl: './editable-ability-card.component.html',
    styleUrls: ['./editable-ability-card.component.scss']
})
export class EditableAbilityCardComponent {
    @Input({required: true}) ability!: Ability;
    @Output() onUpdate = new EventEmitter<Ability>();

    text = TEXTS.ability_page;

    isUpdateMode = false;

    constructor(private fb: FormBuilder) {
    }

    updateAbilityForm = this.fb.group({
        name: ['', Validators.required],
        description: ['', Validators.required],
    });

    setUpdateMode(mode: boolean) {
        this.isUpdateMode = mode;
        this.updateAbilityForm.setValue({
            name: this.ability.name,
            description: this.ability.description,
        });
    }

    updateAbility() {
        if (this.updateAbilityForm.invalid) {
            return;
        }
        const abilityToUpdate: Ability = {
            code: this.ability.code,
            name: this.updateAbilityForm.value.name!,
            description: this.updateAbilityForm.value.description!,
            type: this.ability.type,
        };
        this.onUpdate.emit(abilityToUpdate);
    }
}
