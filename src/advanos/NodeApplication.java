package advanos;

import advanos.gui.DancingGUIFrame;
import advanos.messages.Message;
import advanos.messages.instances.SendNetworkBroadcastMessage;
import advanos.messages.receive.ReceiveUDPMessageParser;
import advanos.messages.receive.ReceivedUDPMessageHandler;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class NodeApplication {

	private DancingGUIFrame gui;

	private final BehaviorSubject<ApplicationState> applicationState;

	public NodeApplication() throws UnknownHostException {
		applicationState = BehaviorSubject.createDefault(new ApplicationState());

		gui = new DancingGUIFrame(applicationState);
		gui.setVisible(true);
		applicationState
				.map(state -> state.processId)
				.distinct()
				.filter(Objects::nonNull)
				.map(processId -> processId + " " + gui.getTitle())
		.subscribe(title -> gui.setTitle(title));

		applicationState.concatMap(state -> createNetworkBroadcastThread())
			.subscribe((succeeded) -> System.out.println("Successfully broadcasted presence = " + succeeded), System.err::println);

		applicationState.concatMap(state -> createReceiveUDPThread(state.application.getUDPPort(), state.group))
				.concatMap(message -> new ReceivedUDPMessageHandler(message, applicationState).perform())
			.subscribe(message -> {

			});
	}

	private Observable<Boolean> createNetworkBroadcastThread() {
		return new SendNetworkBroadcastMessage(applicationState).perform();
	}

	private Observable<Message> createReceiveUDPThread(int port, InetAddress group) {
		ReceiveUDPMessageParser receiveUDPMessageParser = new ReceiveUDPMessageParser(port, group);
		return receiveUDPMessageParser.perform();
	}
}