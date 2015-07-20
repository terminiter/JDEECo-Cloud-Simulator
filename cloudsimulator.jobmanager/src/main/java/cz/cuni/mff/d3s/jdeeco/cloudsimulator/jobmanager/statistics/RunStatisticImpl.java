package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.statistics;

import java.util.Map;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.common.data.StatisticsSaveMode;

public class RunStatisticImpl<T> implements RunStatistic {

	private final String name;
	private final Map<StatisticsSaveMode, T> aggregatedValues;
	private final T[] valuesVector;

	public RunStatisticImpl(String name, Map<StatisticsSaveMode, T> aggregatedValues, T[] valuesVector) {
		this.name = name;
		this.aggregatedValues = aggregatedValues;
		this.valuesVector = valuesVector;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void accept(RunStatisticVisitor visitor) {
		visitor.visit(aggregatedValues, valuesVector);
	}
}
