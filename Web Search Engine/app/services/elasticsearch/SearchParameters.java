package services.elasticsearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MadVish on 5/28/16.
 */
public class SearchParameters {
    private String queryString;
    private int from;
    private List<String> city = new ArrayList<>();
    private List<String> attraction = new ArrayList<>();
    private List<String> subCat1 = new ArrayList<>();
    private List<String> subCat2= new ArrayList<>();
    private String distance;
    private ServerLocation serverLocation;


    public SearchParameters(String queryString,int from) {
        this.from = from;
        this.queryString = queryString;
    }


    public int getFrom() {
        return from;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;

    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public ServerLocation getServerLocation() {
        return serverLocation;
    }

    public void setServerLocation(ServerLocation serverLocation) {
        this.serverLocation = serverLocation;
    }

    public String getQueryString() {
        return queryString;
    }

    public List<String> getCity() {
        return city;
    }

    public void setCity(List<String> city) {
        this.city = city;
    }
    public void setCity(String city){
        this.city.add(city);
    }

    public List<String> getAttraction() {
        return attraction;
    }

    public void setAttraction(List<String> attraction) {
        this.attraction = attraction;
    }

    public void setAttraction(String attraction){
        this.attraction.add(attraction);
    }

    public List<String> getSubCat1() {
        return subCat1;
    }

    public void setSubCat1(List<String> subCat1) {
        this.subCat1 = subCat1;
    }

    public void setSubCat1(String subCat1) {
        this.subCat1.add(subCat1);
    }

    public List<String> getSubCat2() {
        return subCat2;
    }

    public void setSubCat2(List<String> subCat2) {
        this.subCat2 = subCat2;
    }

    public void setSubCat2(String subCat2) {
        this.subCat2.add(subCat2);
    }
}
