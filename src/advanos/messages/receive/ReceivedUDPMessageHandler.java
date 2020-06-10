package advanos.messages.receive;

import advanos.ApplicationState;
import advanos.Host;
import advanos.messages.Message;
import advanos.messages.Messager;
import advanos.messages.instances.SendRingAssignmentUDPMessage;
import advanos.messages.instances.SendTokenConfirmedUDPMessage;
import advanos.messages.instances.SendTokenUDPMessage;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;


public class ReceivedUDPMessageHandler implements Messager<Boolean> {
	private final Message message;
	private final BehaviorSubject<ApplicationState> applicationState$;

	public ReceivedUDPMessageHandler(Message message, BehaviorSubject<ApplicationState> applicationState$) {
		this.message = message;
		this.applicationState$ = applicationState$;
		System.out.println(message);
	}

	@Override
	public synchronized Observable<Boolean> perform() {

		String[] messageParts = message.getMessage().split(" ");

		if (messageParts.length == 0)
			return Observable.error(new IllegalArgumentException("Passed message was invalid: " + message));

		String type = messageParts[0];
		switch (type) {

		case "CS_REQUEST":
			System.out.println("I don't know how to handle this message: " + type);
//			lamportMutexRequest(message, ipAddress);
			break;
		case "BROADCAST_ALIVE":
			return new ReceiveSentToken(this.applicationState$).receiveSentToken(message);

		case "CS_REPLY":
			System.out.println("I don't know how to handle this message: " + type);
//			lamportMutexReply(message, ipAddress);
			break;
		case "ASSIGN":

			Host nextHost = Host.builder()
				.ipAddress(messageParts[1])
				.UDPPort(4000) // TODO: It has to be correct port
				.build();

			ApplicationState currentState = this.applicationState$.getValue();

			ApplicationState newState = ApplicationState.builder()
					.application(currentState.getApplication())
					.hasToken(currentState.getHasToken())
					.hosts(currentState.getHosts())
					.leader(currentState.getLeader())
					.next(nextHost)
					.processId(currentState.getProcessId())
					.build();

			this.applicationState$.onNext(newState);

			return Observable.just(true);
			
		case "SEND":
			return this.applicationState$.map(applicationState -> applicationState.getNext().getProcessID())
				.concatMap(ipAddress -> new ReceiveSentToken(this.applicationState$).receiveSentToken(message));

			
		case "RECEIVED":
			System.out.println("I don't know how to handle this message: " + type);
//			nodeApplication.receiveSentTokenConfirmation(message, ipAddress);
			break;
			
		case "REQUEST":
			if (messageParts[1].equalsIgnoreCase("NEXT")) {

				return this.applicationState$.map(ApplicationState::getNext)
					.concatMap(host -> new SendRingAssignmentUDPMessage(host.getIpAddress(), host).perform());

			}
			break;
		}

		return Observable.error(new IllegalStateException("Unexpected state."));
	}

	private Observable<Boolean> releaseToken(Host next) {
		System.out.println("Releasing token, giving it to " + next);
		SendTokenUDPMessage sendTokenMessage = new SendTokenUDPMessage(next);
		return sendTokenMessage.perform();
	}

	private Observable<Boolean> confirmReceiptOfToken(Host host) {
		System.out.println("Received token from " + host + "! Confirming...");
		SendTokenConfirmedUDPMessage sendTokenConfirmedMessage = new SendTokenConfirmedUDPMessage(host);
		return sendTokenConfirmedMessage.perform();
	}
}

