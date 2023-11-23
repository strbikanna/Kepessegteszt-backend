import * as common from '../../common/common.js';
import { StartScene } from './scenes/StartScene.js';
import { EndScene } from './scenes/EndScene.js';
import { MainScene } from './scenes/MainScene.js';

export function initialize(params) {

    const defaultValuesFromLevel = (level) => {
        return {
            level,
            timeBetweenNumbers: 1500 - (level - 1) * 100,
            maxRound: 3 + Math.floor((level - 1) / 3),
            minNumberCount: 3 + Math.floor((level - 1) / 3),
            maxNumberCount: 3 + Math.floor((level - 1) / 2),
        };
    };

    /*
    | Level | timeBetweenNumbers | maxRound | minNumberCount | maxNumberCount |
    |-------|--------------------|----------|----------------|----------------|
    |   1   |       1500ms       |    3     |       3        |       3        |
    |   2   |       1400ms       |    3     |       3        |       4        |
    |   3   |       1300ms       |    3     |       3        |       4        |
    |   4   |       1200ms       |    4     |       4        |       5        |
    |   5   |       1100ms       |    4     |       4        |       5        |
    |   6   |       1000ms       |    4     |       4        |       6        |
    |   7   |        900ms       |    5     |       5        |       6        |
    |   8   |        800ms       |    5     |       5        |       7        |
    |   9   |        700ms       |    5     |       5        |       7        |
    |  10   |        600ms       |    6     |       6        |       7        |
    */

    const gameScenes = [StartScene, MainScene, EndScene];
    common.initialize(params, defaultValuesFromLevel, gameScenes);
}