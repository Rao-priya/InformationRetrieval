package controllers;

import model.City;
import model.JsonResponse;
import model.LeftNavSettings;
import model.LeftNavSettings.CityLeftNavSettings;
import model.LeftNavSettings.MuseumSubCatLeftNavSettings;
import model.LeftNavSettings.OthersLeftNavSettings;
import model.LeftNavSettings.ParkSubCatLeftNavSettings;
import model.LeftNavSettings.SportsAdvLeftNavSettings;
import model.LeftNavSettings.WildLifeLeftNavSettings;
import model.MuseumSubCat;
import model.OtherAttractions;
import model.PaginationInfo;
import model.ParkSubCat;
import model.PropagationParams;
import model.SearchResult;
import model.SportsAdvSubCat;
import model.WildLifeSubCat;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import services.UserManagement;
import services.elasticsearch.ElasticSearchService;
import services.elasticsearch.QueryParser;
import services.elasticsearch.SearchParameters;
import views.html.advancedSearch;

import javax.inject.Inject;
import javax.print.attribute.standard.MediaSize.Other;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static play.mvc.Results.ok;

public class NewAdvancedSearch {

    @Inject
    FormFactory formFactory;
    @Inject
    ElasticSearchService elasticService;
    @Inject
    UserManagement userManagement;

    public Result index() {
        final SearchResult searchResult = new SearchResult();
        final PropagationParams propagationParams = new PropagationParams();
        return ok(advancedSearch.render(propagationParams, searchResult));
    }

    public Result search() {

        List<String> cities = new ArrayList<String>();
        List<String> citiesForAgg = new ArrayList<>();
        List<String> attraction = new ArrayList<String>();
        List<String> subcat1 = new ArrayList<String>();

        DynamicForm dform =  formFactory.form().bindFromRequest();
        String queryString =  dform.get("searchbox");
        int from = 0;
        int page = 1;

        if(dform.get("from")!=null){
            from = Integer.parseInt(dform.get("from"));
            page = Integer.parseInt(dform.get("page"));
        }

        final PropagationParams propagationParams = new PropagationParams();
        propagationParams.setUserQuery(queryString);
        propagationParams.setEnableLeftNav(true);

        for (City city : City.values()) {
            if(dform.get(city.getCheckBoxName()) != null){
                cities.add(city.getSearchName());
                citiesForAgg.add(city.getAggName());
            }
        }
        List<String> museumSubcats = new ArrayList<>();
        for (MuseumSubCat museumSubCat : MuseumSubCat.values()) {
            if(dform.get(museumSubCat.getCheckBoxName()) != null){
                museumSubcats.add(museumSubCat.getSearchName());
            }
        }
        if(!museumSubcats.isEmpty()){
            attraction.add("Museum");
            subcat1.addAll(museumSubcats);
        }
        List<String> parkSubcats = new ArrayList<>();
        for (ParkSubCat parkSubCat : ParkSubCat.values()) {
            if(dform.get(parkSubCat.getCheckBoxName()) != null){
                parkSubcats.add(parkSubCat.getSearchName());
            }
        }
        if(!parkSubcats.isEmpty()){
            attraction.add("Park");
            subcat1.addAll(parkSubcats);
        }

        List<String> sportsSubcats = new ArrayList<>();
        for (SportsAdvSubCat sportsSubcat : SportsAdvSubCat.values()) {
            if(dform.get(sportsSubcat.getCheckBoxName()) != null){
                sportsSubcats.add(sportsSubcat.getSearchName());
            }
        }
        if(!sportsSubcats.isEmpty()){
            attraction.add("Sports & Adventure");
            subcat1.addAll(sportsSubcats);
        }

        List<String> wildLifeSubcats = new ArrayList<>();
        for (WildLifeSubCat wildLifeSubcat : WildLifeSubCat.values()) {
            if(dform.get(wildLifeSubcat.getCheckBoxName()) != null){
                wildLifeSubcats.add(wildLifeSubcat.getSearchName());
            }
        }
        if(!wildLifeSubcats.isEmpty()){
            attraction.add("Wildlife");
            subcat1.addAll(wildLifeSubcats);
        }

        for (OtherAttractions otherAttractions : OtherAttractions.values()) {
            if(dform.get(otherAttractions.getCheckBoxName())!=null){
                attraction.add(otherAttractions.getSearchName());
            }
        }

        JsonResponse response = new JsonResponse();
        String[] cityArr = new String [citiesForAgg.size()];
        cityArr = citiesForAgg.toArray(cityArr);
        JsonResponse aggResponse = elasticService.createClient(response, cityArr);
        List<CityLeftNavSettings> cityNavSettings = new ArrayList<>();


        for (City city : City.values()) {
            final CityLeftNavSettings navSettings = new CityLeftNavSettings();
            navSettings.setCity(city);
            if(dform.get(city.getCheckBoxName()) != null){
                navSettings.setChecked();
            }
            if(aggResponse.getCity().get(city.getAggLookUpName())!= null){
                navSettings.setCount(aggResponse.getCity().get(city.getAggLookUpName()));
            }
            cityNavSettings.add(navSettings);
        }

        List<MuseumSubCatLeftNavSettings> museumSettings= new ArrayList<>();

        for (MuseumSubCat museumSubCat : MuseumSubCat.values()) {
            final MuseumSubCatLeftNavSettings museumSubCatLeftNavSettings = new MuseumSubCatLeftNavSettings();
            museumSubCatLeftNavSettings.setMuseumSubCat(museumSubCat);
            if(dform.get(museumSubCat.getCheckBoxName()) != null){
                museumSubCatLeftNavSettings.setChecked(true);
            }
            if(aggResponse.getSubcat1().get(museumSubCat.getAggLookupName())!= null){
                museumSubCatLeftNavSettings.setCount(aggResponse.getSubcat1().get(museumSubCat.getAggLookupName()));
            }
            museumSettings.add(museumSubCatLeftNavSettings);
        }

        List<ParkSubCatLeftNavSettings>parkSettings = new ArrayList<>();
        for (ParkSubCat parkSubCat : ParkSubCat.values()) {
            final ParkSubCatLeftNavSettings parkSubCatLeftNavSettings = new ParkSubCatLeftNavSettings();
            parkSubCatLeftNavSettings.setParkSubCat(parkSubCat);
            if(dform.get(parkSubCat.getCheckBoxName()) != null){
                parkSubCatLeftNavSettings.setChecked(true);
            }
            if(aggResponse.getSubcat1().get(parkSubCat.getAggLookupName())!= null){
                parkSubCatLeftNavSettings.setCount(aggResponse.getSubcat1().get(parkSubCat.getAggLookupName()));
            }
            parkSettings.add(parkSubCatLeftNavSettings);
        }

        List<SportsAdvLeftNavSettings> sportAdvSettings = new ArrayList<>();
        for (SportsAdvSubCat sportsAdvSubCat : SportsAdvSubCat.values()) {
            final SportsAdvLeftNavSettings navSettings = new SportsAdvLeftNavSettings();
            navSettings.setSportsAdvSubCat(sportsAdvSubCat);
            if(dform.get(sportsAdvSubCat.getCheckBoxName()) != null){
                navSettings.setChecked(true);
            }
            if(aggResponse.getSubcat1().get(sportsAdvSubCat.getAggLookupName())!= null){
                navSettings.setCount(aggResponse.getSubcat1().get(sportsAdvSubCat.getAggLookupName()));
            }
            sportAdvSettings.add(navSettings);
        }

        List<WildLifeLeftNavSettings> wildlifeSettings = new ArrayList<>();
        for (WildLifeSubCat wildLifeSubCat : WildLifeSubCat.values()) {
            final WildLifeLeftNavSettings navSettings = new WildLifeLeftNavSettings();
            navSettings.setWildLifeSubCat(wildLifeSubCat);
            if(dform.get(wildLifeSubCat.getCheckBoxName()) != null){
                navSettings.setChecked(true);
            }
            if(aggResponse.getSubcat1().get(wildLifeSubCat.getAggLookupName())!= null){
                navSettings.setCount(aggResponse.getSubcat1().get(wildLifeSubCat.getAggLookupName()));
            }
            wildlifeSettings.add(navSettings);
        }

        List<OthersLeftNavSettings> othersSettings = new ArrayList<>();
        for (OtherAttractions otherAttractions : OtherAttractions.values()) {
            final OthersLeftNavSettings navSettings = new OthersLeftNavSettings();
            navSettings.setOtherAttractions(otherAttractions);
            if(dform.get(otherAttractions.getCheckBoxName()) != null){
                navSettings.setChecked(true);
            }
            if(aggResponse.getAttraction().get(otherAttractions.getAggLookupName())!= null){
                navSettings.setCount(aggResponse.getAttraction().get(otherAttractions.getAggLookupName()));
            }
            othersSettings.add(navSettings);
        }




        final LeftNavSettings leftNavSettings = new LeftNavSettings();
        leftNavSettings.setCitySettings(cityNavSettings);

        if(aggResponse.getAttraction().get("museum") != null){
            leftNavSettings.setMuseumCount(aggResponse.getSubcat1().get("museum"));
        }
        leftNavSettings.setMuseumSCSettings(museumSettings);

        if(aggResponse.getAttraction().get("park") != null){
            leftNavSettings.setParkCount(aggResponse.getAttraction().get("park"));
        }
        leftNavSettings.setParkSCSettings(parkSettings);

        if(aggResponse.getAttraction().get("sport") != null){
            leftNavSettings.setSportAdvCount(aggResponse.getAttraction().get("sport"));
        }
        leftNavSettings.setSportsAdvSCSettings(sportAdvSettings);

        if(aggResponse.getAttraction().get("wildlif") != null){
            leftNavSettings.setWildLifeCount(aggResponse.getAttraction().get("wildlif"));
        }
        leftNavSettings.setWildLifeSCSettings(wildlifeSettings);

        leftNavSettings.setOthersSCSettings(othersSettings);

        propagationParams.setLeftNavSettings(leftNavSettings);


        final String suggest = dform.get("suggest");
        boolean enableSuggestion = true;
        if(suggest != null && suggest.equalsIgnoreCase("false")){
            enableSuggestion = false;
        }
        if(enableSuggestion) {
            String correctedQuery = elasticService.getSpellingSuggestions(queryString);
            if (!queryString.equalsIgnoreCase(correctedQuery)) {
                play.Logger.info("Query [" + queryString + "] corrected to [" + correctedQuery + "]");
                propagationParams.setOriginalQuery(queryString);
                propagationParams.setUserQuery(correctedQuery);
                queryString = correctedQuery;
            }
        }

        propagationParams.setSuggest(enableSuggestion);

        final SearchParameters searchParameters = new SearchParameters(queryString, from);
        searchParameters.setCity(cities);
        searchParameters.setAttraction(attraction);
        searchParameters.setSubCat1(subcat1);

        final SearchResult queryResult = elasticService.getQueryResult(searchParameters);

        Map<String, String> parsedResults = QueryParser.parseQuery(searchParameters.getQueryString());

        if(parsedResults.get("cities") != null){
            for (CityLeftNavSettings cityLeftNavSettings : propagationParams.getLeftNavSettings().getCitySettings()) {
                if(cityLeftNavSettings.getCity().getAggLookUpName().equals(parsedResults.get("cities"))){
                    cityLeftNavSettings.setChecked();
                }
            }
        }

        final List<PaginationInfo> paginationInfos = PaginationInfo.calculatePagination(page, from, queryResult.getNextFrom(), queryResult.getTotalHits());
        final String urlParam = reconstructURLParams(dform);
        for (PaginationInfo paginationInfo : paginationInfos) {
            paginationInfo.setUrlParams(urlParam);
        }



        queryResult.setPaginationInfos(paginationInfos);
        queryResult.setCurrentPage(page);
        return ok(advancedSearch.render(propagationParams,queryResult));
    }

    private String reconstructURLParams(DynamicForm dfrom){
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&");
        for (City city : City.values()) {
            if(dfrom.get(city.getCheckBoxName())!=null){
                stringBuilder.append(city.getCheckBoxName()).append("=").append(city.getCheckBoxName()).append("&");
            }
        }
        for (MuseumSubCat museumSubCat : MuseumSubCat.values()) {
            if(dfrom.get(museumSubCat.getCheckBoxName())!= null){
                stringBuilder.append(museumSubCat.getCheckBoxName()).append("=").append(museumSubCat.getCheckBoxName()).append("&");
            }
        }
        for (ParkSubCat parkSubCat : ParkSubCat.values()) {
            if(dfrom.get(parkSubCat.getCheckBoxName())!= null){
                stringBuilder.append(parkSubCat.getCheckBoxName()).append("=").append(parkSubCat.getCheckBoxName()).append("&");
            }
        }
        for (SportsAdvSubCat advSubCat : SportsAdvSubCat.values()) {
            if(dfrom.get(advSubCat.getCheckBoxName())!= null){
                stringBuilder.append(advSubCat.getCheckBoxName()).append("=").append(advSubCat.getCheckBoxName()).append("&");
            }
        }
        for (WildLifeSubCat wildLifeSubCat : WildLifeSubCat.values()) {
            if(dfrom.get(wildLifeSubCat.getCheckBoxName())!= null){
                stringBuilder.append(wildLifeSubCat.getCheckBoxName()).append("=").append(wildLifeSubCat.getCheckBoxName()).append("&");
            }
        }
        for (OtherAttractions otherAttractions : OtherAttractions.values()) {
            if(dfrom.get(otherAttractions.getCheckBoxName())!= null){
                stringBuilder.append(otherAttractions.getCheckBoxName()).append("=").append(otherAttractions.getCheckBoxName()).append("&");
            }
        }
        if(stringBuilder.length() >0){
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }
        return stringBuilder.toString();



    }
}
