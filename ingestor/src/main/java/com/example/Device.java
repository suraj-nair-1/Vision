package com.example;

import org.springframework.boot.json.JacksonJsonParser;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Transient;

public class Device {
	
	
	private String asset_id;
	private float latitude;
	private float longitude;
	private String event_types;

	public Device(){

	}
	public Device(String assetId, float lat, float lng, String etype){
		this.latitude = lat;
		this.longitude = lng;
		this.event_types = etype;
		this.asset_id = assetId;
	}

	
	public float getLat() {
		return this.latitude;
	}
	public float getLong(){
		return this.longitude;
	}
	public void setLat(float lat) {
		this.latitude = lat;
	}
	public void setLong(float lng) {
		this.longitude = lng;
	}

	public void setEtypes(String e) {
		this.event_types = e;
	}
	public String getEtypes(String e) {
		return event_types;
	}



	public String getAsset_id() {
		return asset_id;
	}
	public void setAsset_id(String asset_id) {
		this.asset_id = asset_id;
	}

	
	
	

}
