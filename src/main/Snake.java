package main;

import javax.swing.*;

public class Snake extends JFrame {

    Snake() {
        setResizable(false);
        setTitle("Snek");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("src/rsc/snek.png").getImage());



        GamePanel gamepanel = new GamePanel();

        add(gamepanel);
        pack();
        setLocationRelativeTo(null); // create window in the center of the screen
        setVisible(true);
    }

    public static void main(String[] args) {
        new Snake();
    }

}
