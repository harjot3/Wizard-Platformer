package Panels;

import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.*;

public class MenuPanel extends JPanel {

    private Color backgroundColor;

    public MenuPanel(CardLayout cardLayout, JPanel container) {
        setBackground(backgroundColor);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
    public static void main(String[] args) {

    }
}
