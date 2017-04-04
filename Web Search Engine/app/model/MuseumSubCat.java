package model;



public enum MuseumSubCat {
    Art("art", "Art", "art"),
    History("history", "History", "history"),
    Science("science", "Science", "science");

    String aggLookupName;
    String searchName;
    String checkBoxName;

    MuseumSubCat(final String  aggLookupName, final String searchName, final String checkBoxName) {
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
