package modelDistribute;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class MDServer implements Serializable {
    public static final String BACKWARD_GRAD_LISTEN_PORT = "backward_grad_listen_port";
    public static final String FORWARD_RESULT_LISTEN_PORT = "forward_result_listen_port";
    public final String ip;
    public final HashMap<String, Integer> portMp;
    public MDServer(String ip, HashMap<String, Integer> portMp){
        this.ip = ip;
        this.portMp = portMp;
    }
    public boolean addPort(String usage, Integer portNum){
        if(this.portMp.containsKey(usage)) return false;
        this.portMp.put(usage, portNum);
        return true;
    }

    public boolean removePort(String usage, Integer portNum){
        if(!this.portMp.containsKey(usage)) return false;
        this.portMp.remove(usage);
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MDServer)) return false;
        MDServer mdServer = (MDServer) o;
        return Objects.equals(ip, mdServer.ip) && Objects.equals(portMp, mdServer.portMp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, portMp);
    }
}
