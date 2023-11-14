import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Ability} from "../../model/ability.model";

@Component({
  selector: 'app-edit-game-form',
  templateUrl: './edit-game-form.component.html',
  styleUrls: ['./edit-game-form.component.scss']
})
export class EditGameFormComponent {

  form = new FormGroup({
    name: new FormControl('', Validators.required),
    description: new FormControl('', Validators.required),
    thumbnail: new FormControl('', Validators.required),
    active: new FormControl(false, Validators.required),
    abilities: new FormControl<Ability[]>([], Validators.required),
    configDescription: new FormControl('', Validators.required),
  })
}
