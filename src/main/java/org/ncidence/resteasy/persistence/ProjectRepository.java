package org.ncidence.resteasy.persistence;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.ncidence.resteasy.exceptions.HttpRequestException;
import org.ncidence.resteasy.models.Project;
import org.springframework.http.HttpStatus;

public class ProjectRepository {
	private static Mapper mapper = new DozerBeanMapper();
	private static AtomicLong projectIdFactory = new AtomicLong();

	private static Map<Long, ProjectEntity> projectIdMap = new HashMap<>();
	private static Map<String, ProjectEntity> projectNameMap = Collections.synchronizedMap(new HashMap<>());

	public static Long create(Project project) throws HttpRequestException {
		if (project.getName() == null || project.getName().trim().isEmpty()) {
			throw new HttpRequestException("project name is required", HttpStatus.BAD_REQUEST);
		}
		String name = lowerTrim(project.getName());
		ProjectEntity projectEntity = null;
		synchronized (projectNameMap) {
			if (projectNameMap.containsKey(name)) {
				throw new HttpRequestException("Project name already exists.", HttpStatus.CONFLICT);
			}
			
			project.setId(projectIdFactory.incrementAndGet());
			
			projectEntity = toEntity(project);
			projectEntity.setLastModified(new Date());
			projectEntity.setEtag(etag(project.hashCode()));
			projectNameMap.put(name, projectEntity);
		}
		projectIdMap.put(project.getId(), projectEntity);

		return project.getId();
	}
	
	public static ProjectEntity update(Project project) throws HttpRequestException {
		ProjectEntity projectEntity = null;
		
		String name = lowerTrim(project.getName());
		synchronized (projectNameMap) {
			if (projectNameMap.containsKey(name) && !projectNameMap.get(name).equals(project)) {
				throw new HttpRequestException("Project name already exists.", HttpStatus.CONFLICT);
			}
			if (!projectIdMap.containsKey(project.getId())) {
				throw new HttpRequestException("ID not found.", HttpStatus.NOT_FOUND);
			}
			
			projectEntity = toEntity(project);
			projectEntity.setLastModified(new Date());
			projectEntity.setEtag(etag(project.hashCode()));
			projectNameMap.put(name, projectEntity);
		}
		projectIdMap.put(project.getId(), projectEntity);
		
		return projectEntity;
	}
	
	public static boolean delete(Long id) {
		boolean deleted = false;
		synchronized (projectIdMap) {
			if(projectIdMap.containsKey(id)){
				synchronized (projectNameMap) {
					String name = lowerTrim(projectIdMap.remove(id).getName());
					deleted = projectNameMap.remove(name) != null;
				}
			}
		}
		
		return deleted;
	}
	
	public static ProjectEntity getProject(Long id) throws HttpRequestException {
		if (!projectIdMap.containsKey(id)) {
			throw new HttpRequestException("ID not found.", HttpStatus.NOT_FOUND);
		}
		return projectIdMap.get(id);
	}
	
	public static ProjectEntity toEntity(Project project){
		return mapper.map(project, ProjectEntity.class);
	}
	
	public static Project toModel(ProjectEntity projectEntity){
		return mapper.map(projectEntity, Project.class);
	}
	
	private static String lowerTrim(String name){
		return name.toLowerCase().trim();
	}
	
	private static String etag(int hashCode){
		return "W/\"" + hashCode + "\"";
	}
}
