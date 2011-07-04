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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Plotzy Commands
 * 
 * @author simplyianm
 * @since 0.1
 */
public class Commands {
    /**
     * /pz command
     * 
     * @param sender
     * @param alias
     * @param args
     * @return Success
     * 
     * @since 0.1
     */
    public static boolean pz(CommandSender sender, String alias, String args[]) {
        if (args.length > 0) {
            String function = args[0].toLowerCase();
            String[] newArgs = shiftArgs(args);
            if (function.equals("about")) {
                pzAbout(sender, newArgs);
            } else if (function.equals("create")) {
                pzCreate(sender, newArgs);
            } else if (function.equals("delete")) {
                pzDelete(sender, newArgs);
            } else if (function.equals("expand")) {
                pzExpand(sender, newArgs);
            } else if (function.equals("info")) {
                pzInfo(sender, newArgs);
            } else if (function.equals("test")) {
                sender.sendMessage("Sorry, this is disabled.");
                //Player player = (Player) sender;
                //SpookyRoomGen.spookyRoomGen(player.getLocation());
            } else {
                noArgsError(sender);
            }
        } else {
            noArgsError(sender);
        }
        return true;
    }
    
    /**
     * Error to display if no args are returned.
     * 
     * @param sender 
     * 
     * @since 0.1
     */
    public static void noArgsError(CommandSender sender) {
        sender.sendMessage("Documentation coming soon.");
    }
    
    /**
     * Removes the first arg.
     * 
     * @param args
     * @return Shifted args
     */
    public static String[] shiftArgs(String[] args) {
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        return newArgs;
    }
    
    /**
     * Joins args into a string.
     * 
     * @param args
     * @return String
     * 
     * @since 0.1
     * @author Shade (Chris H)
     */
    public static String argString(Object[] args) {
        if (args.length == 0) return "";
        String out = args[0].toString();
        for (int i = 1; i < args.length; i++) out += " " + args[i];
        return out;
    }
    
    /**
     * Sends the sender information about the plugin.
     * 
     * @param sender
     * @param args 
     * 
     * @since 0.1
     */
    public static void pzAbout(CommandSender sender, String[] args) {
        sender.sendMessage("Plotzy v0.1 made by AlbireoX of www.simplyian.com");
    }
    
    /**
     * Creates a new plot owned by the command executor.
     * 
     * @param sender
     * @param args 
     * 
     * @since 0.1
     */
    public static void pzCreate(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length > 0) {
                String plotName = argString(args);
                Player player = (Player) sender;
                double cost = 1000;
                if (Money.get(player.getName()) >= cost) { //todo: make a config!
                    if (PlotFunctions.plotExists(plotName) == false) {
                        if (plotName.matches("[\\w\\s]+")) { //Alphanumeric, underscores, and spaces
                            String overlappingPlot = PlotFunctions.sphereOverlapsWhichPlotInfluenceName(player.getLocation(), 10);
                            if (overlappingPlot == null) {
                                PlotFunctions.createDefaultPlotForPlayer(plotName, player);
                                sender.sendMessage("You have created the plot " + plotName + ".");
                            } else {
                                sender.sendMessage(ChatColor.RED + "Cannot create a plot here, doing so would overlap the influence of " + overlappingPlot + ".");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Plot name can only contain alphanumeric characters, underscores, and/or spaces.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "There is already a plot with the name " + plotName + ".");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You need " + cost + " coins to purchase a plot!");
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /pz create [Plot name]");
            }
        } else {
            sender.sendMessage("[Plotzy] Sorry, this command is in-game only.");
        }
    }
    
    /**
     * Deletes a plot if the player is a 1 or 2.
     * 
     * @param sender
     * @param args 
     * 
     * @since 0.1
     */
    public static void pzDelete(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String plot = PlotFunctions.inWhichPlot(player.getLocation());
            if (plot != null) {
                if (PlotFunctions.canDeletePlot(plot, player) == true) {
                    //Erm... @todo Confirmation "are you sure you want to delete plotname?"
                    PlotFunctions.deletePlot(plot);
                    sender.sendMessage("You have successfully deleted the plot " + plot + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "You cannot delete a plot that you don't own!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You must be in a plot to delete it.");
            }
        } else {
            sender.sendMessage("[Plotzy] Sorry, this command is in-game only.");
        }
    }
    
    /**
     * Expands the size of the plot.
     * 
     * @param sender
     * @param args 
     * 
     * @since 0.1
     */
    public static void pzExpand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String plot = PlotFunctions.inWhichPlot(player.getLocation());
            if (plot != null) {
                if (PlotFunctions.canExpandPlot(plot, player)) {
                    double plotSize = PlotFunctions.getPlotSize(plot);
                    double expandCost = plotSize * 10;
                    String playerName = player.getName();
                    if (Money.get(playerName) >= expandCost) {
                        Money.subtract(playerName, expandCost);
                        PlotFunctions.expandPlot(plot, 1);
                    } else {
                        sender.sendMessage(ChatColor.RED + "You need " + expandCost + " coins to expand this plot!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to expand this plot.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You aren't in a plot!");
            }
        } else {
            sender.sendMessage("[Plotzy] This command is for in-game use only.");
        }
    }    
    /**
     * Shrinks the size of the plot.
     * 
     * @param sender
     * @param args 
     * 
     * @since 0.1
     */
    public static void pzShrink(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String plot = PlotFunctions.inWhichPlot(player.getLocation());
            if (plot != null) {
                if (PlotFunctions.canShrinkPlot(plot, player)) {
                    PlotFunctions.shrinkPlot(plot, 1);
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to shrink this plot.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You aren't in a plot!");
            }
        } else {
            sender.sendMessage("[Plotzy] This command is for in-game use only.");
        }
    }
    
    /**
     * Returns some useful plot info.
     * 
     * @param sender
     * @param args 
     * 
     * @since 0.1
     */
    public static void pzInfo(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String plot = PlotFunctions.inWhichPlot(player.getLocation());
            if (plot != null) {
                ResultSet plotSet = PlotFunctions.getPlotResultSet(plot);
                try {
                    plotSet.next();
                } catch (SQLException ex) {
                    Database.sqlErrors(ex);
                }
                Location plotCenter = PlotFunctions.getPlotCenter(plotSet);
                double roundedDist = Math.round(plotCenter.distance(player.getLocation()) * 100) / 100;
                double plotSize = PlotFunctions.getPlotSize(plotSet);
                String plotFounder = PlotFunctions.getPlotFounder(plotSet);
                boolean isPrivate = PlotFunctions.plotIsPrivate(plot);
                String privacy = isPrivate == true ? "Private" : "Public" ;
                sender.sendMessage(ChatColor.GREEN + "Plot Information:");
                sender.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + plot + ChatColor.YELLOW + "    Size: " + ChatColor.WHITE + plotSize + ChatColor.YELLOW + "    Founder: " + ChatColor.WHITE + plotFounder);
                sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.WHITE + privacy + ChatColor.YELLOW + "    Distance from center: " + ChatColor.WHITE + roundedDist);
            } else {
                sender.sendMessage(ChatColor.RED + "You are not in a plot!");
            }
        } else {
            sender.sendMessage("[Plotzy] Sorry, this command is in-game only.");
        }
    }
}