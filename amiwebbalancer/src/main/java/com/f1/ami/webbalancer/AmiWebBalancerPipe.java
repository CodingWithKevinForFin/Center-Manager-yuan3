package com.f1.ami.webbalancer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.utils.IOH;
import com.f1.utils.LH;

public class AmiWebBalancerPipe implements Runnable, Closeable {

	private static final Logger log = LH.get();
	final private OutputStream out;
	final private InputStream in;
	private int tot;
	private AmiWebBalancerConnection connection;
	private boolean isToServer;

	public AmiWebBalancerPipe(AmiWebBalancerConnection session, boolean isToServer, OutputStream out, InputStream in) {
		this.connection = session;
		this.isToServer = isToServer;
		this.out = out;
		this.in = in;
	}

	@Override
	public void run() {
		try {
			final byte[] buf = new byte[8192];
			try {
				for (;;) {
					final int len = in.read(buf, 0, buf.length);
					if (len == -1)
						break;
					out.write(buf, 0, len);
					this.tot += len;
				}
			} catch (Exception e) {
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "Exception for ", connection.getSession().getClientAddress(), e);
			}
			IOH.close(in);
			IOH.close(out);
			connection.onClosed(this);

		} catch (Throwable t) {
			LH.warning(log, "Unknown error for: ", connection.getSession().getClientAddress(), t);
		}
	}

	@Override
	public void close() throws IOException {
		IOH.close(out);
		IOH.close(in);
	}

	public int getTotalBytesSent() {
		return this.tot;
	}

}
