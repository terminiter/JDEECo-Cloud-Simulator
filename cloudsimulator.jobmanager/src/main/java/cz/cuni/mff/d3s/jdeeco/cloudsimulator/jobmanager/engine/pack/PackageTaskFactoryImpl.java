package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.engine.pack;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.data.models.SimulationConfiguration;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.data.models.SimulationData;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.data.models.SimulationExecution;

public class PackageTaskFactoryImpl implements PackageTaskFactory {

	@Override
	public PackageTask create(SimulationExecution simulationExecution) {

		SimulationConfiguration simulationConfiguration = simulationExecution.getSimulationConfiguration();
		SimulationData simulationData = simulationConfiguration.getSimulationData();
		
		PackageTaskImpl packageTask = new PackageTaskImpl(simulationExecution.getId());
		packageTask.setRepositoryType(simulationData.getVcsType());
		packageTask.setRepositoryRemoteUrl(simulationData.getRepositoryURL());
		packageTask.setRelativePathToPomFile(simulationData.getPathToPom());
		packageTask.setMavenGoals(simulationData.getMavenGoals().split(" "));
		
		packageTask.setUploadName("exec" + simulationExecution.getId());
		
		return packageTask;
	}
}
