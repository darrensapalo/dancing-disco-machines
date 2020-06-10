package advanos.messages.receive;

import advanos.ApplicationState;
import advanos.Host;
import advanos.messages.Message;
import advanos.messages.instances.SendRingRequestUDPMessage;
import advanos.messages.instances.SendTokenConfirmedUDPMessage;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import java.util.LinkedList;

public class ReceiveSentToken {

    private final BehaviorSubject<ApplicationState> applicationState$;

    public ReceiveSentToken(BehaviorSubject<ApplicationState> applicationState$) {
        this.applicationState$ = applicationState$;
    }

    public void addDiscoveredHost(Host host) {

        ApplicationState currentState = this.applicationState$.getValue();
        if(currentState != null) {
            LinkedList<Host> hosts = currentState.getHosts();

            boolean exists = hosts.stream().anyMatch(h -> h.getProcessID().equalsIgnoreCase(host.getProcessID()));

            if (!exists) {
                System.out.println("Adding newly discovered host");
                hosts.add(host);
                this.applicationState$.onNext(currentState);
            }
        }
//
//        if (!hosts.contains(newHost)) {
//            Host lastHost = getLastHost();
//            hosts.add(newHost);
//
//            //		     [0]                    [1]                 [2]
//            // BROADCAST_ALIVE (IP ADDRESS AND PROCESS ID) <LEADER>
//            if (text.length == 3 && text[2].trim().equalsIgnoreCase("LEADER")){
//                newHost.setIsLeader(true);
//                this.leader = newHost;
//
//            }
//
//            if (NodeApplication.IS_LEADER){
//                System.err.println("Beginning to fix network topology...");
//                if (lastHost != null){
//                    if (lastHost.equals(this.leader)){
//                        System.err.println("Leader assigned to first node.");
//                        this.next = newHost;
//                    }else{
//                        System.err.println("The last node, " + lastHost + " is attached to the new node, " + newHost);
//                        assignNextInTokenRing(lastHost, newHost);
//                    }
//                }
//
//
//                if (this.leader != null && !newHost.equals(this.leader)){
//                    System.err.println("Since I already know who the leader is, I'll assign the newest node to send to the leader.");
//                    assignNextInTokenRing(newHost, this.leader);
//                }else{
//                    System.err.println("I still don't know the leader, or maybe the new host is the leader. I'll just assign myself to send to myself.");
//                    this.next = newHost;
//                    // System.out.println("End of reassignment");
//                }
//
//                attemptToDance();
//            }
//        }

    }

    public Observable<Boolean> receiveSentToken(Message message) {

        Host source = message.getSource();
        String[] messageParts = message.getMessage().split(" ");
        source.setProcessID(messageParts[1]);

        this.addDiscoveredHost(source);

        Observable<Boolean> sendConfirm$ = this.applicationState$.map(ApplicationState::getNext)
                .concatMap(nextHost -> new SendTokenConfirmedUDPMessage(nextHost).perform());

        Observable<Boolean> requestNextFromLeader$ = this.applicationState$.map(ApplicationState::getLeader)
                .concatMap(leader -> new SendRingRequestUDPMessage(leader).perform());

        return Observable.empty();
    }
}
