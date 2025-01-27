/**
 * This class is a channel for the RadioInfo
 * program.
 *
 * @Author Pontus Dahlkvist
 * @Date 26/01 -25
 */

/**
 * -------------------- Channel class --------------------
 */
public class Channel {
    private String name;

    private String id;

    /**
     * Constructor.
     *
     * @param name  Channel name.
     * @param id    Channel ID.
     */
    public Channel(String name, String id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Returns channel name.
     *
     * @return  Channel name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns channel ID.
     *
     * @return  Channel ID.
     */
    public String getId() {
        return id;
    }
}
