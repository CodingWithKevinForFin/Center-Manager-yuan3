package com.f1.ami.web;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiProcessStatsLogger;
import com.f1.ami.amicommon.centerclient.AmiCenterClientState;
import com.f1.http.HttpSession;
import com.f1.http.impl.BasicHttpServer;
import com.f1.suite.web.WebState;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.portal.PortletManager;

public class AmiWebHttpServerLogger implements Runnable {
	private static final Logger amilog = Logger.getLogger("AMI_STATS.WEB");
	private BasicHttpServer httpServer;
	private AmiCenterClientState[] caches;

	public AmiWebHttpServerLogger(BasicHttpServer httpServer, AmiCenterClientState[] caches) {
		this.httpServer = httpServer;
		this.caches = caches;
	}

	public void log() {
		int totCount = httpServer.getHttpSessionManager().getSessionsCount();
		long services = httpServer.getStatsServices() - httpServer.getStatsIncludes();
		long opnCount = httpServer.getStatsConnectionsOpened();
		long clsCount = httpServer.getStatsConnectionsClosed();

		long objectsCount = 0;
		long monitoredObjectsCount = 0;
		long hiddenAlwaysCount = 0;
		for (HttpSession i : httpServer.getHttpSessionManager().getSessions()) {
			Map<String, Object> attributes = i.getAttributes();
			WebStatesManager wsm = WebStatesManager.get(i);
			if (wsm == null)
				continue;
			for (String pgid : wsm.getPgIds()) {
				WebState w = (WebState) wsm.getState(pgid);
				if (w == null)
					continue;
				PortletManager pm = w.getPortletManager();
				if (pm == null)
					continue;
				AmiWebService service = (AmiWebService) pm.getServiceNoThrow(AmiWebService.ID);
				if (service == null)
					continue;
				AmiWebManagers t = service.getWebManagers();
				if (t == null)
					continue;
				for (AmiWebManager m : t.getManagers()) {
					objectsCount += m.getObjectsCount();
					monitoredObjectsCount += m.getMonitoredObjectsCount();
					hiddenAlwaysCount += m.getHiddenAlwaysObjectCount();
				}
			}
		}
		int centersConnected = 0;
		int cachedObjects = 0;
		for (AmiCenterClientState i : caches) {
			if (i.isSnapshotProcessed())
				centersConnected++;
			cachedObjects += i.getObjectsCount();
		}

		AmiProcessStatsLogger.log(amilog, "AmiWebHttpServer", //
				"activeSessions", totCount, //
				"httpServices", services, //
				"activeConnections", opnCount - clsCount, //
				"userRows", objectsCount, //
				"userHiddenRows", monitoredObjectsCount, //
				"userHiddenAlwaysRows", hiddenAlwaysCount, //
				"cachedRows", cachedObjects, //
				"centersConnected", centersConnected, //
				"openedConnections", opnCount //
		);
	}

	@Override
	public void run() {
		log();
	}

}
