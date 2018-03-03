import com.sun.javafx.geom.Rectangle;

public class LoginRenderer {
	public int announcementFade = 0;
	public int announcementMovement = (Client.frameWidth - 2);

	final Client client;
	int worldHover = -1;
	int worldsAmount[] = { 1 };
	String world[] = { (ClientConstants.name + " - World 1") };
	int ticks = 0;
	String[] announcements = { 
		("Welcome to " + ClientConstants.name + "!"), 
		("Developers: " + String.join(", ", ClientConstants.developerNames))
	};
	Rectangle worldInfoBox;

	public LoginRenderer(Client client) {
		this.client = client;
		worldInfoBox = new Rectangle(5, (Client.frameHeight - 5), 78, 39);
		worldInfoBox.y -= worldInfoBox.height;
	}

	public void displayLoginScreen() {
		final int centerX = Client.frameWidth / 2, centerY = Client.frameHeight / 2;
		client.resetImageProducers();
		client.refreshFrameSize();
		Client.aRSImageProducer_1109 = new ImageProducer(Client.frameWidth, Client.frameHeight);
		Client.aRSImageProducer_1109.initDrawingArea();
		if (client.loginScreenState == 0) {
			Client.cacheSprite[6].drawARGBSprite((Client.frameWidth / 2) - (Client.cacheSprite[6].myWidth / 2), (Client.frameHeight / 2) - (Client.cacheSprite[6].myHeight / 2));
			Client.cacheSprite[Client.frameMode == Client.ScreenMode.FIXED || client.mouseInRegion(centerX - 83, centerY + 160, centerX - 30, centerY + 197) ? 1 : 0].drawSprite(centerX - 83, centerY + 161);
			Client.cacheSprite[Client.frameMode == Client.ScreenMode.RESIZABLE || client.mouseInRegion(centerX - 23, centerY + 160, centerX + 30, centerY + 197) ? 3 : 2].drawSprite(centerX - 23, centerY + 161);
			Client.cacheSprite[Client.frameMode == Client.ScreenMode.FULLSCREEN || client.mouseInRegion(centerX + 40, centerY + 160, centerX + 90, centerY + 197) ? 5 : 4].drawSprite(centerX + 37, centerY + 161);
			if (client.mouseInRegion(centerX - 64, centerY + 34, centerX + 69, centerY + 77))
				Client.cacheSprite[7].drawSprite(centerX - 64, centerY + 34);
			if (client.mouseInRegion(centerX - 108, centerY - 82, centerX + 143, centerY - 55))
				Client.cacheSprite[8].drawSprite(centerX - 108, centerY - 82);
			if (client.mouseInRegion(centerX - 108, centerY - 36, centerX + 143, centerY - 9))
				Client.cacheSprite[8].drawSprite(centerX - 108, centerY - 36);
			if (client.mouseInRegion(centerX - 138, centerY + 2, centerX - 121, centerY + 19))
				Client.cacheSprite[9].drawSprite(centerX - 138, centerY);
			if (Client.rememberMe)
				Client.cacheSprite[10].drawSprite(centerX - 138, centerY);
			displayAnnounements();
			displayAccounts();
			Client.smallText.method389(true, centerX + 75, 0xB0AFAB, ("Client version " + ClientConstants.version), centerY + 100);
			Client.smallText.method389(true, centerX - 115, 0xB0AFAB, "Remember me?", centerY + 16);
			Client.smallText.method389(true, centerX + 40, client.mouseInRegion(centerX + 40, centerY + 7, centerX + 127, centerY + 17) ? 0xFFFFFF : 0xB0AFAB, "Forgot password?", centerY + 16);
			Client.smallText.method389(true, centerX - 102, 0xB0AFAB, TextClass.capitalize(Client.myUsername) + ((client.loginScreenCursorPos == 0) & (Client.loopCycle % 40 < 20) ? "|" : ""), centerY - 63);
			if (client.mouseInRegion(centerX - 138, centerY - 37, centerX - 112, centerY - 11))
				Client.smallText.method389(true, centerX - 102, 0xB0AFAB, Client.myPassword + ((client.loginScreenCursorPos == 1) & (Client.loopCycle % 40 < 20) ? "|" : ""), centerY - 17);
			else
				Client.smallText.method389(true, centerX - 102, 0xB0AFAB, TextClass.passwordAsterisks(Client.myPassword) + ((client.loginScreenCursorPos == 1) & (Client.loopCycle % 40 < 20) ? "|" : ""), centerY - 17);
			if (client.loginMessage1.length() > 0) {
				Client.smallText.method382(0xB0AFAB, centerX + 4, client.loginMessage1, centerY + 135, true);
				Client.smallText.method382(0xB0AFAB, centerX + 4, client.loginMessage2, centerY + 145, true);
			} else
				Client.smallText.method382(0xB0AFAB, centerX + 4, client.loginMessage2, centerY + 140, true);
		}
		if (client.loginScreenState == 1) {
			Client.cacheSprite[421].drawARGBSprite((Client.frameWidth / 2) - (Client.cacheSprite[421].myWidth / 2), (Client.frameHeight / 2) - (Client.cacheSprite[421].myHeight / 2));
			Client.cacheSprite[418].drawSprite(centerX - 189, centerY - 60);
			for (int index = 0, y = centerY + 117; index < worldsAmount.length; index++, y+= 24)
				displayWorlds(centerX + 180, y, 0xFFFFF, worldsAmount[index], world[index]);
		}
		drawWorldInfo();
		Client.aRSImageProducer_1109.drawGraphics(0, client.graphics, 0);
	}

	public void displayWorlds(int x, int y, int textColor, int world, String worldType) {
		if (ClientConstants.world == world)
			DrawingArea.drawAlphaPixels(x - 315, y - 136, 338, 16, 0xB24D00, 150);
		else if (worldHover == world)
			DrawingArea.drawAlphaPixels(x - 315, y - 136, 338, 16, 0x606060, 150);
		Client.newRegularFont.drawBasicString(Integer.toString(world), x - 337, y - 123, 0xFFFFFF, 0);
		Client.newRegularFont.drawBasicString(worldType, x - 315, y - 123, 0xFFFFFF, 10);
	}

	public void displayAccounts() {
		final int centerX = Client.frameWidth / 2, centerY = Client.frameHeight / 2;
		int y = centerY - 68;
		if (AccountManager.accounts != null) {
			for (int index = 0; index < 3; index++, y += 47) {
				if (index >= AccountManager.getAccounts().size())
            		break;
				Client.cacheSprite[398].drawARGBSprite(centerX + 315, y + 21);
				Client.cacheSprite[395].drawARGBSprite(centerX + 193, y + 21);
				Client.cacheSprite[399].drawARGBSprite(centerX + 185, y + 14);
				if (client.mouseInRegion(centerX + 192, y + 21, centerX + 312, y + 40))
					Client.cacheSprite[396].drawARGBSprite(centerX + 193, y + 21);
				if (client.mouseInRegion(centerX + 315, y + 21, centerX + 332, y + 40))
					Client.cacheSprite[397].drawARGBSprite(centerX + 315, y + 21);
				AccountData account = AccountManager.getAccounts().get(index);
				if (account != null) {
					int rights = account.rank - 1;
					Client.newRegularFont.drawCenteredString((rights != -1 ? "<img=" + rights + "> " : "") + TextClass.capitalize(account.username), centerX + 260, y + 35, (client.mouseInRegion(centerX + 212, y + 21, centerX + 312, y + 40)) ? 0xFFFFFF : 0xB0AFAB, 0);
				}
				if (account.uses != 0 && client.mouseX >= centerX + 192 && client.mouseX <= centerX + 212 && client.mouseY >= y + 21 && client.mouseY <= y + 40)
					client.drawTooltip(client.mouseX + 11, client.mouseY - 20, "Account uses: " + account.uses);
				Client.newRegularFont.drawCenteredString("Clear Account List", centerX + 260, centerY - 57, (client.mouseInRegion(centerX + 210, centerY - 67, centerX + 311, centerY - 57)) ? 0xFFFFFF : 0xB0AFAB, 0);
			}
		}
	}

	public void processLoginScreen() {
		worldInfoBox = new Rectangle(5, (Client.frameHeight - 5), 78, 39);
		worldInfoBox.y -= worldInfoBox.height;
		int centerX = Client.frameWidth / 2, centerY = Client.frameHeight / 2;
		if ((client.clickMode3 == 1) && client.clickInRegion(worldInfoBox.x, worldInfoBox.y, (worldInfoBox.x + worldInfoBox.width - 1), (worldInfoBox.y + worldInfoBox.height - 1)))
			client.loginScreenState = ((client.loginScreenState == 0) ? 1 : 0);
		if (client.loginScreenState == 0) {
            if (client.clickMode3 == 1 && client.clickInRegion(centerX - 83, centerY + 160, centerX - 30, centerY + 197)) {
                Client.frameMode(Client.ScreenMode.FIXED);
        		worldInfoBox = new Rectangle(5, (Client.frameHeight - 5), 78, 39);
        		worldInfoBox.y -= worldInfoBox.height;
            }
            if (client.clickMode3 == 1 && client.clickInRegion(centerX - 23, centerY + 160, centerX + 30, centerY + 197)) {
            	Client.frameMode(Client.ScreenMode.RESIZABLE);
        		worldInfoBox = new Rectangle(5, (Client.frameHeight - 5), 78, 39);
        		worldInfoBox.y -= worldInfoBox.height;
            }
            if (client.clickMode3 == 1 && client.clickInRegion(centerX + 40, centerY + 160, centerX + 90, centerY + 197)) {
            	Client.frameMode(Client.ScreenMode.FULLSCREEN);
        		worldInfoBox = new Rectangle(5, (Client.frameHeight - 5), 78, 39);
        		worldInfoBox.y -= worldInfoBox.height;
            }
			if (client.clickMode3 == 1 && client.clickInRegion(centerX - 108, centerY - 82, centerX + 143, centerY - 55)) // Username
				client.loginScreenCursorPos = 0;
			if (client.clickMode3 == 1 && client.clickInRegion(centerX - 108, centerY - 36, centerX + 143, centerY - 9)) // Password
				client.loginScreenCursorPos = 1;
			if (client.clickMode3 == 1 && client.clickInRegion(centerX + 40, centerY + 7, centerX + 127, centerY + 17)) // Account Recovery
				;//client.launchURL("");
			if (client.clickMode3 == 1 && client.clickInRegion(centerX - 138, centerY + 2, centerX - 121, centerY + 19)) {
				Client.rememberMe = !Client.rememberMe;
				if (!Client.rememberMe) {
					Client.myUsername = "";
					Client.myPassword = "";
				}
				SettingHandler.save();
			}
			int y = centerY - 68;
			if (AccountManager.accounts != null) {
				for (int index = 0; index < AccountManager.getAccounts().size(); index++, y += 47) {
                    AccountData account = AccountManager.getAccounts().get(index);
                    if (client.clickMode3 == 1 && client.clickInRegion(centerX + 212, y + 21, centerX + 312, y + 40)) {
                    	if (account.username.length() > 0 && account.password.length() > 0) {
                            client.loginFailures = 0;
            				if (Client.myUsername != account.username || Client.myPassword != account.password) {
            					Client.myUsername = account.username;
            					Client.myPassword = account.password;
            				}
                            client.login(account.username, account.password, false);
                            if (Client.loggedIn)
                               return;
                    	}
                    }
                    if (client.clickMode3 == 1 && client.clickInRegion(centerX + 315, y + 21, centerX + 330, y + 40))
                    	AccountManager.removeAccount(account);
                }
				if (client.clickMode3 == 1 && client.clickInRegion(centerX + 210, centerY - 67, centerX + 311, centerY - 57))
					AccountManager.clearAccountList();
			}
			if (client.clickMode3 == 1 && client.clickInRegion(centerX - 64, centerY + 34, centerX + 69, centerY + 77)) { // Login
				if (Client.myUsername.length() > 0 && Client.myPassword.length() > 0) {
					client.loginFailures = 0;
					client.login(TextClass.capitalize(Client.myUsername), Client.myPassword, false);
					if (Client.loggedIn)
						return;
				} else {
					client.loginMessage1 = "";
					client.loginMessage2 = "Please enter a username and password.";
				}
			}
			do {
				int l1 = client.readChar(-796);
				if (l1 == -1)
					break;
				boolean flag1 = false;
				for (int i2 = 0; i2 < Client.validUserPassChars.length(); i2++) {
					if (l1 != Client.validUserPassChars.charAt(i2))
						continue;
					flag1 = true;
					break;
				}
				if (client.loginScreenCursorPos == 0) {
					if (l1 == 8 && Client.myUsername.length() > 0)
						Client.myUsername = Client.myUsername.substring(0, Client.myUsername.length() - 1);
					if (l1 == 9 || l1 == 10 || l1 == 13)
						client.loginScreenCursorPos = 1;
					if (flag1)
						Client.myUsername += (char) l1;
					if (Client.myUsername.length() > 12)
						Client.myUsername = TextClass.capitalize(Client.myUsername.substring(0, 12));
				} else if (client.loginScreenCursorPos == 1) {
					if (l1 == 8 && Client.myPassword.length() > 0)
						Client.myPassword = Client.myPassword.substring(0, Client.myPassword.length() - 1);
					if (l1 == 9 || l1 == 10 || l1 == 13)
						client.login(Client.myUsername, Client.myPassword, false);
					if (flag1)
						Client.myPassword += (char) l1;
					if (Client.myPassword.length() > 20)
						Client.myPassword = Client.myPassword.substring(0, 20);
				}
			} while (true);
			return;
		} else if (client.loginScreenState == 1) {
			worldHover = -1;
			for (int index = 0, y = (centerY - 19); index < worldsAmount.length; index++, y+= 20) {
				if (client.mouseInRegion((centerX - 136), y, (centerX + 204), (y + 20)))
					worldHover = (index + 1);
				if ((client.clickMode3 == 1) && client.clickInRegion((centerX - 136), y, (centerX + 204), (y + 20))) {
					ClientConstants.world = (index + 1);
//					if (ClientConstants.world != 2)
//					Configuration.economyWorld = true;
//				else
//					Configuration.economyWorld = false;
//					Client.rebuildFrameSize(Client.frameMode, Client.frameWidth, Client.frameHeight);
					client.loginScreenState = 0;
				}
			}
//			if (!client.mouseInRegion((centerX - 136), (centerY - 19), (centerX + 204), (20 * worldsAmount.length)))
//				worldHover = -1;
		}
	}

	public void displayAnnounements() {	
		announcementMovement--;
		announcementFade++;
		if (announcementMovement < -(announcements[ticks].length() - 10)) {
			announcementMovement = (Client.frameWidth + 2);
			if (++ticks >= announcements.length)
				ticks = 0;
		}
		TextDrawingArea.drawAlphaGradient(0, 0, Client.frameWidth, 25, 0xEBAB36, 0x997309, 205 - (int) (50 * Math.sin(announcementFade / 20.0)));
		Client.smallText.method389(true, announcementMovement, 0xffffff, announcements[ticks], 17);
	}

	void drawWorldInfo() {
		if (client.mouseInRegion(worldInfoBox.x, worldInfoBox.y, (worldInfoBox.x + worldInfoBox.width - 1), (worldInfoBox.y + worldInfoBox.height - 1))) {
			Client.cacheSprite[420].drawSprite(worldInfoBox.x, worldInfoBox.y);
			client.drawTooltip((client.mouseX + 11), (client.mouseY - 20), "World Selection");
		}
		else
			Client.cacheSprite[419].drawSprite(worldInfoBox.x, worldInfoBox.y);
		Client.boldText.method389(true, (worldInfoBox.x + 47), 0xFFFFFF, ""+ClientConstants.world, (worldInfoBox.y + 24));
	}
}