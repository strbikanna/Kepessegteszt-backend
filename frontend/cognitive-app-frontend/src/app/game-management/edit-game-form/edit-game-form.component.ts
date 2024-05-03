import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Ability} from "../../model/ability.model";
import {GameManagementService} from "../service/game-management.service";
import {TEXTS} from "../../utils/app.text_messages";
import {AbilityService} from "../../ability/ability.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Game} from "../../model/game.model";
import {ConfigItem} from "../../model/config_item.model";

@Component({
    selector: 'app-edit-game-form',
    templateUrl: './edit-game-form.component.html',
    styleUrls: ['./edit-game-form.component.scss']
})
export class EditGameFormComponent implements OnInit {

    /**
     * Form model for game data
     */
    protected gameForm = this.fb.group({
        name: ['', Validators.required],
        description: ['', Validators.required],
        version: [{value: 1, disabled: true}, Validators.required],
        url: [''],
        thumbnail: [null],
        active: [true, Validators.required],
        abilities: this.fb.array([]),
        configItems: this.fb.array<ConfigItem>([], Validators.required)
    })


    protected loading: boolean = true;
    protected game: Game | undefined;
    protected text = TEXTS.game_management.edit_form
    protected thumbnail: string = ''
    protected actionText = TEXTS.actions

    constructor(private service: GameManagementService,
                private abilityService: AbilityService,
                private route: ActivatedRoute,
                private fb: FormBuilder,
                private router: Router
    ) {
    }

    /**
     * retrieve game by route param id data from server
     */
    ngOnInit(): void {
        this.route.paramMap.subscribe(params => {
            const id = params.get('id') as unknown as number;
            if(id !== null){
                this.loadGameData(id);
            }else{
                this.initWithEmptyGame();
            }
        })

    }

    toFromGroup(formControl: AbstractControl) {
        return formControl as FormGroup
    }

    /**
     * Submit form data to server: update game data and send uploaded thumbnail
     */
    onSubmit() {
        this.loading = true;
        const game: Game = {
            id: this.game?.id,
            name: this.gameForm.controls.name.value ?? '',
            description: this.gameForm.controls.description.value ?? '',
            version: this.gameForm.controls.version.value ?? 1,
            thumbnail: this.game?.thumbnail ?? '',
            active: this.gameForm.controls.active.value ?? true,
            url: this.gameForm.controls.url.value ?? undefined,
            affectedAbilities: this.getFormAffectedAbilities(),
            configDescription: this.game?.configDescription ?? '',
            configItems: this.getFormConfigItems()
        }
        console.log(game)
        // this.service.editGame(game).subscribe(game => {
        //     this.onBack()
        // })
        // if(this.gameForm.controls.thumbnail.value){
        //     const formData = new FormData()
        //     formData.set('file', this.gameForm.controls.thumbnail.value)
        //     this.service.sendGameThumbnail(formData, game.id).subscribe()
        // }
    }

    addConfigItem() {
        const countOfConfigItems = this.gameForm.controls.configItems.length
        const control = new FormControl<ConfigItem>(
            {
                id: undefined,
                paramName: '',
                initialValue: 2,
                hardestValue: 10,
                easiestValue: 1,
                increment: 1,
                paramOrder: countOfConfigItems + 1,
                description: ''
            }
        )
        control.enable()
        this.gameForm.controls.configItems.push(control)
    }

    onDeleteConfigItem(index: number) {
        this.gameForm.controls.configItems.removeAt(index)
    }
    onUpdateConfigItem(index: number, configItem: ConfigItem) {
        this.gameForm.controls.configItems.at(index).setValue(configItem)
    }

    get configItemsForm() {
        return this.gameForm.controls.configItems as FormArray
    }

    /**
     * navigate to game management page
     */
    onBack() {
        this.router.navigate(['/game-management'])
    }

    /**
     * Constructs affected abilities array based on form data
     */
    getFormAffectedAbilities() {
        return this.abilitiesForm.controls
            .filter(abilityForm => this.toFromGroup(abilityForm).controls['included'].value)
            .map(abilityForm => this.toAbility(abilityForm as FormGroup))
    }

    toAbility(formControl: FormGroup) {
        return formControl.controls['ability'].value as Ability
    }

    abilityName(formControl: AbstractControl): string {
        let ability = this.toAbility(formControl as FormGroup)
        return ability.name
    }

    get abilitiesForm() {
        return this.gameForm.controls.abilities as FormArray
    }

    getFormConfigItems(){
        let configItems : ConfigItem[] = []
        this.gameForm.controls.configItems.controls.forEach(control => {
            if(control.value !== null){
                configItems.push(control.value)
            }
        })
        return configItems
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
            game.configItems.forEach(item => {
                const control = new FormControl<ConfigItem>(item)
                control.disable()
                formControls.configItems.push(control)
            })
            this.thumbnail = game.thumbnail

            this.loading = false;
            this.abilityService.getAllAbilities().subscribe(abilities => {
                this.setFormAbilities(abilities)
            })
        })
    }
    private initWithEmptyGame(){
        this.abilityService.getAllAbilities().subscribe(abilities => {
            this.setFormAbilities(abilities)
            this.loading = false;
        })
        this.addConfigItem()
    }

    includesAbility(ability: Ability) {
        if (this.game && this.game.affectedAbilities)
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
