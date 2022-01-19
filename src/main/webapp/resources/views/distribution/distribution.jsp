<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Distributions</title>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="/msc/resources/services/load-user-roles-service.js"></script>

<script type="application/javascript"
	src="../resources/logic/distribution/distribution.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="distributionCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">

		<div ng-if="!distributions" class="text-center mt-5">
			<div class="spinner-border size-spinner" role="status">
				<span class="sr-only">Loading...</span>
			</div>
		</div>

		<div ng-if="distributions">
			<div class="msc-inline-block mr-1 h6" data-ng-if="userRoles &&!isUserASchemeLeader()">Distribution to: </div>
			<div class="btn-group mb-1 mr-3 msc-inline-block" role="group" aria-label="Schemes" data-ng-cloak>
				<button type="button" class="btn btn-secondary" data-ng-repeat="scheme in schemes"  data-ng-if="userRoles && !isUserASchemeLeader()"
						data-ng-click="setCurrentScheme(scheme)" data-ng-disabled="scheme.name === currentScheme.name">{{scheme.name}}</button>
			</div>

			<div class="msc-inline-block m3-3 mr-1 h6" data-ng-if="userRoles && isUserASchemeLeader()" data-ng-cloak>Distributions to {{userScheme.name}}</div>

			<div class="msc-inline-block mr-1 h6">State: </div>
			<div class="btn-group mb-2 mr-3 msc-inline-block" role="group" aria-label="States" data-ng-cloak>
				<button type="button" class="btn" data-ng-class="getStateButtonClass(state)" data-ng-repeat="state in states"
						data-ng-click="setCurrentState(state)" data-ng-disabled="state.id === currentState.id">{{state.name}}</button>
			</div>

			<div class="msc-inline-block mr-3" >
				<div class="msc-inline-block mr-1 h6">Sorting: </div>
				<div class="btn-group mb-1" role="group" aria-label="Sort" data-ng-cloak>
					<button type="button" class="btn btn-secondary"
							data-ng-click="sortCurrentRequests(1)" data-ng-disabled="currentSort[currentScheme.name] === 1">Asc</button>
					<button type="button" class="btn btn-secondary"
							data-ng-click="sortCurrentRequests(2)" data-ng-disabled="currentSort[currentScheme.name] === 2">Desc</button>
					<button type="button" class="btn btn-secondary"
							data-ng-click="sortCurrentRequests(3)" data-ng-disabled="currentSort[currentScheme.name] === 3" data-ng-if="currentScheme.name === 'All'">Scheme</button>
				</div>
			</div>

			<div class="my-1 msc-inline-block">
				<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in pageSizes" data-ng-model="pageSize" data-ng-change="updatePageable()"></select> <span class="">entries</span>
			</div>

			<div id="{{'accordion' + $index}}" class="" data-ng-repeat="distribution in distributions" data-ng-show="needToShow($index, distribution)" data-ng-cloak>
				<div class="card">
					<div class="card-header">
						<div class="d-flex justify-content-between">
							<div class="">
								<h5 class="mb-0" style="display: inline-block;">
									<button class="btn btn-info msc-round bigger-18 mr-2"
											style="color: #FFFFFF; padding-left: 1px; padding-top: 1px"
											data-toggle="collapse"
											data-target="{{'#collapse' + $index}}" aria-expanded="false"
											aria-controls="{{'collapse' + $index}}">
										<i class="material-icons md-14">more_horiz</i>
									</button>
									{{'No ' + distribution.id}}
								</h5>
								<div class="msc-inline-block">at {{distribution.creationDate}} From <b>{{distribution.schemeFrom ? distribution.schemeFrom.name : centralDepotScheme.name}}</b> <span data-ng-show="currentScheme === schemes[0]">To <b>{{distribution.schemeTo.name}}</b></span></div>
							</div>

							<div class="">
								<div class="msc-inline-block" data-ng-class="getStateTextClass(distribution.state)">
									<div class="msc-inline-block mt-1">{{distribution.state.name}}</div>
								</div>
								<div class="msc-inline-block" data-ng-show="request.state.id !== 1">at {{distribution.stateChangeDate}}</div>
							</div>
						</div>
					</div>

					<div id="{{'collapse' + $index}}" class="collapse"
						 aria-labelledby="{{'heading' + $index}}" data-parent="#accordion">
						<div class="card-body pt-0 px-3">
							<div class="" data-ng-repeat="group in distribution.assetGroups">
								<div class="row bg-light border-bottom">
									<div class="col-12 text-center pt-1"><h6>{{group}}</h6></div>
								</div>
								<div class="row py-1 border-bottom h6">
									<div class="col-10">
										Product Name
									</div>
									<div class="col-2">
										Quantity
									</div>
								</div>
								<div class="row py-1" data-ng-class="{'border-bottom':(($index + 1) < distribution.assets.length)}" data-ng-repeat="asset in distribution.assetsGrouped[group]">
									<div class="col-10">
										{{asset.typeOfAssets.fullName}}
									</div>
									<div class="col-2">
										{{asset.quantity}}
									</div>
								</div>
							</div>
							<div class="d-flex justify-content-between mt-2">
								<button class="btn btn-success btn-sm" data-ng-show="distribution.state.id===21 && canShipDistribution()" data-ng-click="openShipDistributionModal(distribution)" data-ng-disabled="shippingDistribution || closingDistribution">Ship Order</button>
								<button class="btn btn-danger btn-sm" data-ng-show="(distribution.state.id===21 || distribution.state.id===23) && canCloseDistribution()" data-ng-click="openCloseDistributionModal(distribution)" data-ng-disabled="shippingDistribution || closingDistribution">Close Distribution</button>
								<button class="btn btn-info btn-sm mr-1" data-ng-show="distribution.state.id===21 || distribution.state.id===23" data-ng-disabled="sendingOrder" data-ng-click="previewDistributionPDF(distribution)">Preview</button>
							</div>
							<div class="row mt-1" data-ng-show="distribution.notes">
								<div class="col-12">Notes: <input class="form-control form-control-sm" data-ng-model="distribution.notes" disabled></div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<pagination parameters="pagesParameters" data-ng-if="pagesParameters"
						class="my-1" data-ng-cloak></pagination>

			<confirm-modal data-modal-id="'shipDistribution'"
						   data-on-confirm="shipDistribution"
						   data-title="'Ship distribution?'"
						   data-text="''"></confirm-modal>

			<confirm-modal data-modal-id="'closeDistribution'"
						   data-on-confirm="closeDistribution"
						   data-title="'Close distribution?'"
						   data-text="''"></confirm-modal>
		</div>

	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
