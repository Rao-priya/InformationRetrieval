package services.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.Suggest.Suggestion;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

import java.util.Iterator;

public class SpellSuggestor {
    private SpellSuggestor() {

    }

    public static String getSpellingSuggestions(String queryString, TransportClient client){
        TermSuggestionBuilder suggestionBuilder = new TermSuggestionBuilder("")
                .text(queryString)
                .field("content.raw")
                .suggestMode("missing")
                .sort("score")
                .minWordLength(3)
                .size(1);

        SearchResponse searchResponse = client.prepareSearch("travel")
                .addSuggestion(suggestionBuilder)
                .execute()
                .actionGet();


        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Suggestion<? extends Entry<? extends Option>>> suggestIter = searchResponse.getSuggest().iterator();
        while(suggestIter.hasNext()){
            Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries = suggestIter.next();
            for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : entries.getEntries()) {
                String suggestString = null;
                for (Suggest.Suggestion.Entry.Option option : entry.getOptions()) {
                    suggestString = option.getText().string();
                }
                if(suggestString == null || suggestString.isEmpty()){
                    suggestString = entry.getText().string();
                }
                stringBuilder.append(suggestString).append(" ");
            }
        }
        return stringBuilder.toString().trim();
    }
}
