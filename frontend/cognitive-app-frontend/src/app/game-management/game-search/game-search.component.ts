import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {UserForAdmin} from "../../admin/model/user-contacts.model";
import {FormControl} from "@angular/forms";
import {ContactService} from "../../admin/common/user_contact_autocomplete/contact_service/contact.service";
import {Game} from "../../model/game.model";
import {GameManagementService} from "../service/game-management.service";

@Component({
  selector: 'app-game-search',
  templateUrl: './game-search.component.html',
  styleUrls: ['./game-search.component.scss']
})
export class GameSearchComponent {

  @Input() text = TEXTS.game_auto_complete;
  @Output() onGameSelected: EventEmitter<Game> = new EventEmitter<Game>();

  protected gameOptions: Game[] = [];
  protected filteredGameOptions: Game[] = [];
  gameAutocompleteForm = new FormControl<Game | string>('')
  private gameCount: number = 0;

  constructor(private service: GameManagementService) { }

  ngOnInit(): void {
    this.service.getGamesCount().subscribe(count => {
      this.gameCount = count;
      this.service.getExistingGamesPaged(0, this.gameCount).subscribe(games => {
        this.gameOptions = games
            .filter(game => game.active)
            .sort((a, b) => a.name.localeCompare(b.name));
        this.filteredGameOptions = this.gameOptions.filter((val, index) => index < 10);
      });
    });

    this.gameAutocompleteForm.valueChanges.subscribe(value => {
      if(!value) return;
      const name = typeof value === 'string' ? value : value?.name;
      this.filteredGameOptions = this.filterGames(name);
    });
  }
  filterGames(value: string) : Game[]{
    if(value === '' || value === ' ') return this.gameOptions;
    const filter = value.toLowerCase();
    return this.gameOptions
        .filter(option => option.name.toLowerCase().includes(filter))
        .sort((a, b) => a.name.localeCompare(b.name))
        .filter((val, index) => index < 10);
  }
  resetAutoComplete(){
    this.gameAutocompleteForm.setValue('');
    this.filteredGameOptions = this.gameOptions.filter((val, index) => index < 10);
  }
  onGameChosen(){
    let game: Game;
    if(typeof this.gameAutocompleteForm.value === 'string'){
      const possibleGame = this.gameOptions.find(game => game.name === this.gameAutocompleteForm.value);
      if(possibleGame === undefined) return;
      game = possibleGame;
    }else{
      if(this.gameAutocompleteForm.value === null) return;
      game = this.gameAutocompleteForm.value;
    }
    this.onGameSelected.emit(game);
    this.resetAutoComplete();
  }
  convertDisplay(game: Game): string{
    return game.name;
  }
}
