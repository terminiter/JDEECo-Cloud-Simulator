package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.engine.vcs;

public interface CodeRepositoryManager {
	
	String prepareRepository(String remoteUrl, VCSType vcsType);
}
