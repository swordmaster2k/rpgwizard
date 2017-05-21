/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.utilities;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.System.out;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;

/**
 * Utility to simplify JSON I/O. View source and scroll to the bottom for
 * example of how to instantiate a class from a JSON file.
 *
 * @author Joel Moore
 */
public class JSON {
    
    public static final int TAB_WIDTH = 4;

    public static String toPrettyJSON(JSONStringer out) {
        return new JSONObject(out.toString()).toString(TAB_WIDTH);
    }
    
    /**
     * Saves an object to disk at the File provided, calling the object's
     * toJSONString() method to get the JSON data to write to disk.
     * @param object
     * @param f
     * @return whether the save succeeded
     */
    public static boolean save(Saveable object, File f) {
        String output;
        try {
            output = object.toJSONString();
        } catch(Exception ex) {
            out.println("JSON data saving failed: " + ex);
            out.println("...while attempting to prepare save data for "
                    + object + ", which was going to be saved to " + f);
            out.println("...Saving canceled.");
            //Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            writer.write(output);
            out.println("JSON written to " + f.getPath());
            return true;
        } catch(IOException ex) {
            out.println("JSON data saving failed: " + ex);
            out.println("...while attempting to save " + object + " to " + f);
            //Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Loads a JSON object from disk at the File provided.
     * @param f
     * @return the JSONObject that was loaded, or null if load failed
     */
    public static JSONObject load(File f) {
        try (BufferedReader reader = new BufferedReader(new FileReader(f))){
            StringBuilder source = new StringBuilder();
            String line = reader.readLine();
            while(line != null) {
                source.append(line);
                line = reader.readLine();
            }
//            out.println(source.toString());
            JSONObject json = new JSONObject(source.toString());
            out.println("JSON read from " + f.getPath());
//            out.println(json.toString());
            return json;
        } catch(IOException | JSONException ex) {
            out.println("JSON data loading failed: " + ex);
            out.println("...while attempting to load " + f);
            //Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Turns a Color object into a JSONObject for saving.
     * @param c
     * @return a new JSONObject made from the Color object
     */
    public static JSONObject color(Color c) {
        JSONStringer s = new JSONStringer();
        s.object();
        s.key("r").value(c.getRed());
        s.key("g").value(c.getGreen());
        s.key("b").value(c.getBlue());
        s.key("a").value(c.getAlpha());
        s.endObject();
        return new JSONObject(s.toString());
    }
    /**
     * Retrieves a Color stored as a JSONObject.
     * @param json
     * @return
     */
    public static Color color(JSONObject json) {
        return new Color(
                json.getInt("r"),
                json.getInt("g"),
                json.getInt("b"),
                json.getInt("a")
        );
    }
    
    /**
     * Indicates that the class can be saved with JSON and that it
     * has a method to populate a JSON object without ending it, suitable for
     * overriding by subclasses that need to add additional data while saving.
     * See method Javadocs for other requirements. If the class shouldn't be
     * saveable to its own file, but needs to be saveable within other classes
     * that use it, just implement org.json.JSONString.
     */
    public interface Saveable extends JSONString {

        /**
         * Saves to disk using JSON. Should call JSON.save(), providing self and
         * a File to write to. Overriding subclasses should also call
         * super.populateJSON() so that they can add in the superclass's data.
         *
         * @return whether the save succeeded
         */
        public boolean save();

        /**
         * Given a JSONStringer that has started making a JSON object, add this
         * class's data to that object without ending the object. Subclasses
         * that override toJSONString() should also override this, calling both
         * super.populateJSON() and populateJSON() from toJSONString() to add
         * the parent class's info to the JSON object alongside the subclass's
         * info.
         *
         * @param json
         */
        public void populateJSON(JSONStringer json);
        
    }

    ////////////////////////////////////////////
    // INSTANTIATING A CLASS FROM A JSON FILE //
    ////////////////////////////////////////////
    // Example: //
    /////////////
    //public class Person implements JSON.Saveable {
    /*
    public Person(File f) {
        //...
        this.harvestJSON(JSON.load(f));
        //...
    }
    
    @Override
    public boolean save() {
        File f = new File(filePath);
        return JSON.save(this, f);
    }
    
    /**
     * Create a new Person from a file and return it.
     *
     * @param fileName the filename (in the format "filename") to load from; the
     * rest of the path to that file and the file extension are added
     * automatically
     * @return a new Person
     */
    /*
    public static Person open(String fileName) {
        File f = new File(GameBase.assetsDir + charactersDir + fileName + sep
                + fileName + ".chr");
        return new Person(f);
    }
    
    @Override
    public String toJSONString() {
        JSONStringer json = new JSONStringer();
        json.object();
        this.populateJSON(json);
        json.endObject();
        return JSON.toPrettyJSON(json);
    }
    
    @Override
    public void populateJSON(JSONStringer json) {
        json.key("name").value(this.name);
        json.key("race").value(this.race);
        json.key("jump").value(this.jump);
        //...etc
    }

    /**
     * Given a JSONObject, set this class's data based on that object.
     * Subclasses that override toJSONString() can also override this, calling
     * super.populateJSON() to add the parent class's info to the JSON object
     * alongside the subclass's info.
     *
     * @param json
     */
    /*
    private void harvestJSON(JSONObject json) {
        this.name = json.optString("name", "???");
        this.race = json.getString("race");
        this.jump = json.optInt("jump", 1);
        //...etc
    }
    }
    
    ////////////////////////
    // Example Subclass: //
    //////////////////////
    //public class Combatant extends Person {
    /**
     * Create a new Combatant from a file and return it.
     *
     * @param fileName the filename (in the format "filename") to load from;
     * the rest of the path to that file is calculated automatically
     * @return a new Combatant
     */
    /*
    public static Combatant open(String fileName) {
        File f = new File(GameBase.assetsDir + charactersDir + fileName + sep
                + fileName + ".chr");
        return new Combatant(f);
    }
    
    public Combatant(File f) {
        super(f);
        this.harvestJSON(JSON.load(f));
        //...
    }
    
    @Override
    public String toJSONString() {
        JSONStringer json = new JSONStringer();
        json.object();
        super.populateJSON(json);
        this.populateJSON(json);
        json.endObject();
        return JSON.toPrettyJSON(json);
    }
    
    @Override
    public void populateJSON(JSONStringer json) {
        json.key("hp").value(this.maxHealth);
        json.key("charge").value(this.maxCharge);
        //...etc
        
        json.key("abilities").array();
        for(Ability a : this.abilityList) {
            json.value(a.name);
        }
        json.endArray();
        //...etc
    }
    private void harvestJSON(JSONObject json) {
        this.maxHealth = json.optInt("hp", 100);
        this.maxCharge = json.optInt("charge", 100);
        //..etc

        JSONArray array = json.optJSONArray("abilities");
        if(array != null) {
            for(int i = 0; i < array.length(); i++) {
                this.abilityList.add(new Ability(array.getString(i)));
            }
        }
        //...etc
    }
    }
    */
    
}
