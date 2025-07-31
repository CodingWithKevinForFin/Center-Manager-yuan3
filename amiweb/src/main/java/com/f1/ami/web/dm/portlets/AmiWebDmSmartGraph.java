package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAbstractTablePortlet;
import com.f1.ami.web.AmiWebDividerPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebTabPortlet;
import com.f1.ami.web.AmiWebTreemapPortlet;
import com.f1.ami.web.charts.AmiWebChartGridPortlet;
import com.f1.ami.web.filter.AmiWebFilterPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.graph.AmiWebGraphHelper;
import com.f1.ami.web.graph.AmiWebGraphManager;
import com.f1.ami.web.graph.AmiWebGraphNode;
import com.f1.ami.web.graph.AmiWebGraphNodeRt;
import com.f1.ami.web.graph.AmiWebGraphNode_Datamodel;
import com.f1.ami.web.graph.AmiWebGraphNode_Datasource;
import com.f1.ami.web.graph.AmiWebGraphNode_Link;
import com.f1.ami.web.graph.AmiWebGraphNode_Panel;
import com.f1.ami.web.graph.AmiWebGraphNode_Realtime;
import com.f1.ami.web.tree.AmiWebTreePortlet;
import com.f1.base.IterableAndSize;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.suite.web.portal.impl.visual.GraphContextMenuFactory;
import com.f1.suite.web.portal.impl.visual.GraphListener;
import com.f1.suite.web.portal.impl.visual.GraphPortlet;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Edge;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Node;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.LongKeyMap;

public class AmiWebDmSmartGraph implements GraphListener, GraphContextMenuFactory, WebMenuListener {
	private static final Logger log = LH.get();
	private static final String STYLE_ST_PN = "_cna=graph_node_st_panel";//static panel
	private static final String STYLE_RT_PN = "_cna=graph_node_rt_panel";//realtime panel
	private static final String STYLE_ST_FP = "_cna=graph_node_st_filterpanel";//filter panel
	private static final String STYLE_VP = "_cna=graph_node_vp";//html panel
	private static final String STYLE_DM = "_cna=graph_node_dms";
	private static final String STYLE_BL = "_cna=graph_node_dmt";
	private static final String STYLE_DS = "_cna=graph_node_ds";
	private static final String STYLE_PR = "_cna=graph_node_pr";//processor
	private static final String STYLE_FD = "_cna=graph_node_fd";//feed

	private static final String STYLE_RT_CT = "_cna=graph_node_rt_chartpanel";
	private static final String STYLE_RT_HM = "_cna=graph_node_rt_heatmappanel";
	private static final String STYLE_RT_MP = "_cna=graph_node_rt_mappanel";
	private static final String STYLE_RT_PR = "_cna=graph_node_rt_processorpanel";
	private static final String STYLE_RT_TP = "_cna=graph_node_rt_tablepanel";
	private static final String STYLE_RT_TR = "_cna=graph_node_rt_treepanel";
	private static final String STYLE_RT_FR = "_cna=graph_node_rt_formpanel";

	private static final String STYLE_ST_CT = "_cna=graph_node_st_chartpanel";
	private static final String STYLE_ST_HM = "_cna=graph_node_st_heatmappanel";
	private static final String STYLE_ST_MP = "_cna=graph_node_st_mappanel";
	private static final String STYLE_ST_PR = "_cna=graph_node_st_processorpanel";
	private static final String STYLE_ST_TP = "_cna=graph_node_st_tablepanel";
	private static final String STYLE_ST_TR = "_cna=graph_node_st_treepanel";
	private static final String STYLE_ST_FR = "_cna=graph_node_st_formpanel";
	private static final String STYLE_ST_TBS = "_cna=graph_node_st_tabspanel";

	private static final int LEFT_PADDING = 140;
	private static final int TOP_PADDING = 100;
	private static final int X_SPACING = 120;
	private static final int Y_SPACING = 120;
	private static final int NODE_WIDTH = 100;
	private static final int NODE_HEIGHT = 60;
	private GraphPortlet graph;
	private AmiWebService service;
	private final LongKeyMap<Node> graphNodes = new LongKeyMap<Node>();
	private int minXPos[];
	private final LongKeyMap<Integer> depths = new LongKeyMap<Integer>();
	private final IdentityHashSet<AmiWebGraphNode<?>> remaining = new IdentityHashSet<AmiWebGraphNode<?>>();
	private int maxDepth;
	private LongKeyMap<AmiWebGraphNode<?>> allNodes = new LongKeyMap<AmiWebGraphNode<?>>();
	private IdentityHashSet<AmiWebGraphNode<?>> origNodes = new IdentityHashSet<AmiWebGraphNode<?>>();
	private IdentityHashSet<AmiWebGraphNode<?>> toSelect = new IdentityHashSet<AmiWebGraphNode<?>>();
	private AmiWebDmTreePortlet owner;
	private boolean allowModification;

	public AmiWebDmSmartGraph(AmiWebDmTreePortlet owner, AmiWebService service, GraphPortlet graph, boolean allowModification) {
		this.graph = graph;
		this.owner = owner;
		this.allowModification = allowModification;
		this.graph.setMenuFactory(this);
		this.service = service;
		this.graph.addGraphListener(this);
		this.graph.setSnapSize(5);
		this.graph.setGridSize(20);
	}

	public void buildGraph(IdentityHashSet<AmiWebGraphNode<?>> origNodes, IdentityHashSet<AmiWebGraphNode<?>> toSelect) {
		if (this.origNodes.equals(origNodes) && this.toSelect.equals(toSelect))
			return;
		this.origNodes.clear();
		this.origNodes.addAll(origNodes);
		this.toSelect.clear();
		this.toSelect.addAll(toSelect);
		this.owner.onGraphNeedsRebuild();
	}
	public void rebuild() {
		IdentityHashMap<AmiWebGraphNode<?>, Byte> allNodes = new IdentityHashMap<AmiWebGraphNode<?>, Byte>();
		for (AmiWebGraphNode<?> i : this.origNodes)
			walkNodes(i, allNodes, (byte) (WALK_SOURCE | WALK_TARGET | WALK_CHILDPANELS | WALK_LINKS));
		this.allNodes.clear();
		for (AmiWebGraphNode i : allNodes.keySet())
			this.allNodes.put(i.getUid(), i);
		this.graphNodes.clear();
		this.graph.clear();
		this.depths.clear();
		this.remaining.clear();
		for (AmiWebGraphNode<?> i : this.allNodes.values())
			if (i.getType() != AmiWebGraphNode.TYPE_LINK)
				this.remaining.add(i);

		//First determine "tallest" hierarchy
		int depth = 0;
		List<AmiWebGraphNode_Datasource> datasources = new ArrayList<AmiWebGraphNode_Datasource>();
		List<AmiWebGraphNode_Realtime> feeds = new ArrayList<AmiWebGraphNode_Realtime>();
		for (AmiWebGraphNode<?> i : remaining) {
			depth = Math.max(depth, determineDepth(i));
			if (i.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
				datasources.add((AmiWebGraphNode_Datasource) i);
			if (i.getType() == AmiWebGraphNode.TYPE_FEED)
				feeds.add((AmiWebGraphNode_Realtime) i);
		}
		for (AmiWebGraphNode<?> i : remaining) {
			if (i.getType() == AmiWebGraphNode.TYPE_PANEL) {
				AmiWebGraphNode_Panel p = (AmiWebGraphNode_Panel) i;
				if (p.getTargetRealtimes().isEmpty())
					this.depths.put(i.getUid(), depth);
			}
		}
		this.maxDepth = depth;
		minXPos = new int[depth + 1];

		//Start with datasources
		for (AmiWebGraphNode_Datasource i : sort(datasources)) {
			if (i.getTargetDatamodels().isEmpty())
				continue;
			Node node = addNode(0, i);
			if (node == null)
				continue;
			List<Node> childs = new ArrayList<Node>();
			for (AmiWebGraphNode_Datamodel j : sort(i.getTargetDatamodels().values()))
				CH.addSkipNull(childs, addToGraph(getX(node), j));
			center(node, childs);
		}
		for (AmiWebGraphNode_Realtime i : sort(feeds)) {
			if (i.getTargetRealtimes().isEmpty())
				continue;
			Node node = addNode(0, i);
			if (node == null)
				continue;
			List<Node> childs = new ArrayList<Node>();
			for (AmiWebGraphNodeRt<?> j : sort(i.getTargetRealtimes().values()))
				CH.addSkipNull(childs, addToGraph(getX(node), j));
			center(node, childs);
		}

		//do datamodels w/o datasources
		for (AmiWebGraphNode i : CH.l(remaining)) {
			if (i.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
				addToGraph(0, (AmiWebGraphNode_Datamodel) i);
			}
		}

		//dangling panels
		for (AmiWebGraphNode i : CH.l(remaining)) {
			if (i.getType() == AmiWebGraphNode.TYPE_PANEL) {
				AmiWebGraphNode_Panel n = (AmiWebGraphNode_Panel) i;
				if (n.getInner() instanceof AmiWebDividerPortlet)
					continue;
			}
			addNode(0, i);
		}

		//build links
		for (AmiWebGraphNode i : allNodes.keySet()) {
			Node sourceNode = graphNodes.get(i.getUid());
			switch (i.getType()) {
				case AmiWebGraphNode.TYPE_DATAMODEL: {
					AmiWebGraphNode_Datamodel dm = (AmiWebGraphNode_Datamodel) i;
					for (AmiWebGraphNode_Datamodel j : sort(dm.getTargetDatamodels().values())) {
						Node targetNode = graphNodes.get(j.getUid());
						if (targetNode != null)
							addDataEdge(sourceNode, targetNode);
					}
					for (AmiWebGraphNode_Panel j : sort(dm.getTargetPanels().values())) {
						Node targetNode = graphNodes.get(j.getUid());
						if (targetNode != null)
							addDataEdge(sourceNode, targetNode);
					}
					break;
				}
				case AmiWebGraphNode.TYPE_DATASOURCE: {
					AmiWebGraphNode_Datasource dm = (AmiWebGraphNode_Datasource) i;
					for (AmiWebGraphNode_Datamodel j : sort(dm.getTargetDatamodels().values())) {
						Node targetNode = graphNodes.get(j.getUid());
						if (targetNode != null) {
							addDataEdge(sourceNode, targetNode);
						}
					}
					break;
				}
				case AmiWebGraphNode.TYPE_PANEL: {
					AmiWebGraphNode_Panel pn = (AmiWebGraphNode_Panel) i;
					for (AmiWebGraphNode_Datamodel dm : sort(pn.getTargetFilterDatamodels().values())) {
						Node td = graphNodes.get(dm.getUid());
						if (td != null) {
							addFilterEdge(sourceNode, td);
							continue;
						}
					}
					for (AmiWebGraphNode_Link link : sort(pn.getTargetLinks().values())) {
						if (link.getTargetDm() != null) {
							Node td = graphNodes.get(link.getTargetDm().getUid());
							if (td != null) {
								addLinkEdge(sourceNode, td);
								continue;
							}
						}
						if (link.getTargetPanel() != null) {
							Node tp = graphNodes.get(link.getTargetPanel().getUid());
							if (tp != null)
								addLinkEdge(sourceNode, tp);
						}
					}
					break;
				}
			}
			if (i instanceof AmiWebGraphNodeRt) {
				for (AmiWebGraphNodeRt<?> t : ((AmiWebGraphNodeRt<?>) i).getTargetRealtimes().values()) {
					Node targetNode = graphNodes.get(t.getUid());
					if (targetNode != null)
						addRealtimeEdge(sourceNode, targetNode);
				}
			}
		}
		for (AmiWebGraphNode i : this.toSelect) {
			Node node = this.graphNodes.get(i.getUid());
			if (node != null)
				node.setSelected(true);
		}
		for (Node i : this.graph.getNodes()) {
			AmiWebGraphNode data = getData(i);
			if (data != null && canExplore(data))
				i.setHtml(i.getHtml() + "<BR><span class='can-explore'></span>");
		}
	}
	private int getX(Node node) {
		return node.getX() - LEFT_PADDING;
	}

	private void center(Node node, List<Node> childs) {
		if (childs.isEmpty())
			return;
		int min = getX(childs.get(0));
		int max = min;
		for (int i = 1; i < childs.size(); i++) {
			int t = getX(childs.get(i));
			min = Math.min(min, t);
			max = Math.max(max, t);
		}
		int mid = (max + min) / 2;
		if (mid > getX(node)) {
			AmiWebGraphNode n = (AmiWebGraphNode) node.getData();
			node.setX(LEFT_PADDING + mid);
			minXPos[depths.get(n.getUid())] = getX(node) + X_SPACING;
		}

	}

	private Node addNode(int minx, AmiWebGraphNode i) {
		if (!this.remaining.remove(i))
			return null;
		int y = depths.get(i.getUid());
		String style;
		String name = i.getLabel();
		String description = i.getDescription();
		if (SH.is(description))
			name = name + "<BR>(" + description + ")";
		switch (i.getType()) {
			case AmiWebGraphNode.TYPE_DATAMODEL:
				AmiWebGraphNode_Datamodel dm = (AmiWebGraphNode_Datamodel) i;
				if (dm.getSourceDatamodels().isEmpty())
					style = STYLE_DM;
				else
					style = STYLE_BL;
				break;
			case AmiWebGraphNode.TYPE_DATASOURCE: {
				style = STYLE_DS;
				break;
			}
			case AmiWebGraphNode.TYPE_FEED: {
				style = STYLE_FD;
				break;
			}
			case AmiWebGraphNode.TYPE_PROCESSOR: {
				style = STYLE_RT_PR;
				break;
			}
			case AmiWebGraphNode.TYPE_PANEL: {
				AmiWebGraphNode_Panel pn = (AmiWebGraphNode_Panel) i;
				if (pn.isRealtime()) {
					if (pn.getInner() instanceof AmiWebTreemapPortlet)
						style = STYLE_RT_HM;
					else if (pn.getInner() instanceof AmiWebAbstractTablePortlet)
						style = STYLE_RT_TP;
					else if (pn.getInner() instanceof AmiWebTreePortlet)
						style = STYLE_RT_TR;
					else
						style = STYLE_RT_PN;
				} else {
					if (pn.getInner() instanceof AmiWebQueryFormPortlet)
						style = STYLE_ST_FR;
					else if (pn.getInner() instanceof AmiWebFilterPortlet)
						style = STYLE_ST_FP;
					else if (pn.getInner() instanceof AmiWebTreePortlet)
						style = STYLE_ST_TR;
					else if (pn.getInner() instanceof AmiWebAbstractTablePortlet)
						style = STYLE_ST_TP;
					else if (pn.getInner() instanceof AmiWebTreemapPortlet)
						style = STYLE_ST_HM;
					else if (pn.getInner() instanceof AmiWebChartGridPortlet)
						style = STYLE_ST_CT;
					else if (pn.getInner() instanceof AmiWebTabPortlet)
						style = STYLE_ST_TBS;
					else
						style = STYLE_ST_PN;
				}
				break;
			}
			default:
				style = "";
		}
		if (i.getInner() == null && i.getType() != AmiWebGraphNode.TYPE_FEED)
			name += "<BR><span style='background:red;color:white;pointer-events:none'>Not Defined</span>";
		int x = Math.max(minx, minXPos[y]);
		minXPos[y] = x + X_SPACING;
		Node node = this.graph.addNode(LEFT_PADDING + x, TOP_PADDING + (this.maxDepth - y) * Y_SPACING, NODE_WIDTH, NODE_HEIGHT, name, style);
		node.setData(i);
		graphNodes.put(i.getUid(), node);
		return node;
	}

	private Node addToGraph(int minx, AmiWebGraphNodeRt<?> dm) {
		Node node = addNode(minx, dm);
		if (node == null)
			return null;
		minx = getX(node);
		List<Node> childs = new ArrayList<Node>();
		for (AmiWebGraphNodeRt<?> j : sort(dm.getTargetRealtimes().values()))
			CH.addSkipNull(childs, addToGraph(minx, j));
		center(node, childs);
		return node;
	}
	private Node addToGraph(int minx, AmiWebGraphNode_Datamodel dm) {
		Node node = addNode(minx, dm);
		if (node == null)
			return null;
		minx = getX(node);
		List<Node> childs = new ArrayList<Node>();
		for (AmiWebGraphNode_Datamodel j : sort(dm.getTargetDatamodels().values()))
			CH.addSkipNull(childs, addToGraph(minx, j));
		for (AmiWebGraphNode_Panel j : sort(dm.getTargetPanels().values()))
			CH.addSkipNull(childs, addNode(minx, j));
		center(node, childs);
		return node;
	}

	private void addDataEdge(Node sourceNode, Node targetNode) {
		Edge edge = this.graph.addEdge(sourceNode.getId(), targetNode.getId());
		edge.setColor("#AEAEAE");
		edge.setDirection(GraphPortlet.DIRECTION_FORWARD);
	}
	private void addRealtimeEdge(Node sourceNode, Node targetNode) {
		Edge edge = this.graph.addEdge(sourceNode.getId(), targetNode.getId());
		edge.setColor("#55aa55");
		edge.setDirection(GraphPortlet.DIRECTION_FORWARD);
	}
	private void addLinkEdge(Node sourceNode, Node targetNode) {
		Edge edge = this.graph.addEdge(sourceNode.getId(), targetNode.getId());
		edge.setColor("#66aa66");
		edge.setIndex(sourceNode.getEdgesBetweenCount(targetNode) * 3 + 5);
		edge.setDirection(GraphPortlet.DIRECTION_FORWARD);
	}
	private void addFilterEdge(Node sourceNode, Node targetNode) {
		Edge edge = this.graph.addEdge(sourceNode.getId(), targetNode.getId());
		edge.setColor("#eaa4a4");
		edge.setIndex(sourceNode.getEdgesBetweenCount(targetNode) * 3 + 5);
		edge.setDirection(GraphPortlet.DIRECTION_FORWARD);
	}
	private int determineDepth(AmiWebGraphNode i) {
		Integer r = depths.get(i.getUid());
		if (r == null) {
			switch (i.getType()) {
				case AmiWebGraphNode.TYPE_DATASOURCE:
				case AmiWebGraphNode.TYPE_FEED:
					r = 0;
					break;
				case AmiWebGraphNode.TYPE_PROCESSOR: {
					AmiWebGraphNode_Realtime dm = (AmiWebGraphNode_Realtime) i;
					int max = 0;
					for (AmiWebGraphNode j : dm.getSourceRealtimes().values())
						max = Math.max(determineDepth(j), max);
					r = max + 1;
					break;
				}
				case AmiWebGraphNode.TYPE_DATAMODEL: {
					AmiWebGraphNode_Datamodel dm = (AmiWebGraphNode_Datamodel) i;
					int max = 0;
					for (AmiWebGraphNode j : dm.getSourceDatamodels().values())
						max = Math.max(determineDepth(j), max);
					for (AmiWebGraphNode j : dm.getSourceDatasources().values())
						max = Math.max(determineDepth(j), max);
					for (AmiWebGraphNode j : dm.getSourceRealtimes().values())
						max = Math.max(determineDepth(j), max);
					//					for (AmiWebGraphNode j : dm.getSourceFilterPanels().values())
					//						max = Math.max(determineDepth(j), max);
					r = max + 1;
					break;
				}
				case AmiWebGraphNode.TYPE_PANEL: {
					AmiWebGraphNode_Panel dm = (AmiWebGraphNode_Panel) i;
					int max = 0;
					for (AmiWebGraphNode j : dm.getSourceDatamodels().values())
						max = Math.max(determineDepth(j), max);
					for (AmiWebGraphNode j : dm.getSourceRealtimes().values())
						max = Math.max(determineDepth(j), max);
					r = max + 1;
					break;
				}
				case AmiWebGraphNode.TYPE_LINK:
					r = 0;
					break;
			}
			if (i instanceof AmiWebGraphNodeRt) {
				AmiWebGraphNodeRt<?> rt = (AmiWebGraphNodeRt<?>) i;

			}
			depths.put(i.getUid(), r);
		}
		return r;

	}

	private <T extends AmiWebGraphNode<?>> Collection<T> sort(Collection<T> in) {
		if (in.isEmpty())
			return Collections.EMPTY_LIST;
		ArrayList<T> r = new ArrayList<T>();
		for (T i : in)
			if (this.depths.containsKey(i.getUid()) || i instanceof AmiWebGraphNode_Link)
				r.add(i);
		Collections.sort(r, comparator);
		return r;
	}

	private final Comparator<AmiWebGraphNode<?>> comparator = new Comparator<AmiWebGraphNode<?>>() {

		@Override
		public int compare(AmiWebGraphNode<?> o1, AmiWebGraphNode<?> o2) {
			Integer d1 = depths.get(o1.getUid());
			Integer d2 = depths.get(o2.getUid());
			int n = OH.compare(d1, d2, true);
			return n == 0 ? AmiWebGraphManager.COMPARATOR_ID.compare(o1, o2) : n;
		}
	};

	@Override
	public void onSelectionChanged(GraphPortlet graphPortlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onContextMenu(GraphPortlet graphPortlet, String action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserClick(GraphPortlet graphPortlet, Node nodeOrNull, int button, boolean ctrl, boolean shft) {
		if (button != 2)
			return;

		IterableAndSize<Node> nodes = getGraph().getSelectedNodes();
		List<AmiWebGraphNode<?>> nodes2 = new ArrayList<AmiWebGraphNode<?>>(nodes.size());
		if (nodes.size() >= 1)
			CH.first(nodes).setSelected(true);
		boolean hasExplore = false;
		for (Node data : nodes) {
			AmiWebGraphNode n = AmiWebDmSmartGraph.getData(data);
			if (n == null)
				return;
			nodes2.add(n);
			if (!hasExplore && canExplore(n))
				hasExplore = true;
		}
		BasicWebMenu menu = AmiWebDmSmartGraphMenu.createContextMenu(getService(), nodes2, this.allowModification);
		int needsDiv = 0;
		if (nodes2.size() > 0) {
			menu.add(0, new BasicWebMenuLink("Navigate To", true, "navigate"));
			needsDiv++;
		}
		if (hasExplore) {
			menu.add(0, new BasicWebMenuLink("Explore", true, "explore"));
			needsDiv++;
		}
		if (needsDiv > 0)
			menu.add(needsDiv, new BasicWebMenuDivider());
		if (menu != null)
			service.getPortletManager().showContextMenu(menu, this);
	}

	@Override
	public void onUserDblClick(GraphPortlet graphPortlet, Integer id) {
	}

	public static AmiWebGraphNode getData(Node node) {
		return (AmiWebGraphNode) node.getData();
	}

	@Override
	public void onKeyDown(String keyCode, String ctrl) {
	}

	public GraphPortlet getGraph() {
		return this.graph;
	}

	@Override
	public void onMenuItem(String id) {
		IterableAndSize<Node> selected = this.graph.getSelectedNodes();
		List<AmiWebGraphNode<?>> nodes = new ArrayList<AmiWebGraphNode<?>>(selected.size());
		for (Node i : selected)
			nodes.add(getData(i));
		if (id.equals("explore")) {
			IdentityHashSet<AmiWebGraphNode<?>> origNodes = new IdentityHashSet<AmiWebGraphNode<?>>(this.origNodes);
			origNodes.addAll(nodes);
			this.buildGraph(origNodes, new IdentityHashSet<AmiWebGraphNode<?>>(nodes));
		} else if (id.equals("navigate")) {
			this.buildGraph(new IdentityHashSet<AmiWebGraphNode<?>>(nodes), new IdentityHashSet<AmiWebGraphNode<?>>(nodes));
		} else
			AmiWebDmSmartGraphMenu.onMenuItem(service, id, nodes);
		//		if (id.equals("add_dm")) {
		//			List<AmiWebDatasourceWrapper> dsList = new ArrayList<AmiWebDatasourceWrapper>();
		//			List<AmiWebDmsImpl> dmList = new ArrayList<AmiWebDmsImpl>();
		//			for (Node n : this.graph.getSelectedNodes()) {
		//				AmiWebGraphNode data = getData(n);
		//				if (data.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
		//					dsList.add(((AmiWebGraphNode_Datasource) data).getInner());
		//				else if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL)
		//					dmList.add(((AmiWebGraphNode_Datamodel) data).getInner());
		//			}
		//			AmiWebUtils.showEditDmPortlet(service, dsList, dmList, "Add Datamodel");
		//		} else if (id.equals("delete_dm")) {
		//			Map<String, AmiWebDm> toDelete = new HashMap<String, AmiWebDm>();
		//			boolean allTransient = true;
		//			for (Node n : this.graph.getSelectedNodes()) {
		//				AmiWebGraphNode_Datamodel data = (AmiWebGraphNode_Datamodel) getData(n);
		//				if (data.getInner().isReadonlyLayout()) {
		//					getManager().showAlert("Datamodel is readonly: " + data.getInner().getDmName());
		//					return;
		//				}
		//				toDelete.put(data.getInner().getAliasDotName(), data.getInner());
		//				if (!data.getInner().isTransient())
		//					allTransient = false;
		//				if (!data.getTargetDatamodels().isEmpty())
		//					getManager().showAlert(
		//							"Cannot delete datamodel <B>" + data.getId() + "</B> is referenced by datamodel(s): <B>" + SH.join(", ", data.getTargetDatamodels().keySet()) + "</B>");
		//				if (!data.getTargetPanels().isEmpty())
		//					getManager().showAlert(
		//							"Cannot delete datamodel <B>" + data.getId() + "</B> is referenced by panel(s): <B>" + SH.join(", ", data.getTargetPanels().keySet()) + "</B>");
		//			}
		//			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "Delete " + toDelete.size() + " selected datamodel(s)?", ConfirmDialogPortlet.TYPE_YES_NO, this)
		//					.setCallback("DELETE").setCorrelationData(toDelete);
		//			if (allTransient)
		//				cdp.fireYesButton();
		//			else
		//				getManager().showDialog("Delete Datamodel", cdp);
		//
		//		} else if (id.equals("view_data")) {
		//
		//			List<AmiWebDm> datamodels = new ArrayList<AmiWebDm>();
		//			List<Node> nodes = this.graph.getSelectedNodesList();
		//			for (int i = 0; i < nodes.size(); i++) {
		//				Node node = nodes.get(i);
		//				AmiWebGraphNode d = getData(node);
		//				if (d.getType() == AmiWebGraphNode.TYPE_DATAMODEL)
		//					datamodels.add(((AmiWebGraphNode_Datamodel) d).getInner());
		//			}
		//
		//			AmiWebDmViewDataPortlet window = new AmiWebDmViewDataPortlet(generateConfig(), datamodels, null, AmiWebDmViewDataPortlet.RESPONSEDATA_DEFAULT);
		//			getManager().showDialog("View Data", window);
		//		} else if (id.equals("view_data_after")) {
		//
		//			List<AmiWebDm> datamodels = new ArrayList<AmiWebDm>();
		//			List<Node> nodes = this.graph.getSelectedNodesList();
		//			for (int i = 0; i < nodes.size(); i++) {
		//				Node node = nodes.get(i);
		//				AmiWebGraphNode d = getData(node);
		//				if (d.getType() == AmiWebGraphNode.TYPE_DATAMODEL)
		//					datamodels.add(((AmiWebGraphNode_Datamodel) d).getInner());
		//			}
		//
		//			AmiWebDmViewDataPortlet window = new AmiWebDmViewDataPortlet(generateConfig(), datamodels, null, AmiWebDmViewDataPortlet.RESPONSEDATA_AFTER_FILTER);
		//			getManager().showDialog("View Data", window);
		//		} else if (id.equals("view_data_before")) {
		//
		//			List<AmiWebDm> datamodels = new ArrayList<AmiWebDm>();
		//			List<Node> nodes = this.graph.getSelectedNodesList();
		//			for (int i = 0; i < nodes.size(); i++) {
		//				Node node = nodes.get(i);
		//				AmiWebGraphNode d = (AmiWebGraphNode) node.getData();
		//				if (d.getType() == AmiWebGraphNode.TYPE_DATAMODEL)
		//					datamodels.add(((AmiWebGraphNode_Datamodel) d).getInner());
		//			}
		//
		//			AmiWebDmViewDataPortlet window = new AmiWebDmViewDataPortlet(generateConfig(), datamodels, null, AmiWebDmViewDataPortlet.RESPONSEDATA_BEFORE_FILTER);
		//			getManager().showDialog("View Data", window);
		//		} else if (id.equals("edit")) {
		//			AmiWebGraphNode data = getData(getContextNode());
		//			if (data.getType() == data.TYPE_DATAMODEL) {
		//				AmiWebUtils.showEditDmPortlet(service, ((AmiWebGraphNode_Datamodel) data).getInner(), "Edit Datamodel");
		//			}
		//		} else if (id.equals("pp")) {
		//			for (Node n : this.graph.getSelectedNodes()) {
		//				AmiWebGraphNode_Datamodel d = (AmiWebGraphNode_Datamodel) getData(getContextNode());
		//				AmiWebDm dm = d.getInner();
		//				dm.setIsPlay(!dm.isPlaying());
		//				//				AmiWebDmLayoutNode pos = d.layoutNode;
		//				//				applyStyle(n, pos);
		//			}
		//
		//		} else if (id.equals("copy")) {
		//			AmiWebGraphNode data = getData(getContextNode());
		//			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
		//				AmiWebAddPanelPortlet window = new AmiWebAddPanelPortlet(generateConfig(), ((AmiWebGraphNode_Datamodel) data).getInner(), true);
		//				getManager().showDialog("Copy DMS", window);
		//			}
		//		} else if (id.equals("exportall")) {
		//			AmiWebViewConfigurationPortlet p = new AmiWebViewConfigurationPortlet(generateConfig());
		//			p.setConfiguration(service.getDmManager().getConfiguration("", service.getDmLayoutManager()));//TODO: support choosing alias
		//			getManager().showDialog("Import/Export Datamodel", p);
		//		} else if (id.equals("import_export")) {
		//			AmiWebGraphNode data = getData(getContextNode());
		//			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
		//				getManager().showDialog("Import/Export Datamodel", new AmiWebDmImportExportPortlet(generateConfig(), ((AmiWebGraphNode_Datamodel) data).getInner()));
		//			}
		//		} else if (id.equals("import_dm")) {
		//			getManager().showDialog("Import/Export Datamodel", new AmiWebDmImportExportPortlet(generateConfig(), null));
		//		} else if (id.startsWith("pause_selected_")) {
		//			boolean play = Boolean.parseBoolean(SH.stripPrefix(id, "pause_selected_", true));
		//			Set<Node> nodes = new HashSet<Node>();
		//			for (Node node : graph.getSelectedNodes()) {
		//				nodes.add(node);
		//			}
		//			for (Node node : nodes) {
		//				AmiWebGraphNode_Datamodel data = (AmiWebGraphNode_Datamodel) getData(node);
		//				data.getInner().setIsPlay(play);
		//			}
		//			graph.clearSelected();
		//		} else if (id.equals("select_dm")) {
		//			List<Node> nodes = new ArrayList<Node>();
		//			for (Node node : graph.getSelectedNodes()) {
		//				AmiWebGraphNode d = getData(node);
		//				if (d.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
		//					nodes.add(node);
		//				}
		//			}
		//			graph.clearSelected();
		//			setSelectedNodes(nodes, true);
		//		} else if (id.equals("select_ds")) {
		//			List<Node> nodes = new ArrayList<Node>();
		//			for (Node node : graph.getSelectedNodes()) {
		//				AmiWebGraphNode d = getData(node);
		//				if (d.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
		//					nodes.add(node);
		//			}
		//			graph.clearSelected();
		//			setSelectedNodes(nodes, true);
		//		} else if (id.equals("select_pt")) {
		//			List<Node> nodes = new ArrayList<Node>();
		//			for (Node node : graph.getSelectedNodes()) {
		//				AmiWebGraphNode d = getData(node);
		//				if (d.getType() == AmiWebGraphNode.TYPE_PANEL)
		//					nodes.add(node);
		//			}
		//			graph.clearSelected();
		//			setSelectedNodes(nodes, true);
		//		} else if (id.startsWith("ph_")) {
		//			GraphPortletHelper.onArrangeMenuItem(id, this.graph.getSelectedNodesList());
		//		} else if ("ds_add".equals(id)) {
		//			AmiWebDatasourceWrapper ds = null;
		//			if (graph.getSelectedNodesList().size() > 0) {
		//				AmiWebGraphNode data = getData(graph.getSelectedNodesList().get(0));
		//				if (data.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
		//					ds = ((AmiWebGraphNode_Datasource) data).getInner();
		//			}
		//			getManager().showDialog("Attach Datasource", new AmiWebDmAddEditDatasourcePortlet(getManager().generateConfig(), ds, false, false));
		//		} else if ("ds_edit".equals(id)) {
		//			IterableAndSize<Node> selectedNodes = this.graph.getSelectedNodes();
		//			if (selectedNodes.size() == 1) {
		//				AmiWebDatasourceWrapper ds = null;
		//				AmiWebGraphNode data = getData(graph.getSelectedNodesList().get(0));
		//				if (data.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
		//					ds = ((AmiWebGraphNode_Datasource) data).getInner();
		//				getManager().showDialog("Edit Datasource", new AmiWebDmAddEditDatasourcePortlet(getManager().generateConfig(), ds, true, false));
		//			}
		//		} else if ("ds_copy".equals(id)) {
		//			AmiWebDatasourceWrapper ds = null;
		//			if (graph.getSelectedNodesList().size() > 0) {
		//				AmiWebGraphNode data = getData(graph.getSelectedNodesList().get(0));
		//				if (data.getType() == AmiWebGraphNode.TYPE_DATASOURCE)
		//					ds = ((AmiWebGraphNode_Datasource) data).getInner();
		//			}
		//			getManager().showDialog("Copy Datasource", new AmiWebDmAddEditDatasourcePortlet(getManager().generateConfig(), ds, false, true));
		//		} else if ("ds_delete".equals(id)) {
		//			IterableAndSize<Node> selectedNodes = this.graph.getSelectedNodes();
		//			for (Node n : selectedNodes) {
		//				if (n.hasEdgesOutgoing()) { // Check that datasource has no children
		//					AmiWebGraphNode_Datasource data = (AmiWebGraphNode_Datasource) getData(n);
		//					AmiWebDatasourceWrapper ds = data.getInner();
		//					getManager().showAlert("Cannot delete datasource "
		//							+ (ds == null ? (data == null ? "" : "<B>" + SH.stripSuffix(data.getId(), DS_NOT_DEFINED_WARNING, false) + "</B> ") : "<B>" + ds.getName() + "</B> ")
		//							+ "because it is referenced by at least one datamodel");
		//					return;
		//				}
		//			}
		//			ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete the selected datasource(s)?",
		//					ConfirmDialogPortlet.TYPE_YES_NO);
		//			dialog.setCallback("confirm_delete");
		//			dialog.addDialogListener(this);
		//			dialog.updateButton(ConfirmDialogPortlet.ID_YES, "Delete");
		//			getManager().showDialog("Confirmation", dialog);
		//		} else if ("add_viz".equals(id)) {
		//			AmiWebGraphNode data = getData(this.getContextNode());
		//			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
		//				AmiWebDm dm = ((AmiWebGraphNode_Datamodel) data).getInner();
		//				this.service.getDesktop().showAddVisualizationPortlet(dm);
		//			}
		//		} else if ("add_filt".equals(id)) {
		//			AmiWebGraphNode data = getData(this.getContextNode());
		//			if (data.getType() == AmiWebGraphNode.TYPE_DATAMODEL) {
		//				AmiWebDm dm = ((AmiWebGraphNode_Datamodel) data).getInner();
		//				List<String> tableNamesSorted = dm.getResponseOutSchema().getTableNamesSorted();
		//				ConfirmDialogPortlet confirm;
		//				if (tableNamesSorted.size() == 1) {
		//					String tableName = CH.first(tableNamesSorted);
		//					confirm = new ConfirmDialogPortlet(generateConfig(), "Select " + dm.getDmName() + " Datamodel?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this, null);
		//					AmiWebFilterPortlet filter = (AmiWebFilterPortlet) this.service.getDesktop().newPortlet(AmiWebFilterPortlet.Builder.ID,
		//							service.getDesktop().getDesktop().getAmiLayoutFullAlias());
		//					filter.setUsedDatamodel(dm.getAliasDotName(), CH.first(tableNamesSorted));
		//					getManager().showDialog("Create Filter", new AmiWebFilterSettingsPortlet(generateConfig(), filter));
		//				} else {
		//					FormPortletSelectField<String> selectTableField = new FormPortletSelectField<String>(String.class, "&nbsp;");
		//					for (String table : tableNamesSorted) {
		//						selectTableField.addOption(table, table);
		//					}
		//					confirm = new ConfirmDialogPortlet(generateConfig(),
		//							"The '<B>" + dm.getDmName() + "</B>' datamodel has " + tableNamesSorted.size() + " tables. Please choose one:", ConfirmDialogPortlet.TYPE_OK_CANCEL,
		//							this, selectTableField);
		//					confirm.setCallback("SELECT_DM_TABLE");
		//					getManager().showDialog("Select " + (tableNamesSorted.size() == 1 ? "Datamodel" : "Table"), confirm);
		//				}
		//			}
		//		} else if ("show_dm_history".equals(id)) {
		//			AmiWebGraphNode_Datamodel data = (AmiWebGraphNode_Datamodel) getData(this.getContextNode());
		//			service.getDesktop().showViewObjectsPortlet();
		//			AmiWebViewObjectsPortlet op = service.getDesktop().getSpecialPortlet(AmiWebViewObjectsPortlet.class);
		//			AmiWebDm dm = data.getInner();
		//			Row rowOnDatamodelsTable = op.getDatamodelsPortlet().getRowByDmAdn(dm.getAliasDotName());
		//			op.getTabsContainer().setActiveTab(op.getDatamodelsPortlet());
		//			op.getDatamodelsPortlet().getTablePortlet().getTable().setSelectedRows(new int[] { rowOnDatamodelsTable.getLocation() });
		//			op.getDatamodelsPortlet().onContextMenu(op.getDatamodelsPortlet().getTablePortlet().getTable(), AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY);
		//		} else if ("show_pnl_history".equals(id)) {
		//			AmiWebGraphNode_Panel data = (AmiWebGraphNode_Panel) getData(this.getContextNode());
		//			service.getDesktop().showViewObjectsPortlet();
		//			AmiWebViewObjectsPortlet op = service.getDesktop().getSpecialPortlet(AmiWebViewObjectsPortlet.class);
		//			AmiWebPortlet panel = data.getInner();
		//			Row rowOnPanelsPortlet = op.getPanelsPortlet().getRowByPortletId(panel.getPortletId());
		//			op.getTabsContainer().setActiveTab(op.getPanelsPortlet());
		//			op.getPanelsPortlet().getTablePortlet().getTable().setSelectedRows(new int[] { rowOnPanelsPortlet.getLocation() });
		//			op.getPanelsPortlet().onContextMenu(op.getPanelsPortlet().getTablePortlet().getTable(), AmiWebViewObjectsPortlet.ACTION_SHOW_HISTORY);
		//}
	}

	private PortletManager getManager() {
		return this.graph.getManager();
	}

	private PortletConfig generateConfig() {
		return this.graph.generateConfig();
	}
	private void setSelectedNodes(Collection<Node> nodes, boolean select) {
		for (Node n : nodes) {
			n.setSelected(select);
		}
	}

	@Override
	public void onMenuDismissed() {
		// TODO Auto-generated method stub

	}

	@Override
	public WebMenu createMenu(GraphPortlet graph) {
		// TODO Auto-generated method stub
		return null;
	}

	//	@Override
	//	public boolean onButton(ConfirmDialogPortlet source, String id) {
	//		if (ConfirmDialogPortlet.ID_YES.equals(id)) {
	//			if ("DELETE".equals(source.getCallback())) {
	//				Map<String, AmiWebDm> todelete = (Map<String, AmiWebDm>) source.getCorrelationData();
	//				for (String s : todelete.keySet())
	//					this.service.getDmManager().removeDm(s);
	//			} else if ("confirm_delete".equals(source.getCallback())) {
	//				ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you REALLY sure you want to delete the selected datasource?",
	//						ConfirmDialogPortlet.TYPE_YES_NO);
	//				dialog.setCallback("reconfirm_delete");
	//				dialog.addDialogListener(this);
	//				dialog.updateButton(ConfirmDialogPortlet.ID_YES, "Yes, delete");
	//				getManager().showDialog("Confirmation", dialog);
	//			} else if ("reconfirm_delete".equals(source.getCallback())) {
	//				List<Node> selectedNodes = graph.getSelectedNodesList();
	//				if (selectedNodes.size() == 1) {
	//					AmiWebGraphNode selData = getData(selectedNodes.get(0));
	//					AmiWebDatasourceWrapper dsw = ((AmiWebGraphNode_Datasource) selData).getInner();
	//					if (dsw != null) {
	//						AmiCenterManageDatasourceRequest req = service.getPortletManager().getTools().nw(AmiCenterManageDatasourceRequest.class);
	//						req.setId(dsw.getId());
	//						req.setName(dsw.getName());
	//						req.setAdapter(dsw.getAdapter());
	//						req.setUrl(dsw.getUrl());
	//						req.setUsername(dsw.getUser());
	//						req.setOptions(dsw.getOptions());
	//						req.setDelete(true);
	//						AmiWebUtils.getService(getManager()).sendRequestToBackend(this, req);
	//						//					} else {
	//						//						getLayoutManager().removeInvalidDatasourceByName(selData.getId());
	//					}
	//				} else { // Delete multiple undefined datasources
	//					//					for (Node n : selectedNodes) {
	//					//						getLayoutManager().removeInvalidDatasourceByName(getData(n).getId());
	//					//					}
	//				}
	//			} else if ("SELECT_DM_TABLE".equals(source.getCallback())) {
	//				AmiWebDm dm = ((AmiWebGraphNode_Datamodel) getData(this.getContextNode())).getInner();
	//				AmiWebFilterPortlet filter = (AmiWebFilterPortlet) this.service.getDesktop().newPortlet(AmiWebFilterPortlet.Builder.ID,
	//						service.getDesktop().getDesktop().getAmiLayoutFullAlias());
	//				filter.setUsedDatamodel(dm.getAliasDotName(),
	//						(String) (source.getInputField() == null ? CH.first(dm.getResponseOutSchema().getTableNamesSorted()) : source.getInputFieldValue())); // Need to make user choose a table
	//				getManager().showDialog("Create Filter", new AmiWebFilterSettingsPortlet(generateConfig(), filter));
	//			}
	//		}
	//		return true;
	//	}

	//	@Override
	//	public void onBackendResponse(ResultMessage<Action> result) {
	//		Action action = result.getAction();
	//		LH.info(log, "Backend action: " + action);
	//		if (action instanceof AmiCenterManageDatasourceResponse) {
	//			AmiCenterManageDatasourceResponse response = (AmiCenterManageDatasourceResponse) action;
	//			if (!response.getOk())
	//				getManager().showAlert(response.getMessage(), response.getException());
	//		}
	//
	//	}

	//	@Override
	//	public void onAdded(AmiWebGraphNode node) {
	//		if (!onChanged(node)) {
	//			Collection<AmiWebGraphNode<?>> sink = new ArrayList<AmiWebGraphNode<?>>();
	//			AmiWebGraphHelper.getNeighboors(node, sink, true, true);
	//			for (AmiWebGraphNode<?> n : sink)
	//				if (onChanged(n))
	//					break;
	//		}
	//	}
	//
	//	private boolean onChanged(AmiWebGraphNode node) {
	//		//		if (!this.allNodes.containsKey(node.getUid()))
	//		//			return false;
	//
	//		IdentityHashSet<AmiWebGraphNode<?>> all = new IdentityHashSet<AmiWebGraphNode<?>>(this.origNodes);
	//		IdentityHashSet<AmiWebGraphNode<?>> selected = new IdentityHashSet<AmiWebGraphNode<?>>();
	//		for (Node i : graph.getSelectedNodes()) {
	//			AmiWebGraphNode data = getData(i);
	//			if (data != null)
	//				selected.add(data);
	//		}
	//		this.buildGraph(all, selected);
	//		return true;
	//	}

	private static byte WALK_SOURCE = 1;
	private static byte WALK_TARGET = 2;
	private static byte WALK_CHILDPANELS = 4;
	private static byte WALK_LINKS = 8;

	private void walkNodes(AmiWebGraphNode<?> t2, IdentityHashMap<AmiWebGraphNode<?>, Byte> nodes, byte walkMask) {
		if (t2 == null)
			return;
		Byte existing = nodes.get(t2);
		if (existing == null) {
			existing = 0;
			nodes.put(t2, walkMask);
		} else {
			nodes.put(t2, (byte) (walkMask | existing));
		}
		byte remaining = (byte) (walkMask & (~existing));
		if (remaining == 0)
			return;
		//		nodes.add(t2);
		final boolean walkSource = MH.allBits(remaining, WALK_SOURCE);
		final boolean walkTarget = MH.allBits(remaining, WALK_TARGET);
		final boolean walkChildPanels = MH.allBits(remaining, WALK_CHILDPANELS);
		final boolean walkLinks = MH.allBits(remaining, WALK_LINKS);
		switch (t2.getType()) {
			case AmiWebGraphNode.TYPE_FEED:
			case AmiWebGraphNode.TYPE_PROCESSOR: {
				break;
			}
			case AmiWebGraphNode.TYPE_DATAMODEL: {
				AmiWebGraphNode_Datamodel dm = (AmiWebGraphNode_Datamodel) t2;
				if (walkSource) {
					for (AmiWebGraphNode i : dm.getSourceDatamodels().values())
						walkNodes(i, nodes, WALK_SOURCE);
					for (AmiWebGraphNode i : dm.getSourceDatasources().values())
						walkNodes(i, nodes, WALK_SOURCE);
					for (AmiWebGraphNode i : dm.getSourceFilterPanels().values())
						walkNodes(i, nodes, WALK_SOURCE);
				}
				if (walkMask == 15) {
					for (AmiWebGraphNode_Link i : dm.getSourceLinks().values()) {
						//						walkNodes(i, nodes, WALK_SOURCE);
						walkNodes(i, nodes, (byte) 0);
						walkNodes(i.getSourcePanel(), nodes, (byte) 0);
					}
				}

				if (walkTarget) {
					for (AmiWebGraphNode i : dm.getTargetDatamodels().values())
						walkNodes(i, nodes, WALK_TARGET);
					for (AmiWebGraphNode i : dm.getTargetPanels().values())
						walkNodes(i, nodes, WALK_TARGET);
				}
				break;
			}
			case AmiWebGraphNode.TYPE_DATASOURCE: {
				AmiWebGraphNode_Datasource dm = (AmiWebGraphNode_Datasource) t2;
				if (walkTarget)
					for (AmiWebGraphNode i : dm.getTargetDatamodels().values())
						walkNodes(i, nodes, WALK_TARGET);
				break;
			}
			case AmiWebGraphNode.TYPE_PANEL: {
				AmiWebGraphNode_Panel dm = (AmiWebGraphNode_Panel) t2;
				if (walkSource) {
					for (AmiWebGraphNode i : dm.getSourceDatamodels().values())
						walkNodes(i, nodes, WALK_SOURCE);
				}
				if (walkMask == 15) {
					for (AmiWebGraphNode_Link i : dm.getTargetLinks().values()) {
						walkNodes(i, nodes, (byte) 0);
						walkNodes(i.getTargetPanel(), nodes, (byte) 0);
						walkNodes(i.getTargetDm(), nodes, (byte) 0);
					}
					for (AmiWebGraphNode_Link i : dm.getSourceLinks().values()) {
						walkNodes(i, nodes, (byte) 0);
						//						walkNodes(i.getSourcePanel(), nodes, (byte) 0);
					}
				}
				if (walkChildPanels)
					for (AmiWebGraphNode i : dm.getChildrenPanels().values())
						walkNodes(i, nodes, (byte) (WALK_SOURCE | WALK_CHILDPANELS | WALK_TARGET | WALK_LINKS));
				break;
			}
			//			case AmiWebGraphNode.TYPE_LINK: {
			//				AmiWebGraphNode_Link dm = (AmiWebGraphNode_Link) t2;
			//				if (walkSource)
			//					walkNodes(dm.getSourcePanel(), nodes, (byte) 0);
			//				if (walkTarget) {
			//					if (dm.getTargetDm() != null)
			//						walkNodes(dm.getTargetDm(), nodes, (byte) 0);
			//					//					else
			//					walkNodes(dm.getTargetPanel(), nodes, (byte) WALK_SOURCE);
			//				}
			//				break;
			//			}
		}
		if (t2 instanceof AmiWebGraphNodeRt) {
			AmiWebGraphNodeRt<?> rt = (AmiWebGraphNodeRt<?>) t2;
			if (walkSource) {
				for (AmiWebGraphNode i : rt.getSourceRealtimes().values())
					walkNodes(i, nodes, WALK_SOURCE);
			}
			if (walkTarget) {
				for (AmiWebGraphNode i : rt.getTargetRealtimes().values())
					walkNodes(i, nodes, WALK_TARGET);
			}
		}
	}

	public boolean canExplore(AmiWebGraphNode<?> data) {
		IdentityHashSet<AmiWebGraphNode<?>> sink = new IdentityHashSet<AmiWebGraphNode<?>>();
		AmiWebGraphHelper.getNeighboors(data, sink, true, true);
		for (AmiWebGraphNode i : sink) {
			if (i.getType() == AmiWebGraphNode.TYPE_LINK) {
				AmiWebGraphNode_Link link = (AmiWebGraphNode_Link) i;
				if (link.getSourcePanel() == data) {
					if (link.getTargetDm() != null) {
						if (!this.allNodes.containsKey(link.getTargetDm().getUid()))
							return true;
					} else if (link.getTargetPanel() != null && !this.allNodes.containsKey(link.getTargetPanel().getUid()))
						return true;
				} else if (link.getTargetPanel() == data || link.getTargetDm() == data) {
					if (link.getSourcePanel() != null && !this.allNodes.containsKey(link.getSourcePanel().getUid()))
						return true;
				}
			} else if (!this.allNodes.containsKey(i.getUid()))
				return true;
		}
		return false;
	}

	public AmiWebService getService() {
		return this.service;
	}

}
