package cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models;

import java.util.HashSet;
import java.util.Set;

/**
 * SimulationExecutionVariable generated by hbm2java
 */
public class SimulationExecutionVariable implements java.io.Serializable {

	private static final long serialVersionUID = 7048776759758475833L;

	private Integer id;
	private SimulationExecution simulationExecution;
	private String name;
	private byte dataType;
	private Set<SimulationRunVariable> simulationRunVariables = new HashSet<SimulationRunVariable>(0);

	public SimulationExecutionVariable() {
	}

	public SimulationExecutionVariable(SimulationExecution simulationExecution, String name, byte dataType) {
		this.simulationExecution = simulationExecution;
		this.name = name;
		this.dataType = dataType;
	}

	public SimulationExecutionVariable(SimulationExecution simulationExecution, String name, byte dataType,
			Set<SimulationRunVariable> simulationRunVariables) {
		this.simulationExecution = simulationExecution;
		this.name = name;
		this.dataType = dataType;
		this.simulationRunVariables = simulationRunVariables;
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

	public Set<SimulationRunVariable> getSimulationRunVariables() {
		return this.simulationRunVariables;
	}

	public void setSimulationRunVariables(Set<SimulationRunVariable> simulationRunVariables) {
		this.simulationRunVariables = simulationRunVariables;
	}
}
