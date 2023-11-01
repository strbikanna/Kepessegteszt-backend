import * as common from '../../../common/common.js';

export const MainScene = {
    key: 'MainScene',
    preload,
    create,
    update,
};

// Game variables
let gameParams;
let currentRound;
let inputEnabled;
let numberSequence;
let inputSequence;
let currentIndex;

// Game objects
let inputDisplay;
let roundText;
let instructionText;

function preload() {
    this.load.setBaseURL(common.getBaseFolder('number-repeating'));
    
    for (let i = 1; i <= 9; i++) {
        this.load.audio(`${i}`, [`${i}.mp3`]);
    }
    this.load.audio('click', ['click.wav'])
    this.load.audio('game_over', ['game_over.wav'])
    this.load.audio('win', ['win.wav'])

    this.load.image('background', 'background.jpg');
}

function create() {
    gameParams = this.game.registry.get('gameParams');
    currentRound = 1;
    inputEnabled = false;

    this.add.image(400, 300, 'background').setScale(1.7);
    instructionText = this.add.text(400, 50, 'Hallgasd a számokat!', common.retroStyle).setFontSize('32px').setOrigin(0.5);
    roundText = this.add.text(30, 30, `${currentRound}/${gameParams.maxRound}`, common.retroStyle).setFontSize('32px');

    // Digital-style input display
    inputDisplay = this.add.text(400, 100, '', common.retroStyle).setFontSize('32px').setOrigin(0.5);

    // Adding on-screen number buttons 1-9
    for (let i = 1; i <= 9; i++) {
        let xPos = 270 + ((i - 1) % 3) * 125;
        let yPos = 225 + Math.floor((i - 1) / 3) * 100;

        createNumberButton(this, xPos, yPos, `${i}`);
    }

    // Additional row for 'C', '0', and 'OK' buttons
    createNumberButton(this, 270, 525, 'C');
    createNumberButton(this, 395, 525, '0');
    createNumberButton(this, 520, 525, 'OK');

    startGame.call(this);
}

function startGame() {
    // Clear previous sequences
    numberSequence = [];
    inputSequence = [];
    currentIndex = 0;
    inputEnabled = false;

    let numberCount = Phaser.Math.Between(gameParams.minNumberCount, gameParams.maxNumberCount);

    // Generate a sequence based on the current round
    for (let i = 0; i < numberCount; i++) {
        numberSequence.push(Phaser.Math.Between(1, 9));
    }

    // After the sequence playback, enable input
    this.time.delayedCall((gameParams.timeBetweenNumbers * numberSequence.length + 1) + 1000, () => {
        inputEnabled = true;
        updateDisplay();
    });

    // Initialize the timer
    const timer = this.time.addEvent({
        delay: gameParams.timeBetweenNumbers,
        callback: playNextNumber,
        callbackScope: this,
        repeat: numberSequence.length - 1
    });
}

function createNumberButton(scene, x, y, text) {
    let bgColor = 0x111111;
    if (text === 'C') {
        bgColor = 0x333333; // Red
    } else if (text === 'OK') {
        bgColor = 0x333333; // Green
    } else {
        bgColor = 0x111111; // Dark gray for number buttons
    }

    // Create a rectangle for the button
    let buttonRect = scene.add.rectangle(x, y, 75, 50, bgColor)
        .setOrigin(0.5)
        .setStrokeStyle(2, 0xA8FF98)  // Green outline
        .setInteractive();  // Make it interactive

    let buttonStyle = common.retroStyle;
    if (text === 'C' || text === 'OK') {
        buttonStyle = {
            ...common.retroStyle,
            fontSize: '32px' // Larger font size for 'C' and 'OK'
        };
    }

    let buttonText = scene.add.text(x, y, text, buttonStyle)
        .setOrigin(0.5);

    // Hover effect with smooth transition
    buttonRect.on('pointerover', function() {
        scene.tweens.killTweensOf(this); // Stop any tweens on this container
        scene.tweens.add({
            targets: this,
            scaleX: 1.2,
            scaleY: 1.2,
            duration: 100,
        });
    });
    buttonRect.on('pointerout', function() {
        scene.tweens.killTweensOf(this); // Stop any tweens on this container
        scene.tweens.add({
            targets: this,
            scaleX: 1,
            scaleY: 1,
            duration: 100,
        });
    });

    buttonRect.on('pointerdown', () => {
        if (!inputEnabled) return;
      
        if (text === 'C') {
            inputSequence = [];
            scene.sound.play('click', common.soundSettings);
        } else if (text === 'OK') {
            if (JSON.stringify(inputSequence) === JSON.stringify(numberSequence)) {
                // Player got the correct sequence
                if (currentRound === gameParams.maxRound) {
                    // Player has completed all rounds, move to the end scene
                    scene.registry.set('gameResult', true);
                    scene.sound.play('win', common.soundSettings);
                    scene.scene.start('EndScene');
                } else {
                    // Move to the next round
                    currentRound++;
                    scene.sound.play('win', common.soundSettings);
                    startGame.call(scene);
                    updateDisplay();
                }
            } else {
                // Incorrect sequence, move to end scene
                scene.registry.set('gameResult', false);
                scene.sound.play('game_over', common.soundSettings);
                scene.scene.start('EndScene');
            }
        } else {
            scene.sound.play('click', common.soundSettings);
            inputSequence.push(parseInt(text));
        }
        updateDisplay();
    });
}

function updateDisplay() {
    inputDisplay.text = inputSequence.join(' ');
    roundText.setText(`${currentRound}/${gameParams.maxRound}`);

    if (inputEnabled) {
        instructionText.setText('Írd be a számokat!');
    } else {
        instructionText.setText('Hallgasd a számokat!');
    }
}

function playNextNumber() {
    if (currentIndex < numberSequence.length) {
        const number = numberSequence[currentIndex];
        this.sound.play(`${number}`);
        currentIndex++;
    }
}

function update() {
    // Nothing here yet
}
