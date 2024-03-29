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
    boolean paused = false;
    boolean p2On = false;
    

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
            if (player.points > 0 && !p2On) {
                if (Math.ceil(Math.random() * 100) >= 80) {
                    illusion = 6;
                }
            }
            if (player.points > 1 && !p2On && illusion == 0) {
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
        } catch (IOException e) {
        } catch (LineUnavailableException e) {
        }
    }

    static int screenSizeX = 800;
    static int screenSizeY = 600;
    Player player = new Player(50, 250, screenSizeX, screenSizeY);
    Player enemy = new Player(750, 250, screenSizeX, screenSizeY);
    Ball ball = new Ball(screenSizeX / 2, screenSizeY / 2, screenSizeX, screenSizeY);
    static int randomR = 255;
    static int randomG = 255;
    static int randomB = 255;

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
        while (true) {
            if (!paused) {
            update();
            }
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

            ball.reset();

            blackout = 0;
            illusion = 0;
            randomizeColors();

            playSound("lose.mid");
        }

        if (!p2On) {
            enemy.autoMove(player, ball, screenSizeX, screenSizeY);
        }

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

        ball.move();
        enemy.move();
        player.move();

        for (int i = 1; i <= 4; i++) {
            int newBallPosX = ball.x + ((ball.xSpeed / 4) * i);
            int newBallPosY = ball.y + ((ball.ySpeed / 4) * i);

            if (newBallPosX <= player.x + player.sizeX && newBallPosX >= player.x) {
                if (newBallPosY >= player.y && ball.y <= player.y + player.sizeY) {
                    ball.x = player.x + player.sizeX;
                    ball.xSpeed = Math.min((ball.xSpeed * -1) + 5, ball.maxXSpeed);

                    i = 5;
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
                    if (p2On) {
                        randomEvent();
                    }
                    randomizeColors();
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

        
        g.setFont(new Font("Impact", Font.PLAIN, 50));

        
        Color randomColor = new Color(randomR, randomG, randomB);
        g.setColor(randomColor);
        g.fillRect(player.x, player.y, player.sizeX, player.sizeY);

        g.fillRect(enemy.x, enemy.y, enemy.sizeX, enemy.sizeY);

        g.fillRect((screenSizeX / 2) - 2, 0, 4, screenSizeY);

        g.fillOval(ball.x, ball.y, ball.size, ball.size);

        if (!p2On){
            g.fillRect(screenSizeX / 2 - 100 + (100 - bulletTimeCharge*5), 10, bulletTimeCharge*5, 20);
        }

        g.drawString("" + player.points, (screenSizeX / 2) - 100, 100);
        g.drawString("" + enemy.points, (screenSizeX / 2) + 100, 100);

        
        if (bulletTime) {
            Color slowColor = new Color(Math.min(randomR + 50, 255), Math.min(randomG + 50, 255),Math.min(randomB + 50, 255));
            g.setColor(slowColor);
            g.setFont(new Font("Impact", Font.PLAIN, 25));
            g.drawString("It's Pongin' time!", (screenSizeX / 2) - 300, (screenSizeY / 2) - 250);

        }

        if (!p2On) {
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

        if (paused) {
            g.setColor(Color.WHITE);
            g.drawString("PAUSED", (screenSizeX/2)-75, (screenSizeY/2));
            g.drawString("P1", 150, (screenSizeY/2)-200);
            if (!p2On) {
                g.drawString("COM", 600, (screenSizeY/2)-200);
            } else {
                g.drawString("P2", 600, (screenSizeY/2)-200);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            ball.reset();
            bulletTimeCharge = 10;
            player.points = 0;
            player.reset();
            enemy.reset();
            enemy.points = 0;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_W) {
            player.speed = -player.speedIncrease;
            if (paused) {
                p2On = !p2On;
                enemy.speed = 0;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            player.speed = player.speedIncrease;
            if (paused) {
                p2On = !p2On;
                enemy.speed = 0;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            if (!p2On) {
                if (!bulletTime && bulletTimeCharge == bulletTimeInit) {
                    playSound("pong_time.wav");
                    ball.xSpeed = ball.xSpeed > 0 ? Math.max(ball.xSpeed / 2, 3) : Math.min(ball.xSpeed / 2, -3);
                    ball.ySpeed = ball.ySpeed > 0 ? Math.max(ball.ySpeed / 2, 3) : Math.min(ball.ySpeed / 2, -3);
                    bulletTime = true;
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            randomizeColors();
            paused = !paused;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (p2On) {
                enemy.speed = enemy.speedIncrease;
            }
            if (paused) {
                p2On = !p2On;
                enemy.speed = 0;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (p2On) {
                enemy.speed = -enemy.speedIncrease;
            }
            if (paused) {
                p2On = !p2On;
                enemy.speed = 0;
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

        if (e.getKeyCode() == KeyEvent.VK_DOWN && enemy.speed > 0 && p2On) {
            enemy.speed = 0;
        } 

        if (e.getKeyCode() == KeyEvent.VK_UP && enemy.speed < 0 && p2On) {
            enemy.speed = 0;
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