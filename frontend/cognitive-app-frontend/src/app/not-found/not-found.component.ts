import {Component} from '@angular/core';
import {TEXTS} from "../utils/app.text_messages";
import {imagePaths} from "../utils/app.image_resources";

@Component({
    selector: 'app-not-found',
    templateUrl: './not-found.component.html',
    styleUrls: ['./not-found.component.scss']
})
export class NotFoundComponent {

    protected readonly text = TEXTS.wildcard
    protected readonly imagePaths = imagePaths;
}
