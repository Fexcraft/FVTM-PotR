package net.fexcraft.mod.fvtm.compat.potr;

import net.minecraftforge.common.config.Config;

@Config(modid = FVTMPotR.MODID)
public class FVTMPotRConfig {
	
	@Config.Comment("Vehicle Emission Modifier")
	@Config.RangeInt(min = 0, max = 16)
	public static float VEHICLE_EMISSION_MODIFIER = 1f;
	
	@Config.Comment("Vehicle Emission Interval (server ticks)")
	@Config.RangeInt(min = 1, max = 100)
	public static int VEHICLE_EMISSION_INTERVAL = 10;
	
	@Config.Comment("Default Vehicle Engine Carbon Emission (when engine isn't configured for fvtm-potr)")
	@Config.RangeInt(min = 0, max = 16)
	public static float VEHICLE_ENGINE_CARBON_EMISSION = 0.5f;

	@Config.Comment("Default Vehicle Engine Sulfur Emission (when engine isn't configured for fvtm-potr)")
	@Config.RangeInt(min = 0, max = 16)
	public static float VEHICLE_ENGINE_SULFUR_EMISSION = 0.01f;

	@Config.Comment("Default Vehicle Engine Dust Emission (when engine isn't configured for fvtm-potr)")
	@Config.RangeInt(min = 0, max = 16)
	public static float VEHICLE_ENGINE_DUST_EMISSION = 0.5f;
	
}