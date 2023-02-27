import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Pong extends JPanel implements Runnable, KeyListener {
    int blackout = 0;
    int illusion = 0;
    boolean bulletTime = false;
    int bulletTimeInit = 20;
    int bulletTimeCharge = 20;

    public void randomizeColors() {
        if (blackout == 0) {
            randomR = (int) Math.max(Math.floor(Math.random() * 255), 50);
            randomG = (int) Math.max(Math.floor(Math.random() * 255), 50);
            randomB = (int) Math.max(Math.floor(Math.random() * 255), 50);
        } else {
            randomR = 5;
            randomG = 5;
            randomB = 5;
        }
    }

    public void randomEvent() {
        if (blackout == 0 && illusion == 0) {
            if (player.points > 0) {
                if (Math.ceil(Math.random() * 100) >= 80) {
                    illusion = 6;
                }
            }
            if (player.points > 1 && illusion == 0) {
                if (Math.ceil(Math.random() * 100) >= 90) {
                    blackout = 4;
                }
            }
            if (Math.ceil(Math.random() * 100) >= 90) {
                int tempSpeed = ball.ySpeed;
                ball.ySpeed = ball.ySpeed > 0 ? Math.max(ball.xSpeed, -ball.xSpeed)
                        : Math.min(ball.xSpeed, -ball.xSpeed);
                ball.xSpeed = ball.xSpeed > 0 ? Math.max(tempSpeed, -tempSpeed) : Math.min(tempSpeed, -tempSpeed);
            }
        } else {
            blackout = Math.max(blackout - 1, 0);
            illusion = Math.max(illusion - 1, 0);
        }
    }
    
    public void moveEnemy() {
        if (Math.ceil(Math.random() * 100) <= 50 && player.points - enemy.points < 2) {
            return;
        }
            if (ball.xSpeed < 0) {
                
                if ((enemy.y + (enemy.sizeY / 2)) <= (screenSizeY / 2) - 20
                || (enemy.y + (enemy.sizeY / 2)) >= (screenSizeY / 2) + 20) {
                    if (screenSizeY / 2 < enemy.y + (enemy.sizeY / 2)) {
                    enemy.speed = -enemy.speedIncrease;
                } else if (screenSizeY / 2 > enemy.y + (enemy.sizeY / 2)) {
                    enemy.speed = enemy.speedIncrease;
                } else {
                    enemy.speed = 0;
                }
            } else {
                enemy.speed = 0;
            }
            
        } else if (ball.xSpeed > 0) {
            
            int targetPos = ball.y + (ball.ySpeed * 2);
            
            if ((enemy.y + (enemy.sizeY / 2)) <= targetPos - 20 || (enemy.y + (enemy.sizeY / 2)) >= targetPos + 20) {
                if (targetPos < enemy.y + (enemy.sizeY / 2)) {
                    enemy.speed = -enemy.speedIncrease;
                } else if (targetPos > enemy.y + (enemy.sizeY / 2)) {
                    enemy.speed = enemy.speedIncrease;
                } else {
                    enemy.speed = 0;
                }
            } else {
                enemy.speed = 0;
            }

        }
    }

    public void playSound(String sound) {
        try {

            // Open an audio input stream.
            URL url = this.getClass().getClassLoader().getResource("./sounds/" + sound);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    Player player = new Player();
    Player enemy = new Player();
    Ball ball = new Ball();
    static int screenSizeX = 800;
    static int screenSizeY = 600;
    static int randomR = 255;
    static int randomG = 255;
    static int randomB = 255;

    static int score = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong!");
        frame.setSize(screenSizeX + 40, screenSizeY + 40);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);

        Pong panel = new Pong();
        panel.setBackground(Color.BLACK);
        panel.setBounds(0, 0, screenSizeX + 40, screenSizeY + 40);
        panel.setVisible(true);

        frame.setLayout(null);
        frame.addKeyListener(panel);
        frame.add(panel);
    }

    public Pong() {

        Thread gameProcess = new Thread(this);
        gameProcess.start();

    }

    public void run() {
        enemy.x = 750;

        while (true) {
            update();
            repaint();
            sleep();
        }

    }

    public void update() {
        if (ball.x >= screenSizeX || ball.x < 0) {

            if (ball.x >= screenSizeX) {
                player.points++;
            } else if (ball.x <= 0) {
                enemy.points++;
            }

            ball.x = screenSizeX / 2;
            ball.y = screenSizeY / 2;
            ball.xSpeed = (Math.floor((Math.random() * 10) % 2) == 0) ? 5 : -5;
            ball.ySpeed = (Math.floor((Math.random() * 10) % 2) == 0) ? 3 : -3;

            blackout = 0;
            illusion = 0;
            randomizeColors();

            playSound("lose.mid");
        }
        moveEnemy();

        if (ball.ySpeed == 0) {
            ball.ySpeed += 1;
        }

        if (ball.y >= screenSizeY - ball.size || ball.y < 0) {
            ball.ySpeed *= -1;
            randomizeColors();
            playSound("wall_hit.wav");

        }

        if (bulletTime && bulletTimeCharge > 0) {
            bulletTimeCharge--;
        } else if (bulletTime && bulletTimeCharge <= 0) {
            bulletTime = false;
            ball.xSpeed = ball.xSpeed * 2;
            ball.ySpeed = ball.ySpeed * 2;
        } else if (!bulletTime && bulletTimeCharge < bulletTimeInit) {
            bulletTimeCharge++;
        }

        ball.x += ball.xSpeed;
        ball.y += ball.ySpeed;

        if (enemy.speed > 0) {
            enemy.y = Math.min(enemy.y + enemy.speed, screenSizeY - enemy.sizeY);
        } else if (enemy.speed < 0) {
            enemy.y = Math.max(enemy.y + enemy.speed, 0);
        }

        if (player.speed > 0) {
            player.y = Math.min(player.y + player.speed, screenSizeY - player.sizeY);
        } else if (player.speed < 0) {
            player.y = Math.max(player.y + player.speed, 0);
        }

        for (int i = 1; i <= 4; i++) {
            int newBallPosX = ball.x + ((ball.xSpeed / 4) * i);
            int newBallPosY = ball.y + ((ball.ySpeed / 4) * i);

            if (newBallPosX <= player.x + player.sizeX && newBallPosX >= player.x) {
                if (newBallPosY >= player.y && ball.y <= player.y + player.sizeY) {
                    ball.x = player.x + player.sizeX;
                    ball.xSpeed = Math.min((ball.xSpeed * -1) + 5, ball.maxXSpeed);

                    i = 5;
                    randomizeColors();
                    playSound("player_hit.wav");

                    if (player.speed > 0) {
                        ball.ySpeed = Math.min(ball.ySpeed + (player.speed / 2), ball.maxYSpeed);
                    } else if (player.speed < 0) {
                        ball.ySpeed = Math.max(ball.ySpeed + (player.speed / 2), -ball.maxYSpeed);
                    } else {
                        if (ball.ySpeed > 0) {
                            ball.ySpeed = Math.min(ball.ySpeed, ball.maxYSpeed);
                        } else {
                            ball.ySpeed = Math.max(ball.ySpeed, -ball.maxYSpeed);
                        }
                    }
                }

            }

            if (newBallPosX >= enemy.x - enemy.sizeX && newBallPosX <= enemy.x) {
                if (newBallPosY >= enemy.y && newBallPosY <= enemy.y + enemy.sizeY) {
                    ball.x = enemy.x - enemy.sizeX;
                    ball.xSpeed = Math.max((ball.xSpeed * -1) - 5, -ball.maxXSpeed);
                    i = 5;

                    playSound("enemy_hit.wav");
                    
                    if (ball.ySpeed > 0) {
                        ball.ySpeed = Math.min(ball.ySpeed, ball.maxYSpeed);
                    } else {
                        ball.ySpeed = Math.max(ball.ySpeed, -ball.maxYSpeed);
                    }
                    randomEvent();
                    randomizeColors();
                }

            }
        }

    }

    public void sleep() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color randomColor = new Color(randomR, randomG, randomB);
        g.setColor(randomColor);
        g.fillRect(player.x, player.y, player.sizeX, player.sizeY);

        g.fillRect(enemy.x, enemy.y, enemy.sizeX, enemy.sizeY);

        g.fillRect((screenSizeX / 2) - 2, 0, 4, screenSizeY);

        g.fillOval(ball.x, ball.y, ball.size, ball.size);

        g.fillRect(screenSizeX / 2 - 100 + (100 - bulletTimeCharge*5), 10, bulletTimeCharge*5, 20);

        g.setFont(new Font("Impact", Font.PLAIN, 50));
        g.drawString("" + player.points, (screenSizeX / 2) - 100, 100);
        g.drawString("" + enemy.points, (screenSizeX / 2) + 100, 100);

        if (bulletTime) {
            Color slowColor = new Color(Math.min(randomR + 50, 255), Math.min(randomG + 50, 255),Math.min(randomB + 50, 255));
            g.setColor(slowColor);
            g.setFont(new Font("Impact", Font.PLAIN, 25));
            g.drawString("It's Pongin' time!", (screenSizeX / 2) - 300, (screenSizeY / 2) - 250);

        }
        
        if (blackout > 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Impact", Font.PLAIN, 25));
            g.drawString("BLACKOUT!", (screenSizeX / 2) + 150, (screenSizeY / 2) - 250);
        }

        if (illusion > 0) {
            g.fillOval(ball.x, ball.y + 100, ball.size, ball.size);
            g.fillOval(ball.x, ball.y - 100, ball.size, ball.size);
            g.setFont(new Font("Impact", Font.PLAIN, 25));
            g.drawString("Illusion!", (screenSizeX / 2) + 150, (screenSizeY / 2) - 250);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            ball.x = screenSizeX/2;
            ball.y = screenSizeY/2;
            ball.xSpeed = ball.initXSpeed;
            ball.ySpeed = ball.initYSpeed;
            bulletTimeCharge = 10;
            player.points = 0;
            player.x = 50;
            player.y = 100;
            enemy.x = 750;
            enemy.y = 100;
            enemy.points = 0;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_W) {
            player.speed = -player.speedIncrease;
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            player.speed = player.speedIncrease;
        }

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            if (!bulletTime && bulletTimeCharge == bulletTimeInit) {
                playSound("pong_time.wav");
                ball.xSpeed = ball.xSpeed > 0 ? Math.max(ball.xSpeed / 2, 3) : Math.min(ball.xSpeed / 2, -3);
                ball.ySpeed = ball.ySpeed > 0 ? Math.max(ball.ySpeed / 2, 3) : Math.min(ball.ySpeed / 2, -3);
                bulletTime = true;
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W && player.speed < 0) {
            player.speed = 0;
        }

        if (e.getKeyCode() == KeyEvent.VK_S && player.speed > 0) {
            player.speed = 0;
        }

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            if (bulletTime) {
                ball.xSpeed = ball.xSpeed * 2;
                ball.ySpeed = ball.ySpeed * 2;
                bulletTime = false;
            }
        }
    }

}