package net.fexcraft.mod.fvtm.compat.potr;

import com.endertech.common.KeyValuePair;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.mods.adpother.blocks.Pollutant;
import com.endertech.minecraft.mods.adpother.emissions.Emission;
import com.endertech.minecraft.mods.adpother.emissions.Emissions;
import com.endertech.minecraft.mods.adpother.init.Pollutants;
import com.endertech.minecraft.mods.adpother.sources.Emitter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VehicleEmitter extends Emitter {
	
	public static float CR, SR, DR;

	public VehicleEmitter(UnitConfig config, Emissions emissions){
		super(config, UnitId.EMPTY, KeyValuePair.from("fvtm:vehicle"), true, "#hardcoded", false, new String[0], emissions, 1);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos){
		//
		return true;
	}

	@Override
	public void emitAt(World world, BlockPos pos){
		super.emitAt(world, pos, FVTMPotRConfig.VEHICLE_EMISSION_MODIFIER);
	}

	@Override
	public void emitAt(World world, BlockPos pos, float factor){
		super.emitAt(world, pos, factor * FVTMPotRConfig.VEHICLE_EMISSION_MODIFIER);
	}
	
	@Override
	public float getEmissionOf(Pollutant<?> poll){
		float ret = 1f;
		if(poll == Pollutants.BuiltIn.CARBON.get()) ret = CR;
		if(poll == Pollutants.BuiltIn.SULFUR.get()) ret = SR;
		if(poll == Pollutants.BuiltIn.DUST.get()) ret = DR;
		return ret * Emission.globalRate;
	}
	
	public static void set(int div, float... vals){
		CR = vals[0] / div;
		SR = vals[1] / div;
		DR = vals[2] / div;
	}

}
