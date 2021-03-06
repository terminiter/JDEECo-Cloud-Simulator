package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack;

import java.util.HashMap;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.ClearPreparingDirectoryProcessor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.CodeRepositoryProcessor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.CompileCodeProcessor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.CopyCompiledToPreparingDirectoryProcessor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.FetchVariableDefinitionsProcessor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.NotifyPackageTaskCompletedProcessor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.PackageTaskProcessingListener;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.PackageTaskProcessor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.SavePackageProcessor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.pack.processors.SetProfilesProcessor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.vcs.CodeRepositoryManager;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.FutureExecutor;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.servers.SimulationDataStorageService;

public class PackagePreparatorImpl
		implements PackagePreparator, PackagingExceptionHandler, PackageTaskProcessingListener {

	private final HashMap<PackageTask, PackagePreparatorListener> taskRepo = new HashMap<>();
	private final PackageTaskProcessor firstProcessor;

	public PackagePreparatorImpl(FutureExecutor executor, CodeRepositoryManager codeRepositoryManager,
			SimulationDataStorageService simulationDataStorageService, String preparationRootDir, String simulationSettingsRelativePath) {

		CodeRepositoryProcessor codeRepositoryProcessor = new CodeRepositoryProcessor(executor, this,
				codeRepositoryManager);
		CompileCodeProcessor compileCodeProcessor = new CompileCodeProcessor(executor, this);
		CopyCompiledToPreparingDirectoryProcessor copyCompiledToPreparingDirectoryProcessor = new CopyCompiledToPreparingDirectoryProcessor(
				preparationRootDir, executor, this);

		// set profiles for simulation - run, statistics, ...
		SetProfilesProcessor setProfilesProcessor = new SetProfilesProcessor(executor, this);
		FetchVariableDefinitionsProcessor fetchVariableDefinitionsProcessor = new FetchVariableDefinitionsProcessor(
				executor, this, simulationSettingsRelativePath);
		SavePackageProcessor savePackageProcessor = new SavePackageProcessor(executor, this,
				simulationDataStorageService);
		ClearPreparingDirectoryProcessor clearPreparingDirectoryProcessor = new ClearPreparingDirectoryProcessor(
				executor, this);
		NotifyPackageTaskCompletedProcessor notifyPackageTaskCompletedProcessor = new NotifyPackageTaskCompletedProcessor(
				executor, this, this);

		codeRepositoryProcessor.continueProcess(compileCodeProcessor)
				.continueProcess(copyCompiledToPreparingDirectoryProcessor).continueProcess(setProfilesProcessor)
				.continueProcess(fetchVariableDefinitionsProcessor).continueProcess(savePackageProcessor)
				.continueProcess(clearPreparingDirectoryProcessor).continueProcess(notifyPackageTaskCompletedProcessor);

		this.firstProcessor = codeRepositoryProcessor;
	}

	@Override
	public void preparePackage(PackageTask packageTask, PackagePreparatorListener listener) {
		synchronized (taskRepo) {
			taskRepo.put(packageTask, listener);
		}
		this.firstProcessor.process(packageTask);
	}

	@Override
	public void exceptionOccured(PackageTask packageTask, PackagingException e) {
		PackagePreparatorListener listener;
		synchronized (taskRepo) {
			listener = taskRepo.remove(packageTask);
		}
		listener.packageExceptionOccured(packageTask, e);
	}

	@Override
	public void packageTaskCompleted(PackageTask packageTask) {
		PackagePreparatorListener listener;
		synchronized (taskRepo) {
			listener = taskRepo.remove(packageTask);
		}
		listener.packagePrepared(packageTask);
	}
}
