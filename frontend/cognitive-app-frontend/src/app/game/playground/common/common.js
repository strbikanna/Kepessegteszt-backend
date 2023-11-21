import { baseConfig } from './config.js';

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
    return `${baseConfig.baseFolder}games/${gameName}/`;
};

export const createUserParams = (params) => {
    return {
        game_id: params.game_id,
        username: params.username,
        access_token: params.access_token,
        config: params
    };
};

export const defaultValuesFromLevel = (configFunc) => (level) => {
    return configFunc(level);
};

// If the level is 0, use the given parameters, otherwise calculate default values based on the level and provided configFunc
export const determineGameParams = (params, configFunc) => {
    return params.level === 0 ? params : defaultValuesFromLevel(configFunc)(params.level || 1);
};

/* Send the gameplay result as a POST request.
 *
 * @param {Object} resultJson - The JSON result of the gameplay.
 * @param {number|string} game_id - The game's ID.
 * @param {string} username - The username of the player.
 */
export function postResult(resultJson, game_id, username, access_token, config) {
    const endpointUrl = `${baseConfig.baseURL}/gameplay`;

    const requestBody = {
        ...resultJson,
        game_id: game_id,
        username: username,
        config: config
    };

    const requestHeaders = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${access_token}`
    };

    console.log('--- Request Details ---');
    console.log('Request URL:', endpointUrl);
    console.log('Request Method:', 'POST');
    console.log('Request Headers:', JSON.stringify(requestHeaders, null, 2));
    console.log('Request Body:', JSON.stringify(requestBody, null, 2));

    fetch(endpointUrl, {
        method: 'POST',
        headers: requestHeaders,
        body: JSON.stringify(requestBody)
    })
    .then(response => response.json())
    .then(data => {
        console.log(data);
    })
    .catch(error => {
        console.error('Error posting data:', error);
    });
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
