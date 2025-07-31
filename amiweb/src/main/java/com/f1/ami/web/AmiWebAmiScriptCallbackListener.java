package com.f1.ami.web;

import java.util.Set;

public interface AmiWebAmiScriptCallbackListener {

	void onInputDatamodelsAdded(AmiWebAmiScriptCallback amiWebAmiScriptCallback, Set<String> nuw);
	void onInputDatamodelsRemoved(AmiWebAmiScriptCallback amiWebAmiScriptCallback, Set<String> removed);
	void onInputDatamodelNameChanged(AmiWebAmiScriptCallback amiWebAmiScriptCallback, String old, String nuw);
}
