/**
 * This class in an episode for the
 * RadioInfo program.
 *
 * @Author Pontus Dahlkvist
 * @Date 25/01 -25
 */

/**
 * -------------------- Imports --------------------
 */
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * -------------------- Episode class --------------------
 */
public class Episode {
    private String title;
    private String starttime;
    private String endtime;
    private String imageURL;
    private String description;

    /**
     * Constructor. Used for gui Detailed mode.
     *
     * @param description   Episode description.
     * @param title         Episode title.
     * @param starttime     Episode start time.
     * @param endTime       Episode end time.
     * @param imageURL      Episode image.
     */
    public Episode(String description, String title, String starttime, String endTime, String imageURL) {
        this.title = title;
        this.starttime = starttime;
        this.endtime = endTime;
        this.imageURL = imageURL;
        this.description = description;

        convertToSwedishTime();
    }

    /**
     * Constructor. Used for gui Light mode.
     *
     * @param description   Episode description.
     * @param title         Episode title.
     * @param starttime     Episode start time.
     * @param endTime       Episode end time.
     */
    public Episode(String description, String title, String starttime, String endTime) {
        this.title = title;
        this.starttime = starttime;
        this.endtime = endTime;
        this.description = description;

        convertToSwedishTime();
    }

    /**
     * Returns episode description.
     *
     * @return  Episode description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns episode image url.
     *
     * @return  Episode image url.
     */
    public String getImage() {
        return imageURL;    }

    /**
     * Returns episode title.
     *
     * @return  Episode title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns episode start time.
     *
     * @return  Episode start time.
     */
    public String getStartTime() {
        return starttime;
    }

    /**
     * Returns episode end time.
     *
     * @return  Episode end time.
     */
    public String getEndTime() {
        return endtime;
    }

    /**
     * Will converts the episodes start time and
     * end time to local swedish time.
     */
    private void convertToSwedishTime() {
        ZonedDateTime zdtStart = ZonedDateTime.parse(starttime);
        ZonedDateTime zdtEnd = ZonedDateTime.parse(endtime);

        ZonedDateTime zdtStartLocal = zdtStart.withZoneSameLocal(ZoneId.of("Europe/Stockholm"));
        ZonedDateTime zdtEndLocal = zdtEnd.withZoneSameLocal(ZoneId.of("Europe/Stockholm"));

        endtime = zdtEndLocal.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        starttime = zdtStartLocal.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
    }
}
