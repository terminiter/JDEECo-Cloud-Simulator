package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.cloud;

import java.util.List;

public interface CloudMachineService {

	List<CloudMachine> listMachines();
	CloudMachine getMachineWithName(String machineName);
	CloudMachineBuilder buildMachineFromTemplate(String machineName, CloudMachineBuilderParams builderParams);
	
	void startMachine(CloudMachine machine);
	void stopMachine(CloudMachine machine);
	void deleteMachine(CloudMachine machine);
}
