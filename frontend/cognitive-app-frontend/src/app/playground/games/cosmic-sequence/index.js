import { StartScene } from './scenes/StartScene.js';
import { EndScene } from './scenes/EndScene.js';
import { MainScene } from './scenes/MainScene.js';

export function initialize(params) {
    // Define a function to calculate default values based on the level
    const defaultValuesFromLevel = (level) => {
        return {
            level: level,
            maxRound: 5 + Math.floor(level / 5) * 5, // Starts at 5, increases by 5 every 5 levels
            minAsteroidCount: 3,
            maxAsteroidCount: 3 + Math.floor(level / 5), // Starts at 3, increases by 1 every 5 levels
            maxHealthPoints: 5 - Math.floor(level / 5), // Starts at 5, decreases by 1 every 5 levels
            numbersVisibalityDuration: 5000 - Math.floor(level / 5) * 500, // Starts at 5000, decreases by 500 every 5 levels
            maxNumber: 10 + Math.floor(level / 5) * 5 // Starts at 10, increases by 5 every 5 levels
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
    console.log(`Level: ${gameParams.level}\nMax round: ${gameParams.maxRound}\nMin asteroid count: ${gameParams.minAsteroidCount}\nMax asteroid count: ${gameParams.maxAsteroidCount}\nHealth points: ${gameParams.maxHealthPoints}\nNumbers visibality duration: ${gameParams.numbersVisibalityDuration}`)
    game.scene.start('StartScene');
}