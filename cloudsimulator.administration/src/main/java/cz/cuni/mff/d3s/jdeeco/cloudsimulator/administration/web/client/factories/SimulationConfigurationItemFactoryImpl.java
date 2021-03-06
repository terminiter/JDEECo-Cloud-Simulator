package cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.client.factories;

import java.util.Optional;

import javax.annotation.Resource;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationConfiguration;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationExecution;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.client.data.SimulationConfigurationItem;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.client.data.SimulationConfigurationItemImpl;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.client.data.SimulationDataItem;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.client.data.SimulationExecutionItem;

public class SimulationConfigurationItemFactoryImpl implements SimulationConfigurationItemFactory {

	@Resource
	private SimulationDataItemFactory dataItemFactory;

	@Resource
	private SimulationExecutionItemFactory executionItemFactory;

	@Override
	public SimulationConfigurationItem create(SimulationConfiguration configuration, boolean addExecutions) {

		SimulationDataItem dataItem = dataItemFactory.create(configuration.getSimulationData());

		Optional<SimulationExecution> lastExecutionOptional = configuration.getSimulationExecutions().stream()
				.max((e1, e2) -> e1.getId().compareTo(e2.getId())); // TODO improvement - speed up
		SimulationExecutionItem lastExecutionItem = lastExecutionOptional.isPresent() ? executionItemFactory
				.create(lastExecutionOptional.get()) : null;

		SimulationConfigurationItem newConfigItem = new SimulationConfigurationItemImpl(configuration, dataItem,
				lastExecutionItem);

		if (addExecutions) {
			configuration.getSimulationExecutions().stream().sorted((c1, c2) -> c2.getId().compareTo(c1.getId()))
					.forEach(c -> newConfigItem.addExecution(executionItemFactory.create(c)));
		}

		return newConfigItem;
	}
}
