package cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.daos;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.SimulationData;

public class SimulationDataDaoImpl extends BaseDaoImpl<SimulationData> implements SimulationDataDao {

	public SimulationDataDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public List<SimulationData> findByProjectId(int projectId) {
		return findListByCriteria(Restrictions.eq("project.id", projectId));
	}
}
