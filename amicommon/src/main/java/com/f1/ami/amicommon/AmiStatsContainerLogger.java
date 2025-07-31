package com.f1.ami.amicommon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.f1.container.Container;
import com.f1.container.ContainerScope;
import com.f1.container.DispatchController;
import com.f1.container.impl.ContainerHelper;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.container.impl.dispatching.RootPartitionStats;
import com.f1.msg.MsgConnection;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.TopMap;

public class AmiStatsContainerLogger implements Runnable {
	public static int MAX_PARTITIONS_PRINT = 64;

	private static final Logger amilog = Logger.getLogger("AMI_STATS.CONTAINER");

	private static final Comparator<RootPartitionActionRunner> RPAR_COMPARATOR = new Comparator<RootPartitionActionRunner>() {

		@Override
		public int compare(RootPartitionActionRunner o1, RootPartitionActionRunner o2) {
			return OH.compare(o1.getActionsAdded(), o2.getActionsAdded());
		}
	};

	private Container container;
	private List<MsgConnection> connections = new ArrayList<MsgConnection>();
	private DispatchController dispatchController;
	private final RootPartitionStats sink = new RootPartitionStats();
	private final HashMap<Object, RootPartitionStats> partitionStats = new HashMap<Object, RootPartitionStats>();
	private final TopMap<RootPartitionActionRunner> tm = new TopMap<RootPartitionActionRunner>(MAX_PARTITIONS_PRINT, RPAR_COMPARATOR);

	public AmiStatsContainerLogger(Container container) {
		this.container = container;
		List<ContainerScope> sink = new ArrayList<ContainerScope>();
		ContainerHelper.getAllChildren(container, sink);
		for (ContainerScope cs : sink) {
			if (cs instanceof MsgSuite) {
				MsgSuite ms = (MsgSuite) cs;
				if (!this.connections.contains(ms.getConnection()))
					this.connections.add(ms.getConnection());
			}
		}
		this.dispatchController = this.container.getDispatchController();
	}
	@Override
	public void run() {
		Collection<RootPartitionActionRunner> runners = this.dispatchController.getRootParititionRunners();

		int count = runners.size();
		boolean atMax = count >= MAX_PARTITIONS_PRINT;
		if (atMax) {
			tm.clear();
		}

		//		for (RootPartitionActionRunner i : runners) {
		//			i.getStats(sink);
		//			if (!atMax)
		//				log(i, sink);
		//			else {
		//				tm.add(i);
		//			}
		//		}
		// // Optimized:
		//		for (RootPartitionActionRunner i : runners) {
		//			if (!atMax) {
		//				i.getStats(sink);
		//				log(i, sink);
		//			} else {
		//				tm.add(i);
		//			}
		//		}
		//		if (atMax) {
		//			for (RootPartitionActionRunner i : tm) {
		//				i.getStats(sink);
		//				log(i, sink);
		//			}
		//			tm.clear();
		//		}

		removeInactivePartitionStats();

		for (RootPartitionActionRunner i : runners) {
			if (!atMax) {
				if (getStats(i, sink))
					log(i, sink);
			} else {
				tm.add(i);
			}
		}
		if (atMax) {
			for (RootPartitionActionRunner i : tm) {
				if (getStats(i, sink))
					log(i, sink);
			}
			tm.clear();
		}
	}

	private void removeInactivePartitionStats() {
		int partitionRunnersCount = this.dispatchController.getRootParititionRunners().size();
		if (partitionRunnersCount == this.partitionStats.size())
			return;
		for (Object partitionId : CH.l(this.partitionStats.keySet())) {
			RootPartitionActionRunner partition = this.dispatchController.getRootPartitionRunner((String) partitionId);
			if (partition == null) {
				this.partitionStats.remove(partitionId);
				if (partitionRunnersCount == this.partitionStats.size())
					break;
			}
		}
	}
	/*
	 * 	Get the stats for each partition, updates active partitions
	 *	Returns true if the stats for the partition has changed 
	 */
	private boolean getStats(RootPartitionActionRunner i, RootPartitionStats sink) {
		i.getStats(sink);
		Object partitionId = i.getPartitionId();
		RootPartitionStats rootPartitionStats = this.partitionStats.get(partitionId);
		if (rootPartitionStats == null) {
			this.partitionStats.put(partitionId, new RootPartitionStats(sink));
			return true;
		} else if (rootPartitionStats.hasChanged(sink)) {
			rootPartitionStats.copyStats(sink);
			return true;
		}
		return false;
	}

	private void log(RootPartitionActionRunner i, RootPartitionStats sink) {
		AmiProcessStatsLogger.log(amilog, "Partition", "added", sink.getActionsAdded(), "processed", sink.getActionsProcessed(), "queued",
				sink.getActionsAdded() - sink.getActionsProcessed(), "totExecTime", sink.getTimeSpentMs(), "execs", sink.getQueueRuns(), "inThreadPool", sink.getInThreadPool(),
				"name", SH.s(i.getPartitionId()), "startTime", i.getStartTime());

	}

}
