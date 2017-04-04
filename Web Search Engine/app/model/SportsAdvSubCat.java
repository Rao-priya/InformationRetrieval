package model;

public enum SportsAdvSubCat {
    OutDoorSports("outdoor sports","Outdoor Sports" ,"outdoorsports"),
    IndoorSports("indoor sports", "Indoor Sports", "indoortsports"),
    WaterSports("water sports", "Water Sports", "watersports");

    String aggLookupName;
    String searchName;
    String checkBoxName;

    SportsAdvSubCat(final String aggLookupName, final String searchName, final String checkBoxName) {
        this.aggLookupName = aggLookupName;
        this.searchName = searchName;
        this.checkBoxName = checkBoxName;
    }

    public String getAggLookupName() {
        return aggLookupName;
    }

    public String getSearchName() {
        return searchName;
    }

    public String getCheckBoxName() {
        return checkBoxName;
    }

}
