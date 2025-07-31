package com.f1.ami.webbalancer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.utils.IOH;
import com.f1.utils.LH;

public class AmiWebBalancerKeepAlive implements Runnable {

	private static final Logger log = LH.get();

	final private AmiWebBalancerServerInstance server;
	final private Socket socket;
	final private OutputStream os;
	final private InputStream is;
	final private byte[] buffer = new byte[1024];
	public AmiWebBalancerKeepAlive(AmiWebBalancerServerInstance server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		socket.setKeepAlive(true);
		IOH.optimize(socket);
		this.os = socket.getOutputStream();
		this.is = socket.getInputStream();
	}
	@Override
	public void run() {
		for (;;)
			try {
				if (is.read(buffer) == -1)
					break;
			} catch (IOException e) {
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "Exception for ", server.getHostPort(), e);
				break;
			}
		IOH.close(os);
		IOH.close(is);
		synchronized (this) {
			notifyAll();
		}
	}
	public void keepAlive() throws IOException {
		Thread thread = new Thread(this, "KAL_" + server.getHostPort());
		thread.setDaemon(true);
		thread.start();
		for (;;) {
			os.write(server.getServer().getTestHttpMethod());
			os.flush();
			synchronized (this) {
				try {
					this.wait(server.getServer().getTestHttpMethodPeriod());
				} catch (InterruptedException e) {
					if (log.isLoggable(Level.FINE))
						LH.fine(log, "Exception for ", server.getHostPort(), e);
				}
			}
		}
	}

}
