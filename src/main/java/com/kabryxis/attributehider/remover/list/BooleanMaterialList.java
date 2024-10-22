package com.kabryxis.attributehider.remover.list;

import org.bukkit.Material;

public class BooleanMaterialList implements MaterialList {
	
	private final boolean shouldRemove;
	
	public BooleanMaterialList(boolean shouldRemove) {
		this.shouldRemove = shouldRemove;
	}
	
	@Override
	public boolean shouldRemove(Material type) {
		return shouldRemove;
	}
	
}
