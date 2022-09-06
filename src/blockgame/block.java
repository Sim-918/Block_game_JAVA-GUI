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

//java.awt->자바에서 제공하는 gui패키지
//java.awt.event->gui이벤트 처리(키보드)
//javax.swing->gui프로그램을 작성하기위한 클래스와 인터페이스 모임

public class block { // 블럭클래스(공크기,블럭크기,ui패널크기,바크기)

	static class Myframe extends JFrame {

		// 상수(constant)지정-> 대문자
		static int BALL_WIDTH = 20; // 공의 넓이
		static int BALL_HEIGHT = 20; // 공의 높이
		static int BLOCK_ROWS = 5; // 블록의 줄수 (세로)
		static int BLOCK_CLUMMS = 10; // 블록의 길이 (가로)
		static int BLOCK_WIDTH = 40; // 블록 가로크기(넓이)
		static int BLOCK_HEIGHT = 20; // 블록 세로 크기(높이)
		static int BLOCK_GAP = 3; // 블록사이 간격
		static int BAR_WIDTH = 80; // 사용자가 움직이는 바의 넓이
		static int BAR_HEIGHT = 20; // 사용자가 움직이는 바의 높이
		static int CANVAS_WIDTH = 418 + (BLOCK_GAP * BLOCK_CLUMMS) - BLOCK_GAP; // ui넓이
		static int CANVAS_HEIGHT = 600; // ui높이
		// 변수(variable)
		static MyPanel myPanel = null; // 캠퍼스 역할하는 패널
		static int score = 0; // 점수
		static Timer timer = null; // 타이머

		static Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_CLUMMS]; // 클래스 생성 ->블럭개수
		static Bar bar = new Bar(); // 클래스 생성
		static Ball ball = new Ball(); // 클래스 생성
		static int barXTarget = bar.x; // Target Value -interpolation 조작할수 있는 용량할당
		static int dir = 0; // 공이 움직이는 방향 0:up-right 1: down-right 2: up-left 3: down-left
		static int ballSpeed = 5; // 공속도
		static boolean isGameFinish = false;

		static class Ball { // 공 클래스
			int x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2; // 캔버스의 반을나누고 공의 넓이를 반으로 나누고 빼면 중앙에 위치
			int y = CANVAS_HEIGHT / 2 - BALL_HEIGHT / 2; // ..
			int width = BALL_WIDTH;
			int height = BALL_HEIGHT;

			// 공의 연산 함수
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

		static class Bar { // 바 클래스
			int x = CANVAS_WIDTH / 2 - BAR_WIDTH / 2;
			int y = CANVAS_WIDTH - 30;
			int width = BAR_WIDTH;
			int height = BAR_HEIGHT;
		}

		static class Block { // 블럭클래스 for문으로 초기화다시해야된다.
			int x = 0;
			int y = 0;
			int width = BLOCK_WIDTH;
			int height = BLOCK_HEIGHT;
			int color = 0;// 0:white 1:yellow 2:blue 3:mazanta 4:red 블록색갈별로 점수를 다르게
			boolean isHidden = false; // 맞추면 화면에서 사라지게

		}

		static class MyPanel extends JPanel {
			public MyPanel() { // 생성자
				this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT); // 패널사이즈 지정
				this.setBackground(Color.black); // 백그라운드 색상 검정

			}

			@Override
			public void paint(Graphics g) { // 그래픽 함수
				super.paint(g);
				Graphics2D g2d = (Graphics2D) g; // 그래픽을 그리기위해 지원하는 클래스 형변환

				drawUI(g2d);
			}

			private void drawUI(Graphics2D g2d) {
				// draw Bloacks
				// 이중for문
				for (int i = 0; i < BLOCK_ROWS; i++) { // 10칸짜리 블록을 지정
					for (int j = 0; j < BLOCK_CLUMMS; j++) {
						if (blocks[i][j].isHidden) { // 블록이 히든이면
							continue; // 계속 순환
						}
						if (blocks[i][j].color == 0) { // 컬러가 0일때
							g2d.setColor(Color.WHITE); // 흰색으로 지정
						} else if (blocks[i][j].color == 1) { // 컬러가 1일때
							g2d.setColor(Color.YELLOW); // 노랑으로 지정
						} else if (blocks[i][j].color == 2) { // 컬러가 2일때
							g2d.setColor(Color.BLUE); // 파랑으로 지정
						} else if (blocks[i][j].color == 3) { // 컬러가 3일때
							g2d.setColor(Color.MAGENTA); // 마젠타으로 지정
						} else if (blocks[i][j].color == 4) { // 컬러가 4일때
							g2d.setColor(Color.RED); // 빨강으로 지정
						}
						// fillRect -> 그리는 함수
						g2d.fillRect(blocks[i][j].x, blocks[i][j].y, // 실제로 그리는 부분(블록x,블록y,
								blocks[i][j].width, blocks[i][j].height);// 블럭가로,블럭세로)
					}
					// draw score
					g2d.setColor(Color.WHITE);
					g2d.setFont(new Font("TimesRoman", Font.BOLD, 20)); // (폰트글꼴,폰트크기,폰트 사이즈)
					g2d.drawString("score:" + score, CANVAS_WIDTH / 2 - 30, 20); // 스코어
					if (isGameFinish) {
						g2d.setColor(Color.RED);
						g2d.drawString("Game Finished!", CANVAS_WIDTH / 2 - 70, 40); // 스코어
					}
					// draw Ball
					g2d.setColor(Color.WHITE); // 공색깔
					g2d.fillOval(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT);// 가로세로가같은 공

					// draw bar
					g2d.setColor(Color.WHITE); // 바색깔
					g2d.fillRect(bar.x, bar.y, BAR_WIDTH, BAR_HEIGHT);
				}
			}
		}

		public Myframe(String title) {
			super(title);
			this.setVisible(true); // Myframe 초기화 setVisible -> 창을 화면에 나타낼 것인지 설정
			this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT); // 창의 크기지정
			this.setLocation(400, 300); // 창의 위치를 옮김
			this.setLayout(new BorderLayout()); // 레이아웃 생성
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 윈도우 창이 제대로 닫히게 하는기능

			initData();// 변수기능 초기화

			myPanel = new MyPanel(); // 클래스 지정
			this.add("Center", myPanel); // 위치지정

			setKeyListener(); // 키보드 조작
			startTimer();

		}

		public void initData() { // 변수기능 초기화함수
			for (int i = 0; i < BLOCK_ROWS; i++) { // 10칸짜리 블록을 지정
				for (int j = 0; j < BLOCK_CLUMMS; j++) {
					blocks[i][j] = new Block(); // 블록 연산자지정
					blocks[i][j].x = BLOCK_WIDTH * j + BLOCK_GAP * j; // 블록x좌표속성(가로)
					blocks[i][j].y = 100 + i * BLOCK_HEIGHT + BLOCK_GAP * i;// 블럭y축속성->상단에 여백+블록높이(세로)
					blocks[i][j].width = BLOCK_WIDTH;
					blocks[i][j].height = BLOCK_HEIGHT;
					blocks[i][j].color = 4 - i; // 0:white 1:yellow 2:blue 3:mazanta 4:red
					blocks[i][j].isHidden = false; // 블록이 깨지면 없어지는 것으로 설정

				}
			}
		}

		public void setKeyListener() { // 키보드 조작함수생성
			this.addKeyListener(new KeyAdapter() { // 키보드 수용 함수
				// Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_LEFT) { // 화살표왼쪽키
						System.out.println("pressed left key");
						barXTarget -= 20; // 바움직임이 끊기지 않게 움지이는 함수
						if (bar.x < barXTarget) { // 키를 연타했을때(예외처리)
							barXTarget = bar.x;
						}
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) { // 화살표 오른쪽키
						System.out.println("pressed right key");
						barXTarget += 20;
						if (bar.x > barXTarget) { // 키를 연타했을때(예외처리)
							barXTarget = bar.x;
						}
					}
				}
			});
		}

		public void startTimer() { // 타이머 함수 생성
			timer = new Timer(20, new ActionListener() { // swing에서 지원하는 타이머 함수

				@Override
				public void actionPerformed(ActionEvent e) { // 타이머 이벤트
					movement(); // movement(공) 함수정의
					checkCollision();// 충돌처리 정의(벽and바)
					checkCollisionBlock();// 블럭 충돌처리 정의(블록)
					myPanel.repaint();// ui새로업데이트 redraw

					isGameFinish();
				}
			});
			timer.start(); // 실제타이머 시작
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

		public void movement() { // 공의 움직임
			if (bar.x < barXTarget) {
				bar.x += 5;
			} else if (bar.x > barXTarget) {
				bar.x -= 5;
			}
			if (dir == 0) { // up-right
				ball.x += ballSpeed; // 공의 속도
				ball.y -= ballSpeed;
			} else if (dir == 1) { // down-right
				ball.x += ballSpeed; // 공의 속도
				ball.y += ballSpeed;
			} else if (dir == 2) { // up-left
				ball.x -= ballSpeed; // 공의 속도
				ball.y -= ballSpeed;
			} else if (dir == 3) { // down-left
				ball.x -= ballSpeed; // 공의 속도
				ball.y += ballSpeed;
			}
		}

		// duplRect함수 선언
		public boolean duplRect(Rectangle rect1, Rectangle rect2) {
			return rect1.intersects(rect2); // 충돌여부체크(check two rect is duplicated)
		}

		public void checkCollision() { // 벽과 바의 충돌 함수
			if (dir == 0) { // up-right
				// wall
				if (ball.y < 0) { // wall upper
					dir = 1;
				}
				if (ball.x > CANVAS_WIDTH - BALL_WIDTH) { // 오른쪽벽에부딪쳤을때
					dir = 2;
				}
				// bar(위쪽으로 가고있을때는 바의 대한 충돌 x)
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
				if (ball.x > CANVAS_WIDTH - BALL_WIDTH) { // 오른쪽벽에부딪쳤을때
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
				if (ball.y < 0) { // wall upper(위쪽벽)
					dir = 3;
				}
				if (ball.x < 0) { // left wall(왼쪽벽)
					dir = 0;
				}
				// bar(위쪽으로 가고있을때는 바의 대한 충돌 x)
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

		public void checkCollisionBlock() { // 블럭의 출동
			// 0:up-right 1: down-right 2: up-left 3: down-left
			for (int i = 0; i < BLOCK_ROWS; i++) {
				for (int j = 0; j < BLOCK_CLUMMS; j++) {
					Block block = blocks[i][j];
					if (block.isHidden == false) {
						if (dir == 0) { // 0:up-right
							if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
									new Rectangle(block.x, block.y, block.width, block.height))) {
								if (ball.x > block.x + 2 && // 공의 실제크기보다 살짝크게해(+2) 블럭충돌
										ball.getRightCenter().x <= block.x + block.width - 2) {
									// 블록바텀충돌
									dir = 1;
								} else {
									// 블록왼쪽 충돌
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
								if (ball.x > block.x + 2 && // 공의 실제크기보다 살짝크게해(+2) 블럭충돌
										ball.getRightCenter().x <= block.x + block.width - 2) {
									// 블록탑충돌
									dir = 0;
								} else {
									// 블록왼쪽 충돌
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
								if (ball.x > block.x + 2 && // 공의 실제크기보다 살짝크게해(+2) 블럭충돌
										ball.getRightCenter().x <= block.x + block.width - 2) {
									// 블록바텀충돌
									dir = 3;
								} else {
									// 블록오른쪽 충돌
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
								if (ball.x > block.x + 2 && // 공의 실제크기보다 살짝크게해(+2) 블럭충돌
										ball.getRightCenter().x <= block.x + block.width - 2) {
									// 블록탑충돌
									dir = 2;
								} else {
									// 블록왼쪽 충돌
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
