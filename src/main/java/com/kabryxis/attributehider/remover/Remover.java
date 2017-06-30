package com.kabryxis.attributehider.remover;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import com.kabryxis.attributehider.AttributeHider;

public abstract class Remover {
	
	protected final AttributeHider plugin;
	protected final Field field;
	
	public Remover(AttributeHider plugin, Field field) {
		this.plugin = plugin;
		this.field = field;
	}
	
	public abstract void remove(Player player);
	
}
