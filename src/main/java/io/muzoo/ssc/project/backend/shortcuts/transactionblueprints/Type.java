package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints;


public enum Type {
    
    FAVORITES,
    RECURRING,
    NONE,
    ;

    public Type getType(String type){
        try{
            return valueOf(type);
        }
        catch (NullPointerException | IllegalArgumentException e){
            e.printStackTrace();
            return NONE;
        }
    }

}
