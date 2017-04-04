package model;

public enum OtherAttractions {
    Beaches("beach", "Beach", "beach"),
    NightLife("nightlif", "Nightlife", "nightlif"),
    ArchitecturalLandMarks("landmark","Architectural Landmarks","landmark"),
    AmusmentPark("amus", "Amusement Parks", "amus");

    String aggLookupName;
    String searchName;
    String checkBoxName;

    OtherAttractions(final String aggLookupName, final String searchName, final String checkBoxName) {
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
