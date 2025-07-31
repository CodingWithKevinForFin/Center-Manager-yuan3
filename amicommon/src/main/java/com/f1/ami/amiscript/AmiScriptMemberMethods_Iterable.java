package com.f1.ami.amiscript;

import java.util.Iterator;

import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Iterable extends AmiScriptBaseMemberMethods<Iterable> {

	private AmiScriptMemberMethods_Iterable() {
		super();

		addMethod(ITERATOR);
	}

	private static final AmiAbstractMemberMethod<Iterable> ITERATOR = new AmiAbstractMemberMethod<Iterable>(Iterable.class, "iterator", Iterator.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Iterable targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.iterator();
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
			return "Returns an iterator over this containers objects";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Iterable";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<Iterable> getVarType() {
		return Iterable.class;
	}
	@Override
	public Class<Iterable> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Iterable INSTANCE = new AmiScriptMemberMethods_Iterable();
}
