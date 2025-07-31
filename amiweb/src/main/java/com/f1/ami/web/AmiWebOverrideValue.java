package com.f1.ami.web;

import com.f1.base.Clearable;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebOverrideValue<T> implements Clearable {

	private boolean isOverride = false;
	private T value = null;
	private T oValue = null;

	public AmiWebOverrideValue(T value) {
		this.value = value;
	}

	//The materialized value
	public T get() {
		return isOverride ? oValue : value;
	}

	public T getOverride() {
		return oValue;
	}

	//return the original value (regardless of override)
	public T getValue() {
		return value;
	}

	public boolean isOverride() {
		return this.isOverride;
	}

	//returns true if materialized value changed
	public boolean set(T value, boolean clearOverride) {
		T orig = get();
		this.value = value;
		if (clearOverride && this.isOverride) {
			this.oValue = null;
			this.isOverride = false;
		}
		return OH.ne(orig, get());
	}

	//returns true if materialized value changed
	public boolean setOverride(T value) {
		// old value
		T orig = get();
		isOverride = true;
		this.oValue = value;
		// compare old value to new value
		return OH.ne(orig, get());
	}
	public boolean setOverrideOrClearIfSame(T value) {
		// old value
		T orig = get();
		if (OH.eq(this.value, value)) {
			if (!isOverride)
				return false;
			this.isOverride = false;
			this.oValue = null;
			return OH.ne(orig, get());
		}
		isOverride = true;
		this.oValue = value;
		// compare old value to new value
		return OH.ne(orig, get());
	}

	//returns true if materialized value changed
	public boolean clearOverride() {
		if (!isOverride)
			return false;
		T orig = get();
		this.oValue = null;
		this.isOverride = false;
		return OH.ne(orig, get());
	}

	@Override
	public void clear() {
		this.oValue = this.value = null;
		this.isOverride = false;
	}

	@Override
	public String toString() {
		if (!isOverride)
			return SH.toString(value);
		else {
			return oValue + " (Overriding " + value + ")";
		}
	}

	public static boolean clearOverrides(AmiWebOverrideValue<?>... values) {
		boolean r = false;
		for (AmiWebOverrideValue<?> t : values)
			if (t.clearOverride())
				r = true;
		return r;
	}

	//If true, then return the 'materialized' aka 'current' value.  If false, return the default value
	public T getValue(boolean override) {
		return override && this.isOverride ? this.oValue : this.value;
	}

	//if true, then just override the value (not effecting the actual underlying original value) if false then clear the override and update the default value
	//returns true if the materialized value changed as a result
	public boolean setValue(T value, boolean override) {
		return override ? this.setOverrideOrClearIfSame(value) : this.set(value, true);
	}

	//this clears out the override
	public boolean setOverrideToDefault() {
		return isOverride && this.set(this.oValue, true);
	}

}
