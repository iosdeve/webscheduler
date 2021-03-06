<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ew" uri="http://www.openthinks.com/easyweb"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="dailey.yet@outlook.com">
<title>Task - New</title>
<%@ include file="../template/head.style.jsp"%>
<link rel="stylesheet" href="${ew:pathS('/static/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css')}">
<link rel="stylesheet" href="${ew:pathS('/static/bootstrap-switch/css/bootstrap3/bootstrap-switch.min.css')}">
<link rel="stylesheet" href="${ew:pathS('/static/bootstrap-select/css/bootstrap-select.min.css')}">
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
				<h1 class="page-header">Task <small>New</small></h1>
				<form action="${ew:path('/task/add')}" method="post"
					class="form-horizontal">
					<div class="form-group">
						<label for="taskname" class="col-sm-2 control-label">Task
							name</label>
						<div class="col-sm-10">
							<input type="text" class="form-control" id="taskname"
								name="taskname" placeholder="task name" required>
						</div>
					</div>

					<div class="form-group">
						<label for="taskname" class="col-sm-2 control-label">Task
							type</label>
						<div class="col-sm-10">
							<select id="tasktype" name="tasktype" class="form-control" required>

								<c:forEach var="task" items="${pm.supportTasks }" varStatus="status">
									<option data-required="${task.refDescriber.required }" data-ref="st_${status.index }" value="${task.type.name }" title="${task.describer.description }">${task.type.simpleName }</option>
								</c:forEach>
								<option value="" disabled="disabled">--------------------------</option>
								<c:forEach var="task" items="${pm.customTasks }" varStatus="status">
									<option data-required="${task.refDescriber.required }" data-ref="ct_${status.index }" value="${task.type.name }" title="${task.describer.description }">${task.type.simpleName }</option>
								</c:forEach>
							</select>
							<div class="hidden" id="tasktype-ref">
								<c:forEach var="task" items="${pm.supportTasks }" varStatus="status">
									<code id="st_${status.index }" >${task.refDescriber.description }</code>
								</c:forEach>
								<c:forEach var="task" items="${pm.customTasks }" varStatus="status">
									<code id="ct_${status.index }">${task.refDescriber.description }</code>
								</c:forEach>
							</div>
						</div>
					</div>
					
					<div class="form-group">
						<label for="tasktrigger" class="col-sm-2 control-label">Task trigger</label>
						<div class="col-sm-10">
							<select id="tasktrigger" name="tasktrigger" class="form-control" required>
								<c:forEach var="trigger" items="${pm.triggers }" varStatus="status">
									<option  data-ref=".${trigger.tag}" value="${trigger.name }" title="${trigger.display }">${trigger.display }</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<!-- trigger details for simple -->
					<div class="trigger-group simple1-trigger simple2-trigger" style="display: none">
						<div class="form-group" data-bind-target=".simple2-trigger">
							<label for="startdate" class="col-sm-2 control-label">Start date</label>
							<div class="col-sm-10">
								<div class="input-group">
									<input class="form-control form-datetime" size="16" type="datetime" name="startdate" id="startdate" data-date-format="yyyy-mm-dd hh:ii" readonly>
									<span class="input-group-addon" role="datetime-addon" data-action="remove"><i class="fa fa-times" aria-hidden="true"></i></span>
									<span class="input-group-addon" role="datetime-addon" data-action="show"><i class="fa fa-calendar" aria-hidden="true"></i></span>
								</div>
							</div>
						</div>					
						<div class="form-group">
							<label for="repeatable" class="col-sm-2 control-label">Repeat options</label>
							<div class="col-sm-10">
								<input class="bootstrap-switch" data-ref=".repeat-options-group" data-label-text="Repeatable" type="checkbox" name="repeatable" id="repeatable">
								<!-- <input class="bootstrap-switch" data-label-text="Forever" type="checkbox" name="repeatforever" id="repeatforever"> -->
							</div>
						</div>
						<!-- repeat options -->
						<div class="repeat-options-group">
							<div class="form-group">
								<div class="col-sm-5 col-sm-offset-2">
									<div class="input-group">
										<input placeholder="Repeat interval" min="0" title="Repeat interval" class="form-control" type="number" name="repeatinterval" id="repeatinterval">
										<span class="input-group-addon">Second</span>
									</div>
								</div>
								<div class="visible-xs-inline">
									<p></p>
								</div>
								<div class="col-sm-5">
									<!-- <input placeholder="Repeat count" min="0" title="Repeat count" class="" type="number" name="repeatcount" id="repeatcount"> -->
									<select name="repeatcount"
									title="Choose one of the following..."
									 class="selectpicker show-tick " id="repeatcount" data-width="auto" data-live-search="true" data-show-subtext="true">
										<option data-content="<span class='label label-warning'>Repeat forever</span>" value="2147483647"  >Repeat forever</option>
										<option data-divider="true"></option>
										<c:forEach var="i" begin="1" end="10">
											<option value="${i}" >Repeat ${i} time</option>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class="form-group">
								<div class="col-sm-10 col-sm-offset-2">
									<div class="input-group">
										<input class="form-control form-datetime" placeholder="End date" type="datetime" name="enddate" id="enddate" data-date-format="yyyy-mm-dd hh:ii" readonly>
										<span class="input-group-addon" role="datetime-addon" data-action="remove"><i class="fa fa-times" aria-hidden="true"></i></span>
										<span class="input-group-addon" role="datetime-addon" data-action="show"><i class="fa fa-calendar" aria-hidden="true"></i></span>
									</div>
								</div>
							</div>	
						</div><!-- end of repeat options -->
						
					</div>
					
					<!-- trigger details for cron -->
					<div class="form-group trigger-group cron-trigger" style="display: none" >
						<label for="cronexpr" class="col-sm-2 control-label">Cron expression</label>
						<div class="col-sm-10">
							<div class=" input-group">
								<input class="form-control" type="text" name="cronexpr" id="cronexpr" >
								<span class="input-group-addon" role="cron-addon" data-ref=".cron-trigger" data-action="check" title="Expression validation" data-link="${ew:path('/task/check/cron')}"><i class="fa fa-check" aria-hidden="true"></i></span>
								<span class="input-group-addon" role="cron-addon" data-action="help" title="Cron help" data-link="${ew:path('/help')}#trigger-cron"><i class="fa fa-question-circle" aria-hidden="true"></i></span>
							</div>
						</div>
					</div>
					
					<div class="form-group">
							<label for="taskshared" class="col-sm-2 control-label">Shared</label>
							<div class="col-sm-10">
								<input class="bootstrap-switch"  data-label-text="Shared" type="checkbox" name="taskshared_switch" id="taskshared_switch">
								<input type="hidden" value="true" name="taskshared" id="taskshared"/>
							</div>
					</div>
					
					<div class="form-group">
						<label for="taskref" class="col-sm-2 control-label">Task
							properties</label>
						<div class="col-sm-10">
							<textarea class="form-control" id="taskref" name="taskref" rows="10"></textarea>
						</div>
					</div>
					
					<div class="form-group">
						<div class="col-sm-offset-2 col-sm-10 ">
							<button type="submit" class="btn btn-primary">Create</button>
							<a class="btn btn-default " href="${ew:path('/task/index')}" role="button">Cancel</a>
						</div>
					</div>

				</form>
			</div>
		</div>
	</div>

	<%@ include file="../template/body.script.jsp"%>
	<script type="text/javascript" src="${ew:pathS('/static/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js')}"></script>
	<script type="text/javascript" src="${ew:pathS('/static/bootstrap-select/js/bootstrap-select.min.js')}"></script>
	<script type="text/javascript" src="${ew:pathS('/static/bootstrap-switch/js/bootstrap-switch.min.js')}"></script>
	<script type="text/javascript" src="${ew:pathS('/static/CodeMirror/lib/codemirror.js')}"></script>
	<script type="text/javascript" src="${ew:pathS('/static/CodeMirror/mode/properties/properties.js')}"></script>
	<script type="text/javascript" src="${ew:pathS('/static/CodeMirror/mode/xml/xml.js')}"></script>
	<script type="text/javascript" src="${ew:pathS('/static/js/task.add.js')}"></script>
</body>
</html>