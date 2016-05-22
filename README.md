#The Matrix

Control Neo with the arrow keys to dodge deadly projectiles.

The Matrix is made from the following classes:

Game: Contains the top-level frame that contains the game and allows it to run.

GameCourt: Contains the majority of the logic and functionality of the game. Displays the playing area, the player, various projectiles and hearts, shows the score, is able to set and retrieve high scores, and is repainted depending on the screen that the user is on.

State: Contains the various states that the game can be in, i.e. high scores screen, menu screen, playing, instructions, and game over.

GameObj: Contains the most basic information about each game object, like position, height, width, and bounds.

Neo: Extends GameObj. The player's character. Has between 0 and 5 lives. The game ends when Neo runs out of lives. He moves instantaneously in fixed distances.

Heart: Extends GameObj. Hearts spawn randomly in the game. Colliding with one increases lives by one or adds 5 to score if Neo already has 5 lives.

Projectile: Extends GameObj. Abstract class that adds velocity and more advanced collision detection for the various projectiles.

Circle: The first projectile that takes one life per hit.

Spike: The second projectile that takes 2 lives per hit.

Bomb: The third projectile that takes all lives on hit.

Playing the game should be clear: just press play to play, moving with the arrow keys, instructions to learn more about the game, and high scores to see high scores.