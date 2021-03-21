package net.fexcraft.mod.fvtm.compat.potr;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.mods.adpother.Main;
import com.endertech.minecraft.mods.adpother.emissions.Emissions;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FVTMPotR.MODID, name = FVTMPotR.NAME, version = FVTMPotR.VERSION)
public class FVTMPotR {
	
    public static final String MODID = "fvtm_potr";
    public static final String NAME = "FVTM PotR Compat";
    public static final String VERSION = "1.0";
    private static Logger logger;
    //
    public static VehicleEmitter VECHILE;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        logger.log(Level.INFO, "FVTM > Pollution of the Realms > Compat > Active");
		CapabilityManager.INSTANCE.register(VehicleEmissionCache.class, new VehicleEmissionCacheHandler.Storage(), new VehicleEmissionCacheHandler.Callable());
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
    	Emissions emissions = Emissions.of();
    	emissions.carbon(1f);
    	emissions.sulfur(1f);
    	emissions.dust(1f);
        Main.getEmitters().add(VECHILE = new VehicleEmitter(new UnitConfig(new File("./config/fvtm/potr_vehicle.cfg")), emissions));
    }
    
	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
	}
    
}
