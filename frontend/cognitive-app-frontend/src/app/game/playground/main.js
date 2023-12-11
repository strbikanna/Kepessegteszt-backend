export function initializeGame(params) {
    document.getElementById('fullscreen-btn').addEventListener('click', () => {
        fullscreenButton()
    });
	document.querySelector("header h1").innerText = params.gameTitle;

	import(`./games/${params.gameName}/index.js`)
		.then(gameModule => {
			gameModule.initialize(params);
		})
		.catch(err => {
			console.error("Failed to load the game module:", err);
		});
}


function fullscreenButton(){
    if (document.fullscreenElement) {
        document.exitFullscreen();
    } else {
        let gameContainer = document.getElementById('game-container');
        if (gameContainer.requestFullscreen) {
            gameContainer.requestFullscreen();
        } else if (gameContainer.msRequestFullscreen) { /* IE/Edge */
            gameContainer.msRequestFullscreen();
        } else if (gameContainer.mozRequestFullScreen) { /* Firefox */
            gameContainer.mozRequestFullScreen();
        } else if (gameContainer.webkitRequestFullscreen) { /* Chrome, Safari & Opera */
            gameContainer.webkitRequestFullscreen();
        }
    }
}
