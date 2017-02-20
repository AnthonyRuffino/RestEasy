package org.ncidence.resteasy.responses;

import java.util.List;

import org.ncidence.resteasy.models.Project;

public class ProjectsResponse extends ResponseBase {
	private List<Project> data;

	public List<Project> getData() {
		return data;
	}

	public void setData(List<Project> data) {
		this.data = data;
	}
}
