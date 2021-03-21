package net.fexcraft.mod.fvtm.compat.potr;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.mod.fvtm.data.part.Function.StaticFunction;
import net.fexcraft.mod.fvtm.data.part.Part;

public class EmissionFilter extends StaticFunction {
	
	public static final String ID = "fvtm_potr:emission_filter";
	private float carbon = 1, sulfur = 0.1f, dust = 0.1f;
	private ArrayList<String> parts;

	public EmissionFilter(Part part, JsonObject obj){
		super(part, obj);
		if(obj == null) return;
		JsonArray array = obj.has("emissions") ? obj.get("emissions").getAsJsonArray() : new JsonArray();
		carbon = array.size() >= 0 ? array.get(0).getAsFloat() : carbon;
		sulfur = array.size() >= 1 ? array.get(1).getAsFloat() : sulfur;
		dust = array.size() >= 2 ? array.get(2).getAsFloat() : dust;
		parts = obj.has("parts") ? JsonUtil.jsonArrayToStringArray(obj.get("parts").getAsJsonArray()) : null;
	}

	@Override
	public String getId(){
		return ID;
	}
	
	public ArrayList<String> parts(){
		return parts;
	}

	public float carbon(){
		return carbon;
	}

	public float sulfur(){
		return sulfur;
	}

	public float dust(){
		return dust;
	}

}
