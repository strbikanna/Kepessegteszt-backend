import * as common from '../../common/common.js';
import { StartScene } from './scenes/StartScene.js';
import { EndScene } from './scenes/EndScene.js';
import { MainScene } from './scenes/MainScene.js';

export function initialize(params) {
    // Define a function to calculate default values based on the level
    const defaultValuesFromLevel = (level) => {
        const progression = Math.floor(level / 5);
        return {
            level: level,
            maxBalloons: 2 + progression,
            minSpeed: 1 + (level - 1) * 0.1,
            maxSpeed: 1 + (level - 1) * 0.1 + 1,
            maxHealthPoints: 100 - progression * 10,
            gameTime: 100 - progression * 10
        };
    };

    const gameScenes = [StartScene, MainScene, EndScene];
    common.initialize(params, defaultValuesFromLevel, gameScenes);
}