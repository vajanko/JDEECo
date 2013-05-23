package cz.cuni.mff.d3s.deeco.scheduling;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.d3s.deeco.invokable.SchedulableProcess;
import cz.cuni.mff.d3s.deeco.invokable.TriggeredSchedulableProcess;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManager;

/**
 * Special scheduler for testing via JPF.
 * 
 * Each task has a separate thread and is executed repeatedly.
 * Timing is ignored (due to GC - see comments in code).
 * We limit the number of executed periods with respect to 2*P_max, where P_max is the longest period.
 */
public class MultithreadedSchedulerJPF extends Scheduler {

	private Set<Thread> threads;

	public MultithreadedSchedulerJPF() {
		super();
		this.threads = new HashSet<Thread>();

		// JPF optimization -> earlier class loading via a clinit call (in the single threaded part)
		// shrinks state space
		@SuppressWarnings("unused")
		ETriggerType e = ETriggerType.COORDINATOR;
	}

	@Override
	public synchronized void start() {

		// get the longest period (P_max)	
		long maxPeriod = 0;
		for (SchedulableProcess sp : periodicProcesses) {
			long spPeriod = ((ProcessPeriodicSchedule) sp.scheduling).interval;
			if (spPeriod > maxPeriod) maxPeriod = spPeriod;
		}

		System.out.println("[DEBUG] max period = " + maxPeriod);

		if (!running) {
			// let every process run for the number of times its period P fits into 2*P_max
			for (SchedulableProcess sp : periodicProcesses) {
				long spPeriod = ((ProcessPeriodicSchedule) sp.scheduling).interval;
				long repeatCount = (2*maxPeriod) / spPeriod + 1;

				System.out.println("[DEBUG] period = " + spPeriod + ", repeat count = " + repeatCount);

				startPeriodicProcess(sp, repeatCount);
			}
			List<KnowledgeManager> kms = new LinkedList<KnowledgeManager>();
			for (TriggeredSchedulableProcess tsp : triggeredProcesses) {
				// not yet supported (we ignore them for now)
				tsp.registerListener();
				if (!kms.contains(tsp.getKnowledgeManager()))
					kms.add(tsp.getKnowledgeManager());
			}
			for (KnowledgeManager km : kms) {
				km.setListenersActive(true);
			}
			running = true;

			Thread.yield(); // break the transition so that (periodic) processes can be scheduled and executed.
		}
	}

	@Override
	public synchronized void stop() {
		if (running) {
			for (Thread t : threads) {
				t.interrupt();
			}
			List<KnowledgeManager> kms = new LinkedList<KnowledgeManager>();
			for (TriggeredSchedulableProcess tsp : triggeredProcesses) {
				if (!kms.contains(tsp.getKnowledgeManager()))
					kms.add(tsp.getKnowledgeManager());
			}
			for (KnowledgeManager km : kms) {
				km.setListenersActive(false);
			}
			running = false;
		}
	}
	
	@Override
	protected void scheduleProcessForExecution(SchedulableProcess process) {
		startPeriodicProcess(process, 0);
	}

	private void startPeriodicProcess(SchedulableProcess process, long repeatCount) {
		// note that period is intentionally ignored because we just need to capture relevant thread interleavings
		Thread t = new Thread(new PeriodicProcessRunner(process, repeatCount));
		threads.add(t);
		t.start();
	}

	class PeriodicProcessRunner implements Runnable {

		final private SchedulableProcess process;
		final private long repeatCount;

		public PeriodicProcessRunner(SchedulableProcess process, long rc) {
			this.process = process;
			this.repeatCount = rc;
		}

		@Override
		public void run() {
			long count = 0;
			while ( repeatCount == 0 || ((count < repeatCount) && (!Thread.interrupted()))) {
				try {
					process.invoke();
				} catch (Exception e) {
					System.out.println("Process scheduled exception: " + e.getMessage());
					e.printStackTrace();
				}
				count++;
			}
		}
	}

}