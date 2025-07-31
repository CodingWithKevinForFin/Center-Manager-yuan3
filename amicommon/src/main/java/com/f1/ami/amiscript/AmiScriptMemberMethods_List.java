package com.f1.ami.amiscript;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.CH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_List extends AmiScriptBaseMemberMethods<List> {

	private AmiScriptMemberMethods_List() {
		super();

		addMethod(INIT);
		addMethod(GET);
		addMethod(REMOVE);
		addMethod(SET);
		addMethod(INDEX_OF);
		addMethod(ADD);
		addMethod(SPLICE);
		addMethod(BATCH);
		addMethod(CONTAINS);
		addMethod(JSON_PATH);
	}

	private static final AmiAbstractMemberMethod<List> INIT = new AmiAbstractMemberMethod<List>(List.class, null, Object.class, true, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {

				ArrayList r = new ArrayList(Math.max(10, params.length));
				for (int i = 0; i < params.length; i++)
					r.add(params[i]);
				return r;
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "list_elements" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Element to be added in list construction" };
		}
		@Override
		protected String getHelp() {
			return "Initialize a list with specified elements";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<List> CONTAINS = new AmiAbstractMemberMethod<List>(List.class, "contains", Boolean.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Object value = (Object) params[0];
				return targetObject.contains(value);
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
			return new String[] { "element" };
		}
		@Override
		protected String getHelp() {
			return "Returns true if the element is in the list.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<List> INDEX_OF = new AmiAbstractMemberMethod<List>(List.class, "indexOf", Integer.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Object value = (Object) params[0];
				return targetObject.indexOf(value);
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
			return new String[] { "element" };
		}
		@Override
		protected String getHelp() {
			return "Returns the index of the first occurrence of the specified element is in this list, returns -1 if no such index exists.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	private static final AmiAbstractMemberMethod<List> GET = new AmiAbstractMemberMethod<List>(List.class, "get", Object.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Integer index = (Integer) params[0];
				if (index == null || index < 0 || index >= targetObject.size())
					return null;
				return targetObject.get(index);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero based index" };
		}
		@Override
		protected String getHelp() {
			return "Returns the value at the supplied index or null if index is null, or out of bound.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<List> BATCH = new AmiAbstractMemberMethod<List>(List.class, "batch", List.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Integer batchSize = (Integer) params[0];
				return CH.batchSublists(targetObject, batchSize, false);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "batchSize" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the maximum size of each batch" };
		}
		@Override
		protected String getHelp() {
			return "Divides the list into batches (a list of lists), based on the supplied batch size. For example, a list of 13 elements batched using 5 would result in a list of three lists, first list containing elements 1..5, a second list of elements 6..10 and the final third list containing the remaining elements 11..13.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<List> ADD = new AmiAbstractMemberMethod<List>(List.class, "add", Object.class, true, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			for (Object i : params)
				targetObject.add(i);
			return null;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "ordered list of values" };
		}
		@Override
		protected String getHelp() {
			return "Adds the values to the end of the list.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<List> SET = new AmiAbstractMemberMethod<List>(List.class, "set", Object.class, Integer.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Integer index = (Integer) params[0];
				Object element = (Object) params[1];
				return targetObject.set((int) index, element);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "index", "element" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position", "value" };
		}
		@Override
		protected String getHelp() {
			return "Replaces the element at the specified position in this list with the specified element. Returns the replaced element.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<List> REMOVE = new AmiAbstractMemberMethod<List>(List.class, "remove", Object.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				Integer index = (Integer) params[0];
				if (index == null || index < 0 || index >= targetObject.size())
					return null;
				return targetObject.remove((int) index);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "index" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "position of value to remove" };
		}
		@Override
		protected String getHelp() {
			return "Removes the value at the index. Returns the removed element.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<List> SPLICE = new AmiAbstractMemberMethod<List>(List.class, "splice", Integer.class, true, Integer.class, Integer.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
			int index = (Integer) params[0];
			int howmany = (Integer) params[1];
			CH.splice(targetObject, index, howmany, params, 2, params.length);
			return targetObject.size();
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "index", "howmany", "values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "zero based index", "number of items to replace", "values to use in replacement" };
		}
		@Override
		protected String getHelp() {
			return "Inserts, deletes or replaces items in this list. Returns ths size of the resulting list.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	@Override
	public String getVarTypeName() {
		return "List";
	}
	@Override
	public String getVarTypeDescription() {
		return "An ordered collection of data used for random access based on position. Entries are stored in the order added (Backed by Java ArrayList)";
	}
	@Override
	public Class<List> getVarType() {
		return List.class;
	}
	@Override
	public Class<? extends List> getVarDefaultImpl() {
		return ArrayList.class;
	}

	private static final AmiAbstractMemberMethod<List> JSON_PATH = new AmiAbstractMemberMethod<List>(List.class, "jsonPath", Object.class, false, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, List targetObject, Object[] params, DerivedCellCalculator caller) {
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
			return new String[] { "dot(.) delimited path" };
		}

		@Override
		protected String getHelp() {
			return "returns the json at the path, null string is this object, use dot to delimit path";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	public static AmiScriptMemberMethods_List INSTANCE = new AmiScriptMemberMethods_List();
}
