package com.f1.ami.webbalancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.TextMatcherFactory;

public class AmiWebBalancerRouteRule {

	private static final TextMatcherFactory TEXT_MATCHER_FACTORY = new TextMatcherFactory(true, false, false);
	private static final Logger log = LH.get();
	final private LinkedHashMap<String, Double> targetWeights = new LinkedHashMap<String, Double>();
	final private List<AmiWebBalancerServerInstance> targetInstances = new ArrayList<AmiWebBalancerServerInstance>();
	final private String rule;
	final private TextMatcher ruleMatcher;
	final private boolean shouldContinue;

	public AmiWebBalancerRouteRule(String rule, String targets, boolean shouldContinue) {
		this.rule = rule;
		this.ruleMatcher = TEXT_MATCHER_FACTORY.toMatcher(rule);
		for (String line : SH.split(',', targets)) {
			line = SH.trim(line);
			String weightStr = SH.beforeFirst(line, '*', null);
			double weight;
			if (SH.is(weightStr)) {
				try {
					weight = SH.parseDouble(weightStr);
				} catch (Exception e) {
					LH.warning(log, "For Rule '", rule, "' Ignoring target with invalid weight: ", line);
					continue;
				}
				if (weight <= 0) {
					LH.warning(log, "For Rule '", rule, "' Ignoring target with invalid weight: ", line);
					continue;
				}
			} else
				weight = 1.0d;
			String s = SH.afterFirst(line, '*', line);
			Object hp = AmiWebBalancerServerInstance.parseHostPort(s);
			if (hp == null) {
				LH.warning(log, "For Rule '", rule, "' Ignoring invalid target: ", line);
				continue;
			}
			this.targetWeights.put(s, weight);
		}
		this.shouldContinue = shouldContinue;
	}

	public boolean matches(String client) {
		return this.ruleMatcher.matches(client);
	}

	public Set<String> getTargets() {
		return this.targetWeights.keySet();
	}
	public String getRule() {
		return this.rule;
	}
	public void bindTargets(HashMap<String, AmiWebBalancerServerInstance> availableServersByNames) {
		this.targetInstances.clear();
		for (String i : this.targetWeights.keySet())
			this.targetInstances.add(availableServersByNames.get(i));

	}

	int roundRobin = 0;

	public AmiWebBalancerServerInstance getBestTarget() {
		AmiWebBalancerServerInstance r = null;
		Double rWeight = null;
		int size = this.targetInstances.size();
		for (int n = 0; n < size; n++) {
			AmiWebBalancerServerInstance i = this.targetInstances.get((n + roundRobin) % size);
			if (!i.isAlive())
				continue;
			Double weight = this.targetWeights.get(i.getHostPort());
			if (weight == null) {
				LH.warning(log, "Defaulting weight to 1 for missing host: ", i.getHostPort(), ", options are: ", this.targetWeights);
				weight = 1d;
			}
			if ((r == null) //first live session
					|| (i.getActiveSessionsCount() / weight < r.getActiveSessionsCount() / rWeight)//this server has less users after weighting
					|| (i.getActiveSessionsCount() == 0 && r.getActiveSessionsCount() == 0 && weight > rWeight)) {//niether server has any users, pick one with higher weighting
				rWeight = weight;
				r = i;
			}
		}
		roundRobin++;
		return r;
	}

	public boolean shouldContinue() {
		return shouldContinue;
	}

	public boolean hasTargetThatsAlive() {
		int size = this.targetInstances.size();
		for (int n = 0; n < size; n++) {
			AmiWebBalancerServerInstance i = this.targetInstances.get((n + roundRobin) % size);
			if (i.isAlive())
				return true;
		}
		return false;
	}
}
