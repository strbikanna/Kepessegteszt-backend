import { config } from './config.js';

export function postResult(resultJson, gamePlayId) {
    const endpointUrl = `${config.baseURL}/gameplay/${gamePlayId}`; //TODO
    fetch(endpointUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(resultJson)
    })
    .then(response => response.json())
    .then(data => {
        console.log(data);  // Handle the response data if needed
    })
    .catch(error => {
        console.error('Error posting data:', error);
    });
}
