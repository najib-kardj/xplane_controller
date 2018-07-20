/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.irit.ics.circus.hamsters.xplanecontroler.events;

import java.util.EventObject;

/**
 *
 * @author Alexandre Canny <Alexandre.Canny@irit.fr>
 */
public class DataRefChangedEvent extends EventObject{
    
    private int id;
    private String dataRef;
    private float value;
    
    public DataRefChangedEvent(Object source) {
        super(source);
    } 
    
    public DataRefChangedEvent(Object source, int id, String dataRef, float value){
        this(source);
        this.id = id;
        this.dataRef = dataRef;
        this.value = value;
    }

    @Override
    public Object getSource() {
        return super.getSource(); //To change body of generated methods, choose Tools | Templates.
    }    
    
    public int getId() {
        return id;
    }

    public String getDataRef() {
        return dataRef;
    }

    public float getValue() {
        return value;
    }   
    
}
