package cz.cuni.mff.d3s.jdeeco.cloudsimulator.worker.data;

public class SimulationDataImpl implements SimulationData {

	private final String runExecutionPath;
	private final String runResultsPath;
	private final String runLogsPath;

	public SimulationDataImpl(String runExecutionPath, String runResultsPath, String runLogsPath) {
		this.runExecutionPath = runExecutionPath;
		this.runResultsPath = runResultsPath;
		this.runLogsPath = runLogsPath;
	}

	@Override
	public String getExecutionPath() {
		return runExecutionPath;
	}

	@Override
	public String getLocalResultsPath() {
		return runResultsPath;
	}

	@Override
	public String getLocalLogsPath() {
		return runLogsPath;
	}

	@Override
	public String toString() {
		return String.format("SimulationDataImpl [executionPath=%s, resultsPath=%s, logsPath=%s]", runExecutionPath,
				runResultsPath, runLogsPath);
	}

}
