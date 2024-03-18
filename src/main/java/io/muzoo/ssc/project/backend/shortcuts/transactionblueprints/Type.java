package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints;


public enum Type {
    
    FAVORITES,
    RECURRING,
    NONE,
    ;

    public static Type getType(String type){
        try{
            return valueOf(type);
        }
        catch (NullPointerException | IllegalArgumentException e){
            e.printStackTrace();
            return NONE;
        }
    }

}
