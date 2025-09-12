import {Component, OnInit} from '@angular/core';
import {TEXTS} from "../../../text/app.text_messages";
import {AbilityService} from "../../../service/ability/ability.service";
import {Ability, AbilityType} from "../../../model/ability.model";
import {FormBuilder, Validators} from "@angular/forms";

@Component({
    selector: 'app-ability-page',
    templateUrl: './ability-page.component.html',
    styleUrls: ['./ability-page.component.scss']
})
export class AbilityPageComponent implements  OnInit{
    text = TEXTS.ability_page
    floatAbilityList: Ability[] = [];
    enumAbilityList: Ability[] = [];

    constructor(private service: AbilityService, private fb: FormBuilder) {
    }

    newAbilityForm = this.fb.group({
        code: ['', Validators.required],
        name: ['', Validators.required],
        description: ['', Validators.required],
        type: [AbilityType.FLOAT, Validators.required],
    });


    ngOnInit() {
        this.loadAbilities()
    }

    private loadAbilities(){
        this.service.getAllAbilities().subscribe(abilities => {
            this.floatAbilityList = abilities.filter(a => a.type === AbilityType.FLOAT);
            this.enumAbilityList = abilities.filter(a => a.type === AbilityType.ENUM);
        });
    }

    updateAbility(updatedAbility: Ability){
        this.service.updateAbility(updatedAbility).subscribe(() => {
                this.loadAbilities();
            }
        )
    }

    addAbility(){
        if(this.newAbilityForm.invalid){
            return;
        }
        const newAbility : Ability ={
            code: this.newAbilityForm.value.code!,
            name: this.newAbilityForm.value.name!,
            description: this.newAbilityForm.value.description!,
            type: this.newAbilityForm.value.type!,
        }
        this.service.saveAbility(newAbility).subscribe(() => {
            this.loadAbilities();
        })
    }

    protected readonly AbilityType = AbilityType;
}
