import * as common from '../../../common/common.js';

export const MainScene = {
    key: 'MainScene',
    preload,
    create,
    update,
};

// Constants
const balloonTypes = ['balloon_red', 'balloon_green', 'balloon_blue', 'balloon_orange', 'balloon_black'];

// Game variables
let gameParams;
let balloons;
let counter;
let occupiedPositions;


// Game objects
let healthBar;
let healthText;
let scoreText;
let counterText;

// Game Results
let mistakes = 0;
let correct = 0;
let spawnedBallons = 0;
let score;
let healthPoints;

function preload() {
    this.load.setBaseURL(common.getBaseFolder('balloon-pop'));
    this.load.image('background', 'background.png');
    balloonTypes.forEach(type => this.load.image(type, `${type}.png`));
    this.load.spritesheet('explosion', 'explosion.png', { frameWidth: 64, frameHeight: 64 });
}

function getGameParameters() {
    gameParams = this.registry.get('gameParams');
    healthPoints = gameParams.maxHealthPoints;
    counter = gameParams.gameTime;
    score = 0;
    occupiedPositions = [];
}

// Create scene and spawn balloons
function create() {
    // Get game parameters
    getGameParameters.call(this);

    // Initialize background
    this.add.image(400, 300, 'background');

    // Initialize balloons group
    balloons = this.add.group();
    this.input.topOnly = true;
    
    // Spawn initial set of balloons
    for (let i = 0; i < gameParams.maxBalloons; i++) {
        spawnBalloon(this);
    }				

    // Add background shade for better visibility of the score and health bar
    let hudBackground = this.add.graphics();
    hudBackground.fillStyle(0x000000, 0.6);  // Black with 60% opacity
    hudBackground.fillRect(10, 10, 780, 50); // x, y, width, height

    // Initialize health bar
    healthBar = this.add.graphics();
    healthBar.fillStyle(0x00FF00, 1);
    healthBar.fillRect(630, 20, healthPoints * 1.5, 30);

    // Initialize score and health text
    healthText = this.add.text(520, 20, 'Élet:', { fontSize: '32px', fill: '#fff', fontStyle: 'bold' });
    scoreText = this.add.text(16, 20, 'Pont: 0', { fontSize: '32px', fill: '#fff' , fontStyle: 'bold' });
    
    // Add level text
    this.add.text(10, 560, `Szint: ${gameParams.level}`, {
         fontSize: '28px',
         fill: '#fff', 
         fontStyle: 'bold',
         stroke: '#888',
         strokeThickness: 2
    });

    // Update counter every second
    counterText = this.add.text(265, 20, 'Idő: 100', { fontSize: '32px', fill: '#fff' , fontStyle: 'bold' });
    this.time.addEvent({
        delay: 1000, // 1000 milliseconds
        callback: updateCounter,
        callbackScope: this,
        loop: true
    });

    // Add explosion animation
    this.anims.create({
        key: 'explode',
        frames: this.anims.generateFrameNumbers('explosion', { start: 0, end: 15 }),
        frameRate: 20,
        hideOnComplete: true
    });    

    // Handle clicking on balloons
    this.input.on('pointerdown', pointer => popBalloonOnPointerDown(this, pointer));
}

// Update balloon positions
function update() {
    balloons.children.iterate(updateBalloonPosition);
}

// Function to update counter
function updateCounter() {
    if (counter > 0) {
        counter--;
        counterText.setText(`Idő: ${counter}`);
        if (counter <= 10) {
            counterText.setFill('#FF0000');
        }
    } else {
        endGame.call(this);
    }
}

// Move balloon upwards
function updateBalloonPosition(balloon) {
    balloon.y -= balloon.speed;
    if (balloon.y < -50) {
        resetBalloon(balloon);
    }
}

// Update the graphical representation of the health bar
function updateHealthBar() {
    healthBar.clear();
    healthBar.fillStyle(healthPoints > 40 ? 0x00FF00 : 0xFF0000, 1);
    healthBar.fillRect(630, 20, healthPoints * 1.5, 30);
}

// Spawn a new balloon
function spawnBalloon(scene) {
    const position = generateUniquePosition();
    const randomType = Phaser.Math.RND.pick(balloonTypes);
    const newBalloon = scene.add.sprite(position.x, position.y, randomType);
    newBalloon.setScale(0.3);
    newBalloon.speed = calculateSpeed();
    balloons.add(newBalloon);
    spawnedBallons++;
}

function calculateSpeed() {
    return Phaser.Math.Between(gameParams.minSpeed, gameParams.maxSpeed);   
}

// Check for balloon pop when clicked
function popBalloonOnPointerDown(scene, pointer) {
    const clickedBalloons = balloons.getChildren().filter(balloon => 
        balloon.active && 
        Phaser.Geom.Intersects.RectangleToRectangle(balloon.getBounds(), new Phaser.Geom.Rectangle(pointer.x, pointer.y, 1, 1))
    );
    
    if (clickedBalloons.length > 0) {
        // Pop the most recently added balloon (last in the list)
        popBalloon(scene, clickedBalloons[clickedBalloons.length - 1]);
    }
}

// Handle balloon popping
function popBalloon(scene, balloon) {
    // Handle the black balloon differently
    if (balloon.texture.key === 'balloon_black') {
        healthPoints -= 10;
        mistakes++;
        updateHealthBar();
        
        // Trigger the explosion animation at the balloon's position
        let explosion = scene.add.sprite(balloon.x, balloon.y, 'explosion');
        explosion.setScale(1.5);
        explosion.play('explode');
        explosion.once('animationcomplete', () => {
            explosion.destroy();
        });

        // If health points run out, game over
        if (healthPoints <= 0) {
            endGame.call(scene);
        }

        resetBalloon(balloon);

    } else {
        score += 10;
        correct++;
        
        // Tweening effects for other balloons
        scene.tweens.add({
            targets: balloon,
            duration: 100,
            scaleX: 0.35,
            scaleY: 0.35,
            yoyo: true,
            onComplete: () => resetBalloon(balloon)
        });
    }

    // Update score display
    scoreText.setText(`Pont: ${score}`);
}

function endGame() {
    this.registry.set('gameResults', {
        spawnedBallons: spawnedBallons,
        mistakes: mistakes, 
        correct: correct, 
        score: score,
        healthPoints: healthPoints,
        timeLeft: counter,
    });
    this.scene.start('EndScene');
}

// Reset balloon position and type
function resetBalloon(balloon) {
    balloon.active = true;
    const position = generateUniquePosition();
    balloon.x = position.x;
    balloon.y = 650;
    balloon.setTexture(Phaser.Math.RND.pick(balloonTypes));
    balloon.setAngle(0);
    balloon.speed = calculateSpeed();
    console.log(`speed: ${balloon.speed} | type: ${balloon.texture.key} | x: ${balloon.x}`);
}

// Generate a unique position for a new balloon
function generateUniquePosition() {
    const segmentWidth = 100; // Make this as wide as a balloon + desired minimum spacing
    const segments = Array.from({length: Math.floor((700 - 100) / segmentWidth)}, (_, i) => ({
        xStart: 100 + i * segmentWidth,
        xEnd: 100 + (i + 1) * segmentWidth - 1,
        occupied: false
    }));
    const availableSegments = segments.filter(segment => !segment.occupied);
    
    if (availableSegments.length === 0) {
        return null; // No space left to spawn new balloons
    }

    // Randomly select an unoccupied segment
    const selectedSegment = Phaser.Math.RND.pick(availableSegments);
    selectedSegment.occupied = true;

    // Generate a position within this segment
    const position = {
        x: Phaser.Math.Between(selectedSegment.xStart, selectedSegment.xEnd),
        y: 600
    };

    // Optionally: Remove the oldest balloon to free up space
    if (occupiedPositions.length > gameParams.maxBalloons) {
        const oldestPosition = occupiedPositions.shift();
        const oldSegment = segments.find(segment => oldestPosition.x >= segment.xStart && oldestPosition.x <= segment.xEnd);
        if (oldSegment) {
            oldSegment.occupied = false;
        }
    }

    occupiedPositions.push(position);
    return position;
}