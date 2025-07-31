package com.f1.ami.webbalancer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple3;

public class AmiWebBalancerServerInstance implements Runnable {

	private static final Logger log = LH.get();
	public static final long MIN_TEST_PERIOD = 1000;
	private final String host;
	private final int port;
	private final AtomicInteger activeSessionCount = new AtomicInteger(0);
	private final AmiWebBalancerServer server;
	private final String hostPort;
	private Boolean isAlive;
	private Boolean isSecure;

	public AmiWebBalancerServerInstance(AmiWebBalancerServer server, String hostport) {
		Tuple3<Boolean, String, Integer> hp = parseHostPort(hostport);
		if (hp == null)
			throw new RuntimeException("Bad url: " + hostport);
		this.server = server;
		this.isSecure = hp.getA();
		this.host = hp.getB();
		this.port = hp.getC();
		this.hostPort = hostport;
		Thread thread = new Thread(this, "WBSERVMON-" + hostport);
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public String toString() {
		return this.hostPort;
	}

	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}

	//will return null if unknown
	public boolean isAlive() {
		return Boolean.TRUE.equals(isAlive);
	}

	public int getActiveSessionsCount() {
		return this.activeSessionCount.get();
	}

	public int incrementActiveSessionsCount() {
		return this.activeSessionCount.incrementAndGet();
	}
	public int decrementActiveSessionsCount() {
		return this.activeSessionCount.decrementAndGet();
	}

	public String getHostPort() {
		return hostPort;
	}

	public static Tuple3<Boolean, String, Integer> parseHostPort(String url) {
		if (SH.isnt(url))
			return null;
		String protocol = SH.beforeFirst(url, "://", null);
		String s = SH.afterFirst(url, "://", url);
		boolean secure = "https".equalsIgnoreCase(protocol);
		int i = s.indexOf(':');
		if (i < 1 || i == s.length() - 1)
			return null;
		try {
			String host = s.substring(0, i);
			int port = SH.parseInt(s, i + 1, s.length(), 10);
			return new Tuple3<Boolean, String, Integer>(secure, host, port);
		} catch (Exception e) {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Parse error for ", url, e);
			return null;
		}
	}

	private byte[] buffer = new byte[1024];

	@Override
	public void run() {
		try {
			for (;;) {//test forever
				long connectedTime = -1;
				Socket socket = null;
				try {
					socket = newSocket();
					AmiWebBalancerKeepAlive kal = new AmiWebBalancerKeepAlive(this, socket);
					setIsAlive(true);
					connectedTime = EH.currentTimeMillis();
					kal.keepAlive();
				} catch (Exception e) {
					if (log.isLoggable(Level.FINE))
						LH.fine(log, "Connection error for ", this.hostPort, " attempt ", e);
					else
						LH.warning(log, "Connection error for ", this.hostPort, " attempt ");
				} finally {
					IOH.close(socket);
				}
				boolean wasAlive = this.isAlive();
				setIsAlive(false);
				if (!wasAlive || EH.currentTimeMillis() - connectedTime < 1000) //was up less than 1 second, lets wait
					OH.sleep(server.getServerAliveCheckMillis());
			}
		} catch (Throwable t) {
			LH.warning(log, "Unknown error: ", t);
		}
	}
	//	private void keepAlive(final OutputStream os, final InputStream is, long pauseMillis) throws IOException {
	//		final Object sem = new Object();
	//		new Thread() {
	//			public void run() {
	//				for (;;)
	//					try {
	//						if (is.read() == -1)
	//							break;
	//					} catch (IOException e) {
	//						break;
	//					}
	//				IOH.close(os);
	//				IOH.close(is);
	//				synchronized (sem) {
	//					sem.notifyAll();
	//				}
	//			};
	//		};
	//		for (;;) {
	//			os.write(server.getTestHttpMethod());
	//			synchronized (sem) {
	//				try {
	//					sem.wait(pauseMillis);
	//				} catch (InterruptedException e) {
	//				}
	//			}
	//		}
	//	}
	private void setIsAlive(boolean b) {
		if (this.isAlive == null) {
			this.isAlive = b;
			synchronized (this) {
				this.notifyAll();
			}
			if (b)
				LH.warning(log, "Server Connection established, Marked as UP: ", this.hostPort);
			else
				LH.warning(log, "Server Connection failed, Marked as DOWN: ", this.hostPort);
			return;
		}
		if (this.isAlive == b)
			return;
		this.isAlive = b;
		if (b)
			LH.warning(log, "Server Connection reestablished, Marked as UP: ", this.hostPort);
		else
			LH.warning(log, "Server Connection lost, Marked as DOWN: ", this.hostPort);
	}

	public void pauseTillActiveKnown(long timeout) {
		if (isAlive == null)
			try {
				synchronized (this) {
					this.wait(timeout);
				}
			} catch (InterruptedException e) {
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "Exception for ", this.hostPort, e);
			}

	}

	public AmiWebBalancerServer getServer() {
		return this.server;
	}

	public Socket newSocket() throws UnknownHostException, IOException {
		return server.newSocket(this.isSecure, this.host, this.port);
	}

}
