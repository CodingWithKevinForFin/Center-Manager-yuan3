package com.f1.ami.web.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebService;
import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.MappingEntry;
import com.f1.base.Table;
import com.f1.base.ToStringable;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;

public class AmiWebDmTablesetSchema implements ToStringable, Lockable {
	final private HasherMap<String, AmiWebDmTableSchema> tableSchemas = new HasherMap<String, AmiWebDmTableSchema>();
	final private AmiWebService service;
	final private AmiWebAmiScriptCallback owner;
	private boolean locked;

	public AmiWebDmTablesetSchema(AmiWebService service, AmiWebAmiScriptCallback owner) {
		OH.assertNotNull(owner);
		this.service = service;
		this.owner = owner;
	}

	//copy constructor
	public AmiWebDmTablesetSchema(AmiWebDmTablesetSchema other, AmiWebAmiScriptCallback owner) {
		OH.assertNotNull(other);
		OH.assertNotNull(owner);
		this.service = other.getService();
		for (String i : other.getTableNames())
			this.tableSchemas.put(i, new AmiWebDmTableSchema(other.getTable(i), this));
		this.owner = owner;
	}

	public AmiWebDmTablesetSchema(AmiWebService service, AmiWebAmiScriptCallback owner, Tableset tableset) {
		this(service, owner);
		for (String s : tableset.getTableNames())
			addTableSchemaUsingTable(tableset.getTable(s));
	}

	public AmiWebDmsImpl getDatamodel() {
		return this.owner.getDatamodel();
	}

	public Tableset mapToSchema(Tableset request, boolean forceNew) {
		lock();
		Tableset r = new TablesetImpl();
		for (MappingEntry<String, AmiWebDmTableSchema> e : tableSchemas.entries())
			r.putTable(e.getKey(), e.getValue().mapToSchema(request == null ? null : request.getTableNoThrow(e.getKey()), forceNew));
		return r;
	}

	public void addTableSchema(String name, AmiWebDmTableSchema schema) {
		LockedException.assertNotLocked(this);
		OH.assertEqIdentity(schema.getTablesetSchema(), this);
		this.tableSchemas.put(name, schema);
	}
	private void addTableSchemaUsingTable(Table table) {
		LockedException.assertNotLocked(this);
		this.tableSchemas.put(table.getTitle(), new AmiWebDmTableSchema(service, table, this));
	}

	public List<String> getTableNamesSorted() {
		return tableSchemas.getKeysSorted();
	}

	public AmiWebDmTableSchema getTable(String tablename) {
		return tableSchemas.get(tablename);
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = new HashMap<String, Object>();
		List<Map<String, Object>> tables = new ArrayList<Map<String, Object>>(this.tableSchemas.size());
		for (String i : CH.sort(this.tableSchemas.keySet())) {
			AmiWebDmTableSchema schema = this.tableSchemas.get(i);
			tables.add(schema.getConfiguration());
		}
		r.put("tbl", tables);
		return r;
	}

	public void init(Map<String, Object> val) {
		if (val != null) {
			List<Map<String, Object>> tables = (List<Map<String, Object>>) val.get("tbl");
			for (Map<String, Object> i : tables) {
				AmiWebDmTableSchema tb = new AmiWebDmTableSchema(service, (String) i.get("nm"), this);
				tb.init(i);
				addTableSchema(tb.getName(), tb);
			}
		}
		lock();
	}

	public AmiWebAmiScriptCallback getCallback() {
		return this.owner;
	}

	private Set<String> getTableNames() {
		return tableSchemas.keySet();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		boolean first = true;
		for (AmiWebDmTableSchema i : this.tableSchemas.values()) {
			if (first)
				first = false;
			else
				sink.append(", ");
			i.toString(sink);
		}
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return this.locked;
	}

	public boolean isEmpty() {
		return this.tableSchemas.isEmpty();
	}

	public AmiWebService getService() {
		return this.service;
	}

}
