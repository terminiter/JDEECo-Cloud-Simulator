package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.data.models;

// Generated Oct 18, 2014 2:47:15 PM by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;

/**
 * SimulationConfiguration generated by hbm2java
 */
public class SimulationConfiguration implements java.io.Serializable {

	private Integer id;
	private Set<SimulationRun> simulationRuns = new HashSet<SimulationRun>(0);

	public SimulationConfiguration() {
	}

	public SimulationConfiguration(Set<SimulationRun> simulationRuns) {
		this.simulationRuns = simulationRuns;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<SimulationRun> getSimulationRuns() {
		return this.simulationRuns;
	}

	public void setSimulationRuns(Set<SimulationRun> simulationRuns) {
		this.simulationRuns = simulationRuns;
	}

}
