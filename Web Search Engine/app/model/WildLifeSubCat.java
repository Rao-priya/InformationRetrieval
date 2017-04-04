package model;

public enum WildLifeSubCat {
    Zoo("zoo","Zoo","zoo"),
    Aquarium("aquarium","Aquarium","aquarium");

    String aggLookupName;
    String searchName;
    String checkBoxName;

    WildLifeSubCat(final String aggLookupName, final String searchName, final String checkBoxName) {
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
