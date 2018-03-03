package dcrew.rs2.content;

import java.util.ArrayList;
import java.util.logging.Logger;

import dcrew.core.task.Task;
import dcrew.core.task.TaskQueue;
import dcrew.core.util.Utility;
import dcrew.rs2.entity.World;

public class GlobalMessages {
	static Logger logger = Logger.getLogger(GlobalMessages.class.getSimpleName());
	static String newsColor = "<col=013B4F>",
			iconNumber = "<img=8>";

	public final static ArrayList<String> announcements = new ArrayList<String>();
	public static final String[] ANNOUNCEMENTS = { 
		"YOU ARE A BASTARD!",
	};

	public static void declare() {
		for (int i = 0; i < ANNOUNCEMENTS.length; i++)
			announcements.add(ANNOUNCEMENTS[i]);
		logger.info(Utility.format(announcements.size()) + " Announcements have been loaded successfully.");
	}

	public static void initialize() {
		TaskQueue.queue(new Task(250, false) {
			@Override
			public void execute() {
				final String announcement = Utility.randomElement(announcements);
				World.sendGlobalMessage(iconNumber + newsColor + " " + announcement);
			}

			@Override
			public void onStop() {
			}
		});
	}	
}