/**
 * GUI for the program RadioInfo.
 * The program is built by the MVC design pattern,
 * and this class is the View.
 *
 * @Author  Pontus Dahlkvist
 * @Date    26/01 -25
 */

/**
 * -------------------- Imports --------------------
 */

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

/**
 * -------------------- GUI class --------------------
 */

public class GUI {
    private JButton button;
    private JMenuBar menuBar;
    private JMenu menu;
    private Controller controller;
    private JPanel episodePanel;
    private DefaultTableModel tableModel;
    private JTable jTable1;
    private boolean detailedMode = true;
    private JMenu modeMenu;
    private JPanel panel;
    private JFrame frame;
    private JScrollPane scrollPane;
    private JScrollPane scrollTable;
    private JMenuItem lightModeItem;

    /**
     * Constructor.
     */
    public GUI() {
        buildGUI();
    }

    /**
     * Build the GUI with all the
     * components.
     */
    private void buildGUI() {
        buildTable();
        buildPanel();
        buildEpisodePanel();
        buildMenuBar();
        buildFrame();
    }

    /**
     * Adds an actionListener to the "update" button.
     * When the button is pressed the controller will
     * try to update the episodes displayed in this GUI.
     * If it fails it will display a text saying that
     * something went wrong.
     */
    private void updateButtonListener() {
        button.addActionListener(e -> {
            if (controller != null) {
                try {
                    controller.updateEpisodes();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Något gick fel: " + ex.getMessage(), "Fel", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Will build the MenuBar for the GUI.
     */
    private void buildMenuBar() {
        menuBar = new JMenuBar();
        menu = new JMenu("Kanaler");
        modeMenu = new JMenu("Läge");
        menuBar.add(menu);
        menuBar.add(modeMenu);

        modeMenuItems();
    }

    /**
     * Will add the different program modes to
     * a menu. Each menuitem/mode that is added to
     * the menu will also have an actionListener that
     * checks when it is pressed. When one of them is pressed,
     * the controller will change the mode of the program and
     * update the episode list.
     */
    private void modeMenuItems() {
        lightModeItem = new JMenuItem("Lätt");
        lightModeItem.addActionListener(e -> {
            detailedMode = false;
            controller.updateEpisodes();

        });
        JMenuItem detailedModeItem = new JMenuItem("Detaljerad");
        detailedModeItem.addActionListener(e -> {
            detailedMode = true;
            controller.updateEpisodes();

        });
        modeMenu.add(lightModeItem);
        modeMenu.add(detailedModeItem);
    }

    /**
     * Will build the table used to display
     * all episodes.
     */
    private void buildTable() {
        tableModel = new DefaultTableModel(new Object[]{"Program", "Starttid", "Sluttid", "Beskrivning", "Bild"}, 0);
        jTable1 = new JTable(tableModel);
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new ImageTextRenderer());
        scrollTable = new JScrollPane(jTable1);

        jTable1.setDefaultEditor(Objects.class, null);
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(300);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTable1.setRowHeight(100);

        fixTextNewLine(3);
        fixTextNewLine(0);
    }

    /**
     * Will make it so that a specified column
     * in JTable1 will have its text wrap around,
     * instead of continuing off the edge of itself.
     *
     * @param column    The column to be fixed.
     */
    private void fixTextNewLine(int column) {
        jTable1.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value != null) {
                    String text = "<html>" + value.toString().replace("\n", "<br>") + "</html>";
                    setText(text);
                } else {
                    setText("");
                }
            }
        });
    }

    /**
     * Will build the panel that holds all the episodes.
     */
    private void buildEpisodePanel() {
        episodePanel = new JPanel();
        episodePanel.setLayout(new BoxLayout(episodePanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(episodePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    /**
     * Will build a panel holding the update button.
     */
    private void buildPanel() {
        button = new JButton("Uppdatera");
        updateButtonListener();
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new BorderLayout());
        panel.add(button, BorderLayout.SOUTH);
        panel.add(scrollTable, BorderLayout.CENTER);
    }

    /**
     * Will build the main frame for the GUI.
     * Every other component is within this frame.
     */
    private void buildFrame() {
        frame = new JFrame();
        frame.setJMenuBar(menuBar);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("RadioInfo");
        frame.setSize(715, 500);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    /**
     * Will add a controller class to this GUI
     * so they can communicate.
     *
     * @param controller    The controller to be added.
     */
    public void addController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Will add every channel as its own button
     * in the channel menu.
     *
     * @param channels  All channels to be added.
     */
    public void addChannelButtons(ArrayList<Channel> channels) {
        for (Channel channel : channels) {
            JMenuItem menuItem = new JMenuItem(channel.getName());
            menu.add(menuItem);
            menuItem.addActionListener(e -> {
                controller.clickedChannel(channel);

            });
        }
    }

    /**
     * Will check if this GUI is
     * in detailed mode or not.
     *
     * @return  True if detailed, false if not.
     */
    public boolean isDetailedMode() {
        return detailedMode;
    }

    /**
     * Will take an episode from a channel and
     * add it to the GUI so it is displayed.
     * This method will be used in detailed mode.
     *
     * @param program       The title of the episode.
     * @param startTime     The starting time.
     * @param endTime       The ending time.
     * @param description   The episode description.
     * @param image         The episodes image.
     */
    public void addProgramToTable(String program, String startTime, String endTime, String description, String image) {
        try {
            ImageIcon imageIcon = new ImageIcon(new URL(image));
            imageIcon = resizeImage(imageIcon);
            tableModel.addRow(new Object[]{program, startTime, endTime, description, imageIcon});
        } catch (MalformedURLException ex) {
            tableModel.addRow(new Object[]{program, startTime, endTime, description, "Ingen bild"});
        }
    }

    /**
     * Will resize an image so it fits
     * the GUI.
     *
     * @param icon  The image to be resized.
     * @return      The resized image.
     */
    public ImageIcon resizeImage(ImageIcon icon) {
        int width = 100;
        int height = 100;
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /**
     * Will add an episode to the GUI
     * so it is displayed.
     * This method will be used in light mode.
     *
     * @param program       The episodes title.
     * @param startTime     The episodes starting time.
     * @param endTime       The episodes ending time.
     */
    public void addProgramToTable(String program, String startTime, String endTime) {
        tableModel.addRow(new Object[]{program, startTime, endTime});
    }

    /**
     * Will clear all episodes from the
     * GUI so that its clean before new ones come.
     */
    public void clearTable() {
        tableModel.setRowCount(0);
    }

    /**
     * Will fix it so the table can display images.
     * I found this solution online.
     *
     * https://stackoverflow.com/questions/4941372/how-to-insert-image-into-jtable-cell
     */
    private class ImageTextRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(value instanceof ImageIcon) {
                JLabel label = new JLabel((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}


