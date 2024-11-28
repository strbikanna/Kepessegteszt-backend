export interface RecommendedGame {
    id: number | undefined;
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
