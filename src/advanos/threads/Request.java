package advanos.threads;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * This class is used for synchronization between
 * distributed systems. A Request class contains the
 * identifier (process id + IP address) and its
 * Lamport timestamp.
 * 
 * See also: http://en.wikipedia.org/wiki/Lamport_timestamps
 * https://www.youtube.com/watch?v=r7SJOhGF4Nc
 * @author Darren
 *
 */
@Builder
@RequiredArgsConstructor
public class Request {
	@NonNull
	private int timestamp;
	@NonNull
	private String uniqueProcessID;

}
