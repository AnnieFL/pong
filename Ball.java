public class Ball {

        int x = 400;
        int y = 300;

        int initXSpeed = (Math.floor((Math.random() * 10) % 2) == 0) ? 3 : -3;
        int initYSpeed = (Math.floor((Math.random() * 10) % 2) == 0) ? 3 : -3;

        int xSpeed = initXSpeed;
        int ySpeed = initYSpeed;
        int maxXSpeed = 35;
        int maxYSpeed = 10;

        int size = 25;
}
