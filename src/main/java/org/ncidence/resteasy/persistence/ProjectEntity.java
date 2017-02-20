package org.ncidence.resteasy.persistence;

import java.util.Comparator;
import java.util.Date;

public class ProjectEntity {

	private Long id;
	private String name;
	private String owner;
	private boolean closed;

	private Date lastModified;
	private String etag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}
	
	
	//region static comparators
	public static Comparator<ProjectEntity> compareById = new Comparator<ProjectEntity>() {
	    public int compare(ProjectEntity left, ProjectEntity right) {
	        return left.getId().compareTo(right.getId());
	    }
	};
	//endregion static comparators

}
