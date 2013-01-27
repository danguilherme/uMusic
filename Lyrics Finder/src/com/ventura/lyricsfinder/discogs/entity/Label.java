package com.ventura.lyricsfinder.discogs.entity;

import java.net.URL;

import com.google.gson.annotations.SerializedName;

/**
 * The Label resource represents a label, company, recording studio, location,
 * or other entity involved with Artists and Releases. Labels were recently
 * expanded in scope to include things that aren’t labels – the name is an
 * artifact of this history.
 * 
 * @author Guilherme
 * 
 */
public class Label {
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_RESOURCE_URL = "resource_url";
	public static final String KEY_CATNO = "catno";
	public static final String KEY_ENTITY_TYPE = "entity_type";

	@SerializedName(KEY_ID)
	private int id;
	@SerializedName(KEY_NAME)
	private String name;
	@SerializedName(KEY_RESOURCE_URL)
	private URL resourceUrl;
	@SerializedName(KEY_CATNO)
	private String catNo;
	@SerializedName(KEY_ENTITY_TYPE)
	private String entityType;

	public String toString(){
		return name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URL getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(URL resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getCatNo() {
		return catNo;
	}

	public void setCatNo(String catNo) {
		this.catNo = catNo;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
}
