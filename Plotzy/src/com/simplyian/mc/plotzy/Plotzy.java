/*
 * Plotzy Land Protection System for Bukkit
 * Copyright (C) 2011 simplyianm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.simplyian.mc.plotzy;

import com.nijikokun.register.payment.Method;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plotzy Main Class
 * 
 * @author simplyianm
 * @since 0.1
 */
public class Plotzy extends JavaPlugin {
    /**
     * Console output
     * 
     * @since 0.1
     */
    static final Logger log = Logger.getLogger("Minecraft");
    
    /**
     * Block listener
     * 
     * @since 0.1
     */
    private final PlotzyBL blockListener = new PlotzyBL(this);    
    
    /**
     * Player listener
     * 
     * @since 0.1
     */
    private final PlotzyPL playerListener = new PlotzyPL(this);    
    
    /**
     * Server listener
     * 
     * @since 0.1
     */
    private final PlotzySL serverListener = new PlotzySL(this);
        
    /**
     * Money
     * 
     * @since 0.1
     */
    public Money money = new Money(this);  
    
    /**
     * Money
     * 
     * @since 0.1
     */
    public Commands commands = new Commands(this);
    
    /**
     * HashMap of player locations
     * 
     * @since 0.1
     */
    public HashMap<String, Block> playerLocs;
    
    /**
     * Method of payment
     * 
     * @since 0.1
     */
    public Method Method = null;
    
    /**
     * Triggered on the enabling of the plugin.
     * 
     * @since 0.1
     */
    @Override
    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        new Database(this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Event.Priority.Monitor, this);
        playerLocs = new HashMap<String, Block>();
        log.info("[Plotzy] Plugin enabled."); //sc19.servercraft.co:3145
    }

    /**
     * Triggered on the disabling of the plugin.
     * 
     * @since 0.1
     */
    @Override
    public void onDisable() {
        Method = null;
        log.info("[Plotzy] Plugin disabled.");
    }
    
    /**
     * Commands
     * 
     * @param sender
     * @param cmd
     * @param commandLabel
     * @param args
     * @return Did the person reach the command or not?
     * 
     * @since 0.1
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String command = cmd.getName().toLowerCase();
        if (command.equals("pz")) {
            return this.commands.pz(sender, commandLabel, args);
        }
        return false;
    }
}
