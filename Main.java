/**
 * The main class for the RadioInfo program.
 * This program is built on the MVC design pattern.
 * The program is used to find episodes within
 * 12 hours ago and 12 hours from now from channels
 * from Sveriges radio.
 *
 * @Author Pontus Dahlkvist
 * @Date 26/01 -25
 */

/**
 * -------------------- Imports --------------------
 */

import javax.swing.*;

/**
 * -------------------- Main class --------------------
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                GUI gui = new GUI();
                Controller controller = new Controller(gui);
            } catch (Exception ignored) {

            }
        });
    }
}