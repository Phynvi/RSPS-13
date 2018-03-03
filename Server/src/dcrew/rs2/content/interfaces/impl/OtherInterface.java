package dcrew.rs2.content.interfaces.impl;

import dcrew.rs2.content.interfaces.InterfaceHandler;
import dcrew.rs2.entity.player.Player;

/**
 * Handles the other teleport interface
 * @author Daniel
 *
 */
public class OtherInterface extends InterfaceHandler {
	
	public OtherInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"Membership Zone",
			"Staff Zone",
			"Relaxation Zone",

			
	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 61551;
	}

}

