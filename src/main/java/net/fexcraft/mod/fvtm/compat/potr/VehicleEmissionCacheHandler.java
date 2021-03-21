package net.fexcraft.mod.fvtm.compat.potr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.fexcraft.mod.fvtm.data.part.PartData;
import net.fexcraft.mod.fvtm.data.vehicle.VehicleData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class VehicleEmissionCacheHandler implements ICapabilitySerializable<NBTBase>{
	
	private VehicleEmissionCache instance;
	
	public VehicleEmissionCacheHandler(){ instance = new Instance(); }

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		return capability != null && capability == VehicleEmissionCache.VEHICLE_CACHE;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		return capability != null && capability == VehicleEmissionCache.VEHICLE_CACHE ? VehicleEmissionCache.VEHICLE_CACHE.<T>cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT(){
		return VehicleEmissionCache.VEHICLE_CACHE.getStorage().writeNBT(VehicleEmissionCache.VEHICLE_CACHE, instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt){
		VehicleEmissionCache.VEHICLE_CACHE.getStorage().readNBT(VehicleEmissionCache.VEHICLE_CACHE, instance, null, nbt);
	}
	
	public static class Storage implements IStorage<VehicleEmissionCache> {

		@Override
		public NBTBase writeNBT(Capability<VehicleEmissionCache> capability, VehicleEmissionCache instance, EnumFacing side){
			NBTTagCompound compound = new NBTTagCompound();
			float[] vals = instance.accumulated();
			compound.setFloat("carbon", vals[0]);
			compound.setFloat("sulfur", vals[1]);
			compound.setFloat("dust", vals[2]);
			return compound;
		}

		@Override
		public void readNBT(Capability<VehicleEmissionCache> capability, VehicleEmissionCache instance, EnumFacing side, NBTBase nbt){
			if(nbt == null || nbt instanceof NBTTagCompound == false) return;
			NBTTagCompound compound = (NBTTagCompound)nbt;
			if(compound.isEmpty()) return;
			float[] vals = new float[3];
			vals[0] = compound.getFloat("carbon");
			vals[1] = compound.getFloat("sulfur");
			vals[2] = compound.getFloat("dust");
			instance.accumulate(vals);
		}
		
	}
	
	public static class Callable implements java.util.concurrent.Callable<VehicleEmissionCache> {

		@Override
		public VehicleEmissionCache call() throws Exception {
			return new Instance();
		}
		
	}
	
	public static class Instance implements VehicleEmissionCache {
		
		private float[] accumulated = new float[3];
		private ArrayList<EmissionEmitter> emitters = new ArrayList<>();
		private ArrayList<EmissionFilter> filters = new ArrayList<>();
		private ArrayList<String> emitpart = new ArrayList<>();
		private boolean setup;
		private int timer;

		public Instance(){}

		@Override
		public void accumulate(float[] vals){
			if(vals[0] > 0) accumulated[0] += vals[0];
			if(vals[1] > 0) accumulated[1] += vals[1];
			if(vals[2] > 0) accumulated[2] += vals[2];
		}

		@Override
		public float[] accumulated(){
			return accumulated;
		}

		@Override
		public void clearAccumutor(){
			accumulated[0] = 0;//accumulated[0] % 1;
			accumulated[1] = 0;//accumulated[1] % 1;
			accumulated[2] = 0;//accumulated[2] % 1;
		}

		@Override
		public void scanParts(VehicleData data){
			for(Entry<String, PartData> pd : data.getParts().entrySet()){
				if(pd.getValue().hasFunction(EmissionEmitter.ID)){
					emitters.add(pd.getValue().getFunction(EmissionEmitter.ID));
					emitpart.add(pd.getKey());
				}
				if(pd.getValue().hasFunction(EmissionFilter.ID)){
					filters.add(pd.getValue().getFunction(EmissionFilter.ID));
				}
			}
			setup = true;
		}

		@Override
		public List<EmissionEmitter> getEE(){
			return emitters;
		}

		@Override
		public List<String> getEEP(){
			return emitpart;
		}

		@Override
		public List<EmissionFilter> getEF(){
			return filters;
		}

		@Override
		public void clearFunctionCache(){
			emitters.clear();
			filters.clear();
		}

		@Override
		public boolean setup(){
			return setup;
		}

		@Override
		public int timer(){
			return timer;
		}

		@Override
		public void timer(int i){
			timer = i;
		}

		@Override
		public void filter(float[] vals, int idx){
			for(EmissionFilter filter : filters){
				if(filter.parts() == null || filter.parts().contains(emitpart.get(idx))){
					vals[0] -= filter.carbon();
					vals[1] -= filter.sulfur();
					vals[2] -= filter.dust();
				}
			}
			if(vals[0] < 0) vals[0] = 0;
			if(vals[1] < 0) vals[1] = 0;
			if(vals[2] < 0) vals[2] = 0;
		}
		
	}

}
