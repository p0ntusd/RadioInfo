/**
 * This is the controller class for the
 * MVC design pattern for the RadioInfo
 * program.
 *
 * @Author  Pontus Dahlkvist
 * @Date    26/01 -25
 */

/**
 * -------------------- Imports --------------------
 */
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * -------------------- Controller class --------------------
 */
public class Controller implements ActionListener {
    private final GUI gui;
    private Model model;
    private ScheduledExecutorService executor;
    private volatile Channel channel;

    /**
     * Constructor.
     *
     * @param gui   The GUI the controller will control.
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws SAXException
     */
    public Controller(GUI gui) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        this.gui = gui;
        this.gui.addController(this);

        model = new Model();
        model.findAllChannels();
        this.gui.addChannelButtons(model.getAllChannels());

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    updateEpisodes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.HOURS);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /**
     * Will notice when a channel is clicked in the
     * GUI and then update the GUI so it displays
     * that channels episodes.
     *
     * @param channel   The channel that is clicked.
     */
    public synchronized void clickedChannel(Channel channel) {
        this.channel = channel;
        updateEpisodes();
    }

    /**
     * Will update the GUI so that it displays
     * the correct episodes based on what channel
     * is pressed.
     * @throws IOException                      Something went wrong with retrieving the episodes.
     * @throws ParserConfigurationException     Something went wrong with retrieving the episodes.
     * @throws SAXException                     Something went wrong with retrieving the episodes.
     * @throws TransformerException             Something went wrong with retrieving the episodes.
     */
    public synchronized void updateEpisodes() {
        ArrayList<Episode> episodes;
        try {
            episodes = model.findAllEpisodesFromID(channel.getId());

            gui.clearTable();

            for(Episode episode : episodes) {
                if(gui.isDetailedMode()) {
                    gui.addProgramToTable(episode.getTitle(), episode.getStartTime(), episode.getEndTime(), episode.getDescription(), episode.getImage());
                } else {
                    gui.addProgramToTable(episode.getTitle(), episode.getStartTime(), episode.getEndTime());
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            gui.clearTable();
            gui.addProgramToTable("Episodes could not be found.",
                          "Episodes could not be found.",
                          "Episodes could not be found.");
        }
    }
}
