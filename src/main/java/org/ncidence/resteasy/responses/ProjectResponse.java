package org.ncidence.resteasy.responses;

import org.ncidence.resteasy.models.Project;

public class ProjectResponse extends ResponseBase {
	private Project data;

	public Project getData() {
		return data;
	}

	public void setData(Project data) {
		this.data = data;
	}
}
