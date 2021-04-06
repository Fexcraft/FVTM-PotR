package net.fexcraft.mod.fvtm.compat.potr;

import static net.fexcraft.mod.fvtm.compat.potr.FVTMPotRConfig.VEHICLE_EMISSION_INTERVAL;
import static net.fexcraft.mod.fvtm.compat.potr.FVTMPotRConfig.VEHICLE_ENGINE_CARBON_EMISSION;
import static net.fexcraft.mod.fvtm.compat.potr.FVTMPotRConfig.VEHICLE_ENGINE_DUST_EMISSION;
import static net.fexcraft.mod.fvtm.compat.potr.FVTMPotRConfig.VEHICLE_ENGINE_SULFUR_EMISSION;

import java.util.List;

import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.mod.fvtm.data.attribute.Attribute;
import net.fexcraft.mod.fvtm.data.part.Function;
import net.fexcraft.mod.fvtm.event.ResourceEvents;
import net.fexcraft.mod.fvtm.event.TypeEvents;
import net.fexcraft.mod.fvtm.sys.uni.GenericVehicle;
import net.fexcraft.mod.fvtm.util.function.EngineFunction;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber
public class Events {
	
	@SubscribeEvent
	public static void regFunc(ResourceEvents.RegisterFunctions event){
		event.registerFunction("fvtm_potr:emission_emitter", EmissionEmitter.class);
		event.registerFunction("fvtm_potr:emission_filter", EmissionFilter.class);
	}
	
	@SubscribeEvent
	public static void checkEngines(TypeEvents.PartCreated event){
		if(containsEngineButNotEmitter(event.getType().getDefaultFunctions())){
			event.getType().getDefaultFunctions().add(new EmissionEmitter(event.getType(), true,
				VEHICLE_ENGINE_CARBON_EMISSION, VEHICLE_ENGINE_SULFUR_EMISSION, VEHICLE_ENGINE_DUST_EMISSION));
		}
	}
	
	private static boolean containsEngineButNotEmitter(List<Function> defs){
		boolean bool = false;
		for(Function func : defs){
			if(func.getId().equals("fvtm:engine")){
				bool = true;
				break;
			}
		}
		if(bool){
			for(Function func : defs){
				if(func.getId().equals(EmissionEmitter.ID)){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event){
		if(event.getObject().world == null || event.getObject().world.isRemote) return;
		if(event.getObject() instanceof GenericVehicle){
			event.addCapability(new ResourceLocation("fvtm-potr:emission_cache"), new VehicleEmissionCacheHandler());
		}
	}
	
	@SubscribeEvent
	public static void worldTick(WorldTickEvent event){
		if(event.side == Side.CLIENT || event.phase == Phase.START) return;
		for(Entity entity : event.world.loadedEntityList){
			if(entity instanceof GenericVehicle){
				runEmissionChecks((GenericVehicle)entity);
			}
		}
	}

	private static void runEmissionChecks(GenericVehicle vehicle){
		VehicleEmissionCache cache = vehicle.getCapability(VehicleEmissionCache.VEHICLE_CACHE, null);
		if(cache == null || vehicle.getVehicleData() == null) return;
		if(!cache.setup()) cache.scanParts(vehicle.getVehicleData());
		if(vehicle.getVehicleData().hasPart("engine")){
			EngineFunction func = vehicle.getVehicleData().getFunctionInPart("engine", "fvtm:engine");
			if(func.isOn()){
				float[] engi = new float[3];;
				float fuel = vehicle.getVehicleData().getAttributeFloat("fuel_quality", 1);
				if(fuel > 1) fuel = 1;
				if(fuel <= 1) fuel = 1 - fuel;
				float throttle = (float)vehicle.throttle;
				if(throttle < 0) throttle = Math.abs(throttle);
				if(throttle < 0.1) throttle = 0.1f; 
				for(int i = 0; i < cache.getEE().size(); i++){
					EmissionEmitter em = cache.getEE().get(i);
					if(!em.engine()) continue;
					engi[0] = em.carbon() * throttle;
					engi[0] += engi[0] * fuel;
					engi[1] = em.sulfur() * throttle;
					engi[1] += engi[1] * fuel;
					engi[2] = em.dust() * throttle;
					engi[2] += engi[2] * fuel;
					cache.filter(engi, i);
				}
				cache.accumulate(engi);
			}
		}
		float[] vals = new float[3];
		for(int i = 0; i < cache.getEE().size(); i++){
			EmissionEmitter em = cache.getEE().get(i);
			if(em.engine()) continue;
			vals[0] = em.carbon();
			vals[1] = em.sulfur();
			vals[2] = em.dust();
			cache.filter(vals, i);
		}
		cache.accumulate(vals);
		if(cache.timer() >= VEHICLE_EMISSION_INTERVAL){
			pollute(vehicle.world, vehicle, cache.accumulated(), VEHICLE_EMISSION_INTERVAL);
			cache.timer(0);
			cache.clearAccumutor();
		}
		else cache.timer(cache.timer() + 1);
	}
	
	private static BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
	
	private static void pollute(World world, GenericVehicle vehicle, float[] accumulated, int div){
		List<Attribute<?>> list = vehicle.getVehicleData().getAttributeGroup("exhaust");
		if(list.isEmpty()){
			VehicleEmitter.set(div, accumulated);
			FVTMPotR.VECHILE.emitAt(world, pos.setPos(vehicle));
		}
		else{
			Vec3d temp = null;
			Vec3f vec = null;
			if(list.size() > 1){
				accumulated[0] /= list.size();
				accumulated[1] /= list.size();
				accumulated[2] /= list.size();
			}
			VehicleEmitter.set(div, accumulated);
			for(Attribute<?> attr : list){
				vec = attr.vector_value();
				temp = vehicle.getRotPoint().getRelativeVector(vec.x * Static.sixteenth, -vec.y * Static.sixteenth, -vec.z * Static.sixteenth);
				temp = temp.add(vehicle.getPositionVector());
				FVTMPotR.VECHILE.emitAt(world, pos.setPos(temp.x, temp.y, temp.z));
				/*ChunkPollution pollution = WorldData.getChunkPollution(world, new BlockPos(temp.x, temp.y, temp.z));
				pollution.getInfos().forEach(info -> {
					Print.debug(info.getAmount(), info.getPollutant().getRegistryName());
				});*/
			}
		}
	}

}
