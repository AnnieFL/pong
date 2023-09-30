public class Player {
    int x;
    int y;
    int initialX;
    int initialY;
    int sizeX = 20;
    int sizeY = 100;
    int screenSizeY;
    int screenSizeX;

    int speed = 0;
    int points = 0;

    int speedIncrease = 12;
    
    public Player(int x, int y, int screenSizeX, int screenSizeY){
        this.x = x;
        this.initialX = x;
        this.y = y;
        this.initialY = y;

        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;
    }

    public void move() {
        if (this.speed > 0) {
            this.y = Math.min(this.y + this.speed, this.screenSizeY - this.sizeY);
        } else if (this.speed < 0) {
            this.y = Math.max(this.y + this.speed, 0);
        }
    }

    public void resetPos() {
        this.x = this.initialX;
        this.y = this.initialY;
    }    

    public void reset() {
        this.x = this.initialX;
        this.y = this.initialY;
        this.points = 0;
    }

    public void autoMove(Player player, Ball ball, int screenSizeX, int screenSizeY) {
        if (Math.ceil(Math.random() * 100) <= 50 && player.points - this.points < 2) {
            return;
        }
            if ((ball.xSpeed > 0 && this.x < screenSizeX / 2) || (ball.xSpeed < 0 && this.x > screenSizeX / 2)) {
                
                if ((this.y + (this.sizeY / 2)) <= (screenSizeY / 2) - 20
                || (this.y + (this.sizeY / 2)) >= (screenSizeY / 2) + 20) {
                    if (screenSizeY / 2 < this.y + (this.sizeY / 2)) {
                    this.speed = -this.speedIncrease;
                } else if (screenSizeY / 2 > this.y + (this.sizeY / 2)) {
                    this.speed = this.speedIncrease;
                } else {
                    this.speed = 0;
                }
            } else {
                this.speed = 0;
            }
            
        } else if ((ball.xSpeed > 0 && this.x > screenSizeX / 2) || (ball.xSpeed < 0 && this.x < screenSizeX / 2)) {
            
            int targetPos = ball.y + (ball.ySpeed * 2);
            
            if ((this.y + (this.sizeY / 2)) <= targetPos - 20 || (this.y + (this.sizeY / 2)) >= targetPos + 20) {
                if (targetPos < this.y + (this.sizeY / 2)) {
                    this.speed = -this.speedIncrease;
                } else if (targetPos > this.y + (this.sizeY / 2)) {
                    this.speed = this.speedIncrease;
                } else {
                    this.speed = 0;
                }
            } else {
                this.speed = 0;
            }

        }
    }
    
}
