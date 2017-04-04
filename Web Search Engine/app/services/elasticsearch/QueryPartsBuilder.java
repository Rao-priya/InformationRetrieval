package services.elasticsearch;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * Created by MadVish on 5/28/16.
 */
public class QueryPartsBuilder {
    private QueryPartsBuilder() {
    }


    public static QueryBuilder buildBooleanQuery(SearchParameters searchParameters){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(searchParameters.getCity().isEmpty()){
            boolQueryBuilder = boolQueryBuilder.should(QueryBuilders.matchQuery("city", searchParameters.getQueryString()).analyzer("query_analyzer").boost(50));
        }else {
            BoolQueryBuilder localBoolBuilder = QueryBuilders.boolQuery();
            for (String cityName : searchParameters.getCity()) {
                localBoolBuilder.should(QueryBuilders.matchQuery("city.raw",cityName));
            }
            boolQueryBuilder =boolQueryBuilder.must(localBoolBuilder);
        }

        if(searchParameters.getAttraction().isEmpty()){
            boolQueryBuilder = boolQueryBuilder.should(QueryBuilders.matchQuery("attraction", searchParameters.getQueryString()).boost(20));
        }else {
            BoolQueryBuilder localBoolBuilder = QueryBuilders.boolQuery();
            for (String attractionName : searchParameters.getAttraction()) {
                localBoolBuilder.should(QueryBuilders.matchQuery("attraction.raw",attractionName));
            }
            boolQueryBuilder =boolQueryBuilder.must(localBoolBuilder);
        }

        if(searchParameters.getSubCat1().isEmpty()){
            boolQueryBuilder = boolQueryBuilder.should(QueryBuilders.matchQuery("subcat1", searchParameters.getQueryString()).boost(20));
        }else {
            BoolQueryBuilder localBoolBuilder = QueryBuilders.boolQuery();
            for (String subCat1Name : searchParameters.getSubCat1()) {
                localBoolBuilder.should(QueryBuilders.matchQuery("subcat1.raw",subCat1Name));
            }
            boolQueryBuilder =boolQueryBuilder.must(localBoolBuilder);
        }

        if(searchParameters.getSubCat2().isEmpty()){
            boolQueryBuilder = boolQueryBuilder.should(QueryBuilders.matchQuery("subcat2", searchParameters.getQueryString()).boost(5));
        }else {
            BoolQueryBuilder localBoolBuilder = QueryBuilders.boolQuery();
            for (String subCat2Name : searchParameters.getSubCat2()) {
                localBoolBuilder.should(QueryBuilders.matchQuery("subcat2.raw",subCat2Name));
            }
            boolQueryBuilder =boolQueryBuilder.must(localBoolBuilder);
        }

        final StringBuilder contextQueryBuilder = new StringBuilder();
        contextQueryBuilder.append(searchParameters.getQueryString());

        searchParameters.getCity().forEach(city -> contextQueryBuilder.append(" ").append(city));
        searchParameters.getAttraction().forEach(attr -> contextQueryBuilder.append(" ").append(attr));
        searchParameters.getSubCat1().forEach(sc1-> contextQueryBuilder.append(" ").append(sc1));
        searchParameters.getSubCat2().forEach(sc2-> contextQueryBuilder.append(" ").append(sc2));
        final String contentQuery = contextQueryBuilder.toString();

        BoolQueryBuilder finalBuilder = boolQueryBuilder
                .should(QueryBuilders.matchQuery("title", searchParameters.getQueryString()).boost(10))
                .should(QueryBuilders.matchQuery("content", contentQuery).boost((float) 0.5))
                .should(QueryBuilders.matchQuery("keywords", searchParameters.getQueryString()).boost(5));
        return finalBuilder;
    }

    public static SearchRequestBuilder addHighLight(SearchRequestBuilder requestBuilder) {
        SearchRequestBuilder newBuilder = requestBuilder.addHighlightedField("content", 100, 3)
                .setHighlighterPostTags("</b>")
                .setHighlighterPreTags("<b>")
                .setHighlighterNoMatchSize(100)
                .setHighlighterOrder("score");
        return newBuilder;
    }

    public static QueryBuilder getGeoQueryBuilder(SearchParameters searchParameters){
        ServerLocation serverLocation = searchParameters.getServerLocation();
        if(serverLocation != null && StringUtils.isNotEmpty(searchParameters.getDistance())){
            QueryBuilder geoQueryBuilder= QueryBuilders.geoDistanceQuery("coordinates")
                    .distance(searchParameters.getDistance())
                    .lat(serverLocation.getLatitude())
                    .lon(serverLocation.getLongitude());
            return geoQueryBuilder;
        } else {
            return null;
        }
    }

    public static SearchRequestBuilder buildQueryFromSearchParams(SearchRequestBuilder requestBuilder, SearchParameters searchParameters){
        QueryBuilder booleanQuery = buildBooleanQuery(searchParameters);
        requestBuilder = requestBuilder.setQuery(booleanQuery); // set the boolean query

        requestBuilder = addHighLight(requestBuilder); // set the highlight

        QueryBuilder geoQueryBuilder = getGeoQueryBuilder(searchParameters); //set geolocation
        if(geoQueryBuilder != null){
            requestBuilder = requestBuilder.setPostFilter(geoQueryBuilder);
        }

        return requestBuilder;

    }


}
