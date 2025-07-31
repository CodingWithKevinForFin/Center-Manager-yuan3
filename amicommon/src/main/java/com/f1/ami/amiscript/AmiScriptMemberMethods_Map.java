package com.f1.ami.amiscript;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Map extends AmiScriptBaseMemberMethods<Map> {

	private AmiScriptMemberMethods_Map() {
		super();

		addMethod(INIT);
		addMethod(PUT);
		addMethod(PUT_ALL);
		addMethod(GET);
		addMethod(SIZE);
		addMethod(REMOVE);
		addMethod(GET_KEYS);
		addMethod(GET_VALUES);
		addMethod(CONTAINS_KEY);
		addMethod(CONTAINS_VALUE);
		addMethod(CLEAR);
		addMethod(JSON_PATH);
	}

	private static final AmiAbstractMemberMethod<Map> INIT = new AmiAbstractMemberMethod<Map>(Map.class, null, Object.class, true, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			if ((params.length & 1) == 1)
				throw new ExpressionParserException(caller.getPosition(), "Constructor expecting even number of arguments");
			LinkedHashMap r = new LinkedHashMap(Math.max(10, params.length / 2));
			for (int i = 0; i < params.length - 1;)
				r.put(params[i++], params[i++]);
			return r;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key_values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "initialize list with arguments" };
		}
		@Override
		protected String getHelp() {
			return "Init list";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Map> PUT = new AmiAbstractMemberMethod<Map>(Map.class, "put", Object.class, Object.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.put(params[0], params[1]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String getHelp() {
			return "Adds the specified key value pair to this map and returns old value associated with the key. Returns null if key does not exist.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Map> PUT_ALL = new AmiAbstractMemberMethod<Map>(Map.class, "putAll", Object.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.putAll((Map) params[0]);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "m" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "map" };
		}
		@Override
		protected String getHelp() {
			return "Copies all the mappies of the target map to this map";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<Map> GET = new AmiAbstractMemberMethod<Map>(Map.class, "get", Object.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.get(params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with the given key. Returns null if key does not exist.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Map> REMOVE = new AmiAbstractMemberMethod<Map>(Map.class, "remove", Object.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.remove(params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Removes the given key and returns the value associated with the key. Returns null if the key does not exist.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<Map> GET_KEYS = new AmiAbstractMemberMethod<Map>(Map.class, "getKeys", Set.class) {
		@Override
		public Set invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			return new LinkedHashSet(targetObject.keySet());
		}
		@Override
		protected String getHelp() {
			return "Returns the keys as a set.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Map> GET_VALUES = new AmiAbstractMemberMethod<Map>(Map.class, "getValues", List.class) {
		@Override
		public List invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			return new ArrayList(targetObject.values());
		}
		@Override
		protected String getHelp() {
			return "Returns the values as a list.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<Map> CLEAR = new AmiAbstractMemberMethod<Map>(Map.class, "clear", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.size() == 0)
				return false;
			targetObject.clear();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Removes all the mappings from this map. Returns true if the map is changed as a result.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<Map> SIZE = new AmiAbstractMemberMethod<Map>(Map.class, "size", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.size();
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the number of key/value pairs in this map.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<Map> CONTAINS_KEY = new AmiAbstractMemberMethod<Map>(Map.class, "containsKey", Boolean.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.containsKey(params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Returns true if this map contains the specified key. ";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<Map> CONTAINS_VALUE = new AmiAbstractMemberMethod<Map>(Map.class, "containsValue", Boolean.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.containsValue(params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value" };
		}
		@Override
		protected String getHelp() {
			return "Returns true if this map contains the specified value. ";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Map";
	}

	@Override
	public String getVarTypeDescription() {
		return "A mutable collection of unique keys which are mapped to values. Attempts to add duplicates will override existing key-value pairs.  Entries are stored in the order inserted (Backed by java LinkedHashMap)";
	}

	@Override
	public Class<Map> getVarType() {
		return Map.class;
	}

	@Override
	public Class<? extends Map> getVarDefaultImpl() {
		return LinkedHashMap.class;
	}

	private static final AmiAbstractMemberMethod<Map> JSON_PATH = new AmiAbstractMemberMethod<Map>(Map.class, "jsonPath", Object.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Map targetObject, Object[] params, DerivedCellCalculator caller) {
			String s = (String) params[0];
			if (s == null)
				return targetObject;
			return RootAssister.INSTANCE.getNestedValue(targetObject, s, false);
		}
		protected String[] buildParamNames() {
			return new String[] { "jsonPath" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "dot(.) delimited path Ex: " + SH.doubleQuote("portletConfigs.0.portletConfig.dm.0") };
		}

		@Override
		protected String getHelp() {
			return "Walks the JSON path and returns the object at the end of the path. Returns itself if path is null. Use dot(.) to delimit path. Use number to traverse list element and key to traverse map. ";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static AmiScriptMemberMethods_Map INSTANCE = new AmiScriptMemberMethods_Map();
}
