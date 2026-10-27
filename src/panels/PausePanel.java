package panels;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public final class PausePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */

	private static final int z600 = MainFrame.Zoom(600), z10 = MainFrame.Zoom(10), z400 = MainFrame.Zoom(400),
			z100 = MainFrame.Zoom(100), z65 = MainFrame.Zoom(65), z300 = MainFrame.Zoom(300), z4 = MainFrame.Zoom(4),
			z250 = MainFrame.Zoom(250), z80 = MainFrame.Zoom(80), z40 = MainFrame.Zoom(40), z500 = MainFrame.Zoom(500),
			z200 = MainFrame.Zoom(200), z360 = MainFrame.Zoom(360);

	public PausePanel() {
		setBackground(Color.BLACK);
		setLayout(null);
		setBorder(new LineBorder(Color.white, z10));
		setLocation(MainFrame.Zoom(1920) / 2 - z300, MainFrame.Zoom(1080) / 2 - z250);
		setSize(z600, z500);
		JLabel lblResume = new JLabel("Resume");
		lblResume.setHorizontalAlignment(SwingConstants.CENTER);
		lblResume.setForeground(Color.WHITE);
		lblResume.setFont(MainFrame.getPixelFont(z65));
		lblResume.setBorder(new LineBorder(Color.white, z4));
		lblResume.setBounds(z100, z40, z400, z80);
		lblResume.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == MouseEvent.BUTTON1)
					MainFrame.getGamePanel().Resume();
			}

			@Override
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				lblResume.setForeground(Color.WHITE);
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				lblResume.setForeground(Color.yellow);
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		add(lblResume);

		JLabel lblRetry = new JLabel("Retry");
		lblRetry.setHorizontalAlignment(SwingConstants.CENTER);
		lblRetry.setForeground(Color.WHITE);
		lblRetry.setFont(MainFrame.getPixelFont(z65));
		lblRetry.setBorder(new LineBorder(Color.white, z4));
		lblRetry.setBounds(z100, z200, z400, z80);
		lblRetry.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public final void mousePressed(MouseEvent e) {
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
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				lblRetry.setForeground(Color.WHITE);
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				lblRetry.setForeground(Color.yellow);
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		add(lblRetry);

		JLabel lblBack = new JLabel("Back to Title");
		lblBack.setHorizontalAlignment(SwingConstants.CENTER);
		lblBack.setFont(MainFrame.getPixelFont(z65));
		lblBack.setBorder(new LineBorder(Color.white, z4));
		lblBack.setForeground(Color.WHITE);
		lblBack.setBounds(z100, z360, z400, z80);
		lblBack.addMouseListener(new MouseListener() {

			@Override
			public final void mouseReleased(MouseEvent e) {
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
			public final void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				lblBack.setForeground(Color.WHITE);
			}

			@Override
			public final void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				lblBack.setForeground(Color.yellow);
			}

			@Override
			public final void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		add(lblBack);

	}
}
