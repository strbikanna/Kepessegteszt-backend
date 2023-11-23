import * as common from '../../../common/common.js';

export const EndScene = {
    key: 'EndScene',
    preload,
    create,
};

let gameResults;
let gameParams;
let userParams;

function preload() {
    this.load.setBaseURL(common.getBaseFolder('balloon-pop'));
    this.load.image('background', 'background.png');
}

function create() {
    gameParams = this.registry.get('gameParams');
    gameResults = this.registry.get('gameResults');

    // Initialize background
    this.add.image(400, 300, 'background');

    // Add background shade for text
    let textBackground = this.add.graphics();
    textBackground.fillStyle(0x000000, 0.3);
    textBackground.fillRect(10, 170, 780, 220); // x, y, width, height
    
    // Add Game Over text and instructions
    this.add.text(30, 180, 'Játék Vége!', { fontSize: '32px', fill: '#fff', fontStyle: 'bold', textAlign: 'center'});
    this.add.text(30, 280, `Végső pontszám: ${gameResults.score}`, { fontSize: '25px', fill: '#fff', fontStyle: 'bold'});
    this.add.text(30, 320, `Szint: ${gameParams.level}`, { fontSize: '25px', fill: '#fff', fontStyle: 'bold'});
    
    // Create a restart button with graphics
    const button = this.add.graphics();
    button.fillStyle(0x328BA8, 1);
    button.fillRoundedRect(300, 425, 200, 50, 16);
    button.setInteractive(new Phaser.Geom.Rectangle(300, 425, 200, 50), Phaser.Geom.Rectangle.Contains);

    // Add button text
    const buttonText = this.add.text(400, 450, 'Újrakezdés', { fontSize: '32px', fill: '#fff', fontStyle: 'bold' });
    buttonText.setOrigin(0.5, 0.5);

    // Add hover effect
    button.on('pointerover', () => {
        button.clear();
        button.fillStyle(0x7DA1AD, 1);
        button.fillRoundedRect(300, 425, 200, 50, 16);
    });

    button.on('pointerout', () => {
        button.clear();
        button.fillStyle(0x328BA8, 1);
        button.fillRoundedRect(300, 425, 200, 50, 16);
    });

    // Add interactivity
    button.on('pointerdown', () => {
        this.scene.start('StartScene');
    });

    userParams = this.registry.get('userParams');
    common.postResult(gameResults, gameParams, userParams.game_id, userParams.username, userParams.access_token);
}
