package services.elasticsearch;

import model.JsonResponse;
import model.SearchResult;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Created by MadVish on 5/12/16.
 */
@Singleton
public class ElasticSearchService {

    private static int TOPX = 10;
    private static int EXTRA_FETCH = TOPX*2;


    TransportClient client;

    @Inject
    public ElasticSearchService() throws UnknownHostException {
        Settings settings = Settings.settingsBuilder().put("cluster_name", "elasticsearch").build();
        client = new TransportClient.Builder().settings(settings).build();
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
    }

    public SearchResult getQueryResult(SearchParameters searchParameters) {
        play.Logger.debug("Running query " + searchParameters.getQueryString());
        return getSearchResultsForSearchParams(searchParameters);
    }

    public String getSpellingSuggestions(String queryString){
        return SpellSuggestor.getSpellingSuggestions(queryString,client);
    }

    public SearchResult getSearchResultAfterSpellCorrection(SearchParameters searchParameters) {
        final String correctedSpelling = getSpellingSuggestions(searchParameters.getQueryString());
        if(correctedSpelling.equals(searchParameters.getQueryString())){
            searchParameters.setQueryString(correctedSpelling);
        }
        return getSearchResultsForSearchParams(searchParameters);
    }

    private SearchResult getSearchResultsForSearchParams(SearchParameters searchParameters){
        //parse query to figure out if distance query has to be called
        Map<String, String> parsedResults = QueryParser.parseQuery(searchParameters.getQueryString());
        String distance = parsedResults.get("distance");
        if(distance != null){
            play.Logger.debug("Distance" + distance);
            ServerLocation serverLocation = Utils.getGeoLocation();
            if(serverLocation != null){
                searchParameters.setDistance(distance);
                searchParameters.setServerLocation(serverLocation);
            }
        }
        return getQueryResults(searchParameters);
    }


    private SearchResult getQueryResults(SearchParameters searchParameters) {
        SearchRequestBuilder requestBuilder = client.prepareSearch("travel")
                .setFetchSource(new String[]{"title", "liveURL", "snippet", "city"}, null)
                .setFrom(searchParameters.getFrom())
                .setSize(EXTRA_FETCH);
        SearchRequestBuilder builder = QueryPartsBuilder.buildQueryFromSearchParams(requestBuilder, searchParameters);

        SearchResponse searchResponse = builder.execute().actionGet();
        SearchResult searchResult = SearchResultParser.parse(searchResponse, searchParameters.getFrom(),TOPX);

//        searchResult.getPlaces().forEach(p -> play.Logger.debug(p.getCity().get(0) + ":" + p.getLiveURL() + " snippet:" + p.getSnippet()));
        play.Logger.debug("next from: " + searchResult.getNextFrom());
        play.Logger.debug("Total hits: " + searchResult.getTotalHits());
        return searchResult;
    }

    public ArrayList<String> validateFieldNames = new ArrayList<String>(
            Arrays.asList("Lake Tahoe", "Sacramento","Fresno", "Long Beach", "San Francisco", "san diego","Oakland", "Monterey", "Santa Barbara",
            "Santa Cruz","Los Angeles",	"Beach", "Sport", "Dine", "Nightlif", "Park", "Landmark", "Wildlif", "MuseumSubCat", "amus","Outdoor Sports", "Water Sports",
            "Aquarium", "Zoo", "Art", "History",  "Science", "State Park", "National Park","Other Parks"));


    public JsonResponse createClient(JsonResponse response, String... selectedCities) {
        // public  JsonResponse createClient(JsonResponse response, String selectedCities) {
        //For selected cities, build query with aggs on page load
        // String selectedCities = "sanfrancisco","losangeles";
        SearchResponse defaultAggsRequest = client.prepareSearch("travel")
                .setTypes(selectedCities)
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.terms("city").field("city").size(0))
                .addAggregation(AggregationBuilders.terms("attraction").field("attraction").size(0))
                .addAggregation(AggregationBuilders.terms("subcat1").field("subcat1").size(0))
                .addAggregation(AggregationBuilders.terms("subcat2").field("subcat2").size(0))
                .execute().actionGet();
        //System.out.println(defaultAggsRequest.toString());
        //System.out.println("------------------------------------------------------------------------");


        //convert to lower case
        for (int i = 0; i < validateFieldNames.size(); i++) {
            validateFieldNames.set(i, validateFieldNames.get(i).toLowerCase());
        }
        Map<String, Integer> mapCities = new HashMap<String, Integer>();
        Terms city = defaultAggsRequest.getAggregations().get("city");
        for (Terms.Bucket entry : city.getBuckets()) {
            String key = entry.getKey().toString();

            if (validateFieldNames.contains(key)) {
                mapCities.put(key, (int)entry.getDocCount());
            }
        }
        play.Logger.debug(mapCities.toString());
        response.setCity(mapCities);

        // Store response for attraction bucket
        Map<String, Integer> mapAttraction = new HashMap<String, Integer>();
        Terms attraction = defaultAggsRequest.getAggregations().get("attraction");
        for (Terms.Bucket entry : attraction.getBuckets()) {
            String key = entry.getKey().toString();
            if (validateFieldNames.contains(key)) {
                mapAttraction.put(key, (int)entry.getDocCount());
            }
        }
        play.Logger.debug(mapAttraction.toString());
        response.setAttraction(mapAttraction);

        // Store response for subcategory1 bucket
        Map<String, Integer> mapSubcat1 = new HashMap<String, Integer>();
        Terms subcat1 = defaultAggsRequest.getAggregations().get("subcat1");
        for (Terms.Bucket entry : subcat1.getBuckets()) {
            String key = entry.getKey().toString();
            if (validateFieldNames.contains(key)) {
                mapSubcat1.put(key, (int)entry.getDocCount());
            }
        }
        System.out.println(mapSubcat1.toString());
        response.setSubcat1(mapSubcat1);


        return response;
    }





}
