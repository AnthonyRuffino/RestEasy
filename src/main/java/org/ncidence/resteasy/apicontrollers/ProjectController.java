package org.ncidence.resteasy.apicontrollers;

import java.net.URI;
import java.net.URISyntaxException;

import org.ncidence.resteasy.exceptions.HttpRequestException;
import org.ncidence.resteasy.models.Project;
import org.ncidence.resteasy.persistence.ProjectEntity;
import org.ncidence.resteasy.persistence.ProjectRepository;
import org.ncidence.resteasy.responses.ProjectResponse;
import org.ncidence.resteasy.responses.ResponseBase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProjectController {

	private static final String END_POINT = "/project";
	private static final String HEADER_HOST = "host";

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = END_POINT, method = RequestMethod.OPTIONS)
	public ResponseEntity<String> options() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Allow", "OPTIONS,GET,HEAD,POST,PUT,DELETE");
		return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = END_POINT + "/{id}", method = RequestMethod.HEAD)
	public ResponseEntity<ResponseBase> head(@PathVariable(value = "id") Long id, @RequestHeader HttpHeaders headers) {
		return getAndHeadHelper(id, false, headers);
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = END_POINT + "/{id}", method = RequestMethod.GET)
	public ResponseEntity<ResponseBase> get(@PathVariable(value = "id") Long id, @RequestHeader HttpHeaders headers) {
		return getAndHeadHelper(id, true, headers);
	}
	

	/**
	 * 
	 * @param id
	 * @param isGet
	 * @return
	 */
	private ResponseEntity<ResponseBase> getAndHeadHelper(Long id, boolean isGet, HttpHeaders requestHeaders) {

		ProjectResponse projectResponse = new ProjectResponse();

		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			ProjectEntity projectEntity = ProjectRepository.getProject(id);
			setResponseHeadersAndValidateConditions(projectEntity, responseHeaders, requestHeaders, true);
			if(isGet){
				projectResponse.setData(ProjectRepository.toModel(projectEntity));
			}
		} catch (HttpRequestException e) {
			ResponseBase errorResponse = new ResponseBase();
			errorResponse.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(errorResponse, e.getStatusCode());
		}

		ResponseEntity<ResponseBase> responseEntity = null;
		
		if(isGet){
			responseEntity = new ResponseEntity<ResponseBase>(projectResponse, responseHeaders, HttpStatus.OK);
		}else{
			responseEntity = new ResponseEntity<ResponseBase>(responseHeaders, HttpStatus.OK);
		}

		return responseEntity;
	}
	
	private void setResponseHeadersAndValidateConditions(ProjectEntity projectEntity, HttpHeaders responseHeaders, HttpHeaders requestHeaders, boolean isGetOrHead) throws HttpRequestException{
		responseHeaders.setLastModified(projectEntity.getLastModified().getTime());
		responseHeaders.setETag(projectEntity.getEtag());
		
		HeadersUtil.checkIfMatch(requestHeaders, projectEntity.getEtag());
		HeadersUtil.checkIfUnModifiedSince(requestHeaders, projectEntity.getLastModified());
		//TODO: Check how exactly Spring Boot is taking care of If-Modified-Since
		//HeadersUtil.checkIfModifiedSince(requestHeaders, projectEntity.getLastModified());
		HeadersUtil.checkIfNoneMatch(requestHeaders, projectEntity.getEtag(), isGetOrHead);
		
		
	}

	/**
	 * 
	 * @param project
	 * @param headers
	 * @return
	 */
	@RequestMapping(value = END_POINT, method = RequestMethod.POST)
	public ResponseEntity<ResponseBase> post(Project project, @RequestHeader HttpHeaders requestHeaders) {

		if (!requestHeaders.containsKey(HEADER_HOST) || requestHeaders.get(HEADER_HOST) == null
				|| requestHeaders.get(HEADER_HOST).isEmpty()) {
			ResponseBase errorResponse = new ResponseBase();
			errorResponse.setMessage(HEADER_HOST + " header is required.");
			return new ResponseEntity<ResponseBase>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		try {
			Long id = ProjectRepository.create(project);
			project.setId(id);
		} catch (HttpRequestException e) {
			ResponseBase errorResponse = new ResponseBase();
			errorResponse.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(errorResponse, e.getStatusCode());
		}

		ProjectResponse projectResponse = new ProjectResponse();

		HttpHeaders responseHeaders = new HttpHeaders();

		String host = requestHeaders.get(HEADER_HOST).get(0);
		URI location = null;

		try {
			location = new URI("//" + host + "/api" + END_POINT + "/" + project.getId());
			responseHeaders.setLocation(location);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			ResponseBase errorResponse = new ResponseBase();
			errorResponse.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		ResponseEntity<ResponseBase> response = new ResponseEntity<ResponseBase>(projectResponse, responseHeaders,
				HttpStatus.SEE_OTHER);

		return response;
	}

	
	/**
	 * 
	 * @param project
	 * @param headers
	 * @return
	 */
	@RequestMapping(value = END_POINT, method = RequestMethod.PUT)
	public ResponseEntity<ResponseBase> put(Project project, @RequestHeader HttpHeaders requestHeaders) {

		HttpHeaders responseHeaders = new HttpHeaders();
		
		try {
			ProjectEntity projectEntity = ProjectRepository.update(project);
			setResponseHeadersAndValidateConditions(projectEntity, responseHeaders, requestHeaders, false);
		} catch (HttpRequestException e) {
			ResponseBase errorResponse = new ResponseBase();
			errorResponse.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(errorResponse, e.getStatusCode());
		}

		ProjectResponse projectResponse = new ProjectResponse();

		ResponseEntity<ResponseBase> response = new ResponseEntity<ResponseBase>(projectResponse, responseHeaders,
				HttpStatus.NO_CONTENT);

		return response;
	}

	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = END_POINT + "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<ResponseBase> delete(@PathVariable(value = "id") Long id, @RequestHeader HttpHeaders requestHeaders) {
		
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			ProjectEntity projectEntity = ProjectRepository.getProject(id);
			setResponseHeadersAndValidateConditions(projectEntity, responseHeaders, requestHeaders, false);
		} catch (HttpRequestException e) {
			ResponseBase errorResponse = new ResponseBase();
			errorResponse.setMessage(e.getMessage());
			return new ResponseEntity<ResponseBase>(errorResponse, e.getStatusCode());
		}
		
		boolean deleted = ProjectRepository.delete(id);
		ResponseEntity<ResponseBase> response = new ResponseEntity<ResponseBase>(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
		return response;
	}

	

}
