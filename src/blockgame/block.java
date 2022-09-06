package blockgame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

//java.awt->�ڹٿ��� �����ϴ� gui��Ű��
//java.awt.event->gui�̺�Ʈ ó��(Ű����)
//javax.swing->gui���α׷��� �ۼ��ϱ����� Ŭ������ �������̽� ����

public class block { // ��Ŭ����(��ũ��,��ũ��,ui�г�ũ��,��ũ��)

	static class Myframe extends JFrame {

		// ���(constant)����-> �빮��
		static int BALL_WIDTH = 20; // ���� ����
		static int BALL_HEIGHT = 20; // ���� ����
		static int BLOCK_ROWS = 5; // ����� �ټ� (����)
		static int BLOCK_CLUMMS = 10; // ����� ���� (����)
		static int BLOCK_WIDTH = 40; // ��� ����ũ��(����)
		static int BLOCK_HEIGHT = 20; // ��� ���� ũ��(����)
		static int BLOCK_GAP = 3; // ��ϻ��� ����
		static int BAR_WIDTH = 80; // ����ڰ� �����̴� ���� ����
		static int BAR_HEIGHT = 20; // ����ڰ� �����̴� ���� ����
		static int CANVAS_WIDTH = 418 + (BLOCK_GAP * BLOCK_CLUMMS) - BLOCK_GAP; // ui����
		static int CANVAS_HEIGHT = 600; // ui����
		// ����(variable)
		static MyPanel myPanel = null; // ķ�۽� �����ϴ� �г�
		static int score = 0; // ����
		static Timer timer = null; // Ÿ�̸�

		static Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_CLUMMS]; // Ŭ���� ���� ->������
		static Bar bar = new Bar(); // Ŭ���� ����
		static Ball ball = new Ball(); // Ŭ���� ����
		static int barXTarget = bar.x; // Target Value -interpolation �����Ҽ� �ִ� �뷮�Ҵ�
		static int dir = 0; // ���� �����̴� ���� 0:up-right 1: down-right 2: up-left 3: down-left
		static int ballSpeed = 5; // ���ӵ�
		static boolean isGameFinish = false;

		static class Ball { // �� Ŭ����
			int x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2; // ĵ������ ���������� ���� ���̸� ������ ������ ���� �߾ӿ� ��ġ
			int y = CANVAS_HEIGHT / 2 - BALL_HEIGHT / 2; // ..
			int width = BALL_WIDTH;
			int height = BALL_HEIGHT;

			// ���� ���� �Լ�
			Point getCenter() {
				return new Point(x + (BALL_WIDTH / 2), y + (BALL_HEIGHT / 2));
			}

			Point getBottomCenter() {
				return new Point(x + (BALL_WIDTH / 2), y + (BALL_HEIGHT));
			}

			Point getTopCenter() {
				return new Point(x + (BALL_WIDTH / 2), y);
			}

			Point getLeftCenter() {
				return new Point(x, y + (BALL_HEIGHT / 2));
			}

			Point getRightCenter() {
				return new Point(x + (BALL_WIDTH), y + (BALL_HEIGHT / 2));
			}
		}

		static class Bar { // �� Ŭ����
			int x = CANVAS_WIDTH / 2 - BAR_WIDTH / 2;
			int y = CANVAS_WIDTH - 30;
			int width = BAR_WIDTH;
			int height = BAR_HEIGHT;
		}

		static class Block { // ��Ŭ���� for������ �ʱ�ȭ�ٽ��ؾߵȴ�.
			int x = 0;
			int y = 0;
			int width = BLOCK_WIDTH;
			int height = BLOCK_HEIGHT;
			int color = 0;// 0:white 1:yellow 2:blue 3:mazanta 4:red ��ϻ������� ������ �ٸ���
			boolean isHidden = false; // ���߸� ȭ�鿡�� �������

		}

		static class MyPanel extends JPanel {
			public MyPanel() { // ������
				this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT); // �гλ����� ����
				this.setBackground(Color.black); // ��׶��� ���� ����

			}

			@Override
			public void paint(Graphics g) { // �׷��� �Լ�
				super.paint(g);
				Graphics2D g2d = (Graphics2D) g; // �׷����� �׸������� �����ϴ� Ŭ���� ����ȯ

				drawUI(g2d);
			}

			private void drawUI(Graphics2D g2d) {
				// draw Bloacks
				// ����for��
				for (int i = 0; i < BLOCK_ROWS; i++) { // 10ĭ¥�� ����� ����
					for (int j = 0; j < BLOCK_CLUMMS; j++) {
						if (blocks[i][j].isHidden) { // ����� �����̸�
							continue; // ��� ��ȯ
						}
						if (blocks[i][j].color == 0) { // �÷��� 0�϶�
							g2d.setColor(Color.WHITE); // ������� ����
						} else if (blocks[i][j].color == 1) { // �÷��� 1�϶�
							g2d.setColor(Color.YELLOW); // ������� ����
						} else if (blocks[i][j].color == 2) { // �÷��� 2�϶�
							g2d.setColor(Color.BLUE); // �Ķ����� ����
						} else if (blocks[i][j].color == 3) { // �÷��� 3�϶�
							g2d.setColor(Color.MAGENTA); // ����Ÿ���� ����
						} else if (blocks[i][j].color == 4) { // �÷��� 4�϶�
							g2d.setColor(Color.RED); // �������� ����
						}
						// fillRect -> �׸��� �Լ�
						g2d.fillRect(blocks[i][j].x, blocks[i][j].y, // ������ �׸��� �κ�(���x,���y,
								blocks[i][j].width, blocks[i][j].height);// ������,������)
					}
					// draw score
					g2d.setColor(Color.WHITE);
					g2d.setFont(new Font("TimesRoman", Font.BOLD, 20)); // (��Ʈ�۲�,��Ʈũ��,��Ʈ ������)
					g2d.drawString("score:" + score, CANVAS_WIDTH / 2 - 30, 20); // ���ھ�
					if (isGameFinish) {
						g2d.setColor(Color.RED);
						g2d.drawString("Game Finished!", CANVAS_WIDTH / 2 - 70, 40); // ���ھ�
					}
					// draw Ball
					g2d.setColor(Color.WHITE); // ������
					g2d.fillOval(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT);// ���μ��ΰ����� ��

					// draw bar
					g2d.setColor(Color.WHITE); // �ٻ���
					g2d.fillRect(bar.x, bar.y, BAR_WIDTH, BAR_HEIGHT);
				}
			}
		}

		public Myframe(String title) {
			super(title);
			this.setVisible(true); // Myframe �ʱ�ȭ setVisible -> â�� ȭ�鿡 ��Ÿ�� ������ ����
			this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT); // â�� ũ������
			this.setLocation(400, 300); // â�� ��ġ�� �ű�
			this.setLayout(new BorderLayout()); // ���̾ƿ� ����
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ������ â�� ����� ������ �ϴ±��

			initData();// ������� �ʱ�ȭ

			myPanel = new MyPanel(); // Ŭ���� ����
			this.add("Center", myPanel); // ��ġ����

			setKeyListener(); // Ű���� ����
			startTimer();

		}

		public void initData() { // ������� �ʱ�ȭ�Լ�
			for (int i = 0; i < BLOCK_ROWS; i++) { // 10ĭ¥�� ����� ����
				for (int j = 0; j < BLOCK_CLUMMS; j++) {
					blocks[i][j] = new Block(); // ��� ����������
					blocks[i][j].x = BLOCK_WIDTH * j + BLOCK_GAP * j; // ���x��ǥ�Ӽ�(����)
					blocks[i][j].y = 100 + i * BLOCK_HEIGHT + BLOCK_GAP * i;// ��y��Ӽ�->��ܿ� ����+��ϳ���(����)
					blocks[i][j].width = BLOCK_WIDTH;
					blocks[i][j].height = BLOCK_HEIGHT;
					blocks[i][j].color = 4 - i; // 0:white 1:yellow 2:blue 3:mazanta 4:red
					blocks[i][j].isHidden = false; // ����� ������ �������� ������ ����

				}
			}
		}

		public void setKeyListener() { // Ű���� �����Լ�����
			this.addKeyListener(new KeyAdapter() { // Ű���� ���� �Լ�
				// Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_LEFT) { // ȭ��ǥ����Ű
						System.out.println("pressed left key");
						barXTarget -= 20; // �ٿ������� ������ �ʰ� �����̴� �Լ�
						if (bar.x < barXTarget) { // Ű�� ��Ÿ������(����ó��)
							barXTarget = bar.x;
						}
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) { // ȭ��ǥ ������Ű
						System.out.println("pressed right key");
						barXTarget += 20;
						if (bar.x > barXTarget) { // Ű�� ��Ÿ������(����ó��)
							barXTarget = bar.x;
						}
					}
				}
			});
		}

		public void startTimer() { // Ÿ�̸� �Լ� ����
			timer = new Timer(20, new ActionListener() { // swing���� �����ϴ� Ÿ�̸� �Լ�

				@Override
				public void actionPerformed(ActionEvent e) { // Ÿ�̸� �̺�Ʈ
					movement(); // movement(��) �Լ�����
					checkCollision();// �浹ó�� ����(��and��)
					checkCollisionBlock();// �� �浹ó�� ����(���)
					myPanel.repaint();// ui���ξ�����Ʈ redraw

					isGameFinish();
				}
			});
			timer.start(); // ����Ÿ�̸� ����
		}

		public void isGameFinish() {
			// game success!
			int count = 0;
			for (int i = 0; i < BLOCK_ROWS; i++) {
				for (int j = 0; j < BLOCK_CLUMMS; j++) {
					Block block = blocks[i][j];
					if (block.isHidden) {
						count++;
					}
				}
			}
			if (count == BLOCK_ROWS * BLOCK_CLUMMS) {
				// game finished
				// timer.stop()
				isGameFinish = true;
			}
		}

		public void movement() { // ���� ������
			if (bar.x < barXTarget) {
				bar.x += 5;
			} else if (bar.x > barXTarget) {
				bar.x -= 5;
			}
			if (dir == 0) { // up-right
				ball.x += ballSpeed; // ���� �ӵ�
				ball.y -= ballSpeed;
			} else if (dir == 1) { // down-right
				ball.x += ballSpeed; // ���� �ӵ�
				ball.y += ballSpeed;
			} else if (dir == 2) { // up-left
				ball.x -= ballSpeed; // ���� �ӵ�
				ball.y -= ballSpeed;
			} else if (dir == 3) { // down-left
				ball.x -= ballSpeed; // ���� �ӵ�
				ball.y += ballSpeed;
			}
		}

		// duplRect�Լ� ����
		public boolean duplRect(Rectangle rect1, Rectangle rect2) {
			return rect1.intersects(rect2); // �浹����üũ(check two rect is duplicated)
		}

		public void checkCollision() { // ���� ���� �浹 �Լ�
			if (dir == 0) { // up-right
				// wall
				if (ball.y < 0) { // wall upper
					dir = 1;
				}
				if (ball.x > CANVAS_WIDTH - BALL_WIDTH) { // �����ʺ����ε�������
					dir = 2;
				}
				// bar(�������� ������������ ���� ���� �浹 x)
				// none
			} else if (dir == 1) { // down-right
				// wall
				if (ball.y > CANVAS_HEIGHT - BALL_HEIGHT - BALL_HEIGHT) { // wall bottom
					dir = 0;
					// game reset
					dir = 0;
					ball.x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2;
					ball.y = CANVAS_HEIGHT / 2 - BALL_HEIGHT / 2;
					score = 0;
				}
				if (ball.x > CANVAS_WIDTH - BALL_WIDTH) { // �����ʺ����ε�������
					dir = 3;
				}
				// bar
				if (ball.getBottomCenter().y >= bar.y) {
					if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
							new Rectangle(bar.x, bar.y, bar.width, bar.height))) {
						dir = 0;
					}
				}
			} else if (dir == 2) { // up-left
				// wall
				if (ball.y < 0) { // wall upper(���ʺ�)
					dir = 3;
				}
				if (ball.x < 0) { // left wall(���ʺ�)
					dir = 0;
				}
				// bar(�������� ������������ ���� ���� �浹 x)
				// none
			} else if (dir == 3) { // down-left
				// wall
				if (ball.y > CANVAS_HEIGHT - BALL_HEIGHT - BALL_HEIGHT) { // wall bottom
					dir = 2;

					// game reset
					dir = 0;
					ball.x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2;
					ball.y = CANVAS_HEIGHT / 2 - BALL_HEIGHT / 2;
					score = 0;
				}
				if (ball.x < 0) { // wall left
					dir = 1;
				}
				// bar
				if (ball.getBottomCenter().y >= bar.y) {
					if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
							new Rectangle(bar.x, bar.y, bar.width, bar.height))) {
						dir = 2;
					}
				}
			}
		}

		public void checkCollisionBlock() { // ���� �⵿
			// 0:up-right 1: down-right 2: up-left 3: down-left
			for (int i = 0; i < BLOCK_ROWS; i++) {
				for (int j = 0; j < BLOCK_CLUMMS; j++) {
					Block block = blocks[i][j];
					if (block.isHidden == false) {
						if (dir == 0) { // 0:up-right
							if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
									new Rectangle(block.x, block.y, block.width, block.height))) {
								if (ball.x > block.x + 2 && // ���� ����ũ�⺸�� ��¦ũ����(+2) ���浹
										ball.getRightCenter().x <= block.x + block.width - 2) {
									// ��Ϲ����浹
									dir = 1;
								} else {
									// ��Ͽ��� �浹
									dir = 2;
								}
								block.isHidden = true;
								if (block.color == 0) {
									score += 10;
								} else if (block.color == 1) {
									score += 20;
								} else if (block.color == 2) {
									score += 30;
								} else if (block.color == 3) {
									score += 40;
								} else if (block.color == 4) {
									score += 50;
								}
							}
						} else if (dir == 1) { // 1: down-right
							if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
									new Rectangle(block.x, block.y, block.width, block.height))) {
								if (ball.x > block.x + 2 && // ���� ����ũ�⺸�� ��¦ũ����(+2) ���浹
										ball.getRightCenter().x <= block.x + block.width - 2) {
									// ���ž�浹
									dir = 0;
								} else {
									// ��Ͽ��� �浹
									dir = 3;
								}
								block.isHidden = true;
								if (block.color == 0) {
									score += 10;
								} else if (block.color == 1) {
									score += 20;
								} else if (block.color == 2) {
									score += 30;
								} else if (block.color == 3) {
									score += 40;
								} else if (block.color == 4) {
									score += 50;
								}
							}
						} else if (dir == 2) { // 2: up-left
							if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
									new Rectangle(block.x, block.y, block.width, block.height))) {
								if (ball.x > block.x + 2 && // ���� ����ũ�⺸�� ��¦ũ����(+2) ���浹
										ball.getRightCenter().x <= block.x + block.width - 2) {
									// ��Ϲ����浹
									dir = 3;
								} else {
									// ��Ͽ����� �浹
									dir = 0;
								}
								block.isHidden = true;
								if (block.color == 0) {
									score += 10;
								} else if (block.color == 1) {
									score += 20;
								} else if (block.color == 2) {
									score += 30;
								} else if (block.color == 3) {
									score += 40;
								} else if (block.color == 4) {
									score += 50;
								}
							}
						} else if (dir == 3) { // 3: down-left
							if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
									new Rectangle(block.x, block.y, block.width, block.height))) {
								if (ball.x > block.x + 2 && // ���� ����ũ�⺸�� ��¦ũ����(+2) ���浹
										ball.getRightCenter().x <= block.x + block.width - 2) {
									// ���ž�浹
									dir = 2;
								} else {
									// ��Ͽ��� �浹
									dir = 1;
								}
								block.isHidden = true;
								if (block.color == 0) {
									score += 10;
								} else if (block.color == 1) {
									score += 20;
								} else if (block.color == 2) {
									score += 30;
								} else if (block.color == 3) {
									score += 40;
								} else if (block.color == 4) {
									score += 50;
								}
							}
						}
					}
				}
			}
		}

	}

	public static void main(String[] args) {

		new Myframe("Block Game");
	}

}
