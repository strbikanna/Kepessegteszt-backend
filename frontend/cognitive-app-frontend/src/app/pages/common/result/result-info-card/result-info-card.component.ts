import {Component, Input, OnInit} from '@angular/core';
import {Result} from "../../../../model/result.model";
import {TEXTS} from "../../../../utils/app.text_messages";
import {Game} from "../../../../model/game.model";
import {Observable} from "rxjs";

@Component({
    selector: 'app-result-info-card',
    templateUrl: './result-info-card.component.html',
    styleUrls: ['./result-info-card.component.scss']
})
export class ResultInfoCardComponent {

    @Input({required: true}) result!: Result;

    protected readonly texts = TEXTS.result.result_info

    getContent(config: any): string{
        return JSON.stringify(config);
    }


}
