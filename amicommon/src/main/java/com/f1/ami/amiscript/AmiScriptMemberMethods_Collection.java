package com.f1.ami.amiscript;

import java.util.Collection;
import java.util.List;

import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Collection extends AmiScriptBaseMemberMethods<Collection> {

	private AmiScriptMemberMethods_Collection() {
		super();
		addMethod(ADD_ALL);
		addMethod(SIZE);
		addMethod(CLEAR);
		addMethod(SORT);
	}

	private final static AmiAbstractMemberMethod<Collection> ADD_ALL = new AmiAbstractMemberMethod<Collection>(Collection.class, "addAll", Object.class, Iterable.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Collection targetObject, Object[] params, DerivedCellCalculator caller) {
			Iterable<?> i = (Iterable<?>) params[0];
			if (i != null)
				for (Object o : i)
					targetObject.add(o);
			return targetObject;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "valuesToAdd" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "the values to add to this collection" };
		}
		@Override
		protected String getHelp() {
			return "adds all elements to this collection";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private final static AmiAbstractMemberMethod<Collection> CLEAR = new AmiAbstractMemberMethod<Collection>(Collection.class, "clear", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Collection targetObject, Object[] params, DerivedCellCalculator caller) {
			if (targetObject.size() == 0)
				return false;
			targetObject.clear();
			return true;
		}
		@Override
		protected String getHelp() {
			return "Removes all elements in this collection. Returns true if this operation removed at least 1 element, returns false if it was empty";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private final static AmiAbstractMemberMethod<Collection> SIZE = new AmiAbstractMemberMethod<Collection>(Collection.class, "size", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Collection targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.size();
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String getHelp() {
			return "Returns the size of this collection";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Collection> SORT = new AmiAbstractMemberMethod<Collection>(Collection.class, "sort", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Collection targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return CH.sort(targetObject);
			} catch (Exception e) {
				return CH.l(targetObject);
			}
		}
		@Override
		protected String getHelp() {
			return "Returns a list with the specified collection sorted according the elements' natural ordering. This sort is guaranteed to be stable: equal elements will not be reordered as a result of the sort";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Collection";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<Collection> getVarType() {
		return Collection.class;
	}
	@Override
	public Class<Collection> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Collection INSTANCE = new AmiScriptMemberMethods_Collection();
}
