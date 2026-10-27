package panels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class StartPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static BufferedImage title, slime, demon, golem, flytrap, wizard;

	/**
	 * Create the panel.
	 */

	private static final int Zoom(double input) {
		return MainFrame.Zoom(input);
	}

	private static final BufferedImage ZoomImage(BufferedImage input) {
		int width = Zoom(input.getWidth()), height = Zoom(input.getHeight());
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = output.createGraphics();
		graphics2d.drawImage(input, 0, 0, width, height, null);
		graphics2d.dispose();
		return output;
	}

	public static void Load() {
		try {
			title = ZoomImage(ImageIO.read(StartPanel.class.getResourceAsStream("/Images/AfterImage/Title.png")));
			slime = ZoomImage(ImageIO.read(StartPanel.class.getResourceAsStream("/Images/Character/Slime_0.png")));
			demon = ZoomImage(ImageIO.read(StartPanel.class.getResourceAsStream("/Images/Character/Demon_0.png")));
			golem = ZoomImage(ImageIO.read(StartPanel.class.getResourceAsStream("/Images/Character/Golem_0.png")));
			flytrap = ZoomImage(ImageIO.read(StartPanel.class.getResourceAsStream("/Images/Character/FlyTrap_0.png")));
			wizard = ZoomImage(ImageIO.read(StartPanel.class.getResourceAsStream("/Images/Character/Wizard_0.png")));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public StartPanel() {
		setBackground(Color.BLACK);
		setLayout(null);
		setSize(MainFrame.getScreenWidth(), MainFrame.getScreenHeight());

		JLabel lbltitle = new JLabel();
		lbltitle.setIcon(new ImageIcon(title));
		lbltitle.setBounds(Zoom(210), Zoom(40), Zoom(1500), Zoom(471));
		add(lbltitle);

		JLabel lblNewGame = new JLabel("New Game");
		lblNewGame.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewGame.setFont(MainFrame.getPixelFont(Zoom(65)));
		lblNewGame.setForeground(Color.WHITE);
		lblNewGame.setBounds(Zoom(825), Zoom(575), Zoom(227), Zoom(52));
		lblNewGame.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1) {
					setVisible(false);
					GamePanel gamePanel = MainFrame.getGamePanel();
					gamePanel.StopMusic();
					gamePanel.setVisible(true);
					gamePanel.Reset();
					gamePanel.Start(false, 6);
				}
			}

			@Override
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				lblNewGame.setForeground(Color.WHITE);
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				lblNewGame.setForeground(Color.yellow);
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(lblNewGame);

		JLabel lblExit = new JLabel("Exit");
		lblExit.setHorizontalAlignment(SwingConstants.CENTER);
		lblExit.setForeground(Color.WHITE);
		lblExit.setFont(MainFrame.getPixelFont(Zoom(65)));
		lblExit.setBounds(Zoom(900), Zoom(710), Zoom(80), Zoom(52));
		lblExit.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1)
					System.exit(0);
			}

			@Override
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				lblExit.setForeground(Color.WHITE);
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				lblExit.setForeground(Color.yellow);
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(lblExit);

		JLabel lblslime = new JLabel();
		lblslime.setIcon(new ImageIcon(slime));
		lblslime.setBounds(Zoom(150), Zoom(900), Zoom(150), Zoom(105));
		lblslime.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1) {
					setVisible(false);
					GamePanel gamePanel = MainFrame.getGamePanel();
					gamePanel.StopMusic();
					gamePanel.setVisible(true);
					gamePanel.Reset();
					gamePanel.Start(true, 2);
				}
			}

			@Override
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(lblslime);

		JLabel lbldemon = new JLabel();
		lbldemon.setIcon(new ImageIcon(demon));
		lbldemon.setBounds(Zoom(450), Zoom(850), Zoom(210), Zoom(210));
		lbldemon.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1) {
					setVisible(false);
					GamePanel gamePanel = MainFrame.getGamePanel();
					gamePanel.StopMusic();
					gamePanel.setVisible(true);
					gamePanel.Reset();
					gamePanel.Start(true, 3);
				}
			}

			@Override
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(lbldemon);

		JLabel lblgolem = new JLabel();
		lblgolem.setIcon(new ImageIcon(golem));
		lblgolem.setBounds(Zoom(780), Zoom(800), Zoom(300), Zoom(300));
		lblgolem.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1) {
					setVisible(false);
					GamePanel gamePanel = MainFrame.getGamePanel();
					gamePanel.StopMusic();
					gamePanel.setVisible(true);
					gamePanel.Reset();
					gamePanel.Start(true, 4);
				}
			}

			@Override
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(lblgolem);

		JLabel lblflytrap = new JLabel();
		lblflytrap.setIcon(new ImageIcon(flytrap));
		lblflytrap.setBounds(Zoom(1200), Zoom(800), Zoom(240), Zoom(240));
		lblflytrap.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1) {
					setVisible(false);
					GamePanel gamePanel = MainFrame.getGamePanel();
					gamePanel.StopMusic();
					gamePanel.setVisible(true);
					gamePanel.Reset();
					gamePanel.Start(true, 5);
				}
			}

			@Override
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(lblflytrap);

		JLabel lblwizard = new JLabel();
		lblwizard.setIcon(new ImageIcon(wizard));
		lblwizard.setBounds(Zoom(1530), Zoom(770), Zoom(300), Zoom(300));
		lblwizard.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1) {
					setVisible(false);
					GamePanel gamePanel = MainFrame.getGamePanel();
					gamePanel.StopMusic();
					gamePanel.setVisible(true);
					gamePanel.Reset();
					gamePanel.Start(true, 6);
				}
			}

			@Override
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(lblwizard);

	}
}
