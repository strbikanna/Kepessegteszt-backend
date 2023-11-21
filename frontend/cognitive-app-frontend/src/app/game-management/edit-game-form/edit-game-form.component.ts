import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Ability} from "../../model/ability.model";
import {GameManagementService} from "../service/game-management.service";
import {TEXTS} from "../../utils/app.text_messages";
import {AbilityService} from "../../ability/ability.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Game} from "../../model/game.model";

@Component({
    selector: 'app-edit-game-form',
    templateUrl: './edit-game-form.component.html',
    styleUrls: ['./edit-game-form.component.scss']
})
export class EditGameFormComponent implements OnInit {

    gameForm = this.fb.group({
        name: ['', Validators.required],
        description: ['', Validators.required],
        version: [{value: 0, disabled: true}, Validators.required],
        url: [''],
        thumbnail: [null],
        active: [false, Validators.required],
        abilities: this.fb.array([]),
        configDescription: this.fb.array([])
    })

    loading: boolean = true;
    game: Game | undefined;
    text = TEXTS.game_management.edit_form
    thumbnail: string = ''
    actionText = TEXTS.actions

    constructor(private service: GameManagementService,
                private abilityService: AbilityService,
                private route: ActivatedRoute,
                private fb: FormBuilder,
                private router: Router
    ) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
            const id = params.get('id') as unknown as number;
            this.loadGameData(id);
        })

    }

    toFromGroup(formControl: AbstractControl<any>) {
        return formControl as FormGroup
    }

    onSubmit() {
        this.loading = true;
        const game: Game = {
            id: this.game!!.id,
            name: this.gameForm.controls.name.value ?? this.game!!.name,
            description: this.gameForm.controls.description.value ?? this.game!!.description,
            version: this.game!!.version,
            thumbnail: this.game!!.thumbnail,
            active: this.gameForm.controls.active.value ?? this.game!!.active,
            url: this.gameForm.controls.url.value==='' ? this.game!!.url : this.gameForm.controls.url.value ?? undefined,
            affectedAbilities: this.getFormAffectedAbilities(),
            configDescription: this.getFormConfig()
        }
        this.service.editGame(game).subscribe(game => {
            this.onBack()
        })
        if(this.gameForm.controls.thumbnail.value){
            const formData = new FormData()
            formData.set('file', this.gameForm.controls.thumbnail.value)
            this.service.sendGameThumbnail(formData, game.id).subscribe()
        }
    }
    onBack(){
        this.router.navigate(['/game-management'])
    }
    getFormAffectedAbilities() {
        return this.abilitiesForm.controls
            .filter(abilityForm => this.toFromGroup(abilityForm).controls['included'].value)
            .map(abilityForm => this.toAbility(abilityForm as FormGroup))
    }
    getFormConfig() {
        const config: any = {}
       this.configDescriptionForm.controls
            .forEach(configDescriptionForm => {
                const configDescription = this.toFromGroup(configDescriptionForm)
                config[configDescription.controls['key'].value] = configDescription.controls['value'].value
            })
        return config
    }

    toAbility(formControl: FormGroup) {
        return formControl.controls['ability'].value as Ability
    }
    abilityName(formControl: AbstractControl): string{
        let ability = this.toAbility(formControl as FormGroup)
        return ability.name
    }

    get abilitiesForm() {
        return this.gameForm.controls.abilities as FormArray
    }
    get configDescriptionForm() {
        return this.gameForm.controls.configDescription as FormArray
    }

    addConfigDescription(key: string = '', value: string = '') {
        const configDescriptionForm = this.fb.group({
            key: [key, Validators.compose([Validators.required, Validators.pattern('^[a-zA-Z0-9_-]*$')])],
            value: [value, Validators.compose([Validators.required, Validators.pattern('^[a-zA-Z0-9_-]*$')])]
        })
        this.configDescriptionForm.push(configDescriptionForm)
    }
    removeConfigDescription(index: number) {
        this.configDescriptionForm.removeAt(index)
    }

    private loadGameData(id: number) {
        this.service.getGameById(id).subscribe(game => {
            this.game = game;
            const formControls = this.gameForm.controls
            formControls.name.setValue(game.name)
            formControls.description.setValue(game.description)
            formControls.active.setValue(game.active)
            formControls.version.setValue(game.version)
            formControls.url.setValue(game.url ?? '')
            this.thumbnail = game.thumbnail
            Object.entries(game.configDescription).forEach(([key, value]) => {
                this.addConfigDescription(key, value as string)
            })
            this.loading = false;
            this.abilityService.getAllAbilities().subscribe(abilities => {
                this.setFormAbilities(abilities)
            })
        })
    }

    includesAbility(ability: Ability) {
        if(this.game && this.game.affectedAbilities)
            return this.game!!.affectedAbilities.some(gameAbility => gameAbility.code === ability.code)
        else return false
    }

    private setFormAbilities(abilities: Ability[]) {
        abilities.forEach(ability => {
            const abilityForm = this.fb.group({
                    ability: [ability],
                    included: [this.includesAbility(ability)]
                }
            )
            this.abilitiesForm.push(abilityForm)
        })
    }
}
