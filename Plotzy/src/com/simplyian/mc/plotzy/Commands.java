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
     * Plotzy instance
     * 
     * @since 0.1
     */
    private Plotzy pl;
    
    /**
     * Constructor
     * 
     * @param instance 
     * 
     * @since 0.1
     */
    public Commands(Plotzy instance) {
        pl = instance;
    }
    
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
    public boolean pz(CommandSender sender, String alias, String args[]) {
        if (args.length > 0) {
            String function = args[0].toLowerCase();
            String[] newArgs = shiftArgs(args);
            if (function.equals("help")) {
                pzHelp(sender, newArgs);
            } else if (function.equals("about")) {
                pzAbout(sender, newArgs);
            } else if (function.equals("create")) {
                pzCreate(sender, newArgs);
            } else if (function.equals("delete")) {
                pzDelete(sender, newArgs);
            } else if (function.equals("expand")) {
                pzExpand(sender, newArgs);
            } else if (function.equals("shrink")) {
                pzShrink(sender, newArgs);
            } else if (function.equals("info")) {
                pzInfo(sender, newArgs);
            } else if (function.equals("test")) {
                sender.sendMessage("Sorry, this is disabled.");
                //Player player = (Player) sender;
                //SpookyRoomGen.spookyRoomGen(player.getLocation());
            } else {
                pzHelp(sender, newArgs);
            }
        } else {
            pzHelp(sender, args);
        }
        return true;
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
    public void pzAbout(CommandSender sender, String[] args) {
        sender.sendMessage("Plotzy v0.1 made by AlbireoX of www.simplyian.com");
    }
    
    /**
     * Displays usage on the /pz command.
     * 
     * @param sender
     * @param args 
     * 
     * @since 0.3
     */
    public void pzHelp(CommandSender sender, String[] args) {
        sender.sendMessage("Usage: /pz <command>");
        sender.sendMessage("create - " + ChatColor.YELLOW + " Creates a default plot at your location.");
        sender.sendMessage("delete - " + ChatColor.YELLOW + " Deletes the plot you are in.");
        sender.sendMessage("expand - " + ChatColor.YELLOW + " Expands the plot you are in.");
        sender.sendMessage("shrink - " + ChatColor.YELLOW + " Shrinks the plot you are in.");
        sender.sendMessage("info - " + ChatColor.YELLOW + " Gets information about the current plot.");
    }
    
    /**
     * Creates a new plot owned by the command executor.
     * 
     * @param sender
     * @param args 
     * 
     * @since 0.1
     */
    public void pzCreate(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Plotzy.hasPermission(player, "plotzy.plot.create")) {
                if (args.length > 0) {
                    String plotName = argString(args);
                    double cost = PlotFunctions.getDefaultCreateCost();
                    if (Money.get(player.getName()) >= cost) {
                        int playerPlots = Database.getInteger("SELECT COUNT(*) FROM " + Database.prefix + "plotzy_players WHERE py_role = '1' AND py_player = '" + player.getName() + "'");
                        int maxPlots = PlotFunctions.getMaxPlotsPerPlayer();
                        if (playerPlots < maxPlots) {
                            if (PlotFunctions.plotExists(plotName.toLowerCase()) == false) {
                                if (plotName.matches("[\\w\\s]+")) { //Alphanumeric, underscores, and spaces
                                    String overlappingPlot = PlotFunctions.getOverlappingPlot(player.getLocation(), PlotFunctions.getDefaultRadius());
                                    if (overlappingPlot == null) {
                                        String playerName = player.getName();
                                        Plot newPlot = PlotFunctions.createPlot(plotName, player.getLocation(), PlotFunctions.getDefaultRadius());
                                        newPlot.setRole(playerName, 1);
                                        newPlot.setFlag("private", true);
                                        newPlot.setFlag("founder", playerName);
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
                            sender.sendMessage(ChatColor.RED + "You can only own up to " + PlotFunctions.getMaxPlotsPerPlayer() + " plots!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You need " + cost + " coins to purchase a plot!");
                    }
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /pz create [Plot name]");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to create plots in this world.");
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
    public void pzDelete(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Plotzy.hasPermission(player, "plotzy.plot.delete")) {
                Plot plot = PlotFunctions.inWhichPlot(player.getLocation());
                if (plot != null) {
                    if (plot.canDelete(player) == true) {
                        //Erm... @todo Confirmation "are you sure you want to delete plotname?"
                        PlotFunctions.deletePlot(plot);
                        sender.sendMessage("You have successfully deleted the plot " + plot.getName() + ".");
                    } else {
                        sender.sendMessage(ChatColor.RED + "You cannot delete a plot that you don't own!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You must be in a plot to delete it.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to delete plots in this world.");
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
    public void pzExpand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Plotzy.hasPermission(player, "plotzy.plot.expand")) {
                Plot plot = PlotFunctions.inWhichPlot(player.getLocation());
                if (plot != null) {
                    if (plot.canExpand(player) == true) {
                        int byHowMuch = 1;
                        if (args.length > 0) {
                            try {
                                byHowMuch = Integer.parseInt(args[0]);
                            } catch (NumberFormatException ex) {
                                sender.sendMessage(ChatColor.RED + "You can only expand a plot by an integer.");
                                return;
                            }
                        }
                        double plotSize = plot.getSize();
                        if (plotSize + byHowMuch <= PlotFunctions.getMaxSizeAllowed()) {
                            double expandCost = (plotSize + byHowMuch) * PlotFunctions.getDefaultExpandMultiplier();
                            String playerName = player.getName();
                            if (Money.get(playerName) >= expandCost) {
                                Money.subtract(playerName, expandCost);
                                plot.expand(byHowMuch);
                                sender.sendMessage("Plot expanded.");
                            } else {
                                sender.sendMessage(ChatColor.RED + "You need " + expandCost + " coins to expand this plot by " + byHowMuch + "!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "The maximum plot size is " + PlotFunctions.getMaxSizeAllowed() + ".");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to expand this plot.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You aren't in a plot!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to expand plots in this world.");
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
    public void pzShrink(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Plotzy.hasPermission(player, "plotzy.plot.shrink")) {
                Plot plot = PlotFunctions.inWhichPlot(player.getLocation());
                if (plot != null) {
                    if (plot.canShrink(player)) {
                        int byHowMuch = 1;
                        if (args.length > 0) {
                            try {
                                byHowMuch = Integer.parseInt(args[0]);
                            } catch (NumberFormatException ex) {
                                sender.sendMessage(ChatColor.RED + "You can only shrink a plot by an integer.");
                                return;
                            }
                        }
                        if (plot.getSize() - byHowMuch >= 1) {
                            plot.shrink(byHowMuch);
                            sender.sendMessage("Plot shrunk.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "The size of your plot must always be at least 1.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to shrink this plot.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You aren't in a plot!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to shrink plots in this world.");
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
    public void pzInfo(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Plotzy.hasPermission(player, "plotzy.info")) {
                Plot plot = PlotFunctions.inWhichPlot(player.getLocation());
                if (plot != null) {
                    String privacy = plot.getFlagBoolean("private") == true ? "Private" : "Public" ;
                    sender.sendMessage(ChatColor.GREEN + "Plot Information:");
                    sender.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + plot.getName() + ChatColor.YELLOW + "    Size: " + ChatColor.WHITE + plot.getSize() + ChatColor.YELLOW + "    Founder: " + ChatColor.WHITE + plot.getFlag("founder"));
                    sender.sendMessage(ChatColor.YELLOW + "Privacy: " + ChatColor.WHITE + privacy + ChatColor.YELLOW + "    Distance from center: " + ChatColor.WHITE + plot.getCenter().distance(player.getLocation()));
                } else {
                    sender.sendMessage(ChatColor.RED + "You are not in a plot!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to get plot info in this world.");
            }
        } else {
            sender.sendMessage("[Plotzy] Sorry, this command is in-game only.");
        }
    }
}