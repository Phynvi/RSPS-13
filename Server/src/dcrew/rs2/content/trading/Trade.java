package dcrew.rs2.content.trading;

import java.text.NumberFormat;
import java.util.HashMap;

import dcrew.core.util.GameDefinitionLoader;
import dcrew.core.util.Utility;
import dcrew.core.util.NameUtil;
import dcrew.core.util.logger.PlayerLogger;
import dcrew.rs2.entity.item.Item;
import dcrew.rs2.entity.player.Player;
import dcrew.rs2.entity.player.net.out.impl.SendInventoryInterface;
import dcrew.rs2.entity.player.net.out.impl.SendMessage;
import dcrew.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import dcrew.rs2.entity.player.net.out.impl.SendString;

public class Trade {
	public static enum States {
		None,
		Trade,
		TradeAccepted,
		Confirmation,
		ConfirmationAccepted
	}
	
	protected final Player player;
	protected Trade tradingWith;
	protected States state = States.None;
	protected TradeContainer container = new TradeContainer(this);
	protected String lastRequest;
	
	public Trade(Player player) { this.player = player; }

	public static String coinsToString(int amount) {
		if (amount >= 10000000)
			return ((amount / 1000) + "M");
		else if ((amount >= 100000) && (amount < 1000000))
			return ((amount / 1000) + "K");
		return (NumberFormat.getIntegerInstance().format(amount) + " gp");
	}

	public static void sendItemText(Player player) {
		Item[] traded = player.getTrade().getTradedItems();
		Item[] recieving = player.getTrade().getTradingWith().getTradedItems();
		StringBuilder trade = new StringBuilder();
		boolean empty = true;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			Item item = traded[i];
			if (item != null) {
				empty = false;
				trade.append(item.getDefinition().getName());
				trade.append(" x ");
				trade.append(coinsToString(item.getAmount()));
				trade.append("\\n");
			}
		}
		if (empty)
			trade.append("Absolutely nothing!");
		player.getClient().queueOutgoingPacket(new SendString(trade.toString(), 3557));
		trade = new StringBuilder();
		empty = true;
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			Item item = recieving[i];
			if (item != null) {
				empty = false;
				trade.append(item.getDefinition().getName());
				trade.append(" x ");
				trade.append(coinsToString(item.getAmount()));
				trade.append("\\n");
			}
		}
		if (empty)
			trade.append("Absolutely nothing!");
		player.getClient().queueOutgoingPacket(new SendString(trade.toString(), 3558));
	}

	@SuppressWarnings("incomplete-switch")
	public void accept() {
		if ((System.currentTimeMillis() - player.tradeDelay) < 3500) {
			player.send(new SendMessage("@red@The trade screen has been modified! Please wait.."));
			return;
		}
		if (state == States.Trade) {
			if (!player.getInventory().hasSpaceFor(tradingWith.getTradedItems())) {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to make this " + getAction() + "."));
				return;
			}
			state = States.TradeAccepted;
			player.getClient().queueOutgoingPacket(new SendString("Waiting for other player..", 3431));
			tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString("Other player has accepted.", 3431));
		} else if (state == States.Confirmation) {
			state = States.ConfirmationAccepted;
			player.getClient().queueOutgoingPacket(new SendString("Waiting for other player..", 3535));
			tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString("Other player has accepted.", 3535));
		}
		if ((tradingWith != null) && tradingWith.accepted())
			switch (state) {
				case TradeAccepted:
					state = States.Confirmation;
					tradingWith.setState(States.Confirmation);
					sendItemText(player);
					sendItemText(tradingWith.getPlayer());
					container.update();
					player.getClient().queueOutgoingPacket(new SendInventoryInterface(3443, 3213));
					tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendInventoryInterface(3443, 3213));
					break;
				case ConfirmationAccepted:
					end(true);
					state = States.None;
					tradingWith.setState(States.None);
					tradingWith.reset();
					reset();
					break;
			}
	}

	public boolean canAppendTrade() { return ((state == States.Trade) || (state == States.TradeAccepted)); }
	public boolean accepted() { return ((state == States.TradeAccepted) || (state == States.ConfirmationAccepted)); }

	public void begin(Trade tradingWith) {
		player.getClient().queueOutgoingPacket(new SendString(getStatus() + " with: " + NameUtil.uppercaseFirstLetter(tradingWith.getPlayer().getUsername()), 3417));
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString(getStatus() + " with: " + NameUtil.uppercaseFirstLetter(player.getUsername()), 3417));
		player.getClient().queueOutgoingPacket(new SendString("", 3431));
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString("", 3431));
		player.getClient().queueOutgoingPacket(new SendString("Are you sure you want to make this " + getAction() + "?", 3535));
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendString("Are you sure you want to make this " + getAction() + "?", 3535));
		player.getClient().queueOutgoingPacket(new SendInventoryInterface(3323, 3321));
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendInventoryInterface(3323, 3321));
		reset();
		tradingWith.reset();
		this.tradingWith = tradingWith;
		tradingWith.setTradingWith(this);
		state = States.Trade;
		tradingWith.setState(States.Trade);
		container.update();
		tradingWith.container.update();
	}

	public boolean clickTradeButton(int buttonId) {
		switch (buttonId) {
			case 13218:
				accept();
				return true;
			case 13092:
				accept();
				return true;
		}
		return false;
	}

	public void end(boolean success) {
		Item[] traded = getTradedItems();
		Item[] recieving = tradingWith.getTradedItems();
		HashMap<Integer, Integer> trade = new HashMap<>();
		HashMap<Integer, Integer> recieved = new HashMap<>();
		for (int i = 0; i < 28; i++) {
			if (success) {
				if (traded[i] != null) {
					tradingWith.getPlayer().getInventory().insert(traded[i]);
					if (trade.get(traded[i].getId()) != null)
						trade.put(traded[i].getId(), traded[i].getAmount() + trade.get(traded[i].getId()));
					else
						trade.put(traded[i].getId(), traded[i].getAmount());
				}
				if (recieving[i] != null) {
					player.getInventory().insert(recieving[i]);
					if (recieved.get(recieving[i].getId()) != null)
						recieved.put(recieving[i].getId(), recieving[i].getAmount() + recieved.get(recieving[i].getId()));
					else
						recieved.put(recieving[i].getId(), recieving[i].getAmount());
				}
			} else {
				if (traded[i] != null)
					player.getInventory().insert(traded[i]);
				if (recieving[i] != null)
					tradingWith.getPlayer().getInventory().insert(recieving[i]);
			}
		}
		String[][][] strings = new String[2][trade.size()][4];
		int index = 0;
		for (int item : trade.keySet()) {
			Item tradedItem = new Item(item, trade.get(item));
			strings[0][index] = new String[] { Utility.formatPlayerName(player.getUsername()), "" + tradedItem.getAmount(), Utility.formatPlayerName(tradedItem.getDefinition().getName()), Utility.formatPlayerName(tradingWith.player.getUsername()) };
			strings[1][index] = new String[] { Utility.formatPlayerName(tradingWith.player.getUsername()), "" + tradedItem.getAmount(), Utility.formatPlayerName(tradedItem.getDefinition().getName()), Utility.formatPlayerName(player.getUsername()) };
			index++;
		}
		PlayerLogger.TRADE_LOGGER.multiLog(player.getUsername(), "%s has given %s %s to %s", strings[0]);
		PlayerLogger.TRADE_LOGGER.multiLog(tradingWith.player.getUsername(), "%s has received %s %s from %s", strings[1]);
		strings = new String[2][recieved.size()][4];
		index = 0;
		for (int item : recieved.keySet()) {
			Item tradedItem = new Item(item, recieved.get(item));
			strings[0][index] = new String[] { Utility.formatPlayerName(tradingWith.player.getUsername()), "" + tradedItem.getAmount(), Utility.formatPlayerName(tradedItem.getDefinition().getName()), Utility.formatPlayerName(player.getUsername()) };
			strings[1][index] = new String[] { Utility.formatPlayerName(player.getUsername()), "" + tradedItem.getAmount(), Utility.formatPlayerName(tradedItem.getDefinition().getName()), Utility.formatPlayerName(tradingWith.player.getUsername()) };
			index++;
		}
		PlayerLogger.TRADE_LOGGER.multiLog(tradingWith.player.getUsername(), "%s has given %s %s to %s", strings[0]);
		PlayerLogger.TRADE_LOGGER.multiLog(player.getUsername(), "%s has received %s %s from %s", strings[1]);
		if (success) {
			player.getClient().queueOutgoingPacket(new SendMessage("The trade has been accepted."));
			tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendMessage("The trade has been accepted."));
		} else {
			player.getClient().queueOutgoingPacket(new SendMessage("You decline the " + getAction() + "."));
			tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendMessage("The other player declined the " + getAction() + "."));
		}
		player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		tradingWith.getPlayer().getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		player.getInventory().update();
		tradingWith.getPlayer().getInventory().update();
		state = States.None;
		tradingWith.setState(States.None);
	}

	public String getAction() { return "trade"; }
	public TradeContainer getContainer() { return container; }
	public Player getPlayer() { return player; }
	public String getRequestString() { return "trade"; }
	public States getState() { return state; }
	public String getStatus() { return "Trading"; }
	public Item[] getTradedItems() { return container.getItems(); }
	public Trade getTradingWith() { return tradingWith; }

	public void request(Player requested) {
		if (requested.isBusy() || player.isBusy()) {
			player.getClient().queueOutgoingPacket(new SendMessage("The other player is busy at the moment."));
			return;
		}
		if (!player.getController().canTrade()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You can't trade right now."));
			return;
		}
		if ((requested.getTrade().trading())) {
			player.getClient().queueOutgoingPacket(new SendMessage("The other player is busy at the moment."));
			return;
		}
		if (player.ironPlayer()) {
			player.send(new SendMessage("You are in Iron-Man mode and cannot trade!"));
			return;
		}
		if (requested.ironPlayer()) {
			player.send(new SendMessage(requested.getUsername() + " is in Iron-Man mode and cannot trade!"));
			return;
		}
//		if (requested.getRights() == 2) {
//			player.send(new SendMessage("You may not trade Administrators!"));
//			return;
//		}
//		if (player.getRights() == 2) {
//			player.send(new SendMessage("You may not trade since you are an Administrator!"));
//			return;
//		}
		player.getClient().queueOutgoingPacket(new SendMessage("Sending " + getRequestString() + " offer.."));
		lastRequest = requested.getUsername();
		if (requested.getTrade().requested(player))
			begin(requested.getTrade());
		else if (!requested.getPrivateMessaging().ignored(player.getUsername()))
			requested.getClient().queueOutgoingPacket(new SendMessage(NameUtil.uppercaseFirstLetter(player.getUsername()) + ":tradereq:"));
	}

	public boolean requested(Player other) {
		if (lastRequest == null)
			return false;
		return lastRequest.equalsIgnoreCase(other.getUsername());
	}

	public void setState(States state) { this.state = state; }
	public void setTradingWith(Trade tradingWith) { this.tradingWith = tradingWith; }
	public void reset() {
		container = new TradeContainer(this);
		state = States.None;
		tradingWith = null;
		lastRequest = null;
	}

	public boolean trading() { return (state != States.None); }
}