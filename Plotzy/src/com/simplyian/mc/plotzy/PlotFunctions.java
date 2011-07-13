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

import org.bukkit.Location;

/**
 * Plot Functions
 * 
 * @author simplyianm
 * @since 0.1
 */
public class PlotFunctions {
    /**
     * Plotzy Main Class
     * 
     * @since 0.3
     */
    private static Plotzy pl;
    
    /**
     * Constructor
     * 
     * @param instance 
     * 
     * @since 0.3
     */
    public PlotFunctions(Plotzy instance) {
        pl = instance;
    }
    
    /**
     * Gets the current plot the location is in.
     * 
     * @param loc
     * @return Plot object
     * 
     * @since 0.1
     */
    public static Plot inWhichPlot(Location loc) {
        for (Plot plot : pl.plotList.values()) {
            if (plot.contains(loc) == true) return plot;
        }
        return null;
    }
    
    /**
     * Creates a plot according to the specifications.
     * 
     * @param plotName Desired plot name
     * @param size Radius of plot
     * @param center Center of plot
     * @return Plot Plot
     * 
     * @since 0.1
     */
    public static Plot createPlot(String plotName, Location center, int size) {
        if (pl.mysql == true) Database.execute("INSERT INTO " + Database.prefix + "plotzy_plots VALUES (0, '" + plotName + "', '" + size + "', '" + center.getWorld().getName() + "', '" + center.getBlockX() + "', '" + center.getBlockY() + "', '" + center.getBlockZ() + "')");
        pl.plotList.put(plotName, new Plot(plotName, center, size));
        return pl.plotList.get(plotName);
    }
    
    /**
     * Gets the default radius from the configuration file.
     * 
     * @return int
     * 
     * @since 0.1
     */
    public static int getDefaultRadius() {
        return Database.readInt("plot.default_radius") > 0 ? Database.readInt("plot.default_radius") : 10;
    }    
    
    /**
     * Gets the default radius from the configuration file.
     * 
     * @return int
     * 
     * @since 0.1
     */
    public static int getDefaultCreateCost() {
        return Database.readInt("plot.cost_create") > 0 ? Database.readInt("plot.cost_create") : 1000;
    }    
    
    /**
     * Gets the default radius from the configuration file.
     * 
     * @return int
     * 
     * @since 0.1
     */
    public static int getDefaultExpandMultiplier() {
        return Database.readInt("plot.cost_expand") > 0 ? Database.readInt("plot.cost_expand") : 10;
    }    
    
    /**
     * Gets the default radius from the configuration file.
     * 
     * @return int
     * 
     * @since 0.1
     */
    public static int getMaxSizeAllowed() {
        return Database.readInt("plot.max_size") > 0 ? Database.readInt("plot.max_size") : 100;
    }    
    
    /**
     * Gets the default radius from the configuration file.
     * 
     * @return int
     * 
     * @since 0.1
     */
    public static int getMaxPlotsPerPlayer() {
        return Database.readInt("plot.max_plots_per_player") > 0 ? Database.readInt("plot.max_plots_per_player") : 10;
    }
    
    /**
     * Checks if the plot already exists.
     * 
     * @param plotName
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean plotExists(String plotName) {
        return pl.plotList.containsKey(plotName) ? true : false;
    }
    
    /**
     * Gets the name of the first plot found in the database that overlaps the given sphere.
     * (i.e. the oldest plot)
     * 
     * @param center
     * @param radius
     * @return First plot found's name
     */
    public static String getOverlappingPlot(Location center, double radius) {
        for (Plot plot : pl.plotList.values()) {
            if (plot.getCenter().distanceSquared(center) <= Math.pow(plot.getSize() * 1.5 + radius, 2)) return plot.getName();
        }
        return null;
    }
    
    /**
     * Deletes a plot. For good.
     * 
     * @param plot 
     * 
     * @since 0.3
     */
    public static void deletePlot(Plot plot) {
        plot.delete();
        pl.plotList.remove(plot.getName());
    }
}