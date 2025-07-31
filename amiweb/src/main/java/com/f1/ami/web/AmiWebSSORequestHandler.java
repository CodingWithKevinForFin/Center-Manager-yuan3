package com.f1.ami.web;

import java.io.IOException;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpSession;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.suite.web.WebStatesManager;
import com.f1.utils.LH;

public class AmiWebSSORequestHandler extends AbstractHttpHandler {
	private static final java.util.logging.Logger log = LH.get();

	private AmiWebSSOPlugin plugin;
	private String pluginDesc;

	public AmiWebSSORequestHandler(AmiWebSSOPlugin plugin) {
		this.plugin = plugin;
		this.pluginDesc = plugin.getPluginId();
	}

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		super.handle(req);
		HttpSession session = req.getSession(false);
		WebStatesManager wsm = WebStatesManager.get(session);
		if (session == null || wsm == null || !wsm.isLoggedIn()) {
			String url;
			try {
				url = plugin.buildAuthRequest(req);
			} catch (Exception e) {
				LH.warning(log, "Unexpected error building " + this.pluginDesc + " request", e);
				req.getOutputStream().print("Critical Error Building " + this.pluginDesc + " Request.");
				return;
			}
			req.sendRedirect(url);
		} else {
			req.sendRedirect("3forge_sessions");
		}
	}

}
