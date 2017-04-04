package services.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Place;
import model.SearchResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by MadVish on 5/28/16.
 */
public class SearchResultParser {
    private SearchResultParser() {

    }

    public static SearchResult parse(SearchResponse response, int from, int topXCount){
        Set<String> liveUrlStrings = new HashSet<>();
        List<Place> places = new ArrayList<>();
        int cursor=0;
        try {
            ObjectMapper mapper = new ObjectMapper();
            SearchHit[] hits = response.getHits().getHits();
            int count= 0;
            for (SearchHit result : hits) {
                if(count >= topXCount){
                    break;
                }
                //TODO:Change jackson mapper to Elasticsearch json parse.
                Place place = mapper.readValue(result.getSourceAsString(), Place.class);
                if(!liveUrlStrings.contains(place.getLiveURL())){
                    liveUrlStrings.add(place.getLiveURL());
                    count++;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Map.Entry<String, HighlightField> highlightField : result.getHighlightFields().entrySet()) {
                        for (Text text : highlightField.getValue().fragments()) {
                            stringBuilder.append(text.toString());
                            stringBuilder.append("......");
                        }
                    }
                    place.setSnippet(stringBuilder.toString());
                    places.add(place);
                }
                cursor++;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        int nextFrom = from;
        if(!places.isEmpty()){
            nextFrom += cursor + 1;
        }
        long totalHits = response.getHits().getTotalHits();

        return new SearchResult(places,nextFrom, totalHits);

    }
}
