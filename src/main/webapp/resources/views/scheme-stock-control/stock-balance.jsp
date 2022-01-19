<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Stock Balance</title>

<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="../resources/logic/scheme-stock-control/stock-balance.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="stockBalanceCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>
	
	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">
		
		<div class="mb-2 d-none d-md-block">
			<button class="btn btn-warning flex-fill mr-2 mb-2" data-ng-click="openUseStocksWindow(true)">Use Stocks</button>
			<button class="btn btn-success flex-fill mr-2 mb-2" data-ng-click="openUseStocksWindow(false)">Place Stocks</button>
			
			
			<div class="btn-group mr-2 mb-2" role="group" aria-label="Filter">
				<button type="button" class="btn btn-info" data-ng-click="setCurrentState(0)" data-ng-disabled="currentStateId === 0">All</button>
				<button type="button" class="btn btn-warning" data-ng-click="setCurrentState(STATE_USED)" data-ng-disabled="currentStateId === STATE_USED">Used</button>
				<button type="button" class="btn btn-success" data-ng-click="setCurrentState(STATE_PLACED)" data-ng-disabled="currentStateId === STATE_PLACED">Placed</button>
			</div>
		</div>
		
		<div class="mb-2 d-block d-md-none">
			<div class="d-flex">
				<button class="btn btn-warning flex-fill mr-2 mb-2" data-ng-click="openUseStocksWindow(true)">Use Stocks</button>
				<button class="btn btn-success flex-fill mr-2 mb-2" data-ng-click="openUseStocksWindow(false)">Place Stocks</button>
			</div> 
			
			
			<div class="btn-group mr-2 mb-2 d-flex" role="group" aria-label="Filter">
				<button type="button" class="btn btn-info flex-fill" data-ng-click="setCurrentState(0)" data-ng-disabled="currentStateId === 0">All</button>
				<button type="button" class="btn btn-warning flex-fill" data-ng-click="setCurrentState(STATE_USED)" data-ng-disabled="currentStateId === STATE_USED">Used</button>
				<button type="button" class="btn btn-success flex-fill" data-ng-click="setCurrentState(STATE_PLACED)" data-ng-disabled="currentStateId === STATE_PLACED">Placed</button>
			</div>
		</div>
		
		<div class="my-2">
			<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in pageSizes" data-ng-model="pageSize" data-ng-change="updateRecords()"></select> <span class="">entries</span>
		</div>
		<div class="row mx-2 mt-1" data-ng-if="!stockUsageReports">
			<div class="col-12" style="height:300px; background-color: #f8f9fa"></div>
		</div>
		
		<div id="{{'accordion' + $index}}" class="" data-ng-repeat="report in currentRecords" data-ng-show="needToShow($index, report)" data-ng-cloak>
			<div class="card">
				<div class="card-header">
					<div class="">
						<button class="btn btn-info msc-round bigger-18 mr-2"
									style="color: #FFFFFF; padding-left: 1px; padding-top: 1px"
									data-toggle="collapse"
									data-target="{{'#collapse' + $index}}" aria-expanded="false"
									aria-controls="{{'collapse' + $index}}">
									<em class="material-icons md-14">more_horiz</em>
								</button>
						{{report.state.name + " on " + report.creationDate}}
					</div>
				</div>
			</div>
			
			<div id="{{'collapse' + $index}}" class="collapse"
					aria-labelledby="{{'heading' + $index}}" data-parent="#accordion">
					<div class="card-body border pt-0 px-3">
						<div class="" data-ng-repeat="group in report.prodGroupsNames">
							<div class="row bg-light border-bottom">
								<div class="col-12 text-center pt-1"><h6>{{group}}</h6></div>
							</div>
							<div class="d-flex justify-content-between py-1" data-ng-class="{'border-bottom':(($index + 1) < report.prodGroups[group].length)}" data-ng-repeat="reportedType in report.prodGroups[group]">
								<div class="">{{reportedType.typeOfAssets.fullName}}</div>
								<div class="">{{reportedType.amount}}</div>
							</div>
						</div>
						
						<div class="row mt-1" data-ng-show="report.notes">
							<div class="col-12">Comment: <input class="form-control form-control-sm" data-ng-model="report.notes" disabled></div>
						</div>
						
					</div>
			</div>
		</div>
		
		<pagination parameters="pagesParameters" data-ng-if="pagesParameters && stockUsageReports.length > 0"
				class="msc-inline-block mt-1"></pagination>
		
		<div class="row mt-2">
			<div class="col-lg-6 col-12">
				<ul class="nav nav-tabs" role="tablist" data-ng-cloak>
					<li class="nav-item" data-ng-repeat="groupName in groups"><a
						class="nav-link" data-ng-class="{'active': activeTabCV === $index}"
						id="{{'cvTab-' + $index}}" href="#" data-toggle="tab" role="tab"
						aria-selected="{{activeTabCV === $index ? 'true' : 'false'}}"
						data-ng-click="setActiveTabCV($index)">{{groupName}}</a></li>
				</ul>
				<h5>Current Stock Level</h5>
				<div ag-grid="acvGridOptions" style="height: 400px; " class="ag-theme-balham"></div>
			</div>
		</div>
		
		<content-modal modal-id="'createReportModal'" max-width="'700px'" data-ng-cloak
			on-confirm="saveReport" on-confirm-text="'Save'"
			data-title="createReportModalOptions.title" hide-manually-on-confirm="true"
			is-frozen="createReportModalOptions.isFrozen" confirm-disabled="createReportModalOptions.confirmDisabled">
			

			<div class="border px-3 py-2 mt-1" data-ng-class="{'border-danger': groupObj.noTypes}" data-ng-repeat="groupObj in newReport.reportedGroups">
				<div class="row align-items-center bg-light pt-1 pb-1">
					<label class="col-form-label col-lg-1 col-md-2 col-3 msc-label">Group:</label>
					<div class="col-lg-11 col-md-10 col-9">
						<div class="d-flex align-items-center justify-content-between">
							<select class="form-control form-control-sm msc-inline-block"
									id="{{'group' + $index}}" style="max-width: 180px;"
									data-ng-options="group for group in groupsToChooseFrom" data-ng-change="onGroupChange(groupObj)"
									data-ng-model="groupObj.name" data-ng-class="{'is-invalid': rpt.noProductType}">
							</select>
							<div>
								<button data-ng-click="removeGroupFromNewReport()"
										class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
									<i class="material-icons md-18 text-white">clear</i>
								</button>
							</div>
						</div>
					</div> 										
				</div>
									
				<div class="row align-items-center mb-2"
					data-ng-repeat="rpt in groupObj.types"
					data-ng-if="newReport">
					<label class="col-10 col-sm-2 col-lg-1 col-form-label">Asset:</label>
					
					<!-- for phones -->
					<div class="col-1 mt-1 d-inline-block d-sm-none">
						<button data-ng-click="removeAssetFromNewReport(rpt, groupObj)"
								class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
							<i class="material-icons md-14 text-white">clear</i>
						</button>
					</div>
					
					<div class="col-lg-7 col-6 mt-1">
						<searchable-select name="'asset' + $index" set-model="setAsset" array="assetsToChooseFromGrouped[groupObj.name]" add-params="[rpt]" search-by-field="'fullName'" is-disabled="false"
										   is-valid="true" small="true"></searchable-select>
					</div>
					<div class=" col-6 col-sm-3 mt-1">
						<input type="number" class="form-control form-control-sm"
							pattern="[0-9]*" data-ng-model="rpt.amount" data-ng-class="{'is-invalid': rpt.noAmount}" data-ng-change="onReportedTypeChange(rpt)">
					</div>
					<div class="col-1 mt-1 d-none d-sm-inline-block">
						<button data-ng-click="removeAssetFromNewReport(rpt, groupObj)"
								class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
							<i class="material-icons md-14 text-white">clear</i>
						</button>
					</div>
				</div>
							
				<button data-ng-click="addAssetToNewReport(groupObj)"
					class="btn btn-success msc-round bigger-18" data-ng-disabled="!groupObj.name"
					style="color: #FFFFFF; padding-left: 1px; padding-top: 1px">
					<em class="material-icons md-14">add</em>
				</button>
									
			</div>
			
			<button data-ng-click="addGroupToNewReport()"
					class="btn btn-success msc-round bigger-18"
					style="color: #FFFFFF; padding-left: 1px; padding-top: 1px">
					<em class="material-icons md-14">add</em>
				</button>

			<input class="form-control mt-2" data-ng-model="newReport.notes" placeholder="Notes">
		</content-modal>
		
	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
