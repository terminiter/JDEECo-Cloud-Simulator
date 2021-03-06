package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.data;

import java.util.Collection;
import java.util.List;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationExecution;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.statistics.RunStatistics;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.variables.SimulationExecutionVariableDefinitions;

public interface SimulationRepository {
	List<SimulationExecution> listNotCompletedExecutions();
	List<SimulationExecution> listStoppedExecutions(Collection<Integer> executionIds);

	SimulationExecution initializeExecution(int executionId, SimulationExecutionVariableDefinitions variablesDefinitions);
	
	void startRun(int runId);
	void completeRun(int runId, RunStatistics runStatistics);
	void stopRun(int runId);
	
	void startExecution(int executionId);
	void completeExecution(int executionId);
	void stopExecution(int executionId);
}
