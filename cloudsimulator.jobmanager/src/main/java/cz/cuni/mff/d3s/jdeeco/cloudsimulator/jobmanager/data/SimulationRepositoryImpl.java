package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.common.data.SimulationStatus;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.common.data.StatisticsSaveMode;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.daos.SimulationExecutionDao;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.daos.SimulationExecutionStatisticDao;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.daos.SimulationExecutionVariableDao;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.daos.SimulationRunDao;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.daos.SimulationRunStatisticDao;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.daos.SimulationRunVariableDao;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationExecution;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationExecutionStatistic;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationExecutionVariable;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationRun;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationRunStatistic;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationRunStatisticAggdata;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.statistics.ByteArrayConverter;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.statistics.ByteArrayConverterProvider;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.statistics.Type2ByteMapper;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.statistics.RunStatistic;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.statistics.RunStatisticVisitor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.statistics.RunStatistics;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.variables.SimulationExecutionVariableDefinition;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.variables.SimulationExecutionVariableDefinitions;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.variables.SimulationRunVariables;

public class SimulationRepositoryImpl implements SimulationRepository {

	private final SimulationExecutionDao simulationExecutionDao;
	private final SimulationRunDao simulationRunDao;
	private final SimulationExecutionStatisticDao simulationExecutionStatisticDao;
	private final SimulationRunStatisticDao simulationRunStatisticDao;
	private final SimulationExecutionVariableDao simulationExecutionVariableDao;
	private final SimulationRunVariableDao simulationRunVariableDao;
	private final Type2ByteMapper type2ByteMapper;
	private final ByteArrayConverterProvider byteArrayConverterProvider;

	public SimulationRepositoryImpl(SimulationExecutionDao simulationExecutionDao, SimulationRunDao simulationRunDao,
			SimulationExecutionStatisticDao simulationExecutionStatisticDao,
			SimulationRunStatisticDao simulationRunStatisticDao,
			SimulationExecutionVariableDao simulationExecutionVariableDao,
			SimulationRunVariableDao simulationRunVariableDao, Type2ByteMapper type2ByteMapper,
			ByteArrayConverterProvider byteArrayConverterProvider) {
		this.simulationExecutionDao = simulationExecutionDao;
		this.simulationRunDao = simulationRunDao;
		this.simulationExecutionStatisticDao = simulationExecutionStatisticDao;
		this.simulationRunStatisticDao = simulationRunStatisticDao;
		this.simulationExecutionVariableDao = simulationExecutionVariableDao;
		this.simulationRunVariableDao = simulationRunVariableDao;
		this.type2ByteMapper = type2ByteMapper;
		this.byteArrayConverterProvider = byteArrayConverterProvider;
	}

	@Transactional(readOnly = true)
	@Override
	public List<SimulationExecution> listNotCompletedExecutions() {
		return simulationExecutionDao.findAllNotCompleted();
	}

	@Transactional(readOnly = true)
	@Override
	public List<SimulationExecution> listStoppedExecutions(Collection<Integer> executionIds) {
		if (executionIds.isEmpty()) {
			return Collections.<SimulationExecution> emptyList();
		}
		return simulationExecutionDao.findStoppedExecutionsWithIds(executionIds);
	}

	@Transactional(readOnly = false)
	@Override
	public SimulationExecution initializeExecution(int executionId, SimulationExecutionVariableDefinitions variableDefinitions) {
		SimulationExecution execution = simulationExecutionDao.findById(executionId);
		Set<SimulationRun> simulationRuns = execution.getSimulationRuns();
		int runMultiplicator = execution.getRunMultiplicator();

		// add execution variables
		for (SimulationExecutionVariableDefinition variableDefinition : variableDefinitions.getDefinitions()) {
			SimulationExecutionVariable executionVariable = new SimulationExecutionVariable();
			executionVariable.setSimulationExecution(execution);
			executionVariable.setName(variableDefinition.getName());
			executionVariable.setDataType(variableDefinition.getDataType());

			simulationExecutionVariableDao.saveOrUpdate(executionVariable);
		}

		// add possible runs
		for (SimulationRunVariables simulationRunVariables : variableDefinitions.getPossibleRunCombinations()) {
			// TODO improvement - do much better - does not need to be saved
			// after each add
			for (int i = 0; i < runMultiplicator; i++) {
				SimulationRun newRun = createSimulationRun(execution, simulationRunVariables);
				simulationRuns.add(newRun);
				simulationRunDao.saveOrUpdate(newRun);
			}
		}

		execution.setSimulationRuns(simulationRuns);
		simulationExecutionDao.saveOrUpdate(execution);
		
		return execution;
	}

	private SimulationRun createSimulationRun(SimulationExecution execution,
			SimulationRunVariables simulationRunVariables) {
		
		// TODO add variables
		return new SimulationRun(execution);
	}

	@Transactional(readOnly = false)
	@Override
	public void startRun(int runId) {
		SimulationRun run = simulationRunDao.findById(runId);
		run.setStarted(new Date());
		run.setStatus(SimulationStatus.Started);
		simulationRunDao.saveOrUpdate(run);
	}

	@Transactional(readOnly = false)
	@Override
	public void completeRun(int runId, RunStatistics runStatistics) {
		// update run state
		SimulationRun run = simulationRunDao.findById(runId);
		run.setEnded(new Date());
		run.setStatus(SimulationStatus.Completed);
		simulationRunDao.saveOrUpdate(run);

		// save run statistics
		int executionId = run.getSimulationExecution().getId();
		SimulationExecution execution = simulationExecutionDao.findById(executionId);
		for (RunStatistic statistic : runStatistics.getStatistics().values()) {
			saveStatistic(execution, run, statistic);
		}
	}

	private void saveStatistic(SimulationExecution execution, SimulationRun run, RunStatistic statistic) {
		SimulationExecutionStatistic executionStatistic = simulationExecutionStatisticDao.find(execution.getId(),
				statistic.getName());
		if (executionStatistic == null) {
			byte typeAsByte = type2ByteMapper.convert(statistic.getValueClass());
			executionStatistic = new SimulationExecutionStatistic(execution, statistic.getName(), typeAsByte);
			simulationExecutionStatisticDao.saveOrUpdate(executionStatistic);
		}

		SimulationRunStatistic runStatistic = new SimulationRunStatistic(executionStatistic, run);

		RunStatisticVisitor addValuesVisitor = new AddValuesRunStatisticVisitor(runStatistic);
		statistic.accept(addValuesVisitor);

		simulationRunStatisticDao.saveOrUpdate(runStatistic);
	}

	@Transactional(readOnly = false)
	@Override
	public void stopRun(int runId) {
		SimulationRun run = simulationRunDao.findById(runId);
		run.setEnded(new Date());
		run.setStatus(SimulationStatus.Stopped);
		simulationRunDao.saveOrUpdate(run);
	}

	@Transactional(readOnly = false)
	@Override
	public void startExecution(int executionId) {
		SimulationExecution execution = simulationExecutionDao.findById(executionId);
		execution.setStarted(new Date());
		execution.setStatus(SimulationStatus.Started);
		simulationExecutionDao.saveOrUpdate(execution);
	}

	@Transactional(readOnly = false)
	@Override
	public void completeExecution(int executionId) {
		SimulationExecution execution = simulationExecutionDao.findById(executionId);
		execution.setEnded(new Date());
		execution.setStatus(SimulationStatus.Completed);
		simulationExecutionDao.saveOrUpdate(execution);
	}

	@Transactional(readOnly = false)
	@Override
	public void stopExecution(int executionId) {
		SimulationExecution execution = simulationExecutionDao.findById(executionId);
		execution.setEnded(new Date());
		execution.setStatus(SimulationStatus.Stopped);
		simulationExecutionDao.saveOrUpdate(execution);
	}

	private class AddValuesRunStatisticVisitor implements RunStatisticVisitor {

		private final SimulationRunStatistic statisticData;

		public AddValuesRunStatisticVisitor(SimulationRunStatistic statisticData) {
			this.statisticData = statisticData;
		}

		@Override
		public <T> void visit(Class<T> valueClass, Map<StatisticsSaveMode, T> aggregatedValues, T[] valuesVector) {
			ByteArrayConverter<T> converter = byteArrayConverterProvider.get(valueClass);

			if (valuesVector != null) {
				statisticData.setVectorData(converter.convertVector(valuesVector));
			}

			if (aggregatedValues != null) {
				for (Map.Entry<StatisticsSaveMode, T> entry : aggregatedValues.entrySet()) {
					byte[] data = converter.convertScalar(entry.getValue());
					SimulationRunStatisticAggdata aggdata = new SimulationRunStatisticAggdata(statisticData,
							entry.getKey(), data);
					statisticData.getSimulationRunStatisticAggdatas().add(aggdata);

					// TODO is this also saved?
				}
			}
		}

	}
}
