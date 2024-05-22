import {Game} from "./game.model";
import {User} from "./user.model";

export interface RecommendedGame {
    id: string | undefined;
    game: Game;
    recommender: User | null;
    recommendedTo: User | null;
    recommendationDate: Date;
    completed: boolean;
    config: any;
}