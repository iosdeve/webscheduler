package com.openthinks.webscheduler.controller;

import java.util.Collection;
import java.util.Optional;

import org.quartz.CronExpression;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.openthinks.easyweb.WebUtils;
import com.openthinks.easyweb.annotation.AutoComponent;
import com.openthinks.easyweb.annotation.Controller;
import com.openthinks.easyweb.annotation.Jsonp;
import com.openthinks.easyweb.annotation.Mapping;
import com.openthinks.easyweb.annotation.ResponseReturn;
import com.openthinks.easyweb.annotation.ResponseReturn.ResponseReturnType;
import com.openthinks.easyweb.context.handler.WebAttributers;
import com.openthinks.easyweb.context.handler.WebAttributers.WebScope;
import com.openthinks.easyweb.utils.json.OperationJson;
import com.openthinks.libs.utilities.logger.ProcessLogger;
import com.openthinks.webscheduler.help.PageMap;
import com.openthinks.webscheduler.help.StaticChecker;
import com.openthinks.webscheduler.help.StaticDict;
import com.openthinks.webscheduler.help.StaticUtils;
import com.openthinks.webscheduler.help.trigger.TriggerGenerator;
import com.openthinks.webscheduler.model.TaskRunTimeData;
import com.openthinks.webscheduler.model.security.User;
import com.openthinks.webscheduler.model.task.ITaskTrigger;
import com.openthinks.webscheduler.model.task.SupportedTrigger;
import com.openthinks.webscheduler.model.task.TaskAction;
import com.openthinks.webscheduler.model.task.TaskState;
import com.openthinks.webscheduler.service.SchedulerService;
import com.openthinks.webscheduler.service.TaskService;
import com.openthinks.webscheduler.task.ITaskDefinition;
import com.openthinks.webscheduler.task.TaskTypes;

@Controller("/task")
public class TaskController {

	@AutoComponent
	SchedulerService schedulerService;
	@AutoComponent
	TaskService taskService;

	private PageMap newPageMap() {
		return PageMap.build().push(StaticDict.PAGE_ATTRIBUTE_ACTIVESIDEBAR, "tasks");
	}

	@Mapping("/check/cron")
	@Jsonp
	@ResponseReturn(contentType = ResponseReturnType.TEXT_JAVASCRIPT)
	public String validateCron(WebAttributers was) {
		String cronExpr = was.get(StaticDict.PAGE_PARAM_TASK_TRIGGER_CRON_EXPR);
		ProcessLogger.debug("validating cron expression:" + cronExpr);
		if (CronExpression.isValidExpression(cronExpr)) {
			return OperationJson.build().sucess().toString();
		}
		return OperationJson.build().error().toString();
	}

	@Mapping("/unschedule")
	public String unschedule(WebAttributers was) {
		String taskId = was.get(StaticDict.PAGE_PARAM_TASK_ID);
		TaskRunTimeData taskRunTimeData = taskService.getTask(taskId);
		if (!checkState(was, taskRunTimeData, TaskAction.UnSchedule)) {
			return StaticUtils.errorPage(was, this.newPageMap());
		}
		boolean isSuccess = schedulerService.unschedule(taskRunTimeData);
		ProcessLogger.debug("Unschedule " + (isSuccess ? "success" : "failed") + " for task:[" + taskRunTimeData + "]");
		if (isSuccess) {
			taskRunTimeData.setTaskState(TaskState.UN_SCHEDULE);
			taskService.saveTask(taskRunTimeData);
		}
		return index(was);
	}

	@Mapping("/stop")
	public String stop(WebAttributers was) {
		String taskId = was.get(StaticDict.PAGE_PARAM_TASK_ID);
		TaskRunTimeData taskRunTimeData = taskService.getTask(taskId);
		if (!checkState(was, taskRunTimeData, TaskAction.Stop)) {
			return StaticUtils.errorPage(was, this.newPageMap());
		}
		boolean isSuccess = true;
		try {
			schedulerService.interrupt(JobKey.jobKey(taskRunTimeData.getTaskId(), taskRunTimeData.getGroupName()));
		} catch (Exception e) {
			isSuccess = false;
			ProcessLogger.error(e);
			was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_1, "Can not stop this task.", WebScope.REQUEST);
		}
		if (!isSuccess) {
			return StaticUtils.errorPage(was, this.newPageMap());
		}
		return index(was);
	}

	@Mapping("/schedule")
	public String schedule(WebAttributers was) {
		String taskId = was.get(StaticDict.PAGE_PARAM_TASK_ID);
		TaskRunTimeData taskRunTimeData = taskService.getTask(taskId);
		if (!checkState(was, taskRunTimeData, TaskAction.Schedule)) {
			return StaticUtils.errorPage(was, this.newPageMap());
		}
		boolean isSuccess = true;
		Class<? extends ITaskDefinition> clazz = null;
		try {
			clazz = taskRunTimeData.getTaskClass();
		} catch (ClassNotFoundException e) {
			ProcessLogger.error(e);
			was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_1, "Can not found this task type.", WebScope.REQUEST);
		}
		JobDetail job = JobBuilder.newJob(clazz)
				.withIdentity(taskRunTimeData.getTaskId(), taskRunTimeData.getGroupName()).build();
		job.getJobDataMap().put(ITaskDefinition.TASK_DATA, taskRunTimeData);
		ITaskTrigger taskTrigger = taskRunTimeData.getTaskTrigger();
		taskTrigger.setTriggerKey(StaticUtils.createTriggerKey(taskRunTimeData));
		Trigger trigger = taskTrigger.getTrigger();
		ProcessLogger.debug(taskRunTimeData.toString());
		try {
			schedulerService.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			isSuccess = false;
			ProcessLogger.fatal(e);
			was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_2, "Can not schedule this task, " + e.getMessage(),
					WebScope.REQUEST);
		}
		if (!isSuccess) {
			return StaticUtils.errorPage(was, this.newPageMap());
		}
		if (isSuccess) {
			taskRunTimeData.setTaskState(TaskState.SCHEDULED);
			taskService.saveTask(taskRunTimeData);
		}
		return index(was);
	}

	@Mapping("/add")
	public String add(WebAttributers was) {
		TaskRunTimeData taskRunTimeData = new TaskRunTimeData();
		taskRunTimeData.makeDefault();
		taskRunTimeData.setTaskName(was.get(StaticDict.PAGE_PARAM_TASK_NAME));
		taskRunTimeData.setTaskType(was.get(StaticDict.PAGE_PARAM_TASK_TYPE));
		taskRunTimeData.setTaskRefContent(was.get(StaticDict.PAGE_PARAM_TASK_REF));
		//set shared
		String sharedStr = was.get(StaticDict.PAGE_PARAM_TASK_SHARED);
		if (sharedStr != null && !Boolean.valueOf(sharedStr)) {
			taskRunTimeData.setShared(false);
		}
		//set created user
		Optional<User> sessionUser = StaticUtils.getCurrentSessionUser(was);
		if (sessionUser.isPresent()) {
			taskRunTimeData.setCreatedBy(sessionUser.get().getId());
		}
		ITaskTrigger taskTrigger = TriggerGenerator.valueOf(was.get(StaticDict.PAGE_PARAM_TASK_TRIGGER)).generate(was);
		taskRunTimeData.setTaskTrigger(taskTrigger);
		PageMap pm = newPageMap();
		boolean isSuccess = true;
		if (StaticChecker.isCronTaskTrigger(taskRunTimeData)) {
			String cronExpr = StaticChecker.getCronExpr(taskRunTimeData);
			isSuccess = CronExpression.isValidExpression(cronExpr);
			if (!isSuccess) {
				was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_1,
						"Save new task failed, cann't parse the cron expression:[" + cronExpr + "]", WebScope.REQUEST);
				return StaticUtils.errorPage(was, pm);
			}
		}
		try {
			taskService.saveTask(taskRunTimeData);
			ProcessLogger.debug(taskRunTimeData.toString());
		} catch (Exception e) {
			isSuccess = false;
			ProcessLogger.error(e);
			was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_1, "Save new task failed." + e.getMessage(), WebScope.REQUEST);
		}
		if (!isSuccess) {
			return StaticUtils.errorPage(was, pm);
		}
		pm.push("title", "Task - Adding").push("redirectUrl", WebUtils.path("/task/index"));
		return StaticUtils.intermediatePage(was, pm);
	}

	@Mapping("/to/add")
	public String goToAdd(WebAttributers was) {
		PageMap pm = newPageMap();
		pm.push(StaticDict.PAGE_ATTRIBUTE_SUPPORT_TASKS, TaskTypes.getSupportTaskMetaData());
		pm.push(StaticDict.PAGE_ATTRIBUTE_CUSTOM_TASKS, TaskTypes.getCustomTaskMetaData());
		pm.push(StaticDict.PAGE_ATTRIBUTE_TASK_TRIGGERS, SupportedTrigger.values());
		was.storeRequest(StaticDict.PAGE_ATTRIBUTE_MAP, pm);
		return "WEB-INF/jsp/task/add.jsp";
	}

	@Mapping("/to/edit")
	public String goToEdit(WebAttributers was) {
		String taskId = was.get(StaticDict.PAGE_PARAM_TASK_ID);
		TaskRunTimeData taskRunTimeData = taskService.getTask(taskId);
		PageMap pm = newPageMap();
		pm.push(StaticDict.PAGE_ATTRIBUTE_SUPPORT_TASKS, TaskTypes.getSupportTaskMetaData());
		pm.push(StaticDict.PAGE_ATTRIBUTE_CUSTOM_TASKS, TaskTypes.getCustomTaskMetaData());
		pm.push(StaticDict.PAGE_ATTRIBUTE_TASK_TRIGGERS, SupportedTrigger.values());
		pm.push(StaticDict.PAGE_ATTRIBUTE_TASK_META, taskRunTimeData);
		was.storeRequest(StaticDict.PAGE_ATTRIBUTE_MAP, pm);
		return "WEB-INF/jsp/task/edit.jsp";
	}

	@Mapping("/edit")
	public String edit(WebAttributers was) {
		PageMap pm = newPageMap();
		TaskRunTimeData taskRunTimeData = taskService.getTask(was.get(StaticDict.PAGE_PARAM_TASK_ID));
		if (taskRunTimeData == null) {
			was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_1, "Can not modify this entry, maybe it has been removed.",
					WebScope.REQUEST);
			return StaticUtils.errorPage(was, pm);
		}
		taskRunTimeData.setTaskName(was.get(StaticDict.PAGE_PARAM_TASK_NAME));
		taskRunTimeData.setTaskType(was.get(StaticDict.PAGE_PARAM_TASK_TYPE));
		taskRunTimeData.setTaskRefContent(was.get(StaticDict.PAGE_PARAM_TASK_REF));

		if (!checkState(was, taskRunTimeData, TaskAction.Edit)) {
			return StaticUtils.errorPage(was, pm);
		}

		//set shared
		String sharedStr = was.get(StaticDict.PAGE_PARAM_TASK_SHARED);
		if (sharedStr != null && !Boolean.valueOf(sharedStr)) {
			taskRunTimeData.setShared(false);
		}else{
			taskRunTimeData.setShared(true);
		}

		ITaskTrigger oldTaskTrigger = taskRunTimeData.getTaskTrigger();
		ProcessLogger.debug("On edit with old task trigger:" + oldTaskTrigger.toString());
		ITaskTrigger newTaskTrigger = TriggerGenerator.valueOf(was.get(StaticDict.PAGE_PARAM_TASK_TRIGGER))
				.generate(was);
		newTaskTrigger.setTriggerKey(oldTaskTrigger.getTriggerKey());
		ProcessLogger.debug("On edit with new task trigger:" + newTaskTrigger.toString());
		taskRunTimeData.setTaskTrigger(newTaskTrigger);
		boolean isSuccess = true;
		if (StaticChecker.isCronTaskTrigger(taskRunTimeData)) {
			String cronExpr = StaticChecker.getCronExpr(taskRunTimeData);
			isSuccess = CronExpression.isValidExpression(cronExpr);
			if (!isSuccess) {
				was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_1,
						"Change task failed, cann't parse the cron expression:[" + cronExpr + "]", WebScope.REQUEST);
				return StaticUtils.errorPage(was, pm);
			}
		}
		try {
			taskService.saveTask(taskRunTimeData);
			ProcessLogger.debug(taskRunTimeData.toString());
		} catch (Exception e) {
			isSuccess = false;
			ProcessLogger.error(e);
			was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_2, "Change task failed." + e.getMessage(), WebScope.REQUEST);
		}
		if (!isSuccess) {
			return StaticUtils.errorPage(was, pm);
		}
		pm.push("title", "Task - Editing").push("redirectUrl", WebUtils.path("/task/index"));
		return StaticUtils.intermediatePage(was, pm);
	}

	/**
	 * check the given task action is allowed under current task state
	 * @param was
	 * @param taskRunTimeData
	 * @param pm
	 */
	protected boolean checkState(WebAttributers was, TaskRunTimeData taskRunTimeData, TaskAction taskAction) {
		if (!StaticChecker.isAvaiableWith(taskRunTimeData, taskAction)) {
			was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_PRE + "State",
					"This action:[" + taskAction + "] is not allowed since current task is on state:["
							+ taskRunTimeData.getTaskState().getDisplay() + "].",
					WebScope.REQUEST);
			return false;
		}
		return true;
	}

	@Mapping("/remove")
	public String remove(WebAttributers was) {
		PageMap pm = newPageMap();
		TaskRunTimeData taskRunTimeData = taskService.getTask(was.get(StaticDict.PAGE_PARAM_TASK_ID));
		if (taskRunTimeData != null && !checkState(was, taskRunTimeData, TaskAction.Remove)) {
			return StaticUtils.errorPage(was, pm);
		}
		boolean isSuccess = true;
		if (taskRunTimeData != null) {
			isSuccess = taskService.remove(taskRunTimeData);
			if (!isSuccess)
				was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_1, "Internal error happend when removing, try it later.",
						WebScope.REQUEST);
		} else {
			isSuccess = false;
			was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_2, "Can not found this entry, maybe it has been removed.",
					WebScope.REQUEST);
		}
		if (!isSuccess) {
			return StaticUtils.errorPage(was, pm);
		}
		pm.push("title", "Task - Removing").push("redirectUrl", WebUtils.path("/task/index"));
		return StaticUtils.intermediatePage(was, pm);
	}

	@Mapping("/to/view")
	public String view(WebAttributers was) {
		PageMap pm = newPageMap();
		TaskRunTimeData taskRunTimeData = taskService.getTask(was.get(StaticDict.PAGE_PARAM_TASK_ID));
		if (taskRunTimeData == null) {
			was.addError(StaticDict.PAGE_ATTRIBUTE_ERROR_1, "Can not found this entry, maybe it has been removed.",
					WebScope.REQUEST);
			return StaticUtils.errorPage(was, pm);
		}
		was.storeRequest(StaticDict.PAGE_ATTRIBUTE_MAP, pm.push(StaticDict.PAGE_ATTRIBUTE_TASK_META, taskRunTimeData));
		return "WEB-INF/jsp/task/view.jsp";
	}

	@Mapping("/index")
	public String index(WebAttributers was) {
		Collection<TaskRunTimeData> tasks = taskService
				.getValidAndSharedTasks(StaticUtils.getCurrentSessionUser(was).get());
		was.storeRequest(StaticDict.PAGE_ATTRIBUTE_TASK_LIST, tasks);
		return "WEB-INF/jsp/task/index.jsp";
	}
}
