<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>Logs</title>


<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="/msc/resources/logic/logs/logs.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="logsCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>
	
	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">
		
		<div class="row">
			<div class="col-sm-5 col-lg-4">
				Date range (optional):
				<select class="form-control mb-2 " data-ng-model="dateRange" data-ng-change="onDateRangeChange()" placeholder="Date range">
					<option value="1">Today</option>
					<option value="2">Last 7 days</option>
					<option value="3">Last 30 days</option>
				</select>
				<div class="row mb-2">
					<div class="col-md-4">
						Start date:
					</div>
					<div class="col-md-8">
						<datepicker date-format="dd/MM/yyyy" date-max-limit="{{startMaxDate}}" date-typer="true" date-week-start-day="1" date-year-title="Year" date-month-title="Month" button-prev-title="" button-next-title="">
                            <input class="form-control" tabindex="3" type="text"  ng-model="startDate"  ng-change="onDateChange()" ui-mask="99/99/9999" placeholder="__/__/____" model-view-value="true" />
                        </datepicker>
					</div>
				</div>
				<div class="row mb-2">
					<div class="col-md-4">
						End date:
					</div>
					<div class="col-md-8">
						<datepicker date-format="dd/MM/yyyy" date-min-limit="{{endMinDate}}" date-typer="true" date-week-start-day="1" date-year-title="Year" date-month-title="Month" button-prev-title="" button-next-title="">
                            <input class="form-control" tabindex="3" type="text"  ng-model="endDate"  ng-change="onDateChange()" ui-mask="99/99/9999" placeholder="__/__/____" model-view-value="true" />
                        </datepicker>
					</div>
				</div>
				Action Type:
				<select multiple class="form-control mb-2" data-ng-model="groupsForSearch" data-ng-options="ag for ag in actionGroups" data-ng-change="onGroupSelect()">
				</select>
				User:
				<select multiple class="form-control mb-2" data-ng-model="usersForSearch" data-ng-options="u.userEmail as (u.firstName + ' ' + u.lastName) for u in allUsers" data-ng-change="onUserSelect()">
				</select>
				Order ID number:
				<input type="text" class="form-control mb-2" data-ng-model="orderNumber" placeholder="Enter any system order number">
				<div class="mb-2">
					<button class="btn btn-danger" data-ng-click="clearSelection()">Clear Selection</button>
					<button class="btn btn-warning" data-ng-click="search()" data-ng-disabled="searching">Search</button>
				</div>
			</div>
			<div class="col-sm-7 col-lg-8">
				<div ag-grid="logsGridOptions" style="height: 700px;" class="ag-theme-balham"></div>
			</div>
		</div>
		
		<content-modal modal-id="'logShowModal'"
			on-confirm="hideLogShowModal" on-confirm-text="'Hide'" hide-cancel="true"
			data-title="'Log Info'" max-width="'700px'" is-frozen="isFrozen" data-ng-cloak>
			<textarea id="log-text-area" class="form-control" rows="4">{{currentShowingLog.dataObject}}</textarea>
		</content-modal>
		
	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
