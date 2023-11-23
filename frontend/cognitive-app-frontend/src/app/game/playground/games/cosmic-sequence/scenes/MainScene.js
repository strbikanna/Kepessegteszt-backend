import * as common from '../../../common/common.js';

export const MainScene = {
    key: 'MainScene',
    preload,
    create,
    update,
};

// Game variables
let gameParams;
let clickedOrder;
let correctOrder;
let round;
let newRound;
let healthPoints;

// Game objects
let checkmark;
let cross;
let spaceShip;
let asteroids;
let roundText;
let healthText;
let background1;
let background2;
let heart;

function preload() {
    this.load.setBaseURL(common.getBaseFolder('cosmic-sequence'));
    this.load.image('background1', 'background.jpg');
    this.load.image('background2', 'background.jpg');
    this.load.image('space-ship', 'space-ship.png');
    this.load.image('asteroid', 'asteroid.png');
    this.load.image('checkmark', 'checkmark.png');
    this.load.image('cross', 'cross.png');
    this.load.image('heart', 'heart.png');
    this.load.spritesheet('explosion', 'explosion.png', { frameWidth: 64, frameHeight: 64 });
}

function getGameParameters() {
    gameParams = this.registry.get('gameParams');
    healthPoints = gameParams.maxHealthPoints;
    clickedOrder = [];
    correctOrder = [];
    round = 1;
    newRound = false;
}

function create() {
    // Get game parameters
    getGameParameters.call(this);

    background1 = this.add.image(400, 300, 'background1').setScale(1.2);
    background2 = this.add.image(400, -300, 'background2').setScale(1.2); 

    spaceShip = this.add.image(400, 530, 'space-ship').setInteractive();
    spaceShip.setScale(0.2);
    // Add floating animation
    this.tweens.add({
        targets: spaceShip,
        y: 510,
        duration: 1000,
        ease: 'Sine.easeInOut',
        yoyo: true,
        repeat: -1
    });

    roundText = this.add.text(50, 50, `Kör: ${round}/${gameParams.maxRound}`, { fontSize: '32px', fill: '#fff', fontStyle: 'bold'});
    checkmark = this.add.image(400, 70, 'checkmark')
        .setVisible(false)
        .setScale(0.15);
    cross = this.add.image(400, 70, 'cross')
        .setVisible(false)
        .setScale(0.15);
    heart = this.add.image(650, 60, 'heart')
        .setScale(0.15)

    // Add level text
    this.add.text(10, 560, `Szint: ${gameParams.level}`, { fontSize: '28px', fill: '#fff', fontStyle: 'bold',});
    // Add Health Points text field
    healthText = this.add.text(680, 50, `${healthPoints}/${gameParams.maxHealthPoints}`, { fontSize: '32px', fill: '#fff', fontStyle: 'bold'});


    // Add explosion animation
    this.anims.create({
        key: 'explode',
        frames: this.anims.generateFrameNumbers('explosion', { start: 0, end: 15 }),
        frameRate: 20,
        hideOnComplete: true
    });    

    asteroids = this.add.group();
    newRound = true; // Set to true to allow spawning of new asteroids
}

function hideAsteroidNumbers() {
    asteroids.getChildren().forEach(asteroid => {
        const text = asteroid.getData('text');
        if (text) {
            text.setVisible(false);  // Hide the text
        }
        asteroid.setInteractive();  // Enable clicking on the asteroid
        cross.setVisible(false);
        checkmark.setVisible(false);
    });
}

function spawnNewAsteroids(count) {
    clickedOrder = [];
    correctOrder = [];

    const uniqueNumbers = Phaser.Utils.Array.NumberArray(0, gameParams.maxNumber - 1);
    Phaser.Utils.Array.Shuffle(uniqueNumbers);
    uniqueNumbers.splice(count);  // Remove the extra numbers
    uniqueNumbers.sort((a, b) => a - b);  // Sort in ascending order for checking later

    correctOrder = [...uniqueNumbers];  // Store the correct order

    const positions = generateNonOverlappingPositions(count, 100, 200, 750, 400);

    for (let i = 0; i < count; i++) {
        const { x, y } = positions[i];
        spawnAsteroid.call(this, uniqueNumbers[i], x, y);
    }

    this.time.addEvent({
        delay: gameParams.numbersVisibalityDuration,
        callback: hideAsteroidNumbers,
        callbackScope: this
    });
}

function generateNonOverlappingPositions(count, minX, minY, maxX, maxY) {
    let positions = [];

    for (let i = 0; i < count; i++) {
        let position;
        
        do {
            position = { x: Phaser.Math.Between(minX, maxX), y: Phaser.Math.Between(minY, maxY) };
        } while (positions.some(p => Phaser.Math.Distance.Between(p.x, p.y, position.x, position.y) < 100));
        
        positions.push(position);
    }

    return positions;
}

function spawnAsteroid(number, x, y) {
    const asteroid = this.add.image(x, y, 'asteroid');
    asteroid.setScale(0.2);

    const text = this.add.text(0, 0, number, { 
        fontSize: '40px', 
        fill: '#fff', 
        fontStyle: 'bold',
        stroke: '#333',
        strokeThickness: 2
    });
    Phaser.Display.Align.In.Center(text, asteroid);

    asteroid.setData('text', text);
    asteroid.setData('number', number);

    asteroids.add(asteroid);

    asteroid.on('pointerdown', () => {
        const explosion = this.add.sprite(asteroid.x, asteroid.y, 'explosion');
        explosion.setScale(1.5);
        explosion.play('explode');
        explosion.once('animationcomplete', () => { explosion.destroy(); });
        
        asteroid.destroy();
        text.destroy();
        
        clickedOrder.push(number);

        if (asteroids.countActive() === 0) {
            let isCorrect = clickedOrder.every((value, index) => value === correctOrder[index]);

            if (isCorrect) {
                //checkmark.setVisible(true);

                this.tweens.add({
                    targets: [background1, background2],
                    y: '+=600',  // Move each background downwards
                    duration: 2000, 
                    ease: 'Power2',
                    onComplete: () => {
                        if (round < gameParams.maxRound) {
                            round++;
                            roundText.setText(`Kör: ${round}/${gameParams.maxRound}`);
                            newRound = true; // Set to true to allow spawning of new asteroids
                        } else {
                            endGame.call(this, true);
                        }
                    }
                });
                
            } else {
                cross.setVisible(true);

                healthPoints -= 1;
                healthText.setText(`${healthPoints}/${gameParams.maxHealthPoints}`);

                // Check if health points drop to zero
                if (healthPoints <= 0) {
                    endGame.call(this, false);
                }

                newRound = true; // Set to true to allow spawning of new asteroids
                //checkmark.setVisible(false);
            }
        }
    });
}

function endGame(gameWon) {
    this.registry.set('gameResults', {
        gameWon: gameWon,
        round: round,
        healthPoints: healthPoints,
    });
    this.scene.start('EndScene');
}

function update() {

    // Reset the images when they move out of view to create a seamless loop
    if (background1.y >= 900) {  // Assuming the image height is 600 and scaled by 1.2
        background1.y = -300;
    }

    if (background2.y >= 900) {
        background2.y = -300;
    }
    
    if (asteroids.countActive() === 0 && newRound) {
        newRound = false; // Set to false to prevent continuous spawning
        
        let randomAsteroidCount;
        if (round === 1) {
            randomAsteroidCount = 3;
        } else {
            randomAsteroidCount = Phaser.Math.Between(gameParams.minAsteroidCount, gameParams.maxAsteroidCount);
        }
        
        spawnNewAsteroids.call(this, randomAsteroidCount); // Respawn a random number of asteroids
    }
}
