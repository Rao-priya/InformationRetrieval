package model;

import java.util.ArrayList;
import java.util.List;

public class LeftNavSettings {

    List<CityLeftNavSettings> citySettings = new ArrayList<>();
    List<MuseumSubCatLeftNavSettings> museumSCSettings = new ArrayList<>();
    List<ParkSubCatLeftNavSettings> parkSCSettings = new ArrayList<>();
    List<SportsAdvLeftNavSettings> sportsAdvSCSettings = new ArrayList<>();
    List<WildLifeLeftNavSettings> wildLifeSCSettings = new ArrayList<>();
    List<OthersLeftNavSettings> othersSCSettings = new ArrayList<>();


    int museumCount = 0;
    int parkCount = 0;
    int sportAdvCount = 0;
    int wildLifeCount = 0;

    public List<WildLifeLeftNavSettings> getWildLifeSCSettings() {
        return wildLifeSCSettings;
    }

    public void setWildLifeSCSettings(final List<WildLifeLeftNavSettings> wildLifeSCSettings) {
        this.wildLifeSCSettings = wildLifeSCSettings;
    }

    public List<OthersLeftNavSettings> getOthersSCSettings() {
        return othersSCSettings;
    }

    public void setOthersSCSettings(final List<OthersLeftNavSettings> othersSCSettings) {
        this.othersSCSettings = othersSCSettings;
    }

    public int getWildLifeCount() {
        return wildLifeCount;
    }

    public void setWildLifeCount(final int wildLifeCount) {
        this.wildLifeCount = wildLifeCount;
    }

    public List<SportsAdvLeftNavSettings> getSportsAdvSCSettings() {
        return sportsAdvSCSettings;
    }

    public void setSportsAdvSCSettings(final List<SportsAdvLeftNavSettings> sportsAdvSCSettings) {
        this.sportsAdvSCSettings = sportsAdvSCSettings;
    }

    public int getSportAdvCount() {
        return sportAdvCount;
    }

    public void setSportAdvCount(final int sportAdvCount) {
        this.sportAdvCount = sportAdvCount;
    }

    public List<ParkSubCatLeftNavSettings> getParkSCSettings() {
        return parkSCSettings;
    }

    public void setParkSCSettings(final List<ParkSubCatLeftNavSettings> parkSCSettings) {
        this.parkSCSettings = parkSCSettings;
    }

    public int getParkCount() {
        return parkCount;
    }

    public void setParkCount(final int parkCount) {
        this.parkCount = parkCount;
    }

    public List<MuseumSubCatLeftNavSettings> getMuseumSCSettings() {
        return museumSCSettings;
    }

    public void setMuseumSCSettings(final List<MuseumSubCatLeftNavSettings> museumSCSettings) {
        this.museumSCSettings = museumSCSettings;
    }

    public int getMuseumCount() {
        return museumCount;
    }

    public void setMuseumCount(final int museumCount) {
        this.museumCount = museumCount;
    }

    public List<CityLeftNavSettings> getCitySettings() {
        return citySettings;
    }

    public void setCitySettings(final List<CityLeftNavSettings> citySettings) {
        this.citySettings = citySettings;
    }


    public static class OthersLeftNavSettings {
        OtherAttractions otherAttractions;
        boolean checked = false;
        int count = 0;

        public OtherAttractions getOtherAttractions() {
            return otherAttractions;
        }

        public void setOtherAttractions(final OtherAttractions otherAttractions) {
            this.otherAttractions = otherAttractions;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(final boolean checked) {
            this.checked = checked;
        }

        public int getCount() {
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
        }
    }

    public static class WildLifeLeftNavSettings {
        WildLifeSubCat wildLifeSubCat;
        boolean checked = false;
        int count = 0;

        public WildLifeSubCat getWildLifeSubCat() {
            return wildLifeSubCat;
        }

        public void setWildLifeSubCat(final WildLifeSubCat wildLifeSubCat) {
            this.wildLifeSubCat = wildLifeSubCat;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(final boolean checked) {
            this.checked = checked;
        }

        public int getCount() {
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
        }
    }

    public static class SportsAdvLeftNavSettings{
        SportsAdvSubCat sportsAdvSubCat;
        boolean checked = false;
        int count = 0;

        public SportsAdvSubCat getSportsAdvSubCat() {
            return sportsAdvSubCat;
        }

        public void setSportsAdvSubCat(final SportsAdvSubCat sportsAdvSubCat) {
            this.sportsAdvSubCat = sportsAdvSubCat;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(final boolean checked) {
            this.checked = checked;
        }

        public int getCount() {
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
        }
    }
    public static class ParkSubCatLeftNavSettings {
        ParkSubCat parkSubCat;
        boolean checked = false;
        int count= 0;

        public ParkSubCat getParkSubCat() {
            return parkSubCat;
        }

        public void setParkSubCat(final ParkSubCat parkSubCat) {
            this.parkSubCat = parkSubCat;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(final boolean checked) {
            this.checked = checked;
        }

        public int getCount() {
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
        }
    }

    public static class MuseumSubCatLeftNavSettings {
        MuseumSubCat museumSubCat;
        boolean checked = false;
        int count = 0;

        public MuseumSubCat getMuseumSubCat() {
            return museumSubCat;
        }

        public void setMuseumSubCat(final MuseumSubCat museumSubCat) {
            this.museumSubCat = museumSubCat;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(final boolean checked) {
            this.checked = checked;
        }

        public int getCount() {
            return count;
        }

        public void setCount(final int count) {
            this.count = count;
        }
    }

    public static class CityLeftNavSettings{
       City city;
       boolean checked = false;
       int count = 0;

       public City getCity() {
           return city;
       }

       public void setCity(final City city) {
           this.city = city;
       }

       public boolean isChecked() {
           return checked;
       }

       public void setChecked() {
           this.checked = true;
       }

       public int getCount() {
           return count;
       }

       public void setCount(final int count) {
           this.count = count;
       }
   }

}
