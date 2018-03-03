package dcrew;

public class Constants {
	public static final String name = "BASTARD-SCAPE",
			website = "http://www.your-mum.co.uk/";
	public static final double version = 1;
	public static final String[] owners = { "Dcrew" },
			developers = { "Dcrew" };
	
	public static boolean DEV_MODE = true;
	public static boolean WALK_CHECK = true;
	public static boolean STAFF_ONLY = false;
	public static final boolean doubleExperience = false;
	public static int CURRENT_VOTES = 0;
	public static String LAST_VOTER = "None";
	public static int MOST_ONLINE = 0;
	public static final String[] BAD_STRINGS = { 
		"fag", "f4g", "faggot", "nigger", "fuck", "bitch", "whore", 
		"slut", "gay", "lesbian", "scape", ".net", ".com", ".org", 
		"vagina", "dick", "cock", "penis", "hoe", "soulsplit", "ikov", 
		"retard", "cunt",
	};
	public static final String[] BAD_USERNAMES = { 
		"mod", "admin", "moderator", "administrator", "owner", "m0d", "adm1n", "0wner", 
		"retard", "Nigga", "nigger", "n1gger", "n1gg3r", "nigg3r", "n1gga", "cock", 
		"faggot", "fag", "anus", "arse", "fuck", "bastard", "bitch", "cunt", "chode", 
		"damn", "dick", "faggit", "gay", "homo", "jizz", "lesbian", "negro", "pussy", "penis",
		"queef", "twat", "titty", "whore", "b1tch"
	};
	public static final String[] BAD_TITLES = { 
		"owner", "0wner", "own3r", "0wn3r", "admin", "administrator", "dev",
		"developer", "mod", "m0d", "moderator", "m0derator", 
	};
	public final static String[] STAFF_MEMBERS = { 
		"Dcrew"
	};
	public static final String[] LOGIN_MESSAGES = { 
		"There are currently@dre@ /s/ </col>players online.",
		"Be sure to vote every 12 hours for more players!",
	};
	public static final String[] ITEM_IDENTIFICATION_MESSAGES = { };
	public static final int[][] ITEM_DISMANTLE_DATA = {
		{ 12436, 6585 }, //Amulet of fury (or)
		{ 12807, 11926 }, //Odium ward (or)
		{ 12806, 11924 }, //Malediction ward (or)
		{ 12797, 11920 }, //Dragon pickaxe (or)
	};
	public static final int[] BLOCKED_DOORS = {
		26502, 26503, 26504, 26505, 24306, 24309, 26760, 2104, 
		2102, 2100, 26461, 4799, 7129
	};
}