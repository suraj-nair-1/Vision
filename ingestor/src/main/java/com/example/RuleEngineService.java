package com.example;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.boot.json.JacksonJsonParser;

import org.springframework.web.client.RestTemplate;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;


@Service
public class RuleEngineService {
	
	ArrayList<Device> allDevices = new ArrayList<Device>();
	// ArrayList<ParkingSpots> allLocations = new ArrayList<ParkingSpots>();
	long start;
	long end;
	
	String authToken = "";

	@Scheduled(fixedRate=3600000)
	public void getMediaData() {
		Date d = new Date();
		System.out.println("\n\n\n\nGETTING DATA " + d.getTime());
		JacksonJsonParser parser = new JacksonJsonParser();
		
		HttpHeaders header = new HttpHeaders();
        header.add("Authorization", authToken);
        header.add("Predix-Zone-Id", "f512233c-3401-4a4c-bfee-863504f410ab");
        HttpEntity entity = new HttpEntity(header);
        RestTemplate rest = new RestTemplate();
        
        
    	for(Device a: allDevices){
   			System.out.println("\nASSET: " + a.getAsset_id());
    		String id = a.getAsset_id();
    		String url = "https://ie-public-safety.run.aws-usw02-pr.ice.predix.io/v1/assets/"+id+"/media?assetId="+id+"&media-types=Options:-IMAGE,AUDIO,VIDEO&start-ts="+start+"&end-ts="+end+"&size=40&page=0";
    		String shiet = rest.exchange(url, HttpMethod.GET, entity, String.class).getBody();
//        		JacksonJsonParser parser = new JacksonJsonParser();
//        		Map<String, Object> parsedData = parser.parseMap(shiet);
   			System.out.println(shiet);
    		if (shiet != null && !shiet.isEmpty()) {
    			// System.out.println(shiet);
    			Map<String, Object> eventsData = parser.parseMap(shiet);
        		
        		
        		ArrayList<LinkedHashMap> mp = ((ArrayList<LinkedHashMap>)((LinkedHashMap) eventsData.get("_embedded")).get("medias"));
//        		System.out.println(mp);
        		for(LinkedHashMap l : mp){

        			long ts = (long) ((LinkedHashMap)l).get("timestamp");
        			String media_type = (String) ((LinkedHashMap)l).get("media-type");
        			String data_url = (String) ((LinkedHashMap)l).get("url");
        			data_url = "https" + data_url.substring(4);

        			System.out.println("URL: " + data_url);
        			System.out.println("TYPES: " + media_type);
        			
        			
        		}
    		}
    		
    	}
    	start = end;
    	end = start + 3600000;
    
    }

	
	public void initialize(){
		System.out.println("INITIALIZING");
		Date d = new Date();
		this.end = d.getTime();
		this.start = end - 3600000;
		
		resetBearer();
		System.out.println(authToken);

		HttpHeaders header = new HttpHeaders();
		header.add("Authorization", authToken);
		header.add("Predix-Zone-Id", "f512233c-3401-4a4c-bfee-863504f410ab");
		HttpEntity entity  = new HttpEntity(header);
		RestTemplate rest = new RestTemplate();
		String url = "https://ie-public-safety.run.aws-usw02-pr.ice.predix.io/v1/assets/search?q=media-type:IMAGE;VIDEO;AUDIO&bbox=-82.16:-117.163,82.720:117.263&size=40&page=0";
        String locationQueryResponse = rest.exchange(url, HttpMethod.GET, entity, String.class).getBody();

		JacksonJsonParser parser = new JacksonJsonParser();
		Map<String, Object> parsedData = parser.parseMap(locationQueryResponse);
		LinkedHashMap locations = (LinkedHashMap)parsedData.get("_embedded");
        System.out.println(locations);

        ArrayList<LinkedHashMap> allLocs = (ArrayList<LinkedHashMap>)locations.get("assets");

		System.out.println("_____________ ");
		for(int i = 0; i < allLocs.size(); i++){

            LinkedHashMap geoCoordinates = (LinkedHashMap) allLocs.get(i).get("coordinates");
            String p1Coordinates = (String) geoCoordinates.get("P1");
            String [] p1CoordinatesArray = p1Coordinates.split(",");
            System.out.println(p1CoordinatesArray[0]);
            System.out.println(p1CoordinatesArray[1]);

			LinkedHashMap lnks = (LinkedHashMap) allLocs.get(i).get("_links");
			String s = ((LinkedHashMap)lnks.get("self")).get("href").toString();
			String[] s_arr = s.split("/");
            System.out.println("LOCATION: " + s_arr[s_arr.length - 1]);
            String locID = s_arr[s_arr.length - 1];

            String etype  = (String) allLocs.get(i).get("event-type");
            System.out.println(etype);


			Device new_device = new Device(s_arr[s_arr.length - 1], Float.parseFloat(p1CoordinatesArray[0]), Float.parseFloat(p1CoordinatesArray[1]), etype);
			allDevices.add(new_device);

		}
    }


	@Scheduled(fixedRate=10800000)
	public void resetBearer(){
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
			.url("https://795e002b-65cb-483b-adfe-cc6d7b230a5c.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token?grant_type=client_credentials")
			.get()
			.addHeader("authorization", "Basic bWVkaWFfdGVzdDI6bWVkaWE=")
			.addHeader("cache-control", "no-cache")
			.addHeader("postman-token", "bc949dc9-4ead-b53f-ac40-d3260536d90e")
			.build();

		Response response;
		JacksonJsonParser parser = new JacksonJsonParser();
		try {
			
			response = client.newCall(request).execute();
			Map<String, Object> resp = parser.parseMap(response.body().string());
			this.authToken = ("Bearer " + (resp.get("access_token")).toString());
			System.out.println(this.authToken);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	
}
