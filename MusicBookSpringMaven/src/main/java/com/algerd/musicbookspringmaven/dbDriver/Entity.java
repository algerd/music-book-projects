
package com.algerd.musicbookspringmaven.dbDriver;

public abstract class Entity {
       
    public abstract int getId();
    
    public abstract void setId(int id);
  
    public abstract String getName();  

    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Entity) {
            Entity entity = (Entity) obj;
            if (entity.getId() == getId() && entity.getName().equals(getName())) {
                return true;
            }
        }    
		return false;
	}
   
}
