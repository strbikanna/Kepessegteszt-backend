import * as common from '../../../common/common.js';

export const MainScene = {
    key: 'MainScene',
    preload,
    create,
    update,
};

// Game variables
let gameParams;

let inputEnabled;
let inputSequence;
let operationsSequence;

let currentTotal;
let currentRound;
let currentIndex;

// Game objects
let inputDisplay;
let roundText;
let instructionText;

function preload() {
    this.load.setBaseURL(common.getBaseFolder('number-repeating'));
    this.load.image('background', 'background.jpg');
    this.load.audio('click', ['click.wav'])
    this.load.audio('game_over', ['game_over.wav'])
    this.load.audio('win', ['win.wav'])
}

function create() {
    gameParams = this.game.registry.get('gameParams');
    currentRound = 1;

    this.add.image(400, 300, 'background').setScale(1.7);
    instructionText = this.add.text(400, 50, 'Tartsd fejben az eredményt!', common.retroStyle).setFontSize('32px').setOrigin(0.5);
    roundText = this.add.text(30, 30, `${currentRound}/${gameParams.maxRound}`, common.retroStyle).setFontSize('32px');
    
    // Digital-style input display
    inputDisplay = this.add.text(400, 150, '0', common.retroStyle).setFontSize('32px').setOrigin(0.5);

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
    currentTotal = 0;
    inputEnabled = false;
    inputSequence = [];
    operationsSequence = [];
    currentIndex = 0;

    generateOperationsSequence();

    // Start the operation sequence playback
    this.time.addEvent({
        delay: gameParams.timeBetweenNumbers,
        callback: showNextOperation,
        callbackScope: this,
        repeat: operationsSequence.length - 1
    });

    // After the sequence playback, enable input
    this.time.delayedCall(gameParams.timeBetweenNumbers * operationsSequence.length, () => {
        inputEnabled = true;
        updateDisplay();
    });
}

function generateOperationsSequence() {
    let numberCount = Phaser.Math.Between(gameParams.minNumberCount, gameParams.maxNumberCount);
    for (let i = 0; i < numberCount; i++) {
        let operation = gameParams.operations[Phaser.Math.Between(0, gameParams.operations.length - 1)];
        const number = Phaser.Math.Between(gameParams.minNumber, gameParams.maxNumber);
/*
        if (operation === '-' && currentTotal - number < 0) {
            operation = '+';
        } else if (operation === '/' && currentTotal % number !== 0) {
            operation = '*';
        }
  */      
        operationsSequence.push(operation + number);
    }
}

function showNextOperation() {
    if (currentIndex < operationsSequence.length) {
        const operation = operationsSequence[currentIndex];
        applyOperation(operation);
        inputDisplay.setText(operation);
        currentIndex++;
    }
}

function applyOperation(operation) {
    const operator = operation.charAt(0);
    const number = parseInt(operation.substr(1), 10);

    switch (operator) {
        case '+':
            currentTotal += number;
            break;
        case '-':
            currentTotal -= number;
            break;
        case '*':
            currentTotal *= number;
            break;
        case '/':
            currentTotal = Math.floor(currentTotal / number); // Using floor to handle division resulting in fractions
            break;
        default:
            console.error('Invalid operation:', operation);
            break;
    }
}

function updateDisplay() {
    inputDisplay.text = inputSequence.join(' ');
    //roundText.setText(`${currentRound}/${gameParams.maxRound}`);

    if (inputEnabled) {
        instructionText.setText('Írd be a végső eredményt!');
    } else {
        instructionText.setText('Tartsd fejben az eredményt!');
    }
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
            if (JSON.stringify(inputSequence) === JSON.stringify(currentTotal)) {
                if (currentRound === gameParams.maxRound) {
                    // Player has completed all rounds, move to the end scene
                    endGame(true, scene);
                } else {
                    // Move to the next round
                    currentRound++;
                    scene.sound.play('win', common.soundSettings);
                    startGame.call(scene);
                    updateDisplay();
                }
            } else {
                // Incorrect total, move to end scene
                endGame(false, scene);
            }
        } else {
            scene.sound.play('click', common.soundSettings);
            inputSequence.push(parseInt(text));
        }
        updateDisplay();
    });
}

function endGame(gameWon, scene) {
    scene.registry.set('gameResults', {
        gameWon: gameWon
    });
    if (gameWon) {
        scene.sound.play('win', common.soundSettings);
    } else {
        scene.sound.play('game_over', common.soundSettings);
    }
    scene.scene.start('EndScene');
}

function update() {
    // Nothing here yet
}