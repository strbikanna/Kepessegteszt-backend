import { StartScene } from './scenes/StartScene.js';
import { EndScene } from './scenes/EndScene.js';
import { MainScene } from './scenes/MainScene.js';

export function initialize(params) {

    const defaultValuesFromLevel = (level) => {
        let minNumCount = 3 + Math.floor(level / 5);
    
        return {
            level: level,
            timeBetweenNumbers: 1500 - Math.floor(level / 5) * 200,
            maxRound: 3 + Math.floor(level / 5),
            minNumberCount: minNumCount,
            maxNumberCount: minNumCount + Math.floor(level / 5)
        };
    };    

    // If the level is 0, use the given parameters, otherwise calculate default values based on the level
    const gameParams = params.level === 0 ? params : defaultValuesFromLevel(params.level || 1);

	// Configuration for the Phaser game
    const config = {
        type: Phaser.AUTO,
        parent: 'game-container',
        width: 800,
        height: 600,
        scene: [StartScene, MainScene, EndScene],
        scale: {
            mode: Phaser.Scale.FIT,
            autoCenter: Phaser.Scale.CENTER_BOTH
        },
    };

    const retroStyle = {
        fontSize: '26px',
        fill: '#A8FF98',  // Retro greenish color
        fontFamily: 'Courier New',
        shadow: {
            offsetX: 0,
            offsetY: 0,
            color: '#A8FF98',  // Glow effect
            blur: 8,
            fill: true
        }
    };

    const soundSettings = {
        volume: 0.3
    };

    // Create the Phaser game instance
    let game = new Phaser.Game(config);
    game.registry.set('gameParams', gameParams);
    game.registry.set('gamePlayId', params.gamePlayId);
    game.registry.set('retrostyle', retroStyle);
    game.registry.set('soundSettings', soundSettings);
    game.scene.start('StartScene');
}