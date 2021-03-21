package net.fexcraft.mod.fvtm.compat.potr;

import java.util.List;

import net.fexcraft.mod.fvtm.data.vehicle.VehicleData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * Capability to hold temporary animation data.
 */
public interface VehicleEmissionCache {
	
	@CapabilityInject(VehicleEmissionCache.class)
	public static final Capability<VehicleEmissionCache> VEHICLE_CACHE = null;
	
	public void accumulate(float[] vals);
	
	public float[] accumulated();
	
	public void clearAccumutor();
	
	public void scanParts(VehicleData data);
	
	public void clearFunctionCache();
	
	public List<EmissionEmitter> getEE();
	
	public List<String> getEEP();
	
	public List<EmissionFilter> getEF();

	public boolean setup();

	public int timer();

	public void timer(int i);

	public void filter(float[] engi, int idx);

}
