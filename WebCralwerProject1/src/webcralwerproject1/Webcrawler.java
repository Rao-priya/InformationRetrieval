/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webcralwerproject1;

import com.sun.jndi.toolkit.url.Uri;
import java.io.*;
import java.util.StringTokenizer;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.jsoup.select.Selector;
import java.util.Set;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;

/**
 *
 * @author Priyanka
 */
public class Webcrawler {

    /**
     * @param args the command line arguments
     */
    List<String> links = new LinkedList<String>();//stores links on each page 
    Set<String> pagesVisited = new HashSet<String>();// crawled links
    List<String> pagesToVisit = new LinkedList<String>();//links to visit
    HashMap<String, Integer> termfrequency = new HashMap<String, Integer>();//wordcount
    Document htmlDocument;
    String line = null;
    String filename = "./src/webcralwerproject1/specification.csv";                
    String[] input = new String[3];//
    int j = 0, imagecount = 0, MaxPage = 0, word_found = 0, word_notfound = 0,
            crawlcount = 0;
    String DirectoryName = "repository";

    void readcsv() {
        input[2] = "";//intialize searchword default value null;
        boolean crawlerstatus;
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(filename);//read input file 
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                //read each line and tokenize and store in input []
                StringTokenizer st = new StringTokenizer(line, ",");
                input[2] = "";
                while (st.hasMoreTokens()) {
                    input[j++] = st.nextToken();
                    System.out.print("token: " + input[j - 1]);
                }
                crawlcount += 1; //Crawl count
                System.out.println();
                System.out.println("================PART - " + crawlcount + "=====================");
                //  start crawler with string of  input[] containing seed, #pages and searchword(optinal)
                System.out.println("size: PagesToVisit:" + this.pagesToVisit.size() + " VisitedPage: " + this.pagesVisited.size() + " total links inside each page : " + this.links.size());

                boolean spiderstatus = startcrawler(input);

                if (spiderstatus == false) {
                    // if seed is unsafe , continue with nest seed in the specification file
                    j = 0;
                    continue;
                }
                //if seed is safe , continue with content processing of downloaded content
                String contentfile = contentprocessor();
                if (contentfile != null) {

                    countFrequency(contentfile);
                }//done             
                j = 0;//reset inputcounter for next seed
                MaxPage = 0;//second iteration begins

            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Readcsv: Unable to open file '" + filename + "'");
        } catch (IOException ex) {
            System.out.println("Readccsv: Error reading file '" + filename + "'");

        }

    }

    public boolean robotSafe(String myUrl) throws MalformedURLException {
        //Returns TRUE - if SAFE
        URL url = new URL(myUrl);
        //If the seed given happens to be exactly the host of robots.txt, 
        //it can't be found inside robots.txt anyway. So return true
        String OriginalDomainURL = url.getProtocol() + "://" + url.getHost() + "/";
        boolean firstseedflag = false;
        if (OriginalDomainURL.equals(myUrl)) {
            firstseedflag = true;
            return true;
        }
        //This is to form the URL to robots.txt 
        String strHost = url.getHost();
        String strRobot = url.getProtocol() + "://" + strHost + "/robots.txt";
        URL urlRobot;
        try {
            urlRobot = new URL(strRobot);
        } catch (MalformedURLException e) {
            // something weird is happening, so don't trust it
            System.out.println("Inside RobotSafe- UNSAFE because of MalformedURL exception");
            return false;
        }
        //This will read the robots.txt url with an open stream and read all bytes at once
        String strCommands;
        try {
            InputStream urlRobotStream = urlRobot.openStream();
            byte b[] = new byte[10000];
            //The number of bytes actually read is returned as an integer
            int numRead = urlRobotStream.read(b);
            //If the number of bytes returned is -1, the file has an end of file character in the beginning and hence is corrupt
            if (numRead == -1) {
                System.out.println("Inside RobotSafe- SAFE because rbots.txt is corrupt which means we can crawl");
                return true; //if the robots.txt file is corrupt then its okay to crawl that site anyway. (assumption) (for eg: goodreads.com)
            }
            //We now store the read bytes into a string called strCommands
            strCommands = new String(b, 0, numRead);
            urlRobotStream.close();
        } catch (IOException e) {
            System.out.println("Inside RobotSafe- SAFE beacuse there is no robots.txt file");
            return true; // if there is no robots.txt file, it is OK to search
        }
        //Read each line of strCommands and use only lines under user agent * as the disallowed rules
        if (strCommands.contains("Disallow")) // if there are no "disallow" values, then they are not blocking anything.
        {
            String[] split = strCommands.split("\n");
            ArrayList<RobotRule> robotRules = new ArrayList<>();
            String mostRecentUserAgent = null;
            for (int i = 0; i < split.length; i++) {
                String line = split[i].trim();
                if (line.toLowerCase().startsWith("user-agent:")) {
                    int start = line.indexOf(":") + 1;
                    int end = line.length();
                    mostRecentUserAgent = line.substring(start, end).trim();
                }
                //Store the disallowed paths as rules
                if (mostRecentUserAgent != null && mostRecentUserAgent.equals("*")) {
                    if (line.startsWith("Disallow")) {
                        RobotRule r = new RobotRule();
                        r.userAgent = mostRecentUserAgent;
                        int start = line.indexOf(":") + 1;
                        int end = line.length();
                        r.rule = line.substring(start, end).trim();
                        robotRules.add(r);
                    }
                }

            }
            //Check every rule against the incoming path
            for (RobotRule robotRule : robotRules) {
                String path = url.getPath();
                if (robotRule.rule.length() == 0) {
                    System.out.println("Inside RobotSafe- SAFE because from rule.length = 0, allows everything");
                    return true; // allows everything if BLANK
                } else if (robotRule.rule.equals("/")) {
                    System.out.println("Inside RobotSafe- UNSAFE because rule.length = /, allows nothing");
                    return false;       // allows nothing if / is specified
                } else if (robotRule.rule.length() <= path.length()) {
                    String pathCompare = path.substring(0, robotRule.rule.length());
                    if (pathCompare.equals(robotRule.rule)) {
                        System.out.println("Inside RobotSafe- UNSAFE because pathcompare.equals(rule) as paths matched");
                        return false;
                    }

                }
            }
        }
        // System.out.println("Inside RobotSafe- SAFE to crawl");
        return true;
    }

    public boolean startcrawler(String[] in) throws MalformedURLException { //fetch URL
        //while (this.pagesVisited.size() < Integer.parseInt(in[1])) //less than MAX Page to visit
        while (MaxPage < Integer.parseInt(in[1])) {
            String currentUrl;
            //  SpiderLeg leg = new SpiderLeg();
            if (this.pagesToVisit.isEmpty()) { // starting point - first seed
                currentUrl = in[0];
                this.pagesVisited.add(in[0]);
            } else {
                currentUrl = this.nextUrl(); //for all links in particular page,get next link
                if (currentUrl == null & this.pagesToVisit.size() == 0) {
                    System.out.println("No more links to visit on this site");
                    break;
                }
            }
            if (currentUrl.contains("pdf") || currentUrl == "") {
                continue;
            }
            int success;
            boolean flag = robotSafe(currentUrl);//check whether link is safe to crawl
            if (flag) {
                System.out.println("--checked robot.txt for URL: " + currentUrl + " and it is SAFE to crawl");
                success = spider(currentUrl, in[2]);//begin crawling
            } else { //UNSAFE
                System.out.println("--checked robot.txt - URL: " + currentUrl + " and it is UNSAFE to crawl");
                if (this.pagesVisited.size() == 1) {//start seed itself is unsafe
                    this.pagesVisited.clear();
                    this.pagesToVisit.clear();
                    this.links.clear();
                    return false;
                } else {
                    continue;
                }
            }
            word_found = 0;
            word_notfound = 0;
            // writeReportHtml(currentUrl);
            if (success == 1) {
                System.out.println(String.format("**Success** Word %s found at %s", in[2], currentUrl));
                this.pagesToVisit.addAll(getLinks());
                MaxPage++;
            } else if (success == 0) {
                System.out.println(String.format("**Failed! ** Word %s NOT found at %s", in[2], currentUrl));
                //break;
                if (MaxPage == 0) {
                    System.out.println("start seed itself has no word");
                    break;
                }//start seed missing  serchh word }
            } else if (success == -1) {
                break;
            }

        }
        //  this.pagesToVisit.addAll(getLinks());
        System.out.println("\n**Done--- Visited " + MaxPage + " web page(s)");
        System.out.println("size: PagesToVisit:" + this.pagesToVisit.size() + " VisitedPage: " + this.pagesVisited.size() + " total links inside each page : " + this.links.size());

        this.pagesVisited.clear();
        this.pagesToVisit.clear();
        this.links.clear();
        return true;
    }

    public int spider(String url, String word) {
        try {
            Connection connection = Jsoup.connect(url);
            Document htmlDocument = connection.timeout(0).get(); //make connection
            this.htmlDocument = htmlDocument; //download page
            int httpStatuscode = connection.response().statusCode();
            connection.ignoreHttpErrors(false);//ignoreHttpErrors - - false (default) if HTTP errors should be ignored.

            if (connection.response().statusCode() == 200) // 200 is the HTTP OK status code , indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            } else {
                System.out.println("\nHttpStstaus code" + httpStatuscode);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return 0;
            }

            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for (Element link : linksOnPage) {
                if (word == "") { //searchword is null
                    this.links.add(link.absUrl("href"));//copy all links
                    word_found = 1;
                } else {
                    if (link.attr("href").contains(word)) {//copy links that contain searchword
                        this.links.add(link.absUrl("href"));
                        word_found = 1;
                    } else {
                        word_notfound = 1; //search word not present set flag              
                    }
                }
            }
            if (word_found == 1) { //after copying all links write the downloaded content
                if (htmlDocument != null) {
                    String path = writeContent(htmlDocument);
                    writeReportHtml(url, path, httpStatuscode);
                } else {
                    System.out.println("Inside Spider - HTMLDOCUMENt null");
                }
            }
            if (word_notfound == 1 && word_found == 0) {
                return 0; //search word not present in any link
            }
            return 1;//word found
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            System.out.println("Inside Spider - excpetion occured: " + ioe);
            return -1;
        }
    }

    public List<String> getLinks() {
        return this.links;
    }

    public String writeContent(Document htmlDocument) {// throws IOException {
        FileWriter fWriter = null;
        BufferedWriter writer = null;
        String path = null;
        try {
            File file = new File(DirectoryName + "/" + crawlcount);
            if (!file.exists()) {
                if (file.mkdir()) {
                    System.out.println("Repository Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            }
            File f = new File(file.getAbsolutePath() + "/" + MaxPage + "file.html");
            path = f.getAbsolutePath();
            Elements img = htmlDocument.getElementsByTag("img");
            Elements srcc = htmlDocument.getElementsByAttribute("src");
            for (Element el : img) {
                imagecount++;
                el.attr("src", "a");
            }
            // System.out.println("Imagecount : " + imagecount );
            FileUtils.writeStringToFile(f, htmlDocument.html(), "UTF-8");

        } catch (Exception e) {
            System.out.println("Inside writeContent Exception " + e);
        }
        System.out.println("Inside writeContent ");
        return path;
    }

    public void writeReportHtml(String url, String localfilepath, int httpstatuscode) {
        FileWriter fWriter = null;
        BufferedWriter writer = null;
        String lk = "<a href= '" + url + "' target='_blank'> " + url + " </a>";
        String local = "<a href= '" + localfilepath + "' target='_blank'> " + localfilepath + " </a>";
        try {
            File f = new File("./reportHtml.html");
            if (!f.exists()) {
                f.createNewFile();//"./reportHtml.html");
            }
            fWriter = new FileWriter(f, true);//append mode
            writer = new BufferedWriter(fWriter);
            writer.newLine();
            writer.write(lk + " | " + "localpath: " + local + " | imagecount: " + imagecount + " | outlinks: " + links.size() + " | HttpStatusCode: " + httpstatuscode + "<br>");
            imagecount = 0;
            writer.close(); //make sure you close the writer object 
        } catch (Exception e) {
            System.out.println("inside writerportHTMl- " + e);
        }
    }

    /**
     * Returns the next URL to visit (in the order that they were found). We
     * also do a check to make sure this method doesn't return a URL that has
     * already been visited.
     *
     * @return
     */
    String nextUrl() {
        String nextUrl = null;
        do {
           
            if (pagesToVisit.size() != 0) {
                nextUrl = this.pagesToVisit.remove(0);
            } else {
                nextUrl = null; //no new url exsits
                break;
            }

        } while (this.pagesVisited.contains(nextUrl));
        if (nextUrl != null) {
            this.pagesVisited.add(nextUrl);
        }
        return nextUrl;
    }

    public String contentprocessor() {
        File folder = new File(DirectoryName + "/" + crawlcount);
        FileWriter f_write = null;
        Elements p, c = null;
        String contentprocessfile = "./crawler" + crawlcount + "content.html";
        if (!folder.exists()) {
        } else {
            try {
                File[] listOfFiles = folder.listFiles();
                f_write = new FileWriter(contentprocessfile, true);

                //Open repo directory and loop through all files
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        File input = new File(file.getAbsolutePath());
                        Document doc = Jsoup.parse(input, "UTF-8");
                        String title = doc.select("title").toString();
                        Elements n = doc.select("nav").remove();
                        //  String d =doc.select("div.id");
                        doc.select("head").remove();
                        doc.select("link").remove();
                        doc.select("style").remove();
                        doc.select("meta").remove();
                        doc.select("script").remove();
                        doc.select("figure").remove();
                        doc.select("img").remove();
                        doc.select("footer").remove();
                        doc.select("input[type = search]").remove();
                        doc.select("form").remove();
                        doc.select("button").remove();
                        doc.select("video").remove();                 
                        doc.select("div:empty").remove();
                        doc.select("div#footer").remove();
                        doc.select("div#id").remove();
                        doc.select("div#nav").remove();
                         doc.select("div#navigation").remove();
                         doc.select("div.footer").remove();
                          doc.select("div.header").remove();
                          doc.select("li > a[href]").remove();
                        
                        Elements linksOnPage = doc.select("body a[href]");
                        for (Element link : linksOnPage) {
                            if (link.html() == null) {
                                link.remove();//<a></a>
                            } else if (link.html().length() <= 4 ) {// does not contains title of the page 
                                link.remove();
                            }
                                else{
                                int child = link.parentNode().childNodeSize();
                                if (child == 1) {//only element remove
                                    link.remove();
                                }
                            }
                        }
                        f_write.write(doc.text());
                    }
                    f_write.write("<br>");
                }
                f_write.close();
            } catch (Exception e) {
                System.out.println("Inside Contentprocessor" + e);
            }

            return contentprocessfile;
        }
        return null;
    }
  

    public void countFrequency(String cfile) {
        //open processed file tokenize and count word frequency
        if (cfile.length() != 0) { //fi not empty
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            String r = "!@).(':_|,-?/<>* ";
            try {
                FileReader fileReader = new FileReader(cfile);
                String line = null;
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(line, "!@).(':_|,-?/<>*$%^!\" ");
                    while (st.hasMoreTokens()) {
                        String word = st.nextToken().toLowerCase().trim();
                        //   word.replaceAll("\"", "");word.trim();
                        word = word.replaceAll("[^\\w\\s]", "");
                        word = word.replace("\"", "");
                        Matcher m = p.matcher(word);
                        boolean b = m.find();
                        //  System.out.print(" "+word);
                        if (!b) {
                            if (termfrequency.containsKey(word)) {
                                termfrequency.put(word, termfrequency.get(word) + 1);
                            } else {
                                termfrequency.put(word, 1);
                            }
                        } else {
                           
                        }
                    }
                }//after calculating termfequency write it to output file
                writeTermfrequency();
            } catch (IOException ex) {
                System.out.println("Inside countFrequency: " + ex);
            }
        }
    }

    public void writeTermfrequency() {
        try {
            FileWriter f_write = new FileWriter("./crawler" + crawlcount + "output.txt");
            BufferedWriter writer = new BufferedWriter(f_write);

            TreeMap<String, Integer> sortedMap = sortTermFreq(termfrequency);
            Set set = sortedMap.entrySet();
            // Get an iterator
            Iterator i = set.iterator();
            // Display elements
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                writer.write(me.getKey() + " " + me.getValue());
                writer.newLine();
            }
            writer.close();
            termfrequency.clear();
        } catch (Exception e) {
            System.out.println("Inside writerTermFrequency : " + e);
        }
    }

    public TreeMap<String, Integer> sortTermFreq(HashMap<String, Integer> termfreq) {
        //The comparator is used to sort the TreeMap by keys. 
        Comparator<String> comparator = new ValueComparator(termfreq);
        //Creating a TreeMap to enable sorting by keys in our termfreq hashmap
        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(comparator);
        sortedMap.putAll(termfreq);
        return sortedMap;
    }

    class ValueComparator implements Comparator<String> {

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        public ValueComparator(HashMap<String, Integer> map) {
            this.map.putAll(map);
        }

        @Override
        public int compare(String s1, String s2) {
            if (map.get(s1) >= map.get(s2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

  
    public static void main(String[] args) {
        // TODO code application logic here

        Webcrawler web = new Webcrawler();
        web.readcsv();
        System.out.println("\n");
     
    }
}
