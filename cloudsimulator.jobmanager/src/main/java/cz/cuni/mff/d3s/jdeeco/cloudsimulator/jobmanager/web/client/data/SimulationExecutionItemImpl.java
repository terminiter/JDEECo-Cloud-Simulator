package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.web.client.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.data.models.SimulationExecution;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.data.models.SimulationStatus;

public class SimulationExecutionItemImpl implements SimulationExecutionItem {

	private int id;
	private String description;
	private SimulationStatus status;
	private String statusDesc;
	private Date createdDate;
	private Date startedDate;
	private Date endedDate;
	private List<SimulationRunItem> runs = new ArrayList<SimulationRunItem>();
	
	public SimulationExecutionItemImpl() {
	}
	
	public SimulationExecutionItemImpl(SimulationExecution execution) {
		this.id = execution.getId();
		this.description = execution.getDescription();
		this.status = execution.getStatus();
		this.statusDesc = execution.getStatus().toString(); // TODO % done??
		this.createdDate = execution.getCreated();
		this.startedDate = execution.getStarted();
		this.endedDate = execution.getEnded();
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;		
	}

	@Override
	public SimulationStatus getStatus() {
		return status;
	}
	
	@Override
	public String getStatusDesc() {
		return statusDesc;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getStartedDate() {
		return startedDate;
	}

	@Override
	public Date getEndedDate() {
		return endedDate;
	}

	@Override
	public List<SimulationRunItem> getRuns() {
		return runs;
	}

	@Override
	public void addRun(SimulationRunItem run) {
		runs.add(run);
	}
}
