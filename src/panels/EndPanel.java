package panels;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public final class EndPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */

	private static final int z800 = MainFrame.Zoom(800), z600 = MainFrame.Zoom(600), z10 = MainFrame.Zoom(10),
			z400 = MainFrame.Zoom(400), z100 = MainFrame.Zoom(100), z72 = MainFrame.Zoom(72), z65 = MainFrame.Zoom(65),
			z300 = MainFrame.Zoom(300), z4 = MainFrame.Zoom(4), z250 = MainFrame.Zoom(250), z80 = MainFrame.Zoom(80),
			z410 = MainFrame.Zoom(410);

	public EndPanel() {
		setSize(z800, z600);
		setBackground(Color.BLACK);
		setLayout(null);
		setBorder(new LineBorder(Color.white, z10));
		setLocation(MainFrame.Zoom(1920) / 2 - z400, MainFrame.Zoom(1080) / 2 - z300);
		JLabel lblResume = new JLabel("Game Over");
		lblResume.setHorizontalAlignment(SwingConstants.CENTER);
		lblResume.setForeground(Color.WHITE);
		lblResume.setFont(MainFrame.getPixelFont(z100));
		lblResume.setBounds(z100, z72, z600, z100);
		add(lblResume);

		JLabel lblRetry = new JLabel("Retry");
		lblRetry.setHorizontalAlignment(SwingConstants.CENTER);
		lblRetry.setForeground(Color.WHITE);
		lblRetry.setFont(MainFrame.getPixelFont(z65));
		lblRetry.setBorder(new LineBorder(Color.white, z4));
		lblRetry.setBounds(z100, z250, z600, z80);
		lblRetry.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1) {
					MainFrame.getGamePanel().StopMusic();
					MainFrame.getGamePanel().Reset();
					MainFrame.getGamePanel().Start(MainFrame.getGamePanel().isPracticeMode(),
							MainFrame.getGamePanel().getMaxLevel());
					setVisible(false);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				lblRetry.setForeground(Color.WHITE);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				lblRetry.setForeground(Color.yellow);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		add(lblRetry);

		JLabel lblBack = new JLabel("Back to Title");
		lblBack.setHorizontalAlignment(SwingConstants.CENTER);
		lblBack.setFont(MainFrame.getPixelFont(z65));
		lblBack.setBorder(new LineBorder(Color.white, z4));
		lblBack.setForeground(Color.WHITE);
		lblBack.setBounds(z100, z410, z600, z80);
		lblBack.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1) {
					MainFrame.getStartPanel().setVisible(true);
					MainFrame.getStartPanel().requestFocus();
					MainFrame.getGamePanel().StopMusic();
					MainFrame.getGamePanel().PlayMusic("Title");
					setVisible(false);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				lblBack.setForeground(Color.WHITE);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				lblBack.setForeground(Color.yellow);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(lblBack);
	}
}
