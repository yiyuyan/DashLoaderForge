package dev.notalpha.dashloader.client.ui;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.taski.ParentTask;
import dev.notalpha.taski.Task;
import dev.notalpha.taski.builtin.AbstractTask;
import dev.notalpha.taski.builtin.StaticTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Language;

import java.util.HashMap;

public final class DashToastState {
	public Task task = new StaticTask("Idle", 0);
	private final HashMap<String, String> translations;
	private String overwriteText;
	private DashToastStatus status;
	private double currentProgress = 0;
	private long lastUpdate = System.currentTimeMillis();
	private long timeDone = System.currentTimeMillis();


	public DashToastState() {
		var langCode = MinecraftClient.getInstance().getLanguageManager().getLanguage();
		DashLoader.LOG.info(langCode);
		var stream = this.getClass().getClassLoader().getResourceAsStream("dashloader/lang/" + langCode + ".json");
		this.translations = new HashMap<>();
		if (stream != null) {
			DashLoader.LOG.info("Found translations");
			Language.load(stream, this.translations::put);
		} else {
			var en_stream = this.getClass().getClassLoader().getResourceAsStream("dashloader/lang/en_us.json");
			if (en_stream != null) {
				Language.load(en_stream, this.translations::put);
			}
		}
	}

	private void tickProgress() {
		if (Double.isNaN(this.currentProgress)) {
			this.currentProgress = 0.0;
		}
		final double actualProgress = task.getProgress();
		final double divisionSpeed = (actualProgress < this.currentProgress) ? 3 : 30;
		double currentProgress1 = (actualProgress - this.currentProgress) / divisionSpeed;
		this.currentProgress += currentProgress1;
	}

	public double getProgress() {
		final long currentTime = System.currentTimeMillis();
		while (currentTime > this.lastUpdate) {
			this.tickProgress();
			this.lastUpdate += 10; // ~100ups
		}
		return this.currentProgress;
	}

	public String getText() {
		if (this.overwriteText != null) {
			return this.overwriteText;
		}

		String text = concatTask(3, task);
		return this.translations.getOrDefault(text, text);
	}

	public String getProgressText() {
		return this.getProgressText(3, task);
	}

	private String concatTask(int depth, Task task) {
		String name = null;
		if (task instanceof AbstractTask abstractTask) {
			name = abstractTask.getName();
		}

		if (task instanceof ParentTask stepTask) {
			Task subTask = stepTask.getChild();
			if (depth > 1) {
				String subName = concatTask(depth - 1, subTask);
				if (subName != null) {
					return name + "." + subName;
				}
			}
		}

		return name;
	}

	private String getProgressText(int depth, Task task) {
		if (task instanceof ParentTask stepTask) {
			Task subTask = stepTask.getChild();
			if (depth > 1) {
				String subName = getProgressText(depth - 1, subTask);
				if (subName != null) {
					return subName;
				}
			}
		}

		if (task instanceof AbstractTask abstractTask) {
			return abstractTask.getProgressText();
		}
		return null;
	}

	public void setOverwriteText(String overwriteText) {
		this.overwriteText = this.translations.getOrDefault(overwriteText, overwriteText);
	}

	public DashToastStatus getStatus() {
		return status;
	}

	public void setStatus(DashToastStatus status) {
		this.status = status;
	}

	public long getTimeDone() {
		return timeDone;
	}

	public void setDone() {
		this.timeDone = System.currentTimeMillis();
	}
}
