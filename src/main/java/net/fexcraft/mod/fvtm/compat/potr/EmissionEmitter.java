package net.fexcraft.mod.fvtm.compat.potr;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.mod.fvtm.data.part.Function.StaticFunction;
import net.fexcraft.mod.fvtm.data.part.Part;

public class EmissionEmitter extends StaticFunction {
	
	public static final String ID = "fvtm_potr:emission_emitter";
	private float carbon = 1, sulfur = 0.1f, dust = 0.1f;
	private boolean engine;

	public EmissionEmitter(Part part, JsonObject obj){
		super(part, obj);
		if(obj == null) return;
		JsonArray array = obj.has("emissions") ? obj.get("emissions").getAsJsonArray() : new JsonArray();
		carbon = array.size() >= 0 ? array.get(0).getAsFloat() : carbon;
		sulfur = array.size() >= 1 ? array.get(1).getAsFloat() : sulfur;
		dust = array.size() >= 2 ? array.get(2).getAsFloat() : dust;
		engine = JsonUtil.getIfExists(obj, "engine", true);
	}

	public EmissionEmitter(Part type, boolean engine, float... vals){
		super(type, null);
		this.engine = engine;
		carbon = vals[0];
		sulfur = vals[1];
		dust = vals[2];
	}

	@Override
	public String getId(){
		return ID;
	}
	
	public boolean engine(){
		return engine;
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
