import { baseConfig } from '../common/config.js';

/* Send the gameplay result as a POST request.
 *
 * @param {Object} gameResults - The JSON result of the gameplay.
 * @param {Object} gameParams - The JSON of the gameParams.
 * @param {number|string} game_id - The game's ID.
 * @param {string} username - The username of the player.
 * @param {string} access_token - The access token of the player.
 */
export function postResult(gameResults, gameParams, game_id, username, access_token) {
    const endpointUrl = `${baseConfig.baseURL}/gameplay`;

    const requestBody = {
        gameResults,
        gameParams,  
        game_id: game_id,
        username: username
    };

    const requestHeaders = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${access_token}`
    };

    console.groupCollapsed('%cRequest Details', 'color: green; font-weight: bold;');
    console.log('%cURL:', 'color: green;', endpointUrl);
    console.log('%cMethod:', 'color: green;', 'POST');
    console.log('%cHeaders:', 'color: green;', JSON.stringify(requestHeaders, null, 2));
    console.log('%cBody:', 'color: green;', JSON.stringify(requestBody, null, 2));
    console.groupEnd();

    fetch(endpointUrl, {
        method: 'POST',
        headers: requestHeaders,
        body: JSON.stringify(requestBody)
    })
    .then(response => response.json())
    .then(data => {
        console.groupCollapsed('%cResponse Data', 'color: green; font-weight: bold;');
        console.log(data);
        console.groupEnd();
    })
    .catch(error => {
        console.error('%cError posting data:', 'color: red; font-weight: bold;', error);
    });
}

// Initialize a specific game
export function initialize(params, defaultValuesFunc, gameScenes) {
    const gameParams = determineGameParams(params, defaultValuesFunc);
    const gameConfig = getBaseGameConfig(gameScenes);
    const userParams = createUserParams(params);

    let game = new Phaser.Game(gameConfig);
    game.registry.set('gameParams', gameParams);
    game.registry.set('userParams', userParams);
    game.scene.start('StartScene');
    printTableForLevels(defaultValuesFunc, 10);
    logGameParams(gameParams);
}

// Base Game Configuration
export const getBaseGameConfig = (scenes) => {
    return {
        type: Phaser.AUTO,
        parent: 'game-container',
        width: 800,
        height: 600,
        scene: scenes,
        scale: {
            mode: Phaser.Scale.FIT,
            autoCenter: Phaser.Scale.CENTER_BOTH
        },
    };
};

export const getBaseFolder = (gameName) => {
    return `${baseConfig.baseFolder}game_images/${gameName}/assets/`;
};

export const createUserParams = (params) => {
    return {
        game_id: params.game_id,
        username: params.username,
        access_token: params.access_token,
    };
};

export const defaultValuesFromLevel = (configFunc) => (level) => {
    return configFunc(level);
};

// If the level is 0, use the given parameters, otherwise calculate default values based on the level and provided configFunc
export const determineGameParams = (params, configFunc) => {
    return params.level === 0 ? params : defaultValuesFromLevel(configFunc)(params.level || 1);
};

export const logGameParams = (gameParams) => {
    if (gameParams.level > 10) {
        console.error("Too high level: ", gameParams.level)
    }
    //console.groupCollapsed('%cGame Parameters', 'color: green; font-weight: bold;');
    console.log(JSON.stringify(gameParams, null, 2));
    //console.groupEnd();
};

export const printTableForLevels = (configFunction, maxLevel) => {
    // Fetch the first row to get headers
    const firstRow = configFunction(1);
    const headers = Object.keys(firstRow);

    // Calculate max lengths for each column to ensure alignment
    const maxLengths = headers.reduce((acc, header) => {
        const maxDataLength = Math.max(...Array.from({length: maxLevel}, (_, i) => 
            formatValue(configFunction(i + 1)[header]).length
        ));
        
        acc[header] = Math.max(header.length, maxDataLength); // Choose the greater length
        return acc;
    }, {});

    // Create and log header row
    const headerRow = headers.map(header => header.padEnd(maxLengths[header] + 2)).join(' | ');
    console.log(headerRow);

    // Print each row of data
    for (let i = 1; i <= maxLevel; i++) {
        const rowData = configFunction(i);
        const rowString = headers.map(header => 
            formatValue(rowData[header]).padEnd(maxLengths[header] + 2)
        ).join(' | ');
        
        console.log(rowString);
    }
};

// Helper function to format value to 2 decimal places if it's a floating number
function formatValue(value) {
    if (typeof value === 'number' && !Number.isInteger(value)) {
        return value.toFixed(2);
    }
    return String(value);
}

export const retroStyle = {
    fontSize: '26px',
    fill: '#A8FF98',
    fontFamily: 'Courier New',
    shadow: {
        offsetX: 0,
        offsetY: 0,
        color: '#A8FF98',
        blur: 8,
        fill: true
    }
};

export const soundSettings = {
    volume: 0.3
};