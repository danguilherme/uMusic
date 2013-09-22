package com.ventura.umusic.entity.release;

import java.io.Serializable;
import java.net.URL;

public class Label implements Serializable {
	private static final long serialVersionUID = 969427184161868101L;

	private int id;
	private String name;
	private URL resourceUrl;
	private String catNo;
	private String entityType;

	@Override
	public String toString() {
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

	public String getCategoryNumber() {
		return catNo;
	}

	public void setCategoryNumber(String catNo) {
		this.catNo = catNo;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
}
