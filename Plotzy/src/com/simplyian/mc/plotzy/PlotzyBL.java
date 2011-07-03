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
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Plotzy Block Listener
 * 
 * @author simplyianm
 * @since 0.1
 */
public class PlotzyBL extends BlockListener {
    /**
     * Main Class Include
     * 
     * @since 0.1
     */
    private static Plotzy pl;
    
    /**
     * Constructor
     * 
     * @param instance 
     * 
     * @since 0.1
     */
    PlotzyBL(Plotzy instance) {
        pl = instance;
    }
    
    /**
     * Triggered when a block is broken.
     * 
     * @param event 
     * 
     * @since 0.1
     */
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (!player.isOp()) {
            String plotName = PlotFunctions.inWhichPlot(event.getBlock().getLocation());
            if (plotName != null) { //Separating if blocks to prevent an unnecessary transaction
                if (PlotFunctions.canBreakBlocksInPlot(plotName, player) == false) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Sorry, you don't have permission to break blocks in " + plotName + ".");
                }
            }
        }
    }
    
    /**
     * Triggered when a block is placed.
     * 
     * @param event 
     * 
     * @since 0.1
     */
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (!player.isOp()) {
            String plotName = PlotFunctions.inWhichPlot(event.getBlock().getLocation());
            if (plotName != null) { //Separating if blocks to prevent an unnecessary transaction
                if (PlotFunctions.canPlaceBlocksInPlot(plotName, player) == false) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Sorry, you don't have permission to place blocks in " + plotName + ".");
                }
            }
        }
    }
}
