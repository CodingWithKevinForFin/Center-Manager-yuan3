package com.f1.ami.relay;

import java.util.BitSet;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.ami.amicommon.msg.AmiRelayConnectionMessage;
import com.f1.ami.amicommon.msg.AmiRelayErrorMessage;
import com.f1.ami.amicommon.msg.AmiRelayLoginMessage;
import com.f1.ami.amicommon.msg.AmiRelayLogoutMessage;
import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectDeleteMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectMessage;
import com.f1.ami.amicommon.msg.AmiRelayStatusMessage;
import com.f1.utils.LH;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiRelayRouter {
	private static final Logger log = LH.get();
	private final AmiRelayRouteChain chain;
	private final boolean hasRules;
	private final ReusableCalcFrameStack sf;
	//	private final Map<String, AmiRelayRouteChain> chains;// = new HasherMap<String, AmiRelayRouteChain>();
	private long startTime;
	private String fileName;
	//	private AmiRelayRoute[] routes;
	private boolean debug;
	private long statsInCount;
	private long statsOutCount;
	private long statsDropCount;
	private long statsTotalNanos;

	public AmiRelayRouter(String fileName, AmiRelayRoute[] routes, ReusableCalcFrameStack sf, boolean hasRules, AmiRelayRouteChain chain, boolean debug) {
		this.startTime = System.currentTimeMillis();
		this.debug = debug;
		this.fileName = fileName;
		this.sf = sf;
		this.hasRules = hasRules;
		//		this.routes = routes;
		//		this.wildcardChain = wildcardChain;
		this.chain = chain;
	}
	//	public AmiRelayRouteChain getChain(String type) {
	//		final AmiRelayRouteChain r = this.chains.get(type);
	//		return r != null ? r : this.wildcardChain;
	//	}
	public void getRoutes(AmiRelayMessage message, BitSet sink) {
		statsInCount++;
		long start = System.nanoTime();
		char msg;
		String type;
		String id;
		byte[] params;
		if (message instanceof AmiRelayCommandDefMessage) {
			msg = 'C';
			params = ((AmiRelayCommandDefMessage) message).getParams();
			id = ((AmiRelayCommandDefMessage) message).getCommandId();
			type = "__COMMAND";//((AmiRelayCommandDefMessage) message).getCommandId();
		} else if (message instanceof AmiRelayObjectDeleteMessage) {
			msg = 'D';
			params = ((AmiRelayObjectDeleteMessage) message).getParams();
			id = ((AmiRelayObjectDeleteMessage) message).getId();
			type = ((AmiRelayObjectDeleteMessage) message).getType();
		} else if (message instanceof AmiRelayObjectMessage) {
			msg = 'O';
			params = ((AmiRelayObjectMessage) message).getParams();
			id = ((AmiRelayObjectMessage) message).getId();
			type = ((AmiRelayObjectMessage) message).getType();
		} else if (message instanceof AmiRelayStatusMessage) {
			msg = 'S';
			params = ((AmiRelayStatusMessage) message).getParams();
			id = null;
			type = ((AmiRelayObjectMessage) message).getType();
		} else if (message instanceof AmiRelayConnectionMessage) {
			msg = ' ';
			params = ((AmiRelayConnectionMessage) message).getParams();
			id = null;
			type = null;
		} else if (message instanceof AmiRelayLoginMessage) {
			msg = ' ';
			params = ((AmiRelayLoginMessage) message).getParams();
			id = null;
			type = null;
		} else if (message instanceof AmiRelayLogoutMessage) {
			msg = ' ';
			params = ((AmiRelayLogoutMessage) message).getParams();
			id = null;
			type = null;
		} else if (message instanceof AmiRelayErrorMessage) {
			msg = ' ';
			params = new byte[] { 0, 0 };
			id = null;
			type = null;
		} else {
			LH.warning(log, "Unhandled type: " + message);
			return;
		}
		//		AmiRelayRouteChain chain = getChain(type);
		chain.getRoutes(sf.reset(AmiRelayHelper.toValues(chain.getInterestedTypes(), chain.getInterestedTypeKeys(), params, type, id)), type, msg, sink, debug, message);
		this.statsTotalNanos += System.nanoTime() - start;
		if (!sink.isEmpty()) {
			statsOutCount += sink.cardinality();
		} else
			statsDropCount++;
	}

	//Needs concurrent
	public boolean shouldRoute(AmiRelayMessage message, byte centerId) {
		char msg;
		String type;
		String id;
		byte[] params;
		if (message instanceof AmiRelayCommandDefMessage) {
			msg = 'C';
			params = ((AmiRelayCommandDefMessage) message).getParams();
			id = ((AmiRelayCommandDefMessage) message).getCommandId();
			type = "__COMMAND";//((AmiRelayCommandDefMessage) message).getCommandId();
		} else if (message instanceof AmiRelayObjectDeleteMessage) {
			msg = 'D';
			params = ((AmiRelayObjectDeleteMessage) message).getParams();
			id = ((AmiRelayObjectDeleteMessage) message).getId();
			type = ((AmiRelayObjectDeleteMessage) message).getType();
		} else if (message instanceof AmiRelayObjectMessage) {
			msg = 'O';
			params = ((AmiRelayObjectMessage) message).getParams();
			id = ((AmiRelayObjectMessage) message).getId();
			type = ((AmiRelayObjectMessage) message).getType();
		} else if (message instanceof AmiRelayStatusMessage) {
			msg = 'S';
			params = ((AmiRelayObjectMessage) message).getParams();
			id = null;
			type = ((AmiRelayObjectMessage) message).getType();
		} else {
			LH.warning(log, "Unhandled type: " + message);
			return false;
		}
		//		AmiRelayRouteChain chain = wildcardChain;//getChain(type);
		return chain.shouldRoute(sf.reset(AmiRelayHelper.toValues(chain.getInterestedTypes(), chain.getInterestedTypeKeys(), params, type, id)), type, msg, centerId, debug);
	}

	//Needs concurrent
	public boolean hasRules() {
		return hasRules;
	}
	public long getStartTime() {
		return startTime;
	}
	public String getFileName() {
		return this.fileName;
	}
	public AmiRelayRoute[] getRoutes() {
		return this.chain == null ? null : this.chain.getRoutes();
	}
	public long getStatsInCount() {
		return statsInCount;
	}
	public long getStatsOutCount() {
		return statsOutCount;
	}
	public long getStatsDropCount() {
		return statsDropCount;
	}
	public long getStatesTotalNanos() {
		return statsTotalNanos;
	}
	public boolean getDebugMode() {
		return this.debug;
	}
	public void setDebug(boolean b) {
		this.debug = b;
	}
	public void resetStats() {
		this.statsInCount = 0L;
		this.statsOutCount = 0L;
		this.statsDropCount = 0L;
		this.statsTotalNanos = 0L;
	}

}
