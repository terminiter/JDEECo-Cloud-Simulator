package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.vcs;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.common.data.VCSType;

public interface CodeRepositoryManager {
	
	String prepareRepository(String remoteUrl, VCSType vcsType);
}
