package com.f1.ami.webbalancer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.utils.IOH;
import com.f1.utils.LH;

public class AmiWebBalancerConnection implements Runnable {
	private static final Logger log = LH.get();

	final private Socket clientSocket;
	private Socket serverSocket;
	final private AmiWebBalancerClientSession session;
	final private int connectionId;
	private AmiWebBalancerPipe toServerPipe;
	private AmiWebBalancerPipe toClientPipe;
	private AtomicInteger activeConnections = new AtomicInteger();

	public AmiWebBalancerConnection(AmiWebBalancerClientSession session, int connectionId, Socket socket) {
		this.session = session;
		this.connectionId = connectionId;
		this.clientSocket = socket;
	}

	@Override
	public void run() {
		try {
			AmiWebBalancerServerInstance hostPort = this.session.getOrResolveServerHostPort();
			if (hostPort == null) {
				try {
					PrintStream out;
					LH.info(log, "Sending error code 100 to ", this.session.getClientAddress());
					out = new PrintStream(this.clientSocket.getOutputStream());
					out.print("HTTP/1.1 503 Service Unavailable\r\n");
					String body = "<html><body><span style='color:blue'>3Forge Ultrafast load balancer V1.0 </span><P>Error code <B><span style='color:red'>100 - No Servers Available";
					out.print("Content-Length: " + body.length() + "\r\n");
					out.print("Content-Type: text/html;charset=utf-8htm\r\n\r\n");
					out.print(body);
					out.flush();
					//										InputStream is = this.clientSocket.getInputStream();
					//					is.read();
					//					while (is.available() > 0)
					//						if (is.read() == -1)
					//							break;
				} catch (IOException e) {
				}
				IOH.close(this.clientSocket);
				return;
			}
			try {

				this.serverSocket = hostPort.newSocket();//new Socket(hostPort.getHost(), hostPort.getPort());
				this.serverSocket.setKeepAlive(true);
				IOH.optimize(this.serverSocket);
				this.toServerPipe = new AmiWebBalancerPipe(this, true, clientSocket.getOutputStream(), serverSocket.getInputStream());
				this.toClientPipe = new AmiWebBalancerPipe(this, false, serverSocket.getOutputStream(), clientSocket.getInputStream());
			} catch (Exception e) {
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "Unexpected error binding server ==> client: ", hostPort.getHostPort(), " ==> ", session.getClientAddress(), ", closing client connection", e);
				else
					LH.warning(log, "Unexpected error binding server ==> client: ", hostPort.getHostPort(), " ==> ", session.getClientAddress(), ", closing client connection");
				IOH.close(this.clientSocket);
				IOH.close(this.serverSocket);
				this.toServerPipe = null;
				this.toClientPipe = null;
				return;
			}
			this.session.onOpened(this);
			this.activeConnections.set(2);
			final Thread thread1 = new Thread(toServerPipe, "WBTOSERVER-" + this.session.getSessionId() + "-" + this.connectionId);
			final Thread thread2 = new Thread(toClientPipe, "WBTOCLIENT-" + this.session.getSessionId() + "-" + this.connectionId);
			thread1.start();
			thread2.start();
		} catch (Throwable t) {
			LH.warning(log, "Unknown error: ", t);
		}
	}

	public void onClosed(AmiWebBalancerPipe p1) {
		if (activeConnections.decrementAndGet() == 0)
			this.session.onClosed(this);
	}

	public void close() {
		IOH.close(toServerPipe);
		IOH.close(toClientPipe);
	}

	public long getBytesToServer() {
		AmiWebBalancerPipe t = this.toServerPipe;
		return t == null ? 0 : t.getTotalBytesSent();
	}
	public long getBytesToClient() {
		AmiWebBalancerPipe t = this.toClientPipe;
		return t == null ? 0 : t.getTotalBytesSent();
	}

	public AmiWebBalancerClientSession getSession() {
		return this.session;
	}
}
