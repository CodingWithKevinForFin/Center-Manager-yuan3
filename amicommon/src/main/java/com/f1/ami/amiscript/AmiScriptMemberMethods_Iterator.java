package com.f1.ami.amiscript;

import java.util.Iterator;

import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Iterator extends AmiScriptBaseMemberMethods<Iterator> {

	private AmiScriptMemberMethods_Iterator() {
		super();

		addMethod(NEXT);
		addMethod(HAS_NEXT, "hasNext");
	}

	private static final AmiAbstractMemberMethod<Iterator> NEXT = new AmiAbstractMemberMethod<Iterator>(Iterator.class, "next", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Iterator targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.next();
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Returns the next value from the iterator";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<Iterator> HAS_NEXT = new AmiAbstractMemberMethod<Iterator>(Iterator.class, "hasNext", Object.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Iterator targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.hasNext();
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Returns true if the iteration has more elements.(In other words, returns true if next would return an element rather than throwing an exception.)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Iterator";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<Iterator> getVarType() {
		return Iterator.class;
	}
	@Override
	public Class<Iterator> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Iterator INSTANCE = new AmiScriptMemberMethods_Iterator();
}
