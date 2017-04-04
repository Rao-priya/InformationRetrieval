package services.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Utils {

    private  Utils() {
    }

    private static String getIPAddress(){
        String myIp = "73.15.200.75";
        try{
            URL url = new URL("http://myexternalip.com/json");// there is a limit of 30 requests/ min
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = bufferedReader.readLine();
            bufferedReader.close();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(line);
            myIp = jsonNode.path("ip").asText();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return myIp;
    }

    public static ServerLocation getGeoLocation() {
        String ipAddress = getIPAddress();
        if (null != ipAddress) {
            try {
                URL url = new URL("http://ip-api.com/json/" + ipAddress); //1000 request per min limit
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = bufferedReader.readLine();
                bufferedReader.close();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(line);
                double lat = jsonNode.path("lat").asDouble();
                double lon = jsonNode.path("lon").asDouble();
                String countryCode = jsonNode.path("countryCode").asText();
                String country = jsonNode.path("country").asText();
                String region = jsonNode.path("region").asText();
                String regionName = jsonNode.path("regionName").asText();
                String city = jsonNode.path("city").asText();

                ServerLocation serverLocation = new ServerLocation();
                serverLocation.setLatitude(lat);
                serverLocation.setLongitude(lon);
                serverLocation.setCountryCode(countryCode);
                serverLocation.setCountryName(country);
                serverLocation.setRegion(region);
                serverLocation.setRegionName(regionName);
                serverLocation.setCity(city);
                return serverLocation;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



}
