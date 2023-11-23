import * as common from '../../../common/common.js';

export const MainScene = {
    key: 'MainScene',
    preload,
    create,
    update,
};

let currentRound;
let timer;
let score;
let backgroundSpeed;

let leftButton;
let rightButton;
let leftKey;
let rightKey;

let background1;
let background2;
let spaceShip;

let scoreContainer;
let timerProgressBar;
let roundIndicators;
let speedIndicator;
let scoreText;

function preload() {
    this.load.setBaseURL(common.getBaseFolder('cosmic-control'));
    this.load.image('background1', 'background.jpg');
    this.load.image('background2', 'background.jpg');
    this.load.image('space-ship', 'space-ship.png');
}

function create() {
    background1 = this.add.image(400, 300, 'background1').setScale(1.2);
    background2 = this.add.image(400, -300, 'background2').setScale(1.2);

    spaceShip = this.add.image(400, 530, 'space-ship').setScale(0.2);
    this.tweens.add({
        targets: spaceShip,
        y: 510,
        duration: 1000,
        ease: 'Sine.easeInOut',
        yoyo: true,
        repeat: -1
    });

    currentRound = 1;
    score = 0;
    backgroundSpeed = 2;

    // Score Container
    scoreText = this.add.text(30, 25, `Pont: ${score}`, {
        fontSize: '36px',
        fill: '#fff',
        fontStyle: 'bold',
    });

    // Speed Indicator Bar - Outer box
    //this.add.rectangle(spaceShip.x + 80, 520, 22, 102, 0x000000, 1).setStrokeStyle(2, 0xffffff);
    // Speed Indicator Bar - Actual progress
    //speedIndicator = this.add.rectangle(spaceShip.x + 80, 560, 20, 0, 0x00FFFF); // (x, y, width, height)

    // Timer Progress Bar - Outer box
    this.add.rectangle(400, 40, 202, 22, 0x000000, 1).setStrokeStyle(2, 0xffffff);

    // Timer Progress Bar - Actual progress
    timerProgressBar = this.add.rectangle(500, 40, 200, 20, 0x00FF00);
    timerProgressBar.setOrigin(1, 0.5);


    // Round Indicators
    roundIndicators = [];
    for (let i = 0; i < 3; i++) {
        const indicator = this.add.circle(650 + (i * 30), 45, 10, (i < currentRound) ? 0x00FF00 : 0x888888);
        roundIndicators.push(indicator);
    }

    // Left Button
    leftButton = this.add.rectangle(100, 300, 100, 100, 0x00FF00).setAlpha(0.5).setInteractive();
    this.add.text(85, 280, 'A', {
        fontSize: '36px',
        fill: '#fff',
        fontStyle: 'bold',
        fontFamily: 'Arial'
    });

    // Right Button
    rightButton = this.add.rectangle(700, 300, 100, 100, 0xFF0000).setAlpha(0.5).setInteractive();
    this.add.text(685, 280, 'L', {
        fontSize: '36px',
        fill: '#fff',
        fontStyle: 'bold',
        fontFamily: 'Arial'
    });

    // Keyboard Controls
    leftKey = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.A);
    rightKey = this.input.keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.L);

    setupRound.call(this);
}

function setupRound() {
    if (timer) {
        timer.destroy();
    }

    // Gray out both buttons during the setup
    leftButton.setAlpha(0.2);
    rightButton.setAlpha(0.2);

    // Remove previous event listeners
    leftButton.removeAllListeners('pointerdown');
    rightButton.removeAllListeners('pointerdown');
    leftKey.removeAllListeners('down');
    rightKey.removeAllListeners('down');

    // Adding a delay of 3 seconds between rounds
    this.time.delayedCall(3000, () => {
        // Start the timer for the round after the delay
        timer = this.time.addEvent({
            delay: 10000, // 10 seconds per round
            callback: endRound,
            callbackScope: this,
            loop: false
        });

        // Set up the buttons after the delay
        updateButtonStatus();

        let lastAction = 'right';

        const handleLeftButton = () => {
            if (currentRound === 1) {
                score++;
                speedUpBackground();
            } else if (currentRound === 3 && lastAction === 'right') {
                score++;
                speedUpBackground();
                lastAction = 'left';
            } else {
                if (score > 0) {
                    score--;
                }
                slowDownBackground();
            }
            updateScoreText();
        };

        const handleRightButton = () => {
            if (currentRound === 2) {
                score++;
                speedUpBackground();
            } else if (currentRound === 3 && lastAction === 'left') {
                score++;
                speedUpBackground();
                lastAction = 'right';
            } else {
                if (score > 0) {
                    score--;
                }
                slowDownBackground();
            }
            updateScoreText();
        };

        leftButton.on('pointerdown', handleLeftButton);
        rightButton.on('pointerdown', handleRightButton);

        leftKey.on('down', handleLeftButton);
        rightKey.on('down', handleRightButton);
    });
}

function speedUpBackground() {
    backgroundSpeed += 0.5;
    backgroundSpeed = Math.min(backgroundSpeed, 10);
}

function slowDownBackground() {
    backgroundSpeed -= 1;
    backgroundSpeed = Math.max(backgroundSpeed, 2);
}

function updateScoreText() {
    scoreText.setText(`Pont: ${score}`);
}

function updateButtonStatus() {
    if (currentRound === 1) {
        leftButton.setAlpha(1);
        rightButton.setAlpha(0.2);
    }
    else if (currentRound === 2) {
        leftButton.setAlpha(0.2);
        rightButton.setAlpha(1);
    }
    else if (currentRound === 3) {
        leftButton.setAlpha(1);
        rightButton.setAlpha(1);
    }
}

function endRound() {
    if (currentRound < 3) {
        currentRound++;
        setupRound.call(this);
    } else {
        this.registry.set('finalScore', score);
        this.scene.start('EndScene');
    }
}

function update() {
    // Move the background
    background1.y += backgroundSpeed;
    background2.y += backgroundSpeed;

    if (background1.y >= 900) {
        background1.y = -300;
    }

    if (background2.y >= 900) {
        background2.y = -300;
    }

    // Update Timer Progress Bar
    if (timer && timer.getElapsed) {
        let elapsedTime = (10 - timer.getElapsed() / 1000).toFixed(1);
        timerProgressBar.width = 200 * (elapsedTime / 10);  
    }

    // Update Speed Indicator Bar
    //speedIndicator = this.add.rectangle(spaceShip.x + 80, 560, 20, 20, 0x00FFFF); // (x, y, width, height)
    
    // Update Round Indicators
    for (let i = 0; i < 3; i++) {
        roundIndicators[i].fillColor = (i < currentRound) ? 0x00FF00 : 0x888888;
    }
}