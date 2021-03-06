package cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.services;

import java.util.List;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.Project;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.client.data.ProjectVisibilitySettings;

public interface ProjectService {
	List<Project> listProjects();
	List<Project> listVisibleProjects();
	List<Project> listHiddenProjects();

	Project getProjectById(int projectId);
	
	Project createProject(String name, String description);
	Project editProject(int projectId, String name, String description);

	void hideProject(int projectId);
	void editProjectsVisibility(List<ProjectVisibilitySettings> visibilitySettings);
}
