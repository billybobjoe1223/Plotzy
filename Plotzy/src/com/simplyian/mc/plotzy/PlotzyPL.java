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

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Plotzy Player Listener
 * 
 * @author simplyianm
 * @since 0.1
 */
public class PlotzyPL extends PlayerListener {
    /**
     * Main Class Include
     * 
     * @since 0.1
     */
    private static Plotzy pz;
    
    /**
     * Constructor
     * 
     * @param instance 
     * 
     * @since 0.1
     */
    public PlotzyPL(Plotzy instance) {
        pz = instance;
    }
    
    /**
     * Triggered when the player moves. Thanks luciddream!
     * 
     * @param event 
     * 
     * @since 0.1
     */
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        String pname = player.getName();
        Block oldBlock;
        Block newBlock = player.getLocation().getBlock();
        oldBlock = pz.playerLocs.containsKey(pname) == true ? pz.playerLocs.get(pname) : newBlock;
        pz.playerLocs.put(pname, newBlock);
        if (oldBlock != newBlock) {
            Plot oldPlot = PlotFunctions.inWhichPlot(oldBlock.getLocation());
            Plot newPlot = PlotFunctions.inWhichPlot(newBlock.getLocation());
            if (oldPlot == null && newPlot == null) { //Walking through wilderness
                //player.sendMessage("You are in wilderness.");
            } else if (oldPlot == null && newPlot != null) { //Into a plot named newPlot
                player.sendMessage("You have entered " + newPlot.getName() + ".");
            } else if (oldPlot != null && newPlot == null) { //Out of a plot named oldPlot
                player.sendMessage("You have left " + oldPlot.getName() + ".");
            } else if (oldPlot.equals(newPlot)) { //Moving through a plot
                //player.sendMessage("You are moving through " + newPlot + ".");
            } else if (!oldPlot.equals(newPlot)) { //Moving between plots
                player.sendMessage("You have left " + oldPlot.getName() + " and entered " + newPlot.getName() + ".");
            } else {
                pz.cnsl("onPlayerMove error");
                player.sendMessage("Plot error");
            }
        }
    }
    
    /**
     * Triggered when a player interacts with an object.
     * 
     * @param event 
     * 
     * @since 0.1
     */
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        int interactedWith = event.getClickedBlock().getTypeId();
        if (interactedWith == 69 || interactedWith == 77) { //lever or button
            Player player = event.getPlayer();
            Plot plot = PlotFunctions.inWhichPlot(player.getLocation());
            if (plot != null) {
                if (interactedWith == 69) { //lever
                    if (plot.canUseLevers(player) == false) {
                        player.sendMessage(ChatColor.RED + "You can't use levers in a private plot that isn't yours.");
                        event.setCancelled(true);
                    }
                } else if (interactedWith == 77) { //button
                    if (plot.canUseButtons(player) == false) {
                        player.sendMessage(ChatColor.RED + "You can't use buttons in a private plot that isn't yours.");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}