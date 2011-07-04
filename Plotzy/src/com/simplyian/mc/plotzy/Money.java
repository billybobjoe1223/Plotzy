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
import com.nijikokun.register.payment.Method.MethodAccount;

/**
 * Money Class
 * 
 * @author simplyianm
 * @since 0.1
 */
public class Money {
    /**
     * Plotzy Main Class
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
    public Money(Plotzy instance) {
        pl = instance;
    }
    
    /**
     * Gets the money of the player.
     * 
     * @param playerName 
     * 
     * @since 0.1
     */
    public double get(String playerName) {
        return pl.Method.getAccount(playerName).balance();
    }
    
    /**
     * Adds money to the player's account.
     * 
     * @param playerName 
     * 
     * @since 0.1
     */
    public boolean add(String playerName, double amount) {
        return pl.Method.getAccount(playerName).add(amount);
    }
    
    /**
     * Subtracts money from the player's account.
     * 
     * @param playerName 
     * 
     * @since 0.1
     */
    public boolean subtract(String playerName, double amount) {
        return pl.Method.getAccount(playerName).subtract(amount);
    }
}
