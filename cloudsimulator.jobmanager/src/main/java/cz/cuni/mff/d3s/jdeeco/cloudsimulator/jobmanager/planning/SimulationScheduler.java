package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.planning;

public interface SimulationScheduler {

	SimulationPlan getSimulationPlan();
	void recalculateSchedule();
}
