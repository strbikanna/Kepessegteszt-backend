import {Injectable} from '@angular/core';
import {map, Observable, of} from "rxjs";
import {RecommendedGame} from "../../model/recommended_game.model";
import {SimpleHttpService} from "../../utils/simple-http.service";
import {User} from "../../model/user.model";
import {Game} from "../../model/game.model";
import {UserInfo} from "../../auth/userInfo";
import {HttpClient} from "@angular/common/http";

/**
 * Service that calls backend for recommended game data.
 */
@Injectable({
    providedIn: 'root'
})
export class RecommendedGameService{
    private readonly path = '/recommended_game'
    private readonly systemRecommendedPath = '/system_recommended'
    constructor(private http: HttpClient, private helper: SimpleHttpService) { }

    getTeacherRecommendedGames(): Observable<RecommendedGame[]> {
        return this.mockData
    }

    /**
     * Returns recommended game made by backend.
     */
    getGamesForCurrentUser(): Observable<RecommendedGame[]> {
        return this.http.get<RecommendedGame[]>(`${this.helper.baseUrl}${this.path}${this.systemRecommendedPath}`).pipe(
            map(recommendedGames =>
                recommendedGames.map(rg =>{
                    rg.config.game_id = rg.id
                    return rg
                })
            )
        )
    }

    getRecommendedGamesToUser(user: User): Observable<RecommendedGame[]> {
        let games: RecommendedGame[] = []
        this.mockData.subscribe(data => {
            data.forEach(rec_game => {
                if (rec_game.recommendedTo!.username === user.username && rec_game.recommender!.username === UserInfo.currentUser.username) {
                    games.push(rec_game)
                }
            })
        })
        return of(games)
    }

    getNotYetRecommendedGamesToUser(user: User): Observable<Game[]> {
        let games: Game[] = []
        let allGames: Game[] = []
        let already_rec_games: String[] = []
        this.mockData.subscribe(data => {
            data.forEach(rec_game => {
                if (rec_game.recommendedTo!.username === user.username && rec_game.recommender!.username === UserInfo.currentUser.username) {
                    already_rec_games.push(rec_game.game.name)
                }
            })
        })
        this.mockGames.subscribe(mockgames => allGames = mockgames)
        allGames.forEach(game => {
            if (game.active && !already_rec_games.includes(game.name)) {
                games.push(game)
            }
        })
        return of(games)
    }

    getScientistRecommendedGames(): Observable<RecommendedGame[]> {
        return this.mockData
    }


    getAllGames(): Observable<RecommendedGame[]> {
        return this.mockData
    }
    saveRecommendedGame(recommendedTo: User, game: Game, config: String) {
        let newId: string = ""
        let gameName: string = ""
        switch (game.name) {
            case "Pop the balloons": newId = "balloon-001-123-505"; gameName = "balloon-pop"; break;
            case "Radio buttons": newId = "radio-001-123-505"; gameName = "number-repeating"; break;
            case "Cosmic sequence": newId = "cosmic-sec-001-123-505"; gameName = "cosmic-sequence"; break;
        }
        let newRecommendation = {
            id: newId,
            recommender: UserInfo.currentUser,
            recommendedTo: recommendedTo,
            completed: false,
            recommendationDate: new Date(),
            game: game,
            config: {
                game_id: game.id,
                gameName: gameName,
                gameTitle: game.name,
                level: config
            }
        }
        this.mockData.subscribe(recommendations => recommendations.push(newRecommendation))
    }

    deleteRecommendedGame(recommendedGame: RecommendedGame) {
        this.mockData.subscribe(games => games.forEach( (game, index) => {
            if (game === recommendedGame) {
                games.splice(index, 1)
            }
        }))
    }

    private mockGames: Observable<Game[]> = of([
            {
                id: 2,
                name: "Pop the balloons",
                description: "Try not to die from the bombs.",
                thumbnail: "../../assets/balloon_game.jpg",
                url: undefined,
                configDescription: {
                    maxLevel: 5,
                    maxPoints: 50,
                    maxExtraPoints: 10,
                    maxTime: 60,
                    extraPointsName: "Health points",
                    pointsName: "Score",
                },
                active: true,
                version: 1,
                affectedAbilities: [],
                configItems: [],
            },
            {
                id: 3,
                name: "Radio buttons",
                description: "Push the correct buttons of the secret radio.",
                thumbnail: "../../assets/number_game.jpg",
                url: undefined,
                configDescription: {
                    maxLevel: 5,
                    maxPoints: 100,
                    maxTime: 60,
                    extraPointsName: "Extra points",
                    pointsName: "Points",
                },
                active: false,
                version: 2,
                affectedAbilities: [],
                configItems: [],
            },
            {
                id: 4,
                name: "Radio buttons",
                description: "Will you push the correct buttons of the secret radio?",
                thumbnail: "../../assets/number_game.jpg",
                url: undefined,
                configDescription: {
                    maxLevel: 6,
                    maxPoints: 100,
                    maxTime: 60,
                    extraPointsName: "Extra points",
                    pointsName: "Points",
                },
                active: false,
                version: 1,
                affectedAbilities: [],
                configItems: [],
            },
            {
                id: 6,
                name: "Radio buttons",
                description: "Push the correct buttons of the secret radio! And here comes the very and mostest extra long text to test the wrapping. ",
                thumbnail: "../../assets/number_game.jpg",
                url: undefined,
                configDescription: {
                    maxLevel: 8,
                    maxPoints: 100,
                    maxTime: 60,
                    extraPointsName: "Extra points",
                    pointsName: "Points",
                },
                active: true,
                version: 3,
                affectedAbilities: [],
                configItems: [],
            },
            {
                id: 7,
                name: "Cosmic sequence",
                description: "Enjoy space life.",
                thumbnail: "../../assets/cosmic.jpg",
                active: true,
                url: undefined,
                version: 1,
                configDescription: {
                    game_id: 3,
                    gameName: "cosmic-sequence",
                    gameTitle: "Are you a space cadet?",
                    level: 2
                },
                affectedAbilities: [],
                configItems: [],
            }
        ]
    )

    private mockData: Observable<RecommendedGame[]> = of(
        [
            {
                id: "balloon-001-123-504",
                recommender: {
                    username: "teacher_user",
                    firstName: "Teacher",
                    lastName: "Teresa",
                    email: "imaTeacher@education.hu",
                    roles: ["TEACHER"]
                },
                recommendedTo: {
                    username: "student_user",
                    firstName: "Student",
                    lastName: "Simon",
                    email: "imaStudent@gmail.com",
                    roles: ["STUDENT"]
                },
                completed: false,
                recommendationDate: new Date(),
                game: {
                    id: 2,
                    name: "Pop the balloons",
                    description: "Try not to die from the bombs.",
                    thumbnail: "../../assets/balloon_game.jpg",
                    url: undefined,
                    configDescription: {},
                    active: true,
                    version: 1,
                    affectedAbilities: [],
                    configItems: [],

                },
                config: {
                    game_id: 2,
                    gameName: "balloon-pop",
                    gameTitle: "Pop The Balloon",
                    level: 1
                },
            },
            {
                id: "radio-001-123-504",
                recommender: {
                    username: "teacher_user",
                    firstName: "Teacher",
                    lastName: "Teresa",
                    email: "imaTeacher@education.hu",
                    roles: ["TEACHER"]
                },
                recommendedTo: {
                    username: "student_user",
                    firstName: "Student",
                    lastName: "Simon",
                    email: "imaStudent@gmail.com",
                    roles: ["STUDENT"]
                },
                completed: false,
                recommendationDate: new Date(),
                game: {
                    id: 6,
                    name: "Radio buttons",
                    description: "Push the correct buttons of the secret radio.",
                    thumbnail: "../../assets/number_game.jpg",
                    url: undefined,
                    configDescription: {},
                    active: true,
                    version: 1,
                    affectedAbilities: [],
                    configItems: [],
                },
                config: {
                    game_id: 6,
                    gameName: "number-repeating",
                    gameTitle: "Rock The Radio",
                    level: 1
                },
            },
            {
                id: "cosmic-sec-001-123-504",
                recommender: {
                    username: "teacher_user",
                    firstName: "Teacher",
                    lastName: "Teresa",
                    email: "imaTeacher@education.hu",
                    roles: ["TEACHER"]
                },
                recommendedTo: {
                    username: "smart_student",
                    firstName: "Smart",
                    lastName: "Martha",
                    email: "smarty11@gmail.com",
                    roles: ["STUDENT"]
                },
                completed: false,
                recommendationDate: new Date(),
                game: {
                    id: 7,
                    name: "Cosmic sequence",
                    description: "Enjoy space life.",
                    thumbnail: "../../assets/cosmic.jpg",
                    url: undefined,
                    configDescription: {},
                    active: true,
                    version: 1,
                    affectedAbilities: [],
                    configItems: [],
                },
                config: {
                    game_id: 7,
                    gameName: "cosmic-sequence",
                    gameTitle: "Are you a space cadet?",
                    level: 2
                },
            }
        ]
    )
}
