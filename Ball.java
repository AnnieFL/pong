public class Ball {
        int initialX;
        int initialY;
        int x;
        int y;
        int screenSizeX;
        int screenSizeY;

        int xSpeed = generateSpeed('X');
        int ySpeed = generateSpeed('Y');
        int maxXSpeed = 35;
        int maxYSpeed = 10;

        int size = 25;

        public Ball(int x, int y, int screenSizeX, int screenSizeY) {
                this.initialX = x;
                this.x = x;
                this.initialY = y;
                this.y = y;

                this.screenSizeX = screenSizeX;
                this.screenSizeY = screenSizeY;
        }

        public int generateSpeed(char axis) {
                if (axis == 'X') {
                        return (Math.floor((Math.random() * 10) % 2) == 0) ? 5 : -5;
                } else if (axis == 'Y') {
                        return (Math.floor((Math.random() * 10) % 2) == 0) ? 3 : -3;
                }
                return 0;
        }

        public void move() {
                this.x += this.xSpeed;
                this.y += this.ySpeed;
        }

        public void reset() {
                this.x = this.initialX;
                this.y = this.initialY;

                this.xSpeed = generateSpeed('X');
                this.ySpeed = generateSpeed('Y');
        }
}
