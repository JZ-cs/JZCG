package TestDataDistribute;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class testIP {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        System.out.println(addr.getHostAddress());
    }
}
