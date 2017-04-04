package model;

public class PropagationParams {
    String userQuery;
    String originalQuery;

    boolean enableLeftNav = false;

    boolean suggest = true;

    public boolean isSuggest() {
        return suggest;
    }

    public void setSuggest(final boolean suggest) {
        this.suggest = suggest;
    }

    LeftNavSettings leftNavSettings;

    public LeftNavSettings getLeftNavSettings() {
        return leftNavSettings;
    }

    public void setLeftNavSettings(final LeftNavSettings leftNavSettings) {
        this.leftNavSettings = leftNavSettings;
    }

    public boolean isEnableLeftNav() {
        return enableLeftNav;
    }

    public void setEnableLeftNav(final boolean enableLeftNav) {
        this.enableLeftNav = enableLeftNav;
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(final String userQuery) {
        this.userQuery = userQuery;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public void setOriginalQuery(final String originalQuery) {
        this.originalQuery = originalQuery;
    }
}
