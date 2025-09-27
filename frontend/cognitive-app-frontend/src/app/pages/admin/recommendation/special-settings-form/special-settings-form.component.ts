import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import SpecialSettings, {DistractionType} from "../../../../model/user/special_settings.model";
import {FormBuilder, Validators} from "@angular/forms";
import {RecommendationService} from "../../../../service/recommendation/recommendation.service";
import {Observable} from "rxjs";
import {TEXTS} from "../../../../text/app.text_messages";

@Component({
    selector: 'app-special-settings-form',
    templateUrl: './special-settings-form.component.html',
    styleUrl: './special-settings-form.component.scss'
})
export class SpecialSettingsFormComponent implements OnInit {
    @Input({required: true}) usernameObservable!: Observable<string>;
    username?: string;
    @Output() specialSettingsChange = new EventEmitter<void>();

    protected userSpecialSettings?: SpecialSettings;
    protected readonly texts = TEXTS.recommendation_page.specialSettings


    constructor(
        private service: RecommendationService, private fb: FormBuilder,
    ) {}

    ngOnInit(): void {
        this.usernameObservable.subscribe(username => {
            this.username = username;
            if (this.username && this.username !== '') this.loadSpecialSettings(this.username);
        })
    }

    protected specialSettingsForm = this.fb.group({
        blackScreen: [false, []],
        notification: [false, []],
        sound: [false, []],
        visual: [false, []],
        pavlovian: [false, []],
        minInterval: [10, [Validators.required]],
        maxInterval: [30, [Validators.required]],
        validMinutes: [15, [Validators.required]],
    }, { validators: this.intervalValidator });

    loadSpecialSettings(username: string) {
        this.service.getSpecialSettingsOfUser(username).subscribe(settings => {
            this.userSpecialSettings = settings;
            this.specialSettingsForm.setValue({
                blackScreen: settings.distractionTypes.includes(DistractionType.BLACKSCREEN),
                notification: settings.distractionTypes.includes(DistractionType.NOTIFICATION),
                sound: settings.distractionTypes.includes(DistractionType.SOUND),
                visual: settings.distractionTypes.includes(DistractionType.VISUAL),
                pavlovian: settings.distractionTypes.includes(DistractionType.PAVLOVIAN),
                minInterval: settings.minInterval ?? 10,
                maxInterval: settings.maxInterval ?? 30,
                validMinutes: settings.validMinutes,
            })
        })
    }

    updateSpecialSettings() {
        if (!this.username) return;
        const settings: SpecialSettings = {
            distractionTypes: [],
            minInterval: this.specialSettingsForm.value.minInterval || undefined,
            maxInterval: this.specialSettingsForm.value.maxInterval || undefined,
            validMinutes: this.specialSettingsForm.value.validMinutes || 15,
        };
        if (this.specialSettingsForm.value.blackScreen) settings.distractionTypes.push(DistractionType.BLACKSCREEN);
        if (this.specialSettingsForm.value.notification) settings.distractionTypes.push(DistractionType.NOTIFICATION);
        if (this.specialSettingsForm.value.sound) settings.distractionTypes.push(DistractionType.SOUND);
        if (this.specialSettingsForm.value.visual) settings.distractionTypes.push(DistractionType.VISUAL);
        if (this.specialSettingsForm.value.pavlovian) settings.distractionTypes.push(DistractionType.PAVLOVIAN);

        this.service.updateSpecialSettingsOfUser(this.username, settings).subscribe(updatedSettings => {
            this.userSpecialSettings = updatedSettings;
            this.specialSettingsChange.emit();
        })
    }

    isSaveEnabled(): boolean {
        return this.username !== '' && this.specialSettingsForm.valid;
    }

    intervalValidator(control: any){
        const min = control.get('minInterval')?.value;
        const max = control.get('maxInterval')?.value;
        if (min != null && max != null && min > max) {
            return { intervalInvalid: true };
        }
        return null;
    }
}
