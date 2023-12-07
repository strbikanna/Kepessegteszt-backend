import * as common from '../../common/common.js';
import { StartScene } from './scenes/StartScene.js';
import { EndScene } from './scenes/EndScene.js';
import { MainScene } from './scenes/MainScene.js';

export function initialize(params) {

    const defaultValuesFromLevel = (level) => {
        return {
            level,
            timeBetweenNumbers: 2000,
            maxRound: 3,
            minNumberCount: 3,
            maxNumberCount: 3,
            maxNumber: 10,
            minNumber: 1,
            operations: ['+'],
        };
    };

    const gameScenes = [StartScene, MainScene, EndScene];
    common.initialize(params, defaultValuesFromLevel, gameScenes);
}