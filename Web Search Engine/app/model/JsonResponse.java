package model;

import java.util.List;
import java.util.Map;

public class JsonResponse {
    public JsonResponse(){
        
    }

	private Map<String, Integer> city;
	private Map<String, Integer> attraction;
	private Map<String, Integer> subcat1;

	public Map<String, Integer> getCity() {
		return city;
	}
	public void setCity(Map<String, Integer> city) {
		this.city = city;
	}
	public Map<String, Integer> getAttraction() {
		return attraction;
	}
	public void setAttraction(Map<String, Integer> attraction) {
		this.attraction = attraction;
	}
	public Map<String, Integer> getSubcat1() {
		return subcat1;
	}
	public void setSubcat1(Map<String, Integer> subcat1) {
		this.subcat1 = subcat1;
	}

}

