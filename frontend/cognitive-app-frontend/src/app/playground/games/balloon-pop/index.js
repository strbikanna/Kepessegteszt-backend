import { StartScene } from './scenes/StartScene.js';
import { EndScene } from './scenes/EndScene.js';
import { MainScene } from './scenes/MainScene.js';

export function initialize(params) {
    // Define a function to calculate default values based on the level
    const defaultValuesFromLevel = (level) => {
        return {
            level: level,
            maxBalloons: 2 + Math.floor(level / 5),
            minSpeed: 2 + (level - 1) * 0.2,
            maxSpeed: 2 + (level - 1) * 0.2 + 2,
            maxHealthPoints: 100 - Math.floor(level / 5) * 10,
            gameTime: 100 - Math.floor(level / 5) * 10
        };
    };

    // If the level is 0, use the given parameters, otherwise calculate default values based on the level
    const gameParams = params.level === 0 ? params : defaultValuesFromLevel(params.level || 1);

    // Define the game configuration
    const config = {
        type: Phaser.AUTO,
        parent: 'game-container',
        width: 800,
        height: 600,
        scene: [StartScene, MainScene, EndScene],
        scale: {
            mode: Phaser.Scale.FIT,
            autoCenter: Phaser.Scale.CENTER_BOTH
        }
    };

    // Create a new Phaser game with the configuration and set the game parameters in the registry
    const game = new Phaser.Game(config);
    game.registry.set('gameParams', gameParams);
    console.log(`Level: ${gameParams.level}\nMax balloons: ${gameParams.maxBalloons}\nMin speed: ${gameParams.minSpeed}\nMax speed: ${gameParams.maxSpeed}\nHealth points: ${gameParams.maxHealthPoints}\nGame time: ${gameParams.gameTime}`)
    game.scene.start('StartScene');
}