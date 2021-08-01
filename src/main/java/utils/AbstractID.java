package utils;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractID {
    private UUID ID;
    public AbstractID(){
        this.ID = UUID.randomUUID();
    }
    public AbstractID(long mostSigBits, long leastSigBits){
        this.ID = new UUID(mostSigBits, leastSigBits);
    }
    public UUID getID(){
        return this.ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof AbstractID){
            AbstractID that = (AbstractID) o;
            return Objects.equals(ID, that.ID);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
