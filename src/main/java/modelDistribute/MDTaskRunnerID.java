package modelDistribute;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class MDTaskRunnerID implements Serializable {
    public MDServer mdServer;
    public UUID uuid;
    public MDTaskRunnerID(MDServer mdServer){
        this.uuid = UUID.randomUUID();
        this.mdServer = mdServer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MDTaskRunnerID)) return false;
        MDTaskRunnerID that = (MDTaskRunnerID) o;
        return Objects.equals(mdServer, that.mdServer) && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mdServer, uuid);
    }
}
