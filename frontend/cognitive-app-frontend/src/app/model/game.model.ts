export interface Game {
    id: string;
    name: string;
    description: string;
    thumbnail: string;
    url: string | undefined;
    config: any;
}