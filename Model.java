/**
 * This is the model part of the MVC
 * design pattern for the program RadioInfo.
 * It takes care of the hardest operations.
 *
 * @Author  Pontus Dahlkvist
 * @Date 26/01 -25
 */

/**
 * -------------------- Imports --------------------
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * -------------------- Model class --------------------
 */
public class Model {
    private volatile ArrayList<Channel> allChannels = new ArrayList<>();

    /**
     * Constructor.
     */
    public Model() {

    }

    /**
     * Will find all the channels that
     * Sveriges radio offers, and save them in
     * an ArrayList.
     *
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public synchronized void findAllChannels() throws IOException, ParserConfigurationException, SAXException {
        URL url = new URL("http://api.sr.se/api/v2/channels" + "?pagination=false");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(con.getInputStream());

        NodeList channels = document.getElementsByTagName("channel");
        for(int i = 0; i < channels.getLength(); i++) {
            Element channelElement = (Element) channels.item(i);
            String id = channelElement.getAttribute("id");
            String name = channelElement.getAttribute("name");
            Channel channel = new Channel(name, id);
            allChannels.add(channel);
        }
    }

    /**
     * Will return all the channels that was found
     * by the findAllChannels() method.
     *
     * @return  A list of all channels from Sveriges radio.
     */
    public synchronized ArrayList<Channel> getAllChannels() {
        return allChannels;
    }

    /**
     * Internal function used for debygg. Not used
     * by the program but I decided to keep it. Maybe I
     * should remove it?
     */
    private void printAllChannels() {
        for(Channel c : allChannels) {
            System.out.println(c.getName() + " " + c.getId());
        }
    }

    /**
     * Will take a channel ID and use it to find all
     * episodes that channel will run within a timespan
     * and save them in an ArrayList.
     * The timespan is 12 hours before now, and 12 hours
     * after now. So the episode has to start within
     * that timespan to be saved in the list.
     *
     * @param id    The channel ID.
     * @return      All episodes within the timespan.
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public ArrayList<Episode> findAllEpisodesFromID(String id) throws IOException, ParserConfigurationException, SAXException {
        String todaysDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String yesterdaysDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String tomorrowsDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        ArrayList<Episode> episodeList = new ArrayList<>();

        episodeList.addAll(findAllEpisodesFromDate(id, todaysDate));
        episodeList.addAll(findAllEpisodesFromDate(id, tomorrowsDate));
        episodeList.addAll(findAllEpisodesFromDate(id, yesterdaysDate));

        return filterEpisodes(episodeList);
    }

    /**
     * Will find all episodes from a specific channel from
     * Sveriges radio on a certain date and return them.
     *
     * @param id        The ID for the channel.
     * @param date      The date.
     * @return          The list of all found episodes.
     * @throws IOException                      Something went wrong with retrieving the episodes.
     * @throws ParserConfigurationException     Something went wrong with retrieving the episodes.
     * @throws SAXException                     Something went wrong with retrieving the episodes.
     */
    public ArrayList<Episode> findAllEpisodesFromDate(String id, String date) throws IOException, ParserConfigurationException, SAXException {
        URL url = new URL("http://api.sr.se/api/v2/scheduledepisodes?channelid="
                + id + "&date=" + date + "&pagination=false");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(con.getInputStream());

        NodeList episodes = document.getElementsByTagName("scheduledepisode");
        ArrayList<Episode> episodeList = new ArrayList<>();

        for(int i = 0; i < episodes.getLength(); i++) {
            Element episodeElement = (Element) episodes.item(i);

            Node titleNode = episodeElement.getElementsByTagName("title").item(0);
            String title = titleNode.getTextContent();

            Node startTimeNode = episodeElement.getElementsByTagName("starttimeutc").item(0);
            String startTime = startTimeNode.getTextContent();

            Node endTimeNode = episodeElement.getElementsByTagName("endtimeutc").item(0);
            String endTime = endTimeNode.getTextContent();

            Node imageNode = episodeElement.getElementsByTagName("imageurl").item(0);
            String imageURL;

            Node descriptionNode = episodeElement.getElementsByTagName("description").item(0);
            String description = descriptionNode.getTextContent();

            if(imageNode != null) {
                imageURL = imageNode.getTextContent();
                Episode episode = new Episode(description, title, startTime, endTime, imageURL);
                episodeList.add(episode);
            } else {
                Episode episode = new Episode(description, title, startTime, endTime);
                episodeList.add(episode);
            }
        }
        return episodeList;
    }

    /**
     * Will take a list of episodes and filter them so
     * that only the episodes that started within 12
     * hours ago and 12 hours from now will remain.
     * Those episodes will be returned.
     *
     * @param allEpisodes   The list of episodes to be filtered.
     * @return              The list of filtered episodes.
     */
    public ArrayList<Episode> filterEpisodes(ArrayList<Episode> allEpisodes) {
        ArrayList<Episode> filteredEpisodes = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Stockholm"));
        LocalDateTime starterTime = now.minusHours(12);
        LocalDateTime enderTime = now.plusHours(12);

        for (Episode episode : allEpisodes) {
            String episodeTime = episode.getStartTime();

            String currentYear = String.valueOf(now.getYear());
            String fullDateTime = currentYear + "-" + episodeTime;

            LocalDateTime startTime = LocalDateTime.parse(fullDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (startTime.isAfter(starterTime) && startTime.isBefore(enderTime)) {
                filteredEpisodes.add(episode);
            }
        }

        return filteredEpisodes;
    }



}
