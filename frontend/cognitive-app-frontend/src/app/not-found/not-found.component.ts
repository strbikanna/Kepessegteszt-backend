import { Component } from '@angular/core';
import {TEXTS} from "../utils/app.text_messages";

@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.scss']
})
export class NotFoundComponent {

  text = TEXTS.wildcard
}
