package com.f1.ami.web.centermanager.portlets;

import java.util.Map;

import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;

public class AmiCenterManagerReviewApplyScriptPortlet extends GridPortlet implements WebTreeContextMenuListener {
	final private FastTreePortlet stepTree;
	final private WebTreeNode reviewSqlNode;
	final private WebTreeNode applySqlNode;
	final private GridPortlet panelGrid;
	final private InnerPortlet reviewOrApplyPanel;
	final private DividerPortlet div;
	final private AmiCenterManagerReviewScriptPortlet reviewPortlet;
	final private AmiCenterManagerApplyScriptPortlet applyPortlet;

	public AmiCenterManagerReviewApplyScriptPortlet(PortletConfig config) {
		super(config);
		stepTree = new FastTreePortlet(generateConfig());
		stepTree.setFormStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());
		stepTree.getTree().setRootLevelVisible(false);
		stepTree.getTree().addMenuContextListener(this);
		//TODO: add FastTree.hideSearch() js
		reviewSqlNode = createNode(stepTree.getRoot(), "Review SQL Script", null, null);
		applySqlNode = createNode(stepTree.getRoot(), "Apply SQL Script", null, null);
		reviewSqlNode.setSelected(true);
		reviewPortlet = new AmiCenterManagerReviewScriptPortlet(generateConfig());
		applyPortlet = new AmiCenterManagerApplyScriptPortlet(generateConfig());

		panelGrid = new GridPortlet(generateConfig());
		reviewOrApplyPanel = panelGrid.addChild(reviewPortlet, 0, 0, 1, 1);
		reviewOrApplyPanel.setPortlet(reviewPortlet);
		getManager().onPortletAdded(reviewPortlet);
		getManager().onPortletAdded(applyPortlet);

		div = new DividerPortlet(generateConfig(), true, stepTree, panelGrid);
		div.setOffsetFromTopPx(205);
		addChild(div);
	}

	private WebTreeNode createNode(WebTreeNode parent, String title, String icon, Object data) {
		WebTreeNode r = stepTree.createNode(title, parent, false, data);
		r.setIconCssStyle(icon == null ? null : "_bgi=url('" + icon + "')");
		return r;
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {

	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (node == reviewSqlNode)
			reviewOrApplyPanel.setPortlet(reviewPortlet);
		else if (node == applySqlNode)
			reviewOrApplyPanel.setPortlet(applyPortlet);
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}

}
