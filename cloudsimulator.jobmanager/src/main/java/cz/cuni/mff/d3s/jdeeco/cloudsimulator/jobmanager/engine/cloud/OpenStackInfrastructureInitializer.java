package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.engine.cloud;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Image;
import org.openstack4j.model.compute.SecGroupExtension;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.identity.Tenant;
import org.openstack4j.model.network.AttachInterfaceType;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.Subnet;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.cloud.OpenStackConnector;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.cloud.OpenStackConnectorImpl;

public class OpenStackInfrastructureInitializer {

	private OpenStackConnector openStackConnector = new OpenStackConnectorImpl(
			OpenStackInfrastructureInitializerParameters.ENDPOINT, OpenStackInfrastructureInitializerParameters.USER,
			OpenStackInfrastructureInitializerParameters.PASSWORD,
			OpenStackInfrastructureInitializerParameters.TENANT_NAME);

	public static void main(String[] args) {
		OpenStackInfrastructureInitializer initializer = new OpenStackInfrastructureInitializer();
		initializer.initialize();
	}

	private OSClient connect() {
		return openStackConnector.connect();
	}

	private Tenant getTenant(OSClient os) {
		return os.identity().tenants().getByName(OpenStackInfrastructureInitializerParameters.TENANT_NAME);
	}

	private Network getNetworkByName(OSClient os, String networkName) {
		return os.networking().network().list().stream().filter(x -> x.getName().equals(networkName)).findFirst().get();
	}

	private void initialize() {
		OSClient os = connect();
		Tenant tenant = getTenant(os);

		createNetworkInfrastructure(os, tenant);
		createStorage(os, tenant);

		createJobManager(os, tenant);
		createInitialWorkers(os, tenant);
	}

	private void createNetworkInfrastructure(OSClient os, Tenant tenant) {
		// create inner network
		Network innerNetwork = os
				.networking()
				.network()
				.create(Builders.network().name(OpenStackInfrastructureInitializerParameters.INNER_NETWORK_NAME)
						.tenantId(tenant.getId()).build());

		// create inner network subnet
		Subnet innerNetworkSubnet = os
				.networking()
				.subnet()
				.create(Builders.subnet().name(OpenStackInfrastructureInitializerParameters.INNER_NETWORK_SUBNET_NAME)
						.networkId(innerNetwork.getId()).tenantId(tenant.getId()).ipVersion(IPVersionType.V4)
						.addPool("192.168.0.1", "192.168.254.254").cidr("192.168.0.0/16").build());

		// create router
		Network outerNetwork = getNetworkByName(os, OpenStackInfrastructureInitializerParameters.OUTER_NETWORK_NAME);
		Router router = os
				.networking()
				.router()
				.create(Builders.router().name(OpenStackInfrastructureInitializerParameters.ROUTER_NAME)
						.adminStateUp(true).externalGateway(outerNetwork.getId()).build());

		// attach an external interface
		os.networking().router()
				.attachInterface(router.getId(), AttachInterfaceType.SUBNET, innerNetworkSubnet.getId());

		// create security groups and rules
		SecGroupExtension httpSecurityGroup = os.compute().securityGroups().create("http", "http");
		os.networking()
				.securityrule()
				.create(Builders.securityGroupRule().securityGroupId(httpSecurityGroup.getId()).direction("ingress")
						.protocol("tcp").portRangeMin(80).portRangeMax(80).build());

		SecGroupExtension sshSecurityGroup = os.compute().securityGroups().create("ssh", "ssh");
		os.networking()
				.securityrule()
				.create(Builders.securityGroupRule().securityGroupId(sshSecurityGroup.getId()).direction("ingress")
						.protocol("tcp").portRangeMin(22).portRangeMax(22).build());
	}

	private void createStorage(OSClient os, Tenant tenant) {
		os.objectStorage().containers()
				.create(OpenStackInfrastructureInitializerParameters.STORAGE_DATAPACKAGE_CONTAINER);
	}

	private void createJobManager(OSClient os, Tenant tenant) {
		// create flavor
		Flavor flavor = Builders.flavor().name("Job Manager Flavor").ram(8192).vcpus(6).disk(240).build();
		flavor = os.compute().flavors().create(flavor);

		// get image
		Image jobManagerImage = os.compute().images()
				.get(OpenStackInfrastructureInitializerParameters.JOB_MANAGER_TEMPLATE_NAME);

		// create a job manager model object
		ServerCreate jobManagerCreate = Builders.server()
				.name(OpenStackInfrastructureInitializerParameters.JOB_MANAGER_NAME).flavor(flavor.getId())
				.image(jobManagerImage.getId()).build();

		// boot the server
		Server server = os.compute().servers().bootAndWaitActive(jobManagerCreate, 1000 * 60 * 10);

		// public IP
		Network outerNetwork = getNetworkByName(os, OpenStackInfrastructureInitializerParameters.OUTER_NETWORK_NAME);
		NetFloatingIP publicIP = os.networking().floatingip()
				.create(Builders.netFloatingIP().floatingNetworkId(outerNetwork.getId()).build());

		os.compute().floatingIps().addFloatingIP(server, publicIP.getFloatingIpAddress());
	}

	private void createInitialWorkers(OSClient os, Tenant tenant) {

		// create flavor
		Flavor flavor = Builders.flavor().name("Worker Flavor").ram(2048).vcpus(2).disk(120).build();
		flavor = os.compute().flavors().create(flavor);

		for (int i = 0; i < OpenStackInfrastructureInitializerParameters.INITIAL_WORKER_COUNT; i++) {
			createInitialWorker(os, tenant, i, flavor);
		}
	}

	private void createInitialWorker(OSClient os, Tenant tenant, int workerIndex, Flavor flavor) {
		String workerName = OpenStackInfrastructureInitializerParameters.WORKER_NAME_PREFIX + workerIndex;

		// get image
		Image workerImage = os.compute().images()
				.get(OpenStackInfrastructureInitializerParameters.WORKER_TEMPLATE_NAME);

		// create a worker model object
		ServerCreate workerCreate = Builders.server().name(workerName).flavor(flavor.getId())
				.image(workerImage.getId()).build();

		// boot the server
		os.compute().servers().boot(workerCreate);
	}

	private void clean() {
		OSClient os = connect();

		cleanNetworkInfrastructure(os);
//		cleanStorage(os);

		cleanJobManager(os);
		cleanInitialWorkers(os);
	}

	private void cleanNetworkInfrastructure(OSClient os) {
		// TODO Auto-generated method stub

	}

	private void cleanStorage(OSClient os) {
		os.objectStorage().containers()
				.delete(OpenStackInfrastructureInitializerParameters.STORAGE_DATAPACKAGE_CONTAINER);
	}

	private void cleanJobManager(OSClient os) {
		// TODO Auto-generated method stub

	}

	private void cleanInitialWorkers(OSClient os) {
		// TODO Auto-generated method stub

	}
}