import {Component, OnInit} from '@angular/core';
import {TEXTS} from "../utils/app.text_messages";
import {Observable, of, Subject} from "rxjs";
import {GameplayModel} from "../model/gameplay.model";

@Component({
    selector: 'app-games',
    templateUrl: './games.component.html',
    styleUrls: ['./games.component.scss']
})
export class GamesComponent implements OnInit {

    public text = TEXTS.games;
    public gamesForYou : Observable<GameplayModel[]> = new Observable<GameplayModel[]>()

    ngOnInit(): void {
        this.gamesForYou = of(
            [
                {
                    id: "balloon001-223-554",
                    name: "Pop the balloons",
                    description: "Try not to die from the bombs.",
                    thumbnail: "../../assets/balloon_game.jpg",
                    config: JSON.stringify({level: 1}),
                },
                {
                    id: "radio-num-001-223-554",
                    name: "Radio buttons",
                    description: "Push the correct buttons of the secret radio.",
                    thumbnail: "../../assets/number_game.jpg",
                    config: JSON.stringify({level: 1}),

                }
            ]
        )
    }

}
