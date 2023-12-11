import * as common from '../../../common/common.js';

export const EndScene = {
    key: 'EndScene',
    preload,
    create,
};

// Initialize variables
let finalRound;
let remainingHealthPoints;
let endMessage;
let gameParams;
let gameResults;
let userParams;
let gameWon;

function preload() {
    this.load.setBaseURL(common.getBaseFolder('cosmic-sequence'));
    this.load.image('background', 'background.jpg');
}

function create() {
    // Get game parameters
    gameParams = this.registry.get('gameParams');
    gameResults = this.registry.get('gameResults');
    finalRound = this.registry.get('finalRound');
    remainingHealthPoints = this.registry.get('remainingHealthPoints');

    if (gameResults.gameWon) {
        endMessage = 'Nyertél!';
    } else {
        endMessage = 'Próbáld újra!';
    }

    this.add.image(400, 300, 'background').setScale(1.2);

    // Display final score and life
    this.add.text(30, 180, endMessage, { fontSize: '48px', fill: '#fff', fontStyle: 'bold' });
    this.add.text(30, 250, `Kör: ${gameResults.round}/${gameParams.maxRound}`, { fontSize: '32px', fill: '#fff' });
    this.add.text(30, 300, `Maradék élet: ${gameResults.healthPoints}`, { fontSize: '32px', fill: '#fff' });

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

    userParams = this.registry.get('userParams');
    common.postResult(gameResults, gameParams, userParams.game_id, userParams.username, userParams.access_token);
}