package panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

import animation.Animation;
import bullet.Bullet;
import character.Chain;
import character.Demon;
import character.Enemy;
import character.FlyTrap;
import character.Golem;
import character.Player;
import character.Slime;
import character.Wizard;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import enemyattack.EnemyAttack;
import enemyattack.Warning;

public final class GamePanel extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;
	private static int BorderWidth = Zoom(8);
	private ExecutorService ThreadPool;
	private Player player = null;
	private Rectangle Border;
	private List<Bullet> bullets = new CopyOnWriteArrayList<>();
	private Iterator<Bullet> bulletmainIterator, bulletpaintIterator;
	private List<EnemyAttack> enemyAttacks = new CopyOnWriteArrayList<>();
	private Iterator<EnemyAttack> enemyAttackmainIterator, enemyAttackpaintIterator;
	private List<Enemy> enemys = new CopyOnWriteArrayList<>();
	private Iterator<Enemy> enemymainIterator, enemypaintIterator;
	private List<Warning> warningsList = new CopyOnWriteArrayList<>();
	private Iterator<Warning> warningIterator, paintIterator;
	private volatile boolean gameRunning = true, isPause = false;
	private Robot robot;
	private int Level;
	private EndPanel endPanel;
	private PausePanel pausePanel;
	private WinPanel winPanel;
	private AudioInputStream backgroundMusic, sounds;
	private Clip backgroundMusicClip, soundsClip;
	private int nextLevelTick = 1;
	private Chain chain;
	private long time;
	private boolean practiceMode;
	private int maxLevel = 6;

	private final Runnable mainRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			long start;
			DoublePolygon playerHitbox;
			DoublePoint playerCenterPoint;
			while (gameRunning) {
				if (!isPause) {
					start = System.currentTimeMillis();
					if (Animation.isGameEnd())
						Animation.GameEndNext(player);
					else {
						player.Move();
						player.StaminaUp();
						Animation.Next(player);
					}
					playerCenterPoint = player.getCenterPoint();
					playerHitbox = player.getHitbox();
					bulletmainIterator = bullets.iterator();
					while (bulletmainIterator.hasNext()) {
						Bullet bullet = bulletmainIterator.next();
						if (bullet.isRemoveable())
							bullets.remove(bullet);
						else {
							bullet.Move(playerCenterPoint);
							if (Animation.isCountering())
								bullet.Countered(player.getCenterCircle());
							if (bullet.HitCheck(playerHitbox))
								BeenHit();
						}
					}
					warningIterator = warningsList.iterator();
					while (warningIterator.hasNext()) {
						Warning warning = warningIterator.next();
						if (!warning.isAvailable())
							warningsList.remove(warning);
						else
							warning.next();
					}
					enemyAttackmainIterator = enemyAttacks.iterator();
					while (enemyAttackmainIterator.hasNext()) {
						EnemyAttack enemyAttack = enemyAttackmainIterator.next();
						if (enemyAttack.isRemoveable())
							enemyAttacks.remove(enemyAttack);
						else {
							enemyAttack.Next();
							if (enemyAttack.HitCheck(playerHitbox))
								BeenHit();
						}
					}
					enemymainIterator = enemys.iterator();
					while (enemymainIterator.hasNext()) {
						Enemy enemy = enemymainIterator.next();
						if (!enemy.isRemoveable()) {
							enemy.Next(playerCenterPoint);
							if (!Animation.isGameEnd())
								enemy.HitCheck(playerHitbox);
						} else
							enemys.remove(enemy);
					}
					if (enemys.isEmpty()) {
						StopMusic();
						nextLevelTick--;
						if (nextLevelTick == 0)
							NextLevel();
					}
					chain.Next();
					time += 6;
					start = System.currentTimeMillis() - start;
					if (start >= 6)
						continue;
					else {
						try {
							Thread.sleep(6 - start);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							Thread.currentThread().interrupt();
						}
					}

				} else
					System.currentTimeMillis();
			}
		}
	};

	private final Runnable paintRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (gameRunning) {
				if (!isPause) {
					repaint();
					try {
						Thread.sleep(5);
					} catch (Exception e) {
						// TODO: handle exception
					}
				} else
					System.currentTimeMillis();
			}
		}
	};

	private static final int zoom500 = Zoom(500), zoom1420 = Zoom(1420), zoom900 = Zoom(900), zoom120 = Zoom(120);

	private final Runnable mouseRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			boolean CounterLtoR = true;
			long CounterStart = 0, AttackStart = 0;
			double CounterStartY = 0, AttackStartX = 0;
			Point e;
			while (gameRunning) {
				if (!isPause) {
					e = MouseInfo.getPointerInfo().getLocation();
					if (e.getX() <= zoom500) {
						if (CounterLtoR) {
							CounterStart = System.currentTimeMillis();
							CounterLtoR = true;
							CounterStartY = e.getY();
						} else {
							if (System.currentTimeMillis() - CounterStart <= 150
									&& Math.abs(e.getY() - CounterStartY) <= Zoom(400))
								player.Counter(false);
							CounterLtoR = true;
						}
					} else if (e.getX() >= zoom1420) {
						if (!CounterLtoR) {
							CounterStart = System.currentTimeMillis();
							CounterLtoR = false;
							CounterStartY = e.getY();
						} else {
							if (System.currentTimeMillis() - CounterStart <= 150
									&& Math.abs(e.getY() - CounterStartY) <= Zoom(400))
								player.Counter(true);
							CounterLtoR = false;
						}
					}
					if (e.getY() >= zoom900) {
						AttackStart = System.currentTimeMillis();
						AttackStartX = e.getX();
					} else if (e.getY() <= zoom120) {
						if (System.currentTimeMillis() - AttackStart <= 100
								&& Math.abs(e.getX() - AttackStartX) <= Zoom(500))
							player.Attack();
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException ex) {
						// TODO Auto-generated catch block
						Thread.currentThread().interrupt();
					}
				} else
					System.currentTimeMillis();
			}
		}
	};

	private static final int Zoom(double input) {
		return MainFrame.Zoom(input);
	}

	public final synchronized void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}

	public final synchronized void addAttack(EnemyAttack attack) {
		enemyAttacks.add(attack);
		warningsList.add(attack.getWarning());
	}

	public final synchronized void addEnemy(Enemy enemy) {
		enemys.add(enemy);
	}

	public final synchronized void setPlayerLocking(boolean Locking) {
		player.setLocking(Locking);
	}

	public final synchronized void Clear() {
		bullets.clear();
		enemyAttacks.clear();
		warningsList.clear();
	}

	public final Chain getChain() {
		return chain;
	}

	private static final int slimeX = Zoom(960), slimeY = Zoom(170), demonX1 = Zoom(1350), demonX2 = Zoom(570),
			golemY = Zoom(200);

	private final void NextLevel() {
		Clear();
		if (++Level == maxLevel) {
			gameRunning = false;
			repaint();
			StopMusic();
			winPanel.Win(time, practiceMode);
			robot.mouseMove(MainFrame.getScreenWidth() / 2, MainFrame.getScreenHeight() / 2);
			PlaySound("Victory");
		} else {
			switch (Level) {
			case 1:
				addEnemy(new Slime(slimeX, slimeY));
				break;
			case 2:
				addEnemy(new Demon(demonX1, slimeY));
				addEnemy(new Demon(demonX2, slimeY));
				break;
			case 3:
				addEnemy(new Golem(slimeX, golemY));
				break;
			case 4:
				addEnemy(new FlyTrap(player.getPosition().getX() < slimeX ? Zoom(1660) : Zoom(260), golemY));
				break;
			case 5:
				addEnemy(new Wizard(slimeX, golemY));
				break;
			}
			PlayMusic(String.format("Level_%d", Level));
			nextLevelTick = 180;
		}
	}

	public final void PlayMusic(String filename) {
		try {
			backgroundMusicClip = AudioSystem.getClip();
			backgroundMusic = AudioSystem.getAudioInputStream(new BufferedInputStream(
					GamePanel.class.getResourceAsStream(String.format("/Musics/%s.wav", filename))));
			backgroundMusicClip.open(backgroundMusic);
			backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
			backgroundMusicClip.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public final void PlaySound(String filename) {
		try {
			soundsClip = AudioSystem.getClip();
			sounds = AudioSystem.getAudioInputStream(new BufferedInputStream(
					GamePanel.class.getResourceAsStream(String.format("/Sounds/%s.wav", filename))));
			soundsClip.open(sounds);
			soundsClip.loop(0);
			soundsClip.start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public final void StopMusic() {
		backgroundMusicClip.stop();
	}

	public final void GameEnd() {
		StopMusic();
		gameRunning = false;
		repaint();
		endPanel.setVisible(true);
		robot.mouseMove(MainFrame.getScreenWidth() / 2, MainFrame.getScreenHeight() / 2);
		PlaySound("Game_Over");
	}

	private final void BeenHit() {
		if (!Animation.isGameEnd()) {
			Animation.setGameEnd(true);
			PlaySound("Dead");
		}
	}

	private final void Pause() {
		isPause = true;
		pausePanel.setVisible(true);
	}

	public final void Resume() {
		isPause = false;
		pausePanel.setVisible(false);
	}

	public final void Reset() {
		endPanel = MainFrame.getEndPanel();
		pausePanel = MainFrame.getPausePanel();
		winPanel = MainFrame.getWinPanel();
		ThreadPool = Executors.newFixedThreadPool(3);
		Animation.Reset();
		setLocale(Locale.US);
		getInputContext().selectInputMethod(Locale.US);
		getInputContext().setCompositionEnabled(false);
		bullets.clear();
		enemyAttacks.clear();
		enemys.clear();
		gameRunning = false;
		removeAll();
		try {
			robot = new Robot();
		} catch (Exception e) {
			// TODO: handle exception
		}
		setBackground(Color.black);
		setLayout(null);
		setSize(MainFrame.getScreenWidth(), MainFrame.getScreenHeight());
		player = new Player();
		player.setPosition(Zoom(928), Zoom(950));
		Border = new Rectangle(Zoom(60), Zoom(60), Zoom(1800), Zoom(960));
		time = 0;
		chain = new Chain();
	}

	public final boolean isPracticeMode() {
		return practiceMode;
	}

	public final int getMaxLevel() {
		return maxLevel;
	}

	public final void Start(boolean practiceMode, int maxLevel) {
		this.maxLevel = maxLevel;
		this.practiceMode = practiceMode;
		if (practiceMode)
			Level = maxLevel - 2;
		else
			Level = 0;
		NextLevel();
		gameRunning = true;
		isPause = false;
		ThreadPool.execute(mainRunnable);
		ThreadPool.execute(paintRunnable);
		ThreadPool.execute(mouseRunnable);
		requestFocus();
	}

	public final void Load() {
		Animation.Load();
		Enemy.Load(this);
		Bullet.Load(this);
		EnemyAttack.Load(this);
	}

	public GamePanel() {
		addKeyListener(this);
	}

	private static final int zoom10 = Zoom(10), zoom30 = Zoom(30), zoom15 = Zoom(15);

	@Override
	public final void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		g.setColor(Color.white);
		if (!practiceMode) {
			g.setFont(MainFrame.getPixelFont(zoom30));
			g.drawString(String.format("%02d:%02d.%03d", time / 60000, time / 1000 % 60, time % 1000), 0, zoom15);
		}
		g.fillRect(Border.x, Border.y + Border.height + zoom10,
				(int) (Border.width * (Double.valueOf(player.getStamina()) / 100)), zoom10);
		g.fillRect(Border.x, Border.y, Border.width, Border.height);
		g.setColor(Color.black);
		g.fillRect(Border.x + BorderWidth, Border.y + BorderWidth, Border.width - 2 * BorderWidth,
				Border.height - 2 * BorderWidth);
		g.setColor(Color.red);
		bulletpaintIterator = bullets.iterator();
		enemyAttackpaintIterator = enemyAttacks.iterator();
		enemypaintIterator = enemys.iterator();
		paintIterator = warningsList.iterator();
		while (enemypaintIterator.hasNext())
			enemypaintIterator.next().Paint(g);
		while (paintIterator.hasNext())
			paintIterator.next().Paint(g);
		while (bulletpaintIterator.hasNext())
			bulletpaintIterator.next().PaintBullet(g);
		while (enemyAttackpaintIterator.hasNext())
			enemyAttackpaintIterator.next().paintAttack(g);
		if (Animation.isGameEnd())
			Animation.paintGameEndAnimation(g, player.getPosition());
		else {
			Animation.paintPlayerAnimation(g, player.getPosition().getX());
			player.Paint(g);
		}
		chain.Paint(g);
	}

	@Override
	public final void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public final void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (isPause)
			return;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			player.setLeftMove(false);
			break;
		case KeyEvent.VK_D:
			player.setRightMove(false);
			break;
		}
	}

	private static final int zoom70 = Zoom(70), zoom1810 = Zoom(1810);

	@Override
	public final void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			if (!isPause)
				player.setLeftMove(true);
			break;
		case KeyEvent.VK_D:
			if (!isPause)
				player.setRightMove(true);
			break;
		case KeyEvent.VK_ESCAPE:
			if (gameRunning && !Animation.isGameEnd())
				Pause();
			break;
		case KeyEvent.VK_SPACE:
			if (player.getPosition().getX() > zoom70 && player.getPosition().getX() < zoom1810 && !isPause)
				player.Dash();
			break;
		}
	}
}
