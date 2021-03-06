package cz.cuni.mff.d3s.jdeeco.cloudsimulator.worker.execution;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.FutureExecutor;

public class AsyncSimulationExecutorDecorator implements SimulationExecutor {

	private static final Logger logger = LoggerFactory.getLogger(AsyncSimulationExecutorDecorator.class);

	private final SimulationExecutor simulationExecutor;
	private Future<?> future;
	private FutureExecutor futureExecutor;

	public AsyncSimulationExecutorDecorator(SimulationExecutor simulationExecutor, FutureExecutor futureExecutor) {
		this.simulationExecutor = simulationExecutor;
		this.futureExecutor = futureExecutor;
	}


	@Override
	public SimulationExecutorParameters getParameters() {
		return simulationExecutor.getParameters();
	}
	
	@Override
	public void start() {
		if (future != null) return;

		logger.debug("Starting future.");
		
		Runnable startInternalRunnable = () -> this.simulationExecutor.start();
		this.future = this.futureExecutor.executeWithFuture(startInternalRunnable);
	}

	@Override
	public void stop() {

		logger.debug("Stopping simulation executor.");
		simulationExecutor.stop();
		try {
			logger.debug("Waiting for future to end.");
			future.get(); // wait for end
		} catch (ExecutionException | InterruptedException e) {
			logger.error("Error occured while stopping simulation with parameters: " + getParameters(), e);
		}
	}
}
