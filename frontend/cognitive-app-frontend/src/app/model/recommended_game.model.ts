import {User} from "./user.model";

export interface RecommendedGame {
    id: string | undefined;
    gameId: number;
    name: string;
    description: string;
    thumbnail: string;
    recommender?: string | null;
    recommendedTo: string;
    recommendationDate: Date;
    completed: boolean;
    config: any;
}

export const mockRecommendedGame: RecommendedGame = {
    id: "1",
    gameId: 1,
    name: "Game",
    description: "Description",
    thumbnail: "Thumbnail",
    recommender: null,
    recommendedTo: "Student Simon",
    recommendationDate: new Date(),
    completed: true,
    config: {
        "config1": 1,
        "config2": 2
    }
}