package cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.tasks;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class AfterCommitExecutorImpl extends TransactionSynchronizationAdapter implements AfterCommitExecutor {

	private static final ThreadLocal<Queue<Runnable>> callbacks = new ThreadLocal<Queue<Runnable>>();
	private Logger logger = LoggerFactory.getLogger(AfterCommitExecutorImpl.class);

	/**
	 * Execute
	 * 
	 * Schedule callback to be run after commit. Register itself with transaction if needed.
	 * 
	 * @param runnable
	 *            Runnable object containing the callback.
	 * 
	 */
	@Override
	public void execute(Runnable runnable) {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			logger.debug("Not in transaction, running afthercommit callback now.");
			runnable.run();
		} else {
			Queue<Runnable> threadRunnables = callbacks.get();
			if (threadRunnables == null) {
				threadRunnables = new LinkedList<Runnable>();
				callbacks.set(threadRunnables);
				TransactionSynchronizationManager.registerSynchronization(this);
			}
			logger.debug("Adding callback for transaction commit");
			threadRunnables.add(runnable);
		}
	}

	/**
	 * After commit
	 * 
	 * Run the registered callbacks.
	 * 
	 */
	@Override
	public void afterCommit() {
		Queue<Runnable> threadRunnables = callbacks.get();
		while (!threadRunnables.isEmpty()) {
			Runnable runnable = threadRunnables.poll();
			try {
				logger.debug("Running aftercommit calback");
				runnable.run();
			} catch (RuntimeException e) {
				logger.error("Failed to execute aftercommit callback", e);
			}
		}
	}

	/**
	 * After completion
	 * 
	 * Remove all callbacks when transaction ends (aborts or commits)
	 * 
	 */
	@Override
	public void afterCompletion(int status) {
		callbacks.remove();
	}
}
