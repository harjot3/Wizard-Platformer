import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import Panels.PlatformGamePanel;
import Panels.MenuPanel;

/**
 * PJ-05 -- Main.java
 * <p>
 * This is the Main class in the application, and is where the program is ran through.
 * There are JPanels created for when the main menu, when the user loses the game, and for when they win. There are also according JButtons for each of the panels' options. In Java swing, you cannot re-use JButtons, so buttons with the same functionality had to be re-created for their respective JPanels. Shifting through the panels is done via CardLayout.
 * Additionally, actionListeners are written for each button so when clicked they show
 * the correct JPanel.   
 * 
 * 
 * @author Harjot Singh, 10841
 * @version Apr 24, 2026
 */
public class Main extends JFrame {
    CardLayout cardLayout = new CardLayout();
    JPanel container = new JPanel(cardLayout);

    public Main() {
        JButton playGameButton = new JButton("Play Game");
        JButton restartPlayButton = new JButton("Play Again");
        JButton restartMenuButton = new JButton("Return to Main Menu");
        JButton victoryPlayButton = new JButton("Play Again");
        JButton victoryMenuButton = new JButton("Return to Main Menu");

        JLabel menuLabel = new JLabel("Welcome to Wizard Platformer!");
        JLabel restartLabel = new JLabel("You Lost! Would you like to return to the main menu or play again?");
        JLabel victoryLabel = new JLabel("You won! Would you like to return to the main menu or play again?");

        menuLabel.setForeground(Color.white);
        restartLabel.setForeground(Color.white);

        MenuPanel startMenu = new MenuPanel(cardLayout, container);
        MenuPanel restartMenu = new MenuPanel(cardLayout, container);
        MenuPanel victoryMenu = new MenuPanel(cardLayout, restartMenu);
        PlatformGamePanel gamePanel = new PlatformGamePanel(cardLayout, container);

        startMenu.setBackground(Color.blue);
        startMenu.add(menuLabel);
        startMenu.add(playGameButton);

        restartMenu.setBackground(Color.red);
        restartMenu.add(restartLabel);
        restartMenu.add(restartMenuButton);
        restartMenu.add(restartPlayButton);

        victoryMenu.setBackground(Color.green);
        victoryMenu.add(victoryLabel);
        victoryMenu.add(victoryMenuButton);
        victoryMenu.add(victoryPlayButton);


        container.add(startMenu, "start_menu");
        container.add(gamePanel, "game");
        container.add(restartMenu, "restart_menu");
        container.add(victoryMenu, "victory_menu");

        playGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(container, "game");
                gamePanel.requestFocusInWindow();
            }
        });

        restartPlayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(container, "game");
                gamePanel.requestFocusInWindow();
            }
        });

        restartMenuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(container, "start_menu");
                startMenu.requestFocusInWindow();

            }
        });

        victoryPlayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(container, "game");
                gamePanel.requestFocusInWindow();
            }
        });

        victoryMenuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(container, "start_menu");
                startMenu.requestFocusInWindow();

            }
        });

        JFrame jFrame = new JFrame("Platformer");
        jFrame.add(container);
        jFrame.setSize(800, 800);
        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.setResizable(false);
        jFrame.setLocationRelativeTo(null);
        setCloseWindowKeyStroke(jFrame, "meta W");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });  
    }

    public static void setCloseWindowKeyStroke(JFrame frame, String keyStroke) {
        // setting cmd + w to close window
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(keyStroke), "closeWindow"
        );

        frame.getRootPane().getActionMap().put("closeWindow", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); 
            }
        });
    }
}
