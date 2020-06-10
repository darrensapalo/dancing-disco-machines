package advanos;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class Host {

	private Integer TCPPort;

	private Integer UDPPort;
	@NonNull
	private String ipAddress;

	private String processID;

	@Builder.Default
	private Boolean isLeader = false;

	public static Host buildFromState(ApplicationState state) {
		return Host.builder()
				.ipAddress(state.application.getIpAddress())
				.UDPPort(state.application.getUDPPort())
				.build();
	}
}
