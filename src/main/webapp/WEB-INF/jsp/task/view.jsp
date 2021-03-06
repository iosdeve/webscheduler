<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ew" uri="http://www.openthinks.com/easyweb"%>
<%@ taglib prefix="wsfn" uri="http://www.openthinks.com/webscheduler/fns"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="dailey.yet@outlook.com">
<title>Task - View</title>
<%@ include file="../template/head.style.jsp"%>
<link rel="stylesheet" href="${ew:pathS('/static/CodeMirror/lib/codemirror.css')}">
<link rel="stylesheet" href="${ew:pathS('/static/css/task.css')}">
</head>
<body>
	<jsp:include page="../template/navbar.jsp" />
	<div class="container-fluid">
		<div class="row">
			<jsp:include page="../template/sidebar.jsp">
				<jsp:param name="active" value="tasks" />
			</jsp:include>
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<h1 class="page-header">Task <small>View</small></h1>
				<form action="${ew:path('/task/edit')}" method="post"
					class="form-horizontal">
					<div class="form-group">
						<div class="col-sm-offset-2 col-sm-10 ">
							<div class="btn-group pull-right" role="group" aria-label="Action">
								<c:if test="${wsfn:isSecurity(pageContext,'/task/schedule') and wsfn:isAvaiableWith(pm.tm,'Schedule') }">
									<a class="btn btn-default " role="button" href="${ew:path('/task/schedule') }?taskid=${pm.tm.taskId }">
										<span title="Schedule" class="glyphicon glyphicon-log-in" aria-hidden="true"></span>Schedule
									</a> 
								</c:if>
								<c:if test="${wsfn:isSecurity(pageContext,'/task/unschedule') and wsfn:isAvaiableWith(pm.tm,'UnSchedule') }">
									<a class="btn btn-default btn-danger" role="button" href="${ew:path('/task/unschedule') }?taskid=${pm.tm.taskId }">
										<span title="UnSchedule" class="glyphicon glyphicon-log-out" aria-hidden="true"></span>UnSchedule
									</a> 
								</c:if>
								<c:if test="${wsfn:isSecurity(pageContext,'/task/stop') and wsfn:isAvaiableWith(pm.tm,'Stop') }">
									<a class="btn btn-default " href="${ew:path('/task/stop') }?taskid=${pm.tm.taskId }">
										<span title="Stop" class="glyphicon glyphicon-pause" aria-hidden="true"></span>Stop
								</a>
								</c:if> 
								<c:if test="${wsfn:isSecurity(pageContext,'/task/to/edit') and wsfn:isAvaiableWith(pm.tm,'Edit') }">
									<a class="btn btn-default " href="${ew:path('/task/to/edit') }?taskid=${pm.tm.taskId }">
										<span title="Edit" class="glyphicon glyphicon-pencil" aria-hidden="true"></span>Edit
								</a>
								</c:if> 
								<c:if test="${wsfn:isSecurity(pageContext,'/task/remove') and wsfn:isAvaiableWith(pm.tm,'Remove') }">
									<a class="btn btn-default btn-danger" href="${ew:path('/task/remove') }?taskid=${pm.tm.taskId }" data-confirm="true"  data-toggle="modal" data-target="#confirm-delete">
										<span title="Remove" class="glyphicon glyphicon-trash" aria-hidden="true"></span>Remove
									</a>
								</c:if>	
							</div>						
						</div>
					</div>
					<div class="form-group">
						<label for="taskid" class="col-sm-2 control-label">Task
							ID</label>
						<div class="col-sm-10">
							<input type="hidden" class="form-control" id="taskid"
								name="taskid"  value="${pm.tm.taskId }">
							<p class="form-control-static">${pm.tm.taskId }</p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="taskname" class="col-sm-2 control-label">Task
							name</label>
						<div class="col-sm-10">
							<input type="hidden" class="form-control" id="taskname"
								name="taskname" placeholder="task name" value="${pm.tm.taskName }">
							<p class="form-control-static">${pm.tm.taskName }</p>
						</div>
					</div>

					<div class="form-group">
						<label for="tasktype" class="col-sm-2 control-label">Task
							type</label>
						<div class="col-sm-10">
							<p class="form-control-static">${pm.tm.taskType }</p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="taskstate" class="col-sm-2 control-label">Task
							state</label>
						<div class="col-sm-10">
							<p class="form-control-static">${pm.tm.taskState.display }
							<c:if test="${wsfn:isCompleteWith(pm.tm,true) }">
								<i class="fa fa-check-circle text-success" aria-hidden="true"></i>
							</c:if> 
							<c:if test="${wsfn:isCompleteWith(pm.tm,false) }">
								<i class="fa fa-times-circle-o text-danger" aria-hidden="true"></i>
							</c:if>
							</p>
						</div>
					</div>
					
					<div class="form-group">
						<label for="tasktrigger" class="col-sm-2 control-label">Task
							trigger</label>
						<div class="col-sm-10">
							<p class="form-control-static">${pm.tm.taskTrigger.triggerType.display }</p>
						</div>
					</div>
					<!-- trigger details for simple  -->
					<c:if test="${wsfn:isSimpleTaskTrigger(pm.tm) }">
						<c:if test="${wsfn:isSimple4FixDate(pm.tm) }">
							<div class="form-group">
								<label for="startdate" class="col-sm-2 control-label">Start date</label>
								<div class="col-sm-10">
									<p class="form-control-static">${wsfn:getStartDate(pm.tm)}</p>
								</div>
							</div>	
						</c:if>
						<c:if test="${wsfn:isSimple4Repeatable(pm.tm) }">
							<div class="form-group">
								<label for="repeatinterval" class="col-sm-2 control-label">Repeat interval</label>
								<div class="col-sm-10">
									<p class="form-control-static">${wsfn:getRepeatInterval(pm.tm)} <i title="Second" class="fa fa-clock-o" aria-hidden="true"></i></p>
								</div>
							</div>	
							<div class="form-group">
								<label for="repeatcount" class="col-sm-2 control-label">Repeat count</label>
								<div class="col-sm-10">
									<p class="form-control-static">
										<c:if test="${wsfn:isRepeatForever(pm.tm) }">
											Repeat forever
										</c:if>
										<c:if test="${not wsfn:isRepeatForever(pm.tm) }">
											${wsfn:getRepeatCount(pm.tm)}
										</c:if>
									</p>
								</div>
							</div>
							<c:if test="${wsfn:getEndDate(pm.tm)!=''}">
								<div class="form-group">
									<label for="enddate" class="col-sm-2 control-label">End date</label>
									<div class="col-sm-10">
										<p class="form-control-static">${wsfn:getEndDate(pm.tm)}</p>
									</div>
								</div>	
							</c:if>	
						</c:if>
					</c:if>
					<!-- trigger details for cron -->
					<c:if test="${wsfn:isCronTaskTrigger(pm.tm) }">
						<div class="form-group">
							<label for="cronexpr" class="col-sm-2 control-label">Cron expression</label>
							<div class="col-sm-10">
								<p class="form-control-static">${wsfn:getCronExpr(pm.tm)}</p>
							</div>
						</div>		
					</c:if>
					
					<div class="form-group">
							<label for="taskshared" class="col-sm-2 control-label">Shared</label>
							<div class="col-sm-10">
								<p class="form-control-static">${pm.tm.shared}</p>
							</div>
					</div>
					
					<div class="form-group">
							<label for="createdby" class="col-sm-2 control-label">Create By</label>
							<div class="col-sm-10">
								<p class="form-control-static">${wsfn:getCreatedByName(pm.tm)}</p>
							</div>
					</div>		
								
					<div class="form-group">
						<label for="taskname" class="col-sm-2 control-label">Task
							I/O</label>
						<div class="col-sm-10">
							
							<ul class="nav nav-tabs" role="tablist">
							  <li role="presentation" class="active"><a href="#taskproperties" role="tab" data-toggle="tab">Properties</a></li>
							  <li role="presentation"><a href="#taskresultcontainer" role="tab" data-toggle="tab">Result</a></li>
							</ul>
							
							<div class="tab-content">
							    <div role="tabpanel" class="tab-pane active" id="taskproperties">
							    	<textarea class="form-control " id="taskref" name="taskref" rows="10" readonly>${pm.tm.taskRefContent}</textarea>
							    </div>
							    <div role="tabpanel" class="tab-pane" id="taskresultcontainer">
							    	<textarea class="form-control " id="taskresult" name="taskresult" rows="10" readonly>${pm.tm.lastTaskResult}</textarea>
							    </div>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<div class="modal fade" id="confirm-delete" tabindex="-1" role="dialog" aria-labelledby="confirm" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">Confirm Remove</h4>
                </div>
            
                <div class="modal-body">
                    <p>You are going to remove this task <span class="label label-default" data-title="taskname"></span>, this procedure is irreversible.</p>
                    <p>Do you want to proceed?</p>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <a class="btn btn-danger btn-ok">Delete</a>
                </div>
            </div>
        </div>
    </div>

	<%@ include file="../template/body.script.jsp"%>
	<script type="text/javascript" src="${ew:pathS('/static/CodeMirror/lib/codemirror.js')}"></script>
	<script type="text/javascript" src="${ew:pathS('/static/CodeMirror/mode/properties/properties.js')}"></script>
	<script type="text/javascript" src="${ew:pathS('/static/CodeMirror/mode/xml/xml.js')}"></script>
	<script type="text/javascript"
		src="${ew:pathS('/static/js/task.view.js')}"></script>
</body>
</html>