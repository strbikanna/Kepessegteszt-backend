import * as common from '../../../common/common.js';

export const StartScene = {
    key: 'StartScene',
    preload,
    create
};

function preload() {
    this.load.setBaseURL(common.getBaseFolder('number-repeating'));
    this.load.image('background', 'background.jpg');
    this.load.audio('click', ['click.wav'])
}

function create() {
    this.add.image(400, 300, 'background').setScale(1.7);
    this.add.text(400, 180, 'Number total', common.retroStyle).setOrigin(0.5);
    this.add.text(400, 240, '', common.retroStyle).setOrigin(0.5);
    this.add.text(400, 300, 'Készen állsz?', common.retroStyle).setOrigin(0.5);

    // Create a "Start" button
    createStartButton(this, 400, 400, 'Start');
}

function createStartButton(scene, x, y, text) {
    let buttonRect = scene.add.rectangle(x, y, 150, 60, 0x111111)
        .setOrigin(0.5)
        .setStrokeStyle(2, 0xA8FF98)
        .setInteractive();

    let buttonStyle = {
        ...common.retroStyle,
        fontSize: '32px'
    };

    let buttonText = scene.add.text(x, y, text, buttonStyle)
        .setOrigin(0.5);

    // Hover effect
    buttonRect.on('pointerover', function() {
        scene.tweens.killTweensOf(this);
        scene.tweens.add({
            targets: this,
            scaleX: 1.2,
            scaleY: 1.2,
            duration: 100,
        });
    });
    buttonRect.on('pointerout', function() {
        scene.tweens.killTweensOf(this);
        scene.tweens.add({
            targets: this,
            scaleX: 1,
            scaleY: 1,
            duration: 100,
        });
    });

    buttonRect.on('pointerdown', () => {
        scene.sound.play('click', common.soundSettings);
        scene.scene.start('MainScene');
    });
}