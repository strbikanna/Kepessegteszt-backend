import * as common from '../../../common/common.js';

export const EndScene = {
    key: 'EndScene',
    preload,
    create,
};

// Initialize variables
let finalScore;
let gameParams;

function preload() {
    this.load.setBaseURL(common.getBaseFolder('cosmic-control'));
    this.load.image('background', 'background.jpg');
}

function create() {
    // Get game parameters
    gameParams = this.registry.get('gameParams');
    finalScore = this.registry.get('finalScore');

    this.add.image(400, 300, 'background').setScale(1.2);

    // Display final score and life
    this.add.text(100, 250, `Végső pont: ${finalScore}`, { fontSize: '48px', fill: '#fff', fontStyle: 'bold' });

    // Create a restart button
    const button = this.add.graphics();
    button.fillStyle(0xCC4444, 1);
    button.fillRoundedRect(300, 425, 200, 50, 16);
    button.setInteractive(new Phaser.Geom.Rectangle(300, 425, 200, 50), Phaser.Geom.Rectangle.Contains);

    const buttonText = this.add.text(400, 450, 'Újrakezdés', { fontSize: '32px', fill: '#fff', fontStyle: 'bold'});
    buttonText.setOrigin(0.5, 0.5);

    // Hover effect for the button
    button.on('pointerover', () => {
        button.clear();
        button.fillStyle(0xDD5555, 1);
        button.fillRoundedRect(300, 425, 200, 50, 16);
    });

    button.on('pointerout', () => {
        button.clear();
        button.fillStyle(0xCC4444, 1);
        button.fillRoundedRect(300, 425, 200, 50, 16);
    });

    // On button click, restart the game (go to StartScene)
    button.on('pointerdown', () => {
        this.scene.start('StartScene');
    });
}