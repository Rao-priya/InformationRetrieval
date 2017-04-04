package model;

public enum City {
    SFO("sanfrancisco","san francisco", "San Francisco", "sfo"),
    LA("losangeles", "los angeles", "LA", "la"),
    LakeTahoe("laketahoe", "lake tahoe", "Lake Tahoe", "laketahoe"),
    LongBeach("longbeach", "long beach", "Long Beach","longbeach"),
    SanDiego("sandiego", "san diego", "San Diego", "sandiego"),
    SantaCruz("santacruz", "santa cruz", "Santa Cruz", "santacruz"),
    Monetery("monterey","monterey", "Monterey","monterey"),
    SantaBarbara("santabarbara","santa barbara", "Santa Barbara","santabarbara");

    String aggName;
    String aggLookUpName;
    String searchName;
    String checkBoxName;

    public String getAggLookUpName() {
        return aggLookUpName;
    }

    public void setAggLookUpName(final String aggLookUpName) {
        this.aggLookUpName = aggLookUpName;
    }

    public String getAggName() {
        return aggName;
    }

    public String getSearchName() {
        return searchName;
    }

    public String getCheckBoxName() {
        return checkBoxName;
    }

    City(final String aggName, String aggLookUpName, final String searchName, final String checkBoxName) {
        this.aggName = aggName;
        this.aggLookUpName = aggLookUpName;
        this.searchName = searchName;
        this.checkBoxName = checkBoxName;

    }
}
