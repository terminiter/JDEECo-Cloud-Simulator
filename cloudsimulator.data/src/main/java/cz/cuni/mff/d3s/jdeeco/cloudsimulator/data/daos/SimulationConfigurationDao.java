package cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.daos;

import java.util.List;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationConfiguration;

public interface SimulationConfigurationDao extends BaseDao<SimulationConfiguration> {

	List<SimulationConfiguration> findByProjectId(int projectId);
}