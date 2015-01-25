package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.engine.cloud;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Server;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.cloud.OpenStackConnector;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.cloud.OpenStackComponent;

public class OpenStackMachineService extends OpenStackComponent implements CloudMachineService {

	private final HashMap<String, OpenStackMachine> machinesByName = new HashMap<>();
	private final HashMap<String, OpenStackMachine> machinesById = new HashMap<>();

	private final Object machineLock = new Object();

	public OpenStackMachineService(OpenStackConnector openStackConnector) {
		super(openStackConnector);
	}

	@Override
	public List<CloudMachine> listMachines() {
		synchronized (machineLock) {
			updateFromCloud();
			
			return machinesByName.values().stream().collect(Collectors.toList());
		}
	}

	@Override
	public OpenStackMachine getMachineWithName(String machineName) {
		synchronized (machineLock) {
			if (machinesByName.containsKey(machineName))
				return machinesByName.get(machineName);

			updateFromCloud();

			return machinesByName.containsKey(machineName) ? machinesByName.get(machineName) : null;
		}
	}

	private void updateFromCloud() {
		getClient().compute().servers().list().stream().filter(x -> !machinesById.containsKey(x.getId()))
				.forEach(x -> addMachine(x));
	}

	private OpenStackMachine addMachine(Server server) {
		synchronized (machineLock) {
			OpenStackMachine machine = new OpenStackMachine(server);
			machinesById.put(machine.getId(), machine);
			machinesByName.put(machine.getName(), machine);
			return machine;
		}		
	}

	@Override
	public OpenStackMachineBuilder buildMachineFromTemplate(String templateName, String machineName) {
		return new OpenStackMachineBuilder(openStackConnector, this, templateName, machineName);
	}

	OpenStackMachine registerCreatedMachine(Server newServer) {
		return addMachine(newServer);
	}

	@Override
	public void startMachine(CloudMachine machine) {
		getClient().compute().servers().action(machine.getId(), Action.START);
		
	}

	@Override
	public void stopMachine(CloudMachine machine) {
		getClient().compute().servers().action(machine.getId(), Action.STOP);
	}

	@Override
	public void deleteMachine(CloudMachine machine) {
		synchronized (machine) {
			machinesById.remove(machine.getId());
			machinesByName.remove(machine.getName());
			
			getClient().compute().servers().delete(machine.getId());
		}
	}
}
