import {Component} from "@angular/core";
import {User} from "../../../model/user.model";
import {Game} from "../../../model/game.model";
import {RecommendationService} from "../../../service/recommendation/recommendation.service";
import {AbstractControl, FormBuilder, FormControl, FormGroup, ValidatorFn, Validators} from "@angular/forms";
import {ConfigItem} from "../../../model/config_item.model";
import {TEXTS} from "../../../text/app.text_messages";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Recommendation} from "../../../model/recommendation.model";
import {AuthUser} from "../../../model/user-contacts.model";
import {Observable} from "rxjs";
import {RecommendedGame} from "../../../model/recommended_game.model";
import {Router} from "@angular/router";


@Component({
    selector: 'app-recommendation',
    templateUrl: './recommendation-page.component.html',
    styleUrls: ['./recommendation-page.component.scss']
})
export class RecommendationPageComponent {
    protected readonly texts = TEXTS.recommendation_page
    protected chosenUser: User | undefined;
    protected chosenGame: Game | undefined;
    protected configForm = this.fb.array<FormGroup>([]);
    protected existingRecommendations: Observable<RecommendedGame[]> = new Observable<RecommendedGame[]>();


    constructor(
        private service: RecommendationService, private fb: FormBuilder,
        private _snackbar: MatSnackBar, private router: Router
    ) {}

    onGameSelected(game: Game) {
        this.chosenGame = game;
        this.configForm = this.fb.array(
            game.configItems.map(item =>
                this.fb.group({
                    config: [item],
                    setting: [item.initialValue, [Validators.required,]],
                }, {validators: this.isConfigValid})
            )
        );
        if (this.chosenUser !== undefined) {
            this.loadExistingRecommendations(this.chosenUser.username, game.id)
        }
    }

    onUserSelected(user: AuthUser) {
        this.chosenUser = user;
        this.loadExistingRecommendations(user.username, this.chosenGame?.id);
    }

    isConfigValid: ValidatorFn = (control: AbstractControl) => {
        const formGroup = control as FormGroup
        let config = formGroup.controls['config'].value as ConfigItem
        let value = formGroup.controls['setting'].value as number
        if ((value >= config.easiestValue && value <= config.hardestValue) || (value <= config.easiestValue && value >= config.hardestValue)) return null;
        formGroup.controls['setting'].setErrors({invalidConfigValue: true})
        return {invalidConfigValue: true}
    }

    labelOfControl(control: AbstractControl): string {
        return (control as FormGroup).controls['config'].value.paramName
    }

    descriptionOfControl(control: AbstractControl): string {
        return (control as FormGroup).controls['config'].value.description
    }

    getSettingControl(formGroup: FormGroup): FormControl {
        return formGroup.controls['setting'] as FormControl
    }

    getHint(control: AbstractControl): string {
        const configItem = (control as FormGroup).controls['config'].value as ConfigItem
        return `Legnehezebb: ${configItem.hardestValue}, Legkönnyebb: ${configItem.easiestValue}`
    }

    canSaveRecommendation() {
        return this.configForm.valid && this.chosenGame !== undefined && this.chosenUser !== undefined
    }

    onSubmit() {
        if (!this.canSaveRecommendation()) return;
        const config: any = {}
        this.configForm.controls.forEach(control => {
            const configItem = (control as FormGroup).controls['config'].value as ConfigItem
            config[`${configItem.paramName}`] = (control as FormGroup).controls['setting'].value as number
        })
        const recommendedGame: Recommendation = {
            gameId: this.chosenGame!!.id!!,
            config: config,
            recommendedTo: this.chosenUser!!.username
        }
        this.service.saveRecommendation(recommendedGame).subscribe(() => {
            this._snackbar.open(this.texts.saved, undefined, {duration: 3000})
            this.loadExistingRecommendations(this.chosenUser!!.username, this.chosenGame?.id)
        })
    }

    onDeleteRecommendation(id: number){
        this.service.deleteRecommendation(id).subscribe(() => {
            this._snackbar.open(this.texts.deleted, undefined, {duration: 3000})
            this.loadExistingRecommendations(this.chosenUser!!.username, this.chosenGame?.id)
        })
    }

    filterGamesByActive: (game: Game) => boolean = game => game.active;

    existingRecommendationsEnabled() {
        return this.chosenUser !== undefined;
    }

    onUserClicked() {
        if (this.chosenUser) {
            const params = {username: this.chosenUser.username, name: this.chosenUser.firstName + ' ' + this.chosenUser.lastName}
            this.router.navigate(['/cognitive-profile-admin'], {queryParams: params})
        }
    }
    onGameClicked(){
        if(this.chosenGame){
            const params = {chosenGameIds: this.chosenGame.id, chosenUserNames: this.chosenUser?.username}
            this.router.navigate(['/result'], {queryParams: params})
        }
    }

    onRemoveChosenUser() {
        this.chosenUser = undefined;
        this.onRemoveChosenGame();
        this.existingRecommendations = new Observable<RecommendedGame[]>();
    }

    onRemoveChosenGame() {
        this.chosenGame = undefined;
        this.configForm = this.fb.array<FormGroup>([]);
        this.existingRecommendations = new Observable<RecommendedGame[]>();
        if(this.chosenUser){
            this.loadExistingRecommendations(this.chosenUser.username, undefined);
        }
    }

    private loadExistingRecommendations(username: string, gameId?: number) {
        this.existingRecommendations = this.service.getRecommendationsToUserAndGame(username, gameId);
    }

}
