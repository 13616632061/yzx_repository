package com.ms.SafeEdit;

public interface SoftKeyStatusListener {

	public void onPressed(SoftKey softKey);
	
	public void onDeleted();
	
	public void onConfirm();
	
}
