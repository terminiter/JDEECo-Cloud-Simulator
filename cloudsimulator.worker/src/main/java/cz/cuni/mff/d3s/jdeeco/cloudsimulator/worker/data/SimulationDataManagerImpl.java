package cz.cuni.mff.d3s.jdeeco.cloudsimulator.worker.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.common.extensions.PathEx;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.FutureExecutor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.SimulationId;

public class SimulationDataManagerImpl implements SimulationDataManager {

	private static final Logger logger = LoggerFactory.getLogger(SimulationDataManagerImpl.class);

	private final List<Future<?>> runningFutures = new ArrayList<>();

	private final String localExecutionsRoot;
	private final String resultsDirectoryName;
	private final String logsDirectoryName;

	private final FutureExecutor executor;
	private final SimulationDataListener listener;

	private SimulationDataRepository simulationDataRepository;

	public SimulationDataManagerImpl(String localExecutionsRoot, String resultsDirectoryName, String logsDirectoryName,
			FutureExecutor executor, SimulationDataRepository dataRepository, SimulationDataListener listener) {

		this.localExecutionsRoot = localExecutionsRoot;
		this.resultsDirectoryName = resultsDirectoryName;
		this.logsDirectoryName = logsDirectoryName;

		this.executor = executor;
		this.simulationDataRepository = dataRepository;

		this.listener = listener;
	}

	private void startFuture(Runnable command) {
		Future<?> future = executor.executeWithFuture(command);
		synchronized (runningFutures) {
			runningFutures.removeIf(x -> x.isDone() || x.isCancelled());
			runningFutures.add(future);
		}
	}

	@Override
	public void prepareData(SimulationId simulationId, String startupFile) {
		logger.info("Starting future for data preparation for simulation. Simulation ID: '{}', Startup file: '{}'.", simulationId, startupFile);
		
		startFuture(() -> prepareDataCore(simulationId, startupFile));
	}

	private void prepareDataCore(SimulationId simulationId, String startupFile) {
		logger.info("Preparing data for simulation. Simulation ID: '{}', Startup file: '{}'.", simulationId, startupFile);

		String runExecutionPath = PathEx.combine(localExecutionsRoot, simulationId.getRunId());
		String runLocalResultsPath = PathEx.combine(runExecutionPath, resultsDirectoryName);
		String runLocalLogsPath = PathEx.combine(runExecutionPath, logsDirectoryName);

		SimulationData preparedData = new SimulationDataImpl(runExecutionPath, runLocalResultsPath, runLocalLogsPath,
				startupFile);

		try {
			String packageDir = simulationDataRepository.getPackagePath(simulationId);

			logger.info("Preparing data for simulation. Copying from '{}' to run dir '{}'.", packageDir, runExecutionPath);			
			FileUtils.copyDirectory(new File(packageDir), new File(runExecutionPath));
			logger.info("Preparing data for simulation. Copied data from '{}' to run dir '{}'.", packageDir, runExecutionPath);
			
			listener.dataPrepared(simulationId, preparedData);
		} catch (IOException e) {
			logger.error(
					String.format("Simulation data preparation failed. ID: %s. Data: %s.", simulationId, preparedData),
					e);
		}
	}

	@Override
	public void saveResults(SimulationId simulationId, SimulationData data) {
		logger.info("Starting future for save of results. Simulation ID: '{}', data: '{}'", simulationId, data);
		
		startFuture(() -> saveResultsCore(simulationId, data));
	}

	private void saveResultsCore(SimulationId simulationId, SimulationData data) {
		logger.info("Saving results. Simulation ID: '{}', data: '{}'", simulationId, data);		
		simulationDataRepository.saveResults(data, simulationId);
		logger.info("Results saved. Simulation ID: '{}', data: '{}'", simulationId, data);
		
		listener.resultsSaved(simulationId);
	}

	@Override
	public void Dispose() {
		synchronized (runningFutures) {
			for (Future<?> future : runningFutures) {
				future.cancel(true);
			}
			runningFutures.clear();
		}
	}
}
