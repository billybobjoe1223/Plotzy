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

import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Plot Functions
 * 
 * @author simplyianm
 * @since 0.1
 */
public class PlotFunctions {
    /**
     * Checks if the given location is within the sphere.
     * (uses Math.floor)
     * 
     * @param loc Location
     * @param center Sphere center
     * @param radius Radius of sphere
     * @return Boolean
     * 
     * @since 0.1
     */
    private static boolean inSphere(Location loc, Location center, int radius) {
        return Math.floor(loc.distanceSquared(center)) <= Math.pow(radius, 2D) ? true : false;
    }
    
    /**
     * Checks if the location is within the specified plot.
     * 
     * @param loc
     * @param plot_name
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean inPlot(Location loc, String plot_name) {
        ResultSet plotResultSet = getPlotResultSet(plot_name);
        try {
            plotResultSet.next();
        } catch (SQLException ex) {
            Database.sqlErrors(ex);
        }
        int plotSize = getPlotSize(plotResultSet);
        Location plotCenter = getPlotCenter(plotResultSet);
        return inSphere(loc, plotCenter, plotSize);
    }
    
    /**
     * Gets the size of a given plot via ResultSet.
     * 
     * @param plotResultSet ResultSet row of the plot
     * @return Integer
     */
    public static int getPlotSize(ResultSet plotResultSet) {
        try {
            return plotResultSet.getInt("pl_size");
        } catch (SQLException ex) {
            Database.sqlErrors(ex);
        }
        return 0;
    }    
    
    /**
     * Gets the size of a given plot.
     * 
     * @param plotName
     * @return String
     * 
     * @since 0.1
     */
    public static int getPlotSize(String plotName) {
        try {
            return getPlotResultSet(plotName).getInt("pl_size");
        } catch (SQLException ex) {
            Database.sqlErrors(ex);
        }
        return 0;
    }    
    
    /**
     * Gets the center of a given plot via ResultSet.
     * 
     * @param plotResultSet ResultSet row of the plot
     * @return Location
     * 
     * @since 0.1
     */
    public static Location getPlotCenter(ResultSet plotResultSet) {
        try {
            World world = Bukkit.getServer().getWorld(plotResultSet.getString("pl_world"));
            int x = plotResultSet.getInt("pl_x");
            int y = plotResultSet.getInt("pl_y");
            int z = plotResultSet.getInt("pl_z");
            return new Location(world, x, y, z);
        } catch (SQLException ex) {
            Database.sqlErrors(ex);
        }
        return null;
    }
    
    /**
     * Gets the name of the plot in the current row of the result set.
     * 
     * @param plotResultSet
     * @return String
     * 
     * @since 0.1
     */
    private static String getPlotName(ResultSet plotResultSet) {
        try {
            return plotResultSet.getString("pl_name");
        } catch (SQLException ex) {
            Database.sqlErrors(ex);
        }
        return null;
    }
    
    /**
     * Gets the ResultSet of the row of the plot.
     * 
     * @param plot_name
     * @return ResultSet
     * 
     * @since 0.1
     */
    public static ResultSet getPlotResultSet(String plot_name) {
        return Database.getResultSet("SELECT * FROM " + Database.prefix + "plotzy_plots WHERE pl_name = '" + plot_name + "'");
    }
    
    /**
     * Gets whether any given location is in a plot or not.
     * 
     * @param loc
     * @return boolean
     * 
     * @since 0.1
     */
    public static boolean inAPlot(Location loc) {
        try {
            ResultSet plots = getAllPlotsResultSet();
            while (plots.next()) {
                Location plotCenter = getPlotCenter(plots);
                int plotSize = getPlotSize(plots);
                if (inSphere(loc, plotCenter, plotSize)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            Database.sqlErrors(ex);
        }
        return false;
    }
    
    /**
     * Gets the name of the current plot the location is in.
     * 
     * @param loc
     * @return Plot name
     * 
     * @since 0.1
     */
    public static String inWhichPlot(Location loc) {
        try {
            ResultSet plots = getAllPlotsResultSet();
            while (plots.next()) {
                Location plotCenter = getPlotCenter(plots);
                int plotSize = getPlotSize(plots);
                if (inSphere(loc, plotCenter, plotSize)) {
                    return getPlotName(plots);
                }
            }
            return null;
        } catch (SQLException ex) {
            Database.sqlErrors(ex);
        }
        return null;
    }
    
    /**
     * Gets the ResultSet of all plots.
     * 
     * @param plot_name
     * @return ResultSet
     * 
     * @since 0.1
     */
    private static ResultSet getAllPlotsResultSet() {
        return Database.getResultSet("SELECT * FROM " + Database.prefix + "plotzy_plots");
    }
    
    /**
     * Creates a plot according to the specifications.
     * 
     * @param plotName Desired plot name
     * @param size Radius of plot
     * @param center Center of plot
     * 
     * @since 0.1
     */
    public static void createPlot(String plotName, int size, Location center, String founder) {
        String world = center.getWorld().getName();
        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();
        Database.execute("INSERT INTO " + Database.prefix + "plotzy_plots VALUES (0, '" + plotName + "', '" + size + "', '" + world + "', '" + x + "', '" + y + "', '" + z + "', '" + founder + "')");
    }
    
    /**
     * Deletes the plot, associated player relationships, and flags of a plot.
     * 
     * @param plotname 
     * 
     * @since 0.1
     */
    public static void deletePlot(String plotname) {
        Database.execute("DELETE FROM " + Database.prefix + "plotzy_plots WHERE pl_name = '" + plotname + "'");
        Database.execute("DELETE FROM " + Database.prefix + "plotzy_players WHERE py_plot = '" + plotname + "'");
        Database.execute("DELETE FROM " + Database.prefix + "plotzy_flags WHERE fl_plot = '" + plotname + "'");
    }
    
    /**
     * Gives a player a role within the given plot.
     * 
     * @param plotName Plot name
     * @param player Player to give role to
     * @param role
     *      1 = Owner, 2 = Builder, 3 = Resident
     * 
     * @since 0.1
     */
    public static void addPlotRole(String plotName, String player, int role) {
        Database.execute("INSERT INTO " + Database.prefix + "plotzy_players VALUES (0, '" + plotName + "', '" + player + "', '" + role + "')");
    }
    
    /**
     * Returns the player's role within the given plot.
     * 
     * @param plotName
     * @param player
     * @return Player role:
     *      1 = Owner, 2 = Builder, 3 = Resident
     * 
     * @since 0.1
     */
    private static int getPlotRole(String plotName, String player) {
        int hasRole = Database.getInteger("SELECT COUNT(py_role) FROM " + Database.prefix + "plotzy_players WHERE py_plot = '" + plotName + "' AND py_player = '" + player + "'");
        return hasRole != 0 ? Database.getInteger("SELECT py_role FROM " + Database.prefix + "plotzy_players WHERE py_plot = '" + plotName + "' AND py_player = '" + player + "'") : 0;
    }
    
    /**
     * Gets the plot founder's name.
     * 
     * @param plotName
     * @return 
     * 
     * @since 0.1
     */
    public static String getPlotFounder(String plotName) {
        return Database.getString("SELECT pl_founder FROM " + Database.prefix + "plotzy_plots WHERE pl_name = '" + plotName + "'");
    }    
    
    /**
     * Gets the plot founder's name.
     * 
     * @param plot ResultSet of the plot
     * @return 
     * 
     * @since 0.1
     */
    public static String getPlotFounder(ResultSet plot) {
        try {
            return plot.getString("pl_founder");
        } catch (SQLException ex) {
            Database.sqlErrors(ex);
        }
        return null;
    }
    
    /**
     * Adds a new plot flag to the database.
     * 
     * @param plotName
     * @param flag
     * @param value 
     * 
     * @since 0.1
     */
    public static void addPlotFlag(String plotName, String flag, String value) {
        Database.execute("INSERT INTO " + Database.prefix + "plotzy_flags VALUES (0, '" + plotName + "', '" + flag + "', '" + value + "')");
    }    
    
    /**
     * Adds a new plot flag to the database.
     * 
     * @param plotName
     * @param flag
     * @param value 
     * 
     * @since 0.1
     */
    public static void addPlotFlag(String plotName, String flag, boolean value) {
        Database.execute("INSERT INTO " + Database.prefix + "plotzy_flags VALUES (0, '" + plotName + "', '" + flag + "', '" + value + "')");
    }
    
    /**
     * Gets a plot flag as a string from the database.
     * 
     * @param plotName
     * @param flag
     * @param value
     * @return Flag value as string
     * 
     * @since 0.1
     */
    public static String getPlotFlagString(String plotName, String flag) {
        return Database.getString("SELECT fl_value FROM " + Database.prefix + "plotzy_flags WHERE fl_plot = '" + plotName + "' AND fl_flag = '" + flag + "'");
    }   
    
    /**
     * Gets a plot flag as a boolean from the database.
     * 
     * @param plotName
     * @param flag
     * @param value
     * @return Flag value as boolean
     * 
     * @since 0.1
     */
    public static boolean getPlotFlagBoolean(String plotName, String flag) {
        return Boolean.parseBoolean(getPlotFlagString(plotName, flag));
    }
    
    /**
     * Creates a default plot for the given player.
     * 
     * @param plotName Desired plot name
     * @param player Player object of the player
     * 
     * @since 0.1
     */
    public static void createDefaultPlotForPlayer(String plotName, Player player) {
        String playerName = player.getName();
        createPlot(plotName, 10, player.getLocation(), playerName);
        addPlotRole(plotName, playerName, 1);
        addPlotFlag(plotName, "private", true);
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
        int plotCount = Database.getInteger("SELECT COUNT(pl_name) FROM " + Database.prefix + "plotzy_plots WHERE pl_name = '" + plotName + "'");
        return plotCount != 0 ? true : false;
    }
    
    /**
     * Checks if the plot is private.
     * 
     * @param plotName
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean plotIsPrivate(String plotName) {
        return getPlotFlagBoolean(plotName, "private");
    }
    
    /**
     * Checks if the player can break blocks in the given plot.
     * (Owner or Builder)
     * 
     * @param plotName
     * @param player
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean canBreakBlocksInPlot(String plotName, Player player) {
        if (player.isOp()) return true;
        int role = getPlotRole(plotName, player.getName());
        return role == 1 || role == 2 ? true : false;
    }    
    
    /**
     * Checks if the player can place blocks in the given plot.
     * (Owner or Builder)
     * 
     * @param plotName
     * @param player
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean canPlaceBlocksInPlot(String plotName, Player player) {
        if (player.isOp()) return true;
        int role = getPlotRole(plotName, player.getName());
        return role == 1 || role == 2 || role == 3 ? true : false;
    }
    
    /**
     * Checks if the player can use buttons in the given plot.
     * (Owner, Builder, or Resident)
     * 
     * @param plotName
     * @param player
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean canUseButtonsInPlot(String plotName, Player player) {
        if (player.isOp()) return true;
        if (plotIsPrivate(plotName)) {
            int role = getPlotRole(plotName, player.getName());
            return role == 1 || role == 2 || role == 3 ? true : false;
        } else {
            return true;
        }
    }    
    /**
     * Checks if the player can use levers in the given plot.
     * (Owner, Builder, or Resident)
     * 
     * @param plotName
     * @param player
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean canUseLeversInPlot(String plotName, Player player) {
        if (player.isOp()) return true;
        if (plotIsPrivate(plotName)) {
            int role = getPlotRole(plotName, player.getName());
            return role == 1 || role == 2 || role == 3 ? true : false;
        } else {
            return true;
        }
    }
    
    /**
     * Checks if the player can delete the given plot.
     * (Owner)
     * 
     * @param plotName
     * @param player
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean canDeletePlot(String plotName, Player player) {
        if (player.isOp()) return true;
        int role = getPlotRole(plotName, player.getName());
        return role == 1 ? true : false;
    }    
    
    /**
     * Checks if the player can expand the given plot.
     * (Owner)
     * 
     * @param plotName
     * @param player
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean canExpandPlot(String plotName, Player player) {
        if (player.isOp()) return true;
        int role = getPlotRole(plotName, player.getName());
        return role == 1 ? true : false;
    }    
    
    /**
     * Checks if the player can shrink the given plot.
     * (Owner)
     * 
     * @param plotName
     * @param player
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean canShrinkPlot(String plotName, Player player) {
        if (player.isOp()) return true;
        int role = getPlotRole(plotName, player.getName());
        return role == 1 ? true : false;
    }
    
    /**
     * Checks if two spheres overlap.
     * 
     * @param c1 Center of sphere 1
     * @param c2 Center of sphere 2
     * @param radius1 Radius of sphere 1
     * @param radius2 Radius of sphere 2
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean spheresOverlap(Location c1, Location c2, double radius1, double radius2) {
        return c1.distanceSquared(c2) <= Math.pow(radius1 + radius2, 2) ? true : false;
    }  
    
    /**
     * Checks if a sphere overlaps the given plot.
     * 
     * @param center The sphere's center.
     * @param radius The sphere's radius.
     * @param plot The given plot's ResultSet.
     * @return Boolean
     * 
     * @since 0.1
     */
    public static boolean sphereOverlapsPlot(Location center, double radius, ResultSet plot) {
        return spheresOverlap(center, getPlotCenter(plot), radius, getPlotSize(plot)) ? true : false;
    }    
    
    /**
     * Checks if a sphere overlaps the given plot's influence.
     * 
     * @param center The sphere's center.
     * @param radius The sphere's radius.
     * @param plot The given plot's ResultSet.
     * @return boolean
     * 
     * @since 0.1
     */
    public static boolean sphereOverlapsPlotInfluence(Location center, double radius, ResultSet plot) {
        return spheresOverlap(center, getPlotCenter(plot), radius, getPlotSize(plot) * 1.5) ? true : false;
    }
    
    /**
     * Gets the name of the plot if the sphere overlaps it.
     * 
     * @param center The sphere's center.
     * @param radius The sphere's radius.
     * @param plot The given plot's ResultSet.
     * @return String
     * 
     * @since 0.1
     */
    public static String sphereOverlapsPlotInfluenceName(Location center, double radius, ResultSet plot) {
        return spheresOverlap(center, getPlotCenter(plot), radius, getPlotSize(plot) * 1.5) ? getPlotName(plot) : null;
    }
    
    /**
     * Gets the name of the first plot found in the database that overlaps the given sphere.
     * (i.e. the oldest plot)
     * 
     * @param center
     * @param radius
     * @return First plot found's name
     */
    public static String sphereOverlapsWhichPlotInfluenceName(Location center, double radius) {
        try {
            ResultSet plots = getAllPlotsResultSet();
            String plotName = null;
            while (plots.next()) {
                plotName = sphereOverlapsPlotInfluenceName(center, radius, plots);
                if (plotName != null) return plotName;
            }
        } catch (SQLException ex) {
            Database.sqlErrors(ex);
        }
        return null;
    }
    
    /**
     * Sets the size of the plot.
     * 
     * @param plotName
     * @param size 
     * 
     * @since 0.1
     */
    public static void setPlotSize(String plotName, int size) {
        Database.execute("UPDATE " + Database.prefix + "plotzy_plots SET pl_size = '" + size + "' WHERE pl_name = '" + plotName + "'");
    }
    
    /**
     * Expands a plot.
     * 
     * @param plotName 
     * @param byHowMuch Amount to expand by
     * 
     * @since 0.1
     */
    public static void expandPlot(String plotName, int byHowMuch) {
        int size = getPlotSize(plotName);
        setPlotSize(plotName, size + byHowMuch);
    }
    
    /**
     * Shrinks a plot.
     * 
     * @param plotName 
     * @param byHowMuch Amount to shrink by
     * 
     * @since 0.1
     */
    public static void shrinkPlot(String plotName, int byHowMuch) {
        int size = getPlotSize(plotName);
        setPlotSize(plotName, size - byHowMuch);
    }
}