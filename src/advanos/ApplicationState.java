package advanos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

@Data
@Builder
@AllArgsConstructor
public class ApplicationState {

    /**
     * The host that describes this current application.
     */
    Host application;

    /**
     * A host description of the current leader.
     */
    Host leader;

    /**
     * A host description of the next application.
     */
    Host next;

    /**
     * The list of all the detected hosts.
     */
    LinkedList<Host> hosts;

    /**
     * Flag that determines whether or not this current application has the token.
     */
    @NonNull
    Boolean hasToken;


    /**
     * A string-based description of this application.
     */
    @NonNull
    String processId;

    /**
     * The network group of the set of applications.
     */
    InetAddress group;

    public ApplicationState() throws UnknownHostException {
        hosts = new LinkedList<>();
        hasToken = false;
        final String processID = ManagementFactory.getRuntimeMXBean().getName();
        application = Host.builder()
                .ipAddress(InetAddress.getLocalHost().getHostAddress())
                .UDPPort(4040)
                .processID(processID)
                .build();
        processId = processID;
        group = InetAddress.getByName("239.255.17.0");
    }
}
