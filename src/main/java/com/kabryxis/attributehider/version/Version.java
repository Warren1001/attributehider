package com.kabryxis.attributehider.version;

import org.bukkit.Bukkit;

public class Version {
	
	public final static int UNSUPPORTED = -1;
	
	public final static int v1_8_R1 = 0;
	public final static int v1_8_R2 = 1;
	public final static int v1_8_R3 = 2;
	public final static int v1_9_R1 = 3;
	public final static int v1_9_R2 = 4;
	public final static int v1_10_R1 = 5;
	public final static int v1_11_R1 = 6;
	public final static int v1_12_R1 = 7;
	
	public final static int VERSION;
	
	public final static String STRING;
	
	static {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		STRING = version.substring(version.lastIndexOf('.') + 1);
		switch(STRING) {
		case "v1_8_R1":
			VERSION = v1_8_R1;
			break;
		case "v1_8_R2":
			VERSION = v1_8_R2;
			break;
		case "v1_8_R3":
			VERSION = v1_8_R3;
			break;
		case "v1_9_R1":
			VERSION = v1_9_R1;
			break;
		case "v1_9_R2":
			VERSION = v1_9_R2;
			break;
		case "v1_10_R1":
			VERSION = v1_10_R1;
			break;
		case "v1_11_R1":
			VERSION = v1_11_R1;
			break;
		case "v1_12_R1":
			VERSION = v1_12_R1;
			break;
		default:
			VERSION = UNSUPPORTED;
			break;
		}
	}
	
}
