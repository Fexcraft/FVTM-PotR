package net.fexcraft.mod.fvtm.compat.potr;

import net.fexcraft.lib.mc.api.registry.fCommand;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.lib.mc.utils.Static;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

@fCommand
public class Command extends CommandBase {
	

    @Override
    public String getName(){
        return "fvtm-potr";
    }

    @Override
    public String getUsage(ICommandSender sender){
        return "commands.fvtm_potr.main_usage";
    }

    public String trs(String string){
        return I18n.format(string, new Object[0]);
    }
    
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender){
        return sender instanceof EntityPlayer;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length < 1){
            Print.chat(sender, I18n.format("commands.fvtm_potr.main_usage", new Object[0]));
            return;
        }
        switch(args[0]){
            case "help": {
        		Print.chat(sender, "&9/fvtm-potr emiss");
                break;
            }
            case "emiss": {
            	if(Static.dev()) FVTMPotR.VECHILE.emitAt(sender.getEntityWorld(), sender.getPosition());
            	else Print.chat(sender, "&eOnly available in dev mode.");
                break;
            }
            default: {
                Print.chat(sender, "null [0]");
                break;
            }
        }
    }

}
