package cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.tasks;

public interface StopSimulationTask extends WorkerTask {
	int getSimulationRunId();
}