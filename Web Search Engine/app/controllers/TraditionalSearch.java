package controllers;

import model.PaginationInfo;
import model.Place;
import model.PropagationParams;
import model.SearchResult;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import play.mvc.Result;
import services.UserManagement;
import services.elasticsearch.ElasticSearchService;
import services.elasticsearch.SearchParameters;
import views.html.index;
import views.html.traditionalSearch;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MadVish on 5/12/16.
 */
public class TraditionalSearch extends Controller {

    @Inject
    FormFactory formFactory;
    @Inject
    ElasticSearchService elasticService;
    @Inject
    UserManagement userManagement;

    public Result index() {
        final SearchResult searchResult = new SearchResult();
        final PropagationParams propagationParams = new PropagationParams();
//        return ok(index.render("Welcome to Travalalika"));
        return ok(traditionalSearch.render(propagationParams,searchResult));
    }

//    public Result index(String queryString) {
//        return ok("Query: " + queryString);
//    }

    public Result search() {
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


        final SearchResult queryResult = elasticService.getQueryResult(searchParameters);

        final List<PaginationInfo> paginationInfos = PaginationInfo.calculatePagination(page, from, queryResult.getNextFrom(), queryResult.getTotalHits());
        queryResult.setPaginationInfos(paginationInfos);
        queryResult.setCurrentPage(page);
        return ok(traditionalSearch.render(propagationParams,queryResult));
    }

    public  Result signup() {

        DynamicForm dform =  Form.form().bindFromRequest();
        String email = dform.get("email");
        String p = dform.get("password");
        // session("email",e);

        if(userManagement.signupUser(email, email,p)){
            session("email", email);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            session("starttime", dateFormat.format(date));
        }

        final ArrayList<Place> places = new ArrayList<>();
        final SearchResult searchResult = new SearchResult(places, 0, 0);

        final PropagationParams propagationParams = new PropagationParams();
        propagationParams.setUserQuery(session("query"));
        return ok(traditionalSearch.render(propagationParams,searchResult));
    }

    public Result signout(){
        play.Logger.debug("Inside Traditional Search Engine Signout");
        final Cookie mouseclickcount = request().cookie("mouseclickcount");
        String clickcount = null;
        if(mouseclickcount!= null){
            clickcount = mouseclickcount.value();
        }
        if(session("email") != null) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            session("endtime",dateFormat.format(date));
            getUserData(clickcount,"Traditional Search Engine");
            //System.out.println("Session ended: " + session("email"));
            // System.out.println("Session at time:" +dateFormat.format(date));
            session().remove("starttime");
            session().remove("email");
            session().remove("endtime");
        }
        return redirect("/");
    }

    private void getUserData(String count,String type) {
        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try{
            File f = new File("./app/logs/"+session("email")+".txt");//user1.txt
            if (!f.exists()) {
                f.createNewFile();//"./reportHtml.html");
            }
            fWriter = new FileWriter(f, true);//append mode
            writer = new BufferedWriter(fWriter);
            if(writer !=null){

                writer.write("Search Engine Type: "+ type+"\n");
                writer.newLine();
                writer.write("Session start time: "+ session("starttime")+"\n");
//                writer.newLine();
//                writer.write("Queries: "+ queries+"\n");
                writer.newLine();
                writer.write("Mouse click count: "+ count+"\n");
                writer.newLine();
                writer.write("Session end time: "+ session("endtime")+"\n");
                writer.newLine();
                writer.write("------------------------------------");
                writer.newLine();
                // writer.flush();
                writer.close();
            }else{
            }
        } catch(Exception e){
        }

    }


}
