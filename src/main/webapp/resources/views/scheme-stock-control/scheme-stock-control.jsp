<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>Scheme Stock Control</title>

<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="/msc/resources/services/requests-queue.js"></script>

<script type="application/javascript"
	src="/msc/resources/logic/scheme-stock-control/scheme-stock-control.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>
</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="schemeStockControlCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">
	
		<div class="btn-group mb-1" role="group" aria-label="Schemes" data-ng-cloak>
		  <button type="button" class="btn btn-secondary" data-ng-repeat="scheme in schemes" 
		  		data-ng-click="setCurrentScheme(scheme)" data-ng-disabled="scheme.name === currentScheme.name">{{scheme.name}}</button>
		</div>
		
		<div class="row">
			<div class="col-md-6">
				<ul class="nav nav-tabs" role="tablist" data-ng-cloak>
					<li class="nav-item" data-ng-repeat="groupName in groups" data-ng-show="!userIsOnlyFulfillmentOperator || (userIsOnlyFulfillmentOperator && belongsToFulfillment(groupName))">
					<a	class="nav-link" data-ng-class="{'active': activeTabCV === $index}"
						id="{{'cvTab-' + $index}}" href="#" data-toggle="tab" role="tab"
						aria-selected="{{activeTabCV === $index ? 'true' : 'false'}}"
						data-ng-click="setActiveTabCV($index)">{{groupName}}</a></li>
				</ul>
				<h5>Assets Current Level</h5>
				<div ag-grid="acvGridOptions" style="height: 400px;" class="ag-theme-balham"></div>
			</div>
			<div class="col-md-6">
				<ul class="nav nav-tabs" role="tablist" data-ng-cloak>
					<li class="nav-item" data-ng-repeat="groupName in groups" data-ng-show="!userIsOnlyFulfillmentOperator || (userIsOnlyFulfillmentOperator && belongsToFulfillment(groupName))">
						<a	class="nav-link" data-ng-class="{'active': activeTabMV === $index}"
							id="{{'mvTab-' + $index}}" href="#" data-toggle="tab" role="tab"
							aria-selected="{{activeTabMV === $index ? 'true' : 'false'}}"
							data-ng-click="setActiveTabMV($index)">{{groupName}}</a></li>
				</ul>
				<h5>Assets Minimum Level</h5>
				<div ag-grid="assetsMinValGridOptions" style="height: 400px;" class="ag-theme-balham"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-12">
				<h5 style="display: inline-block" data-ng-cloak>{{'Transfer Queue to ' + currentScheme.name}}</h5>
				<button class="btn btn-sm btn-success float-right mb-1 mx-1" data-ng-click="openCreateDistributionModal()" data-ng-disabled="checkQueueButtonsForDisabled()">Create Distribution</button>
				<button class="btn btn-sm btn-danger float-right mb-1 mx-1" data-ng-click="openClearQueueModal()" data-ng-disabled="checkQueueButtonsForDisabled()">Clear the Queue</button>
			</div>
		</div>
		<div class="row">
			<div class="col-12">
				<div ag-grid="transferQueueGridOptions" style="height: 400px;" class="ag-theme-balham"></div>
			</div>
		</div>
		
		<h5 class="mt-2">Used/Placed</h5>
		
		<div class="my-2 d-block" style="max-width: 400px;" data-ng-if="pagesParameters && pagesParameters.pagesNumber > 0">
			<div class="btn-group mr-2 mb-2 d-flex" role="group" aria-label="Filter">
				<button type="button" class="btn btn-info flex-fill" data-ng-click="setStockBalanceCurrentState(0)" data-ng-disabled="currentStateId === 0">All</button>
				<button type="button" class="btn btn-warning flex-fill" data-ng-click="setStockBalanceCurrentState(STATE_USED)" data-ng-disabled="currentStateId === STATE_USED">Used</button>
				<button type="button" class="btn btn-success flex-fill" data-ng-click="setStockBalanceCurrentState(STATE_PLACED)" data-ng-disabled="currentStateId === STATE_PLACED">Placed</button>
			</div>
		</div>
		
		<div class="my-2">
			<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in pageSizes" data-ng-model="pageSize" data-ng-change="updateSBPageParameters()"></select> <span class="">entries</span>
		</div>
		
		<div class="row mx-2 mt-1" data-ng-if="!stockUsageReports">
			<div class="col-12" style="height:300px; background-color: #f8f9fa"></div>
		</div>
		
		<div id="{{'accordion' + $index}}" class="" data-ng-repeat="report in currentRecords" data-ng-show="needToShowSB($index, report)" data-ng-cloak>
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
		
		<pagination parameters="pagesParameters" data-ng-if="pagesParameters && pagesParameters.pagesNumber > 0"
				class="msc-inline-block mt-1"></pagination>
		
		<content-modal modal-id="'distributionModal'" max-width="'500px'"
			on-confirm="createDistribution" on-confirm-text="'Create'"
			data-title="distributionModalOptions.distributionModalTitle" hide-manually-on-confirm="true"
			is-frozen="distributionModalOptions.isFrozen" confirm-disabled="distributionModalOptions.confirmDisabled">
			<h6>{{transferRequest.productType.fullName}}</h6>
			<form>
				<div class="form-row">
					<div class="form-group col-md-6">
						Transfer <input
									type="number"
									class="form-control"
									style="display: inline-block; width: 100px;"
									data-ng-model="transferRequest.transferAmount"
									autocomplete="off"
									data-ng-class="{'is-invalid' : distributionModalOptions.transferQuantityInvalid}"
									data-ng-change="distributionModalOptions.transferQuantityInvalid = false"
									maxlength="7"
									required>
						from:
					</div>
					<div class="form-group col-md-6">
						<div class="form-check" data-ng-repeat="scheme in schemes" data-ng-if="scheme.name !== currentScheme.name">
						  <input class="form-check-input" type="radio" id="{{'schemaRadio' + $index}}" data-ng-value="scheme" data-ng-model="transferRequest.transferFrom">
						  <label class="form-check-label" for="{{'schemaRadio' + $index}}">
						    {{scheme.name + " (" + getQuantityBySchemeAndProductType(scheme, transferRequest.productType) + ")"}}
						  </label>
						</div>
					</div>
				</div>
			</form>
		</content-modal>
		
		<confirm-modal data-modal-id="'confirmDeleteTransferModal'"
			data-on-confirm="deleteTransfer"
			data-title="'Delete transfer from queue'"
			data-text="'Do you want to delete this transfer from queue?'"></confirm-modal>
			
		<confirm-modal data-modal-id="'confirmClearQueueModal'"
			data-on-confirm="clearQueue"
			data-title="getClearQueueModalTitle"
			data-text="'Do you want to clear this transfer queue?'"></confirm-modal>
			
		<confirm-modal data-modal-id="'confirmCreateDistributionModal'"
			data-on-confirm="createRealDistribution"
			data-title="'Create Distribution'"
			data-text="'Do you want to create distribution from transfer queue?'"></confirm-modal>
	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
