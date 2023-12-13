import * as common from '../../common/common.js';
import { StartScene } from './scenes/StartScene.js';
import { EndScene } from './scenes/EndScene.js';
import { MainScene } from './scenes/MainScene.js';

export function initialize(params) {

    const defaultValuesFromLevel = (level) => {
        return {
            level: level,
            maxRound: 5, 
            minAsteroidCount: 2 + Math.ceil(level / 3),
            maxAsteroidCount: 3 + Math.ceil(level / 2),
            maxHealthPoints: Math.max(6 - Math.ceil(level / 2), 3),
            numbersVisibalityDuration: Math.max(6000 - (level - 1) * 400, 4000),
            maxNumber: Math.min(8 + level * 2, 20),
        };
    };    

    // If the level is 0, use the given parameters, otherwise calculate default values based on the level
    const gameScenes = [StartScene, MainScene, EndScene];
    common.initialize(params, defaultValuesFromLevel, gameScenes);
}