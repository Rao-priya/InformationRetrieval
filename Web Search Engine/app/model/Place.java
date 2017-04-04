package model;

import java.util.List;

/**
 * Created by MadVish on 5/12/16.
 */
public class Place {
    private String title = null;
    private String liveURL = null;
    private play.twirl.api.Html snippet = null;
    private List<String> city = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getLiveURL() {
        return liveURL;
    }

    public void setLiveURL(final String liveURL) {
        this.liveURL = liveURL;
    }

    public play.twirl.api.Html getSnippet() {
        return snippet;
    }

    public void setSnippet(final String snippet) {
        this.snippet = new play.twirl.api.Html(snippet);
    }

    public List<String> getCity() {
        return city;
    }

    public void setCity(final List<String> city) {
        this.city = city;
    }
}
