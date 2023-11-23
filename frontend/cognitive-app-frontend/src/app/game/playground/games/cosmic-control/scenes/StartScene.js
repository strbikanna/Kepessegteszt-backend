import * as common from '../../../common/common.js';

export const StartScene = {
    key: 'StartScene',
    preload,
    create,
};

// Initialize variables
let gameParams;

function preload() {
    this.load.setBaseURL(common.getBaseFolder('cosmic-control'));
    this.load.image('background', 'background.jpg');
}

function create() {
    // Get game parameters
    gameParams = this.registry.get('gameParams');

    this.add.image(400, 300, 'background').setScale(1.2);

    // Add game rules or any introductory text
    this.add.text(35, 60, 'Játékszabályok:', { fontSize: '32px', fill: '#fff', fontStyle: 'bold' });
    this.add.text(35, 120, '1. A cél minél többször nyomd le a megfelelő gombokat.', { fontSize: '20px', fill: '#fff' });
    this.add.text(35, 180, '2. A játék 3 körből áll. ', { fontSize: '20px', fill: '#fff' });
    this.add.text(35, 220, '   Az első körben a bal, a másodikban a jobb gombokat nyomd!', { fontSize: '20px', fill: '#fff' });
    this.add.text(35, 260, '   Az utolsó körben pedig felváltva nyomd a gombokat!', { fontSize: '20px', fill: '#fff' });
    this.add.text(35, 320, '3. A megfelelő gombok lenyomásával nő a pontszámod!', { fontSize: '20px', fill: '#fff' });
    this.add.text(35, 360, '   Rossz gomb esetén viszont csökken !', { fontSize: '20px', fill: '#fff' });

    // Create a start button
    const button = this.add.graphics();
    button.fillStyle(0xCC4444, 1);
    button.fillRoundedRect(300, 425, 200, 50, 16);
    button.setInteractive(new Phaser.Geom.Rectangle(300, 425, 200, 50), Phaser.Geom.Rectangle.Contains);

    const buttonText = this.add.text(400, 450, 'Start', { fontSize: '32px', fill: '#fff', fontStyle: 'bold'});
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

    // On button click, go to MainScene
    button.on('pointerdown', () => {
        this.scene.start('MainScene');
    });

    // Add level text
    this.add.text(10, 560, `Szint: ${gameParams.level}`, {
        fontSize: '32px',
        fill: '#fff',
        fontStyle: 'bold',
    });
}