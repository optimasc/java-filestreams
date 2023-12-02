package com.optimasc.streams;

/**
 * This interface declares the constants used in this API.
 * Numbers in the range 0 to 256 are reserved for the specification,
 * user defined events must use event codes outside that range.
 */
public interface DocumentStreamConstants
{
    /**
     * Indicates an event is a start leaf element. 
     */
    public static final int START_ELEMENT=1;
    /**
     * Indicates an event is an end leaf element.
     */
    public static final int END_ELEMENT=2;
    
    /**
     * Indicates an event that is binary data.
     */
    public static final int DATA=4;

    /**
     * Indicates an event is a start of a document.
     */
    public static final int START_DOCUMENT=7;

    /**
     * Indicates an event is an end of a document.
     */
    public static final int END_DOCUMENT=8;
    
    /**
     * Indicates an event is a start of a node / group.
     */
    public static final int START_GROUP=9;
    /**
     * Indicates an event is an end of a node / group.
     */
    public static final int END_GROUP=10;
    
}
