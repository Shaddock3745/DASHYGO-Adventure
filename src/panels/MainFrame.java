package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public final class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static double ZoomRatio;
	private static int ScreenWidth, ScreenHeight;
	private static MainFrame mainForm;
	private static StartPanel startPanel;
	private static GamePanel gamePanel;
	private static PausePanel pausePanel;
	private static EndPanel endPanel;
	private static WinPanel winPanel;
	private static JPanel MainPane;
	private static Font pixelFont;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainForm = new MainFrame();
					mainForm.setVisible(true);
					mainForm.setCursor(mainForm.getToolkit().createCustomCursor(
							ImageIO.read(MainFrame.class.getResourceAsStream("/Images/Sword/Sword.png")),
							new Point(0, 0), ""));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static final double getZoomRatio() {
		return ZoomRatio;
	}

	public static final int getScreenWidth() {
		return ScreenWidth;
	}

	public static final int getScreenHeight() {
		return ScreenHeight;
	}

	public static final MainFrame getMainForm() {
		return mainForm;
	}

	public static final StartPanel getStartPanel() {
		return startPanel;
	}

	public static final GamePanel getGamePanel() {
		return gamePanel;
	}

	public static final PausePanel getPausePanel() {
		return pausePanel;
	}

	public static final EndPanel getEndPanel() {
		return endPanel;
	}

	public static final WinPanel getWinPanel() {
		return winPanel;
	}

	public static final int Zoom(double input) {
		return (int) Math.round(input * ZoomRatio);
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		LoadFont();
		setUndecorated(true);
		ScreenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		ScreenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		if (ScreenWidth / 16 != ScreenHeight / 9)
			JOptionPane.showMessageDialog(this,
					"Detected no-16:9 monitor.\nTo best experience, please set yout main screen to a 16:9 monitor.",
					"Notice", JOptionPane.WARNING_MESSAGE);
		ZoomRatio = Math.min((double) ScreenWidth / 1920, (double) ScreenHeight / 1080);
		setSize((int) Math.floor(1920 * ZoomRatio), (int) Math.floor(1080 * ZoomRatio));
		setLocation((int) Math.floor((ScreenWidth - Zoom(1920)) / 2),
				(int) Math.floor(((ScreenHeight - Zoom(1080)) / 2)));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainPane = new JPanel();
		MainPane.setBackground(Color.BLACK);
		setContentPane(MainPane);
		MainPane.setLayout(new BorderLayout(0, 0));
		StartPanel.Load();
		endPanel = new EndPanel();
		startPanel = new StartPanel();
		pausePanel = new PausePanel();
		gamePanel = new GamePanel();
		winPanel = new WinPanel();
		winPanel.setVisible(false);
		endPanel.setVisible(false);
		gamePanel.setVisible(false);
		pausePanel.setVisible(false);
		MainPane.add(winPanel);
		MainPane.add(endPanel);
		MainPane.add(pausePanel);
		MainPane.add(startPanel);
		MainPane.add(gamePanel);
		gamePanel.Load();
		gamePanel.PlayMusic("Title");
	}

	public static void LoadFont() {
		try {
			pixelFont = Font.createFont(Font.TRUETYPE_FONT,
					MainFrame.class.getResourceAsStream("/Images/AfterImage/Pixel.ttf"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Font getPixelFont(float size) {
		return pixelFont.deriveFont(Font.PLAIN, size);
	}
}
