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

import com.avaje.ebeaninternal.server.lib.sql.DataSourceException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.config.Configuration;

/**
 * AlbieRPG Database Access Object
 * 
 * @author simplyianm
 * @since 0.1
 */
public class Database {
    /**
     * Database connection
     * 
     * @since 0.1
     */
    private static Connection link;
    
    /**
     * AlbieRPG Plugin Object
     * 
     * @since 0.1
     */
    private static Plotzy pl;
    
    /**
     * The JDBC Connection URL.
     * 
     * @since 0.1
     */
    private static String dbJdbc;
    
    /**
     * Database prefix
     * 
     * @since 0.1
     */
    public static String prefix;
    
    /**
     * Configuration file path
     * 
     * @since 0.1
     */
    private static File config_file = new File("plugins/Plotzy/config.yml");
    
    /**
     * Database constructor
     * 
     * @param instance
     * 
     * @since 0.1
     */
    public Database(Plotzy instance) {
        pl = instance;
        initCfg();
        connect();
        genTables();
    }
    
    /**
     * Config file checker
     * 
     * Does the config exist?
     * 
     * @since 0.1
     */
    public static void initCfg(){
        new File("plugins" + File.separator + "Plotzy").mkdir(); //Make the directory
        if(!config_file.exists()){
            try {
                config_file.createNewFile(); //Create the config file if it's not here already
                generateDefaults(); //Apply the default settings to the file
            } catch (Exception ex) {
                pl.log.info("[Plotzy] Error creating the config file. >:-O");
            }
        } else {
            pl.log.info("[Plotzy] Configuration loaded."); //Load the configuration file
        }
    }
    
    /**
     * Inserts database defaults into the YAML file.
     * 
     * @since 0.1
     */
    private static void generateDefaults() {
        //MySQL
        setValue("mysql.host", "localhost");
        setValue("mysql.dbname", "myDB1");
        setValue("mysql.username", "MyUser1");
        setValue("mysql.password", "thisisastupidpassword");
        setValue("mysql.prefix", "pl_");
        //Costs
        setValue("plot.cost_create", 1000);
        setValue("plot.cost_expand", 10);
        setValue("plot.max_size", 100);
        setValue("plot.max_plots_per_player", 10);
    }
    
    /**
     * Set a value for a key.
     * 
     * @param key
     * @param value 
     */
    private static void setValue(String key, Object value) {
        Configuration config = loadConfig();
        config.setProperty(key, value);
        config.save();
    }
    
    /**
     * Loads the config file.
     * 
     * @return Config file object
     */
    private static Configuration loadConfig(){
        try {
            Configuration config = new Configuration(config_file);
            config.load();
            return config;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Reads the value of a string from the config file
     * 
     * @param key Key name
     */
    private static String readString(String key) {
        Configuration config = loadConfig();
        return config.getString(key, null);
    }    
    
    /**
     * Reads the value of an integer from the config file
     * 
     * @param key Key name
     * @return 
     * @return Integer value
     * 
     * @since 0.3
     */
    public static int readInt(String key) {
        Configuration config = loadConfig();
        return config.getInt(key, 0);
    }
    
    /**
     * Connects to the database.
     * 
     * @since 0.1
     */
    public static void connect() {
        prefix = readString("mysql.prefix");
        String host = readString("mysql.host");
        String dbName = readString("mysql.dbname");
        String username = readString("mysql.username");
        String password = readString("mysql.password");
        dbJdbc = "jdbc:mysql://" + host + "/" + dbName + "?user=" + username + "&password=" + password;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            throw new DataSourceException("Failed to initialize JDBC driver.");
        }
        try {
            link = DriverManager.getConnection(dbJdbc);
            pl.log.info("[Plotzy] Connected to database!");
        } catch (SQLException ex) { //Error handling below:
            pl.log.info("[Plotzy] An error occured while connecting to the database. Perhaps your database info is incorrect?");
            sqlErrors(ex);
        }
    }
    
    /**
     * Closes the database connection.
     * 
     * @since 0.1
     */
    public static void close() {
        try {
            link.close();
        } catch (SQLException ex) {
            pl.log.info("[Plotzy] An error occured while closing the connection.");
            sqlErrors(ex);
        }
    }
    
    /**
     * Are we connected to the database?
     * 
     * @return Connected
     * @throws SQLException 
     * 
     * @since 0.1
     */
    public static boolean isConnected() throws SQLException {
        return link.isValid(5) ? true : false;
    }
    
    /**
     * Prints out all SQL errors.
     * 
     * @param ex 
     * 
     * @since 0.1
     */
    public static void sqlErrors(SQLException ex) {
        pl.log.info("[Plotzy] SQLException: " + ex.getMessage());
        pl.log.info("[Plotzy] SQLState: " + ex.getSQLState());
        pl.log.info("[Plotzy] VendorError: " + ex.getErrorCode());
    }

    /**
     * Table generator
     * 
     * @since 0.1
     */
    public static void genTables() {
        //Plot table: pl_plotzy
        execute("CREATE TABLE IF NOT EXISTS `" + prefix + "plotzy_plots` (" +
            "`pl_id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
            "`pl_name` varchar(40) NOT NULL," +
            "`pl_size` int(10) NOT NULL," +
            "`pl_world` varchar(40) NOT NULL," +
            "`pl_x` int(10) NOT NULL," +
            "`pl_y` int(10) NOT NULL," +
            "`pl_z` int(10) NOT NULL," +
            "PRIMARY KEY (`pl_id`));");
        //Plot player table: pl_plotzy_players
        execute("CREATE TABLE IF NOT EXISTS `" + prefix + "plotzy_players` (" +
            "`py_id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
            "`py_plot` varchar(40) NOT NULL," +
            "`py_player` varchar(40) NOT NULL," +
            "`py_role` int(5) NOT NULL," +
            "PRIMARY KEY (`py_id`));");
        //Plot player table: pl_plotzy_flags
        execute("CREATE TABLE IF NOT EXISTS `" + prefix + "plotzy_flags` (" +
            "`fl_id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
            "`fl_plot` varchar(40) NOT NULL," +
            "`fl_flag` varchar(40) NOT NULL," +
            "`fl_value` text NOT NULL," +
            "PRIMARY KEY (`fl_id`));");
    }
 
    /**
     * Executes an SQL query. (No output)
     * 
     * @param sql The SQL query as a string.
     * 
     * @since 0.1
     */
    public static void execute(String sql) {
        try {
            if (isConnected() == true) {
                try {
                    link.prepareStatement(sql).executeUpdate();
                } catch(SQLException ex) {
                    sqlErrors(ex);
                }
            } else {
                connect();
                execute(sql);
            }
        } catch (SQLException ex) {
            sqlErrors(ex);
        }
    }
    
    /**
     * Gets a result set returned from an SQL query.
     * 
     * @param sql
     * @return ResultSet
     * 
     * @since 0.1
     */
    public static ResultSet getResultSet(String sql) {
        try {
            if (isConnected() == true) {
                try {
                    return link.createStatement().executeQuery(sql);
                } catch (SQLException ex) {
                    sqlErrors(ex);
                }
                return null;
            } else {
                connect();
                return getResultSet(sql);
            }
        } catch (SQLException ex) {
            sqlErrors(ex);
            return null;
        }
    }
    
    /**
     * Gets a string from the database.
     * 
     * @param sql
     * @return String
     * 
     * @since 0.1
     */
    public static String getString(String sql) {
        try {
            ResultSet result = getResultSet(sql);
            result.next();
            return result.getString(1);
        } catch (SQLException ex) {
            sqlErrors(ex);
        }
        return null;
    }
    
    /**
     * Gets an integer from the database.
     * 
     * @param sql
     * @return int
     * 
     * @since 0.1
     */
    public static int getInteger(String sql) {
        try {
            ResultSet result = getResultSet(sql);
            result.next();
            return result.getInt(1);
        } catch (SQLException ex) {
            sqlErrors(ex);
        }
        return 0;
    }
    
    /**
     * Gets a double from the database.
     * 
     * @param sql
     * @return double
     * 
     * @since 0.1
     */
    public static double getDouble(String sql) {
        try {
            ResultSet result = getResultSet(sql);
            result.next();
            return result.getDouble(1);
        } catch (SQLException ex) {
            sqlErrors(ex);
        }
        return 0;
    }
    
    /**
     * Gets a boolean from the database.
     * 
     * @param sql
     * @return boolean
     * 
     * @since 0.1
     */
    public static boolean getBoolean(String sql) {
        try {
            ResultSet result = getResultSet(sql);
            result.next();
            boolean returnValue = result.getBoolean(1);
            return returnValue;
        } catch (SQLException ex) {
            sqlErrors(ex);
        }
        return false;
    }
    
    /**
     * Gets all of the values in a specified column as a list.
     * 
     * @param sql
     * @return List of column values
     */
    public static List getColumn(String sql) {
        List<String> coldata = new ArrayList<String>();
        try {
            ResultSet result = getResultSet(sql);
            while (result.next()) {
                coldata.add(result.getString(1));
            }
            return coldata;
        } catch (SQLException ex) {
            sqlErrors(ex);
        }
        return null;
    }
    
    /**
     * Gets an option as an integer.
     * 
     * @param option
     * @return Option value as an int
     * 
     * @since 0.1
     */
    public static int getOptInteger(String option) {
        return Integer.parseInt(getOptString(option));
    }    
    
    /**
     * Gets an option as a double.
     * 
     * @param option
     * @return Option value as a double
     * 
     * @since 0.1
     */
    public static double getOptDouble(String option) {
        return Double.parseDouble(getOptString(option));
    }    
    
    /**
     * Gets an option as a string.
     * 
     * @param option
     * @return Option value as a string
     * 
     * @since 0.1
     */
    public static String getOptString(String option) {
        return getString("SELECT op_data FROM " + prefix + "plotzy_options WHERE op_name = '" + option + "'");
    }    
    
    /**
     * Gets an option as a boolean.
     * 
     * @param option
     * @return Option value as a boolean
     * 
     * @since 0.1
     */
    public static boolean getOptBoolean(String option) {
        return Boolean.parseBoolean(getOptString(option));
    }
}