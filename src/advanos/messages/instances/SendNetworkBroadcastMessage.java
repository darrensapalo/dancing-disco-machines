package advanos.messages.instances;

import advanos.ApplicationState;
import advanos.Host;
import advanos.messages.send.SendUDPMessage;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import java.util.concurrent.TimeUnit;

/**
 * This message regularly informs all members of the network that a new node is alive.
 */
public class SendNetworkBroadcastMessage {

	private final BehaviorSubject<ApplicationState> applicationState$;

	public SendNetworkBroadcastMessage(BehaviorSubject<ApplicationState> applicationState$)  {
		this.applicationState$ = applicationState$;

		// BROADCAST_ALIVE 4849@192.168.10.1
		// BROADCAST_ALIVE 3241@192.168.10.1 LEADER
	}

	public Observable<Boolean> perform() {

		Observable<Long> interval$ = Observable.interval(1000, TimeUnit.MILLISECONDS);

		return Observable.combineLatest(interval$, this.applicationState$, (interval, state) -> state)
				.concatMap(state -> new SendUDPMessage(this.buildBroadcastMessage(state), Host.buildFromState(state)).perform());
	}

	private String buildBroadcastMessage(ApplicationState state) {
		String LEADER_LABEL = state.getApplication().getIsLeader() ? "LEADER" : "";

		String broadcasting = String.format("BROADCAST_ALIVE %s %s", state.getProcessId(), LEADER_LABEL).trim();

		System.out.println(broadcasting);

		return broadcasting;
	}

}
