import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {AuthUser} from "../../model/user-contacts.model";
import {FormControl} from "@angular/forms";
import {ContactService} from "../../service/contact_service/contact.service";
import {Game} from "../../model/game.model";
import {GameManagementService} from "../../service/game-management/game-management.service";
import {max} from "rxjs";

@Component({
    selector: 'app-game-search',
    templateUrl: './game-search.component.html',
    styleUrls: ['./game-search.component.scss']
})
export class GameSearchComponent implements OnInit {

    @Input() text = TEXTS.game_auto_complete;
    @Input() multiple = false;
    @Input() filterGamesBy: (game: Game) => boolean = () => true;
    @Input() selectedGameIds: number[] = [];
    @Output() onGameSelected: EventEmitter<Game> = new EventEmitter<Game>();
    @Output() onMultipleGameSelected: EventEmitter<Game[]> = new EventEmitter<Game[]>();

    protected defaultGameOptions: Game[] = [];
    protected filteredGameOptions: Game[] = [];
    protected chosenGames: Game[] = [];
    gameAutocompleteForm = new FormControl<Game | string>('')

    constructor(private service: GameManagementService) {
    }

    ngOnInit(): void {
        if(this.selectedGameIds.length > 0){
            this.loadSelectedGames();
        }
        this.service.getExistingGamesPaged(0, 100).subscribe(games => {
            this.defaultGameOptions = games
                .filter(this.filterGamesBy)
                .sort((a, b) => a.name.localeCompare(b.name));
            this.filteredGameOptions = this.defaultGameOptions.slice(0, 10);
        });
        this.gameAutocompleteForm.valueChanges.subscribe(value => {
            if (!value) {
                this.filteredGameOptions = this.defaultGameOptions.slice(0, 10);
                return;
            }
            const name = typeof value === 'string' ? value : value?.name;
            this.filterGames(name);
        });
    }

    filterGames(value: string) {
        const filter = value.replaceAll(' ', '').toLowerCase();
        if (filter.length === 0) {
            this.filteredGameOptions = this.defaultGameOptions.slice(0, 10);
        } else if (filter.length < 3) {
            this.filteredGameOptions = this.defaultGameOptions
                .filter(option => option.name.toLowerCase().includes(filter))
                .sort((a, b) => a.name.localeCompare(b.name))
                .filter((val, index) => index < 10);
        } else {
            this.service.getGamesByName(filter).subscribe(games => {
                this.filteredGameOptions = games;
            });
        }
    }

    resetAutoComplete() {
        this.gameAutocompleteForm.setValue('');
        this.filteredGameOptions = this.defaultGameOptions.slice(0, 10);
    }

    onGameChosen() {
        let game: Game;
        if (typeof this.gameAutocompleteForm.value === 'string') {
            const possibleGame = this.filteredGameOptions.find(game => game.name === this.gameAutocompleteForm.value);
            if (possibleGame === undefined) return;
            game = possibleGame;
        } else {
            if (this.gameAutocompleteForm.value === null) return;
            game = this.gameAutocompleteForm.value;
        }
        if (!this.multiple) {
            this.onGameSelected.emit(game);
        } else {
            this.chosenGames.push(game);
            this.onMultipleGameSelected.emit(this.chosenGames);
        }
    }

    convertDisplay(game: Game): string {
        return game.name;
    }

    onGameRemoved(game: Game) {
        this.chosenGames = this.chosenGames.filter(g => g.id !== game.id);
        this.onMultipleGameSelected.emit(this.chosenGames);
        this.gameAutocompleteForm.setValue('');
    }
    private loadSelectedGames() {
        this.selectedGameIds.forEach(id =>{
            this.service.getGameById(id).subscribe(game => {
                this.chosenGames.push(game);
            })
        })
    }
}
