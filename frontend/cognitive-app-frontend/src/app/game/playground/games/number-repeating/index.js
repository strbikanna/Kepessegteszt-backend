import * as common from '../../common/common.js';
import { StartScene } from './scenes/StartScene.js';
import { EndScene } from './scenes/EndScene.js';
import { MainScene } from './scenes/MainScene.js';

export function initialize(params) {

    const defaultValuesFromLevel = (level) => {
        const progression = Math.floor(level / 5);
        return {
            level,
            timeBetweenNumbers: 1500 - progression * 200,
            maxRound: 3 + progression,
            minNumberCount: 3 + progression,
            maxNumberCount: 3 + progression + progression
        };
    };

    // If the level is 0, use the given parameters, otherwise calculate default values based on the level
    const gameParams = common.determineGameParams(params, defaultValuesFromLevel);
    const gameConfig = common.getBaseGameConfig([StartScene, MainScene, EndScene]);
    const userParams = common.createUserParams(params);

    // Create the Phaser game instance
    let game = new Phaser.Game(gameConfig);
    game.registry.set('gameParams', gameParams);
    game.registry.set('userParams', userParams);
    game.scene.start('StartScene');
}