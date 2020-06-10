package advanos.messages.receive;

import lombok.Data;

@Data
public class SetNextInTokenRing {
    String ipAddress;

    public void setIpAddress(String ipAddress) {
        System.out.println("I now know who is next after me: " + ipAddress);
        this.ipAddress = ipAddress;
    }
}
