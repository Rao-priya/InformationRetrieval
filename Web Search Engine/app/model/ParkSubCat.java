package model;

public enum ParkSubCat {
    NationalPark("national park","National Park", "nationalpark"),
    StatePark("state park","State Park", "statepark");

    String aggLookupName;
    String searchName;
    String checkBoxName;

    ParkSubCat(final String aggLookupName, final String searchName, final String checkBoxName) {
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
