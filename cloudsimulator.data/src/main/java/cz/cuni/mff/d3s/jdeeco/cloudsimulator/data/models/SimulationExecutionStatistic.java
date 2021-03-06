package cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models;

import java.util.HashSet;
import java.util.Set;

/**
 * SimulationExecutionStatistic generated by hbm2java
 */
public class SimulationExecutionStatistic implements java.io.Serializable {

	private static final long serialVersionUID = -143323975798657644L;

	private Integer id;
	private SimulationExecution simulationExecution;
	private String name;
	private byte dataType;
	private Set<SimulationRunStatistic> simulationRunStatistics = new HashSet<SimulationRunStatistic>(0);

	public SimulationExecutionStatistic() {
	}

	public SimulationExecutionStatistic(SimulationExecution simulationExecution, String name, byte dataType) {
		this.simulationExecution = simulationExecution;
		this.name = name;
		this.dataType = dataType;
	}

	public SimulationExecutionStatistic(SimulationExecution simulationExecution, String name, byte dataType,
			Set<SimulationRunStatistic> simulationRunStatistics) {
		this.simulationExecution = simulationExecution;
		this.name = name;
		this.dataType = dataType;
		this.simulationRunStatistics = simulationRunStatistics;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SimulationExecution getSimulationExecution() {
		return this.simulationExecution;
	}

	public void setSimulationExecution(SimulationExecution simulationExecution) {
		this.simulationExecution = simulationExecution;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getDataType() {
		return this.dataType;
	}

	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}

	public Set<SimulationRunStatistic> getSimulationRunStatistics() {
		return this.simulationRunStatistics;
	}

	public void setSimulationRunStatistics(Set<SimulationRunStatistic> simulationRunStatistics) {
		this.simulationRunStatistics = simulationRunStatistics;
	}
}
