package com.f1.ami.web.bpipe.plugin;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.bloomberglp.blpapi.AuthApplication;
import com.bloomberglp.blpapi.AuthOptions;
import com.bloomberglp.blpapi.AuthUser;
import com.bloomberglp.blpapi.CorrelationID;
import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.EventHandler;
import com.bloomberglp.blpapi.Identity;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;
import com.bloomberglp.blpapi.Names;
import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.SessionOptions;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.rt.AmiWebRealtimeProcessor_BPIPE;
import com.f1.container.ContainerTools;
import com.f1.http.HttpRequestResponse;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

public class BPIPEPlugin {
	final private AmiWebOverrideValue<String> sessionID = new AmiWebOverrideValue<String>("");
	final private AmiWebOverrideValue<String> appName = new AmiWebOverrideValue<String>("");
	final private AmiWebOverrideValue<String> authCorrelationID = new AmiWebOverrideValue<String>("");
	final private AmiWebOverrideValue<String> hostPrimary = new AmiWebOverrideValue<String>("");
	final private AmiWebOverrideValue<String> hostSecondary = new AmiWebOverrideValue<String>("");
	final private AmiWebOverrideValue<Integer> portPrimary = new AmiWebOverrideValue<Integer>(0);
	final private AmiWebOverrideValue<Integer> portSecondary = new AmiWebOverrideValue<Integer>(0);
	final private AmiWebOverrideValue<Integer> reconnect = new AmiWebOverrideValue<Integer>(0);
	final private AmiWebOverrideValue<String> authenticationMode = new AmiWebOverrideValue<String>("");

	BasicCalcTypes paramToSchema = new BasicCalcTypes();
	private AmiWebService service;
	private Session session;
	AuthOptions authOptions;
	private CorrelationID authCorrelationId;
	private final static Logger log = LH.get();
	Identity identity = null;
	static HashMap<CorrelationID, AmiWebRealtimeProcessor_BPIPE> allActiveSubscrptions = new HashMap<CorrelationID, AmiWebRealtimeProcessor_BPIPE>();
	HttpRequestResponse requestResponse;

	public BPIPEPlugin(AmiWebService service, HttpRequestResponse r) {
		this.service = service;
		this.requestResponse = r;
	}
	public AmiWebService getService() {
		return this.service;
	}
	public HttpRequestResponse getRequestResponse() {
		return this.requestResponse;
	}
	public boolean onLogin() {
		this.getProperties();
		if (this.createSession() == false) {
			return false;
		}
		return true;
	}
	public void addProcessorSubscription(CorrelationID c, AmiWebRealtimeProcessor_BPIPE p) {
		allActiveSubscrptions.put(c, p);
	}

	public void removeProcessorSubscription(CorrelationID c) {
		allActiveSubscrptions.remove(c);
	}
	public HashMap<CorrelationID, AmiWebRealtimeProcessor_BPIPE> getActiveSubcriptions() {
		return BPIPEPlugin.allActiveSubscrptions;
	}

	//Handles Bloomberg session and makes sure to stop it when user logs out
	public void onLogout() {
		try {
			this.session.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void getProperties() {
		ContainerTools tools = this.getService().getPortletManager().getTools();
		this.setHostPrimary(tools.getRequired("bpipe_plugin_hostPrimary"), true);
		this.setHostSecondary(tools.getOptional("bpipe_plugin_hostSecondary"), true);
		this.setPortPrimary(Integer.valueOf(tools.getRequired("bpipe_plugin_portPrimary")), true);
		if (tools.getOptional("bpipe_plugin_portSecondary") != null)
			this.setPortSecondary(Integer.valueOf(tools.getOptional("bpipe_plugin_portSecondary")), true);
		this.setAppName(tools.getRequired("bpipe_plugin_appName"), true);
		if (tools.getOptional("bpipe_plugin_reconnect") != null)
			this.setReconnect(Integer.valueOf(tools.getOptional("bpipe_plugin_reconnect"), 30), true);
		this.setSessionID("SESSSION" + this.getService().getUserName(), true);
		this.setAuthCorrelationID(this.getService().getUserName(), true);
		this.setAuthenticationMode(tools.getRequired("bpipe_plugin_authenticationMode"), true);
	}

	//Creates a Bloomberg Session
	public boolean createSession() {
		if (this.hostPrimary == null || this.portPrimary == null || this.appName == null || this.sessionID == null) {
			return false;
		}
		SessionOptions sessionOptions = new SessionOptions();
		SessionOptions.ServerAddress[] servers = new SessionOptions.ServerAddress[2];
		servers[0] = new SessionOptions.ServerAddress(hostPrimary.getValue(true), portPrimary.getValue(true));
		servers[1] = new SessionOptions.ServerAddress(hostSecondary.getValue(true), portSecondary.getValue(true));
		sessionOptions.setServerAddresses(servers);
		sessionOptions.setAutoRestartOnDisconnection(true);
		sessionOptions.setNumStartAttempts(reconnect.getValue(true));
		this.authCorrelationId = new CorrelationID(this.getAuthCorrelationId(true));

		//User authentication : Single user mode. Bloomberg does the authentication. User must have Bloomberg terminal running.
		if (SH.equals(authenticationMode.getValue(true), "userAndApp")) {
			String ipAddr = this.getRequestResponse().getHeader().get("X-Forwarded-For");
			if (ipAddr == null || SH.isEmpty(ipAddr))
				ipAddr = this.ipRetriever();
			try {
				this.authOptions = new AuthOptions(AuthUser.createWithManualOptions(this.getService().getUserName(), ipAddr), new AuthApplication(appName.getValue(true)));
			} catch (Throwable e) {
				LH.severe(log, "Cannot authenticate user");
				return false;
			}
			//Application authentication: Multi user mode. Developers must make sure user is entitled instead of Bloomberg 
		} else if (SH.equals(authenticationMode.getValue(true), "application")) {
			this.authOptions = new AuthOptions(new AuthApplication(this.getAppName(true)));
		}
		sessionOptions.setSessionIdentityOptions(this.authOptions, this.authCorrelationId);
		this.session = new Session(sessionOptions, new SubscriptionEventHandler());
		try {
			session.start();
			session.generateAuthorizedIdentity(this.authOptions);
			this.identity = session.getAuthorizedIdentity(this.authCorrelationId);
		} catch (Throwable e) {
			LH.severe(log, "CANNOT START SESSION", e);
			return false;
		}

		try {
			session.openService("//blp/mktdata");
		} catch (Throwable e) {
			LH.severe(log, "FAILED TO OPEN SERVICE MARKET DATA", e);
			return false;
		}
		setSession(session);
		return true;
	}

	public void setHostPrimary(String host, boolean override) {
		this.hostPrimary.setValue(host, override);
	}
	public void setPortPrimary(Integer port, boolean override) {
		this.portPrimary.setValue(port, override);
	}
	public void setHostSecondary(String host, boolean override) {
		this.hostSecondary.setValue(host, override);
	}
	public void setPortSecondary(Integer port, boolean override) {
		this.portSecondary.setValue(port, override);
	}
	public void setAppName(String appName, boolean override) {
		this.appName.setValue(appName, override);
	}
	public void setSessionID(String sessionID, boolean override) {
		this.sessionID.setValue(sessionID, override);
	}
	public void setAuthCorrelationID(String authCorrelationID, boolean override) {
		this.authCorrelationID.setValue(authCorrelationID, override);
	}
	public void setReconnect(int reconnect, boolean override) {
		this.reconnect.setValue(reconnect, override);
	}
	public void setAuthenticationMode(String mode, boolean override) {
		this.authenticationMode.setValue(mode, override);
	}
	public String getAppName(boolean override) {
		return this.appName.getValue(override);
	}
	public String getSessionID(boolean override) {
		return this.sessionID.getValue(override);
	}
	public String getAuthCorrelationId(boolean override) {
		return this.authCorrelationID.getValue(override);
	}
	public String getHostPrimary(boolean override) {
		return this.hostPrimary.getValue(override);
	}
	public int getPortPrimary(boolean override) {
		return this.portPrimary.getValue(override);
	}
	public String getHostSecondary(boolean override) {
		return this.hostSecondary.getValue(override);
	}
	public int getPortSecondary(boolean override) {
		return this.portSecondary.getValue(override);
	}
	public int getReconnect(boolean override) {
		return this.reconnect.getValue(override);
	}
	public String getAuthenticationMode(boolean override) {
		return this.authenticationMode.getValue(override);
	}
	public void setSession(Session s) {
		this.session = s;
	}
	public Session getSession() {
		return this.session;
	}
	public String ipRetriever() {
		String ipAddr = null;
		try {
			for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements();) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements();) {
					InetAddress inetAddress = inetAddresses.nextElement();
					String name = networkInterface.getDisplayName();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof java.net.Inet4Address) {
						if (name.contains("TUN") || name.contains("TAP") || name.contains("PPP"))
							return inetAddress.getHostAddress();
						else
							ipAddr = inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			LH.severe(log, e);
		}
		return ipAddr;
	}

	static class SubscriptionEventHandler implements EventHandler {
		@Override
		public void processEvent(Event event, Session session) {
			MessageIterator msgIter = event.messageIterator();
			while (msgIter.hasNext()) {
				Message msg = msgIter.next();
				if (msg.messageType().equals("MarketDepthUpdates")) { //FOR LEVEL 2 STREAMING
					AmiWebRealtimeProcessor_BPIPE b = BPIPEPlugin.allActiveSubscrptions.get(msg.correlationID());
					Element e = msg.asElement();
					Map v = b.toMapViewLevel2(e);
					b.addToSchema("ticker", (Class) String.class);
					v.put("ticker", msg.correlationID().toString().substring(msg.correlationID().toString().lastIndexOf("ticker_") + 7));
					b.sendMessage((Object) v, msg.timeReceivedMillis());
				} else if (event.eventType() == Event.EventType.SUBSCRIPTION_DATA) { //FOR LEVEL 1 STREAMING
					AmiWebRealtimeProcessor_BPIPE b = BPIPEPlugin.allActiveSubscrptions.get(msg.correlationID());
					Element e = msg.asElement();
					Map v = b.toMapView(e);
					b.addToSchema("ticker", (Class) String.class);
					v.put("ticker", msg.correlationID().toString().substring(msg.correlationID().toString().lastIndexOf("ticker_") + 7));
					b.sendMessage((Object) v, msg.timeReceivedMillis());
				} else if (event.eventType() == Event.EventType.SUBSCRIPTION_STATUS) {
					if (msg.messageType() == Names.SUBSCRIPTION_FAILURE)
						LH.severe(log, "SUBSCRIPTION FAILED: " + msg.asElement());
				} else if (event.eventType() == Event.EventType.SESSION_STATUS) {
					if (msg.messageType() == Names.SESSION_STARTUP_FAILURE)
						LH.severe(log, "SESSION STATUS: " + msg.asElement());
					else if (msg.messageType() == Names.SESSION_CONNECTION_DOWN || msg.messageType() == Names.SESSION_TERMINATED)
						LH.warning(log, "SESSION STATUS: " + msg.asElement());
				} else if (event.eventType() == Event.EventType.SERVICE_STATUS) {
					if (msg.messageType() == Names.SERVICE_OPEN_FAILURE)
						LH.severe(log, "SERVICE STATUS: " + msg.asElement());
				} else if (event.eventType() == Event.EventType.AUTHORIZATION_STATUS) {
					if (msg.messageType() == Names.AUTHORIZATION_FAILURE || msg.messageType() == Names.AUTHORIZATION_REVOKED)
						LH.severe(log, "AUTHORIZATION STATUS: " + msg.asElement());
				}

			}
		}
	}

}
