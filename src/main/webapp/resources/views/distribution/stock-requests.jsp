<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Stock Requests</title>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="../resources/logic/distribution/stock-requests.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="stockRequestsCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>
	
	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">

		<div ng-if="!centralDepotProducts" class="text-center mt-5">
			<div class="spinner-border size-spinner" role="status">
				<span class="sr-only">Loading...</span>
			</div>
		</div>

		<div ng-if="centralDepotProducts">
			<div class="btn-group mb-1 msc-inline-block" role="group" aria-label="Schemes" data-ng-if="!isUserOnlySchemeLeader()" data-ng-cloak>
				<button type="button" class="btn btn-secondary" data-ng-repeat="scheme in schemes"
						data-ng-click="setCurrentScheme(scheme)" data-ng-disabled="scheme.name === currentScheme.name">{{scheme.name}}</button>
			</div>

			<div class="msc-inline-block mr-3">
				<div class="msc-inline-block ml-0 ml-sm-3 mr-1 h6">State: </div>
				<div class="btn-group mb-1 msc-inline-block" role="group" aria-label="States" data-ng-cloak>
					<button type="button" class="btn" data-ng-class="getStateButtonClass(state)" data-ng-repeat="state in states"
							data-ng-click="setCurrentState(state)" data-ng-disabled="state.id === currentState.id">{{state.name}}</button>
				</div>
			</div>

			<div class="msc-inline-block mr-3">
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
			<br>
			<div class="my-1 msc-inline-block">
				<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in pageSizes" data-ng-model="pageSize" data-ng-change="updatePageable()"></select> <span class="">entries</span>
			</div>

			<div class="row mb-1">
				<div class="col-12">
					<button class="btn btn-warning mt-1 mt-sm-0" data-ng-click="askToMergeNewRequests()" data-ng-disabled="merging || !hasNewRequests" data-ng-hide="isUserOnlySchemeLeader()">Merge All New Requests</button>
					<button class="btn btn-success mt-1 mt-sm-0" data-ng-click="addNewRequest()" data-ng-disabled="newStockRequest" data-ng-hide="!userIsAdmin && !userIsSchemeLeader">Add New Request</button>
				</div>
			</div>

			<div id="accordion-new" class="" data-ng-show="newStockRequest" data-ng-cloak>
				<div class="card">
					<div class="card-header">
						<div class="d-flex justify-content-between">
							<div>
								<h5 class="mb-0" style="display: inline-block;">
									<button class="btn btn-info msc-round bigger-18 mr-2"
											style="color: #FFFFFF; padding-left: 1px; padding-top: 1px"
											data-toggle="collapse"
											data-target="#collapseNew" aria-expanded="false"
											aria-controls="collapseNew">
										<em class="material-icons md-14">more_horiz</em>
									</button>
									New Manual Request
								</h5>
							</div>
						</div>
					</div>
					<div id="collapseNew" class="collapse show"
						 aria-labelledby="headingNew" data-parent="#accordion">
						<div class="card-body pt-0 px-3">
							<div class="row">
								<div class="col-12">
									<div class="row mt-2"  data-ng-hide="isUserOnlySchemeLeader()">
										<label class="col-lg-1 col-md-2 col-3 col-form-label msc-label pt-1">Scheme:</label>
										<div class="col-lg-11 col-md-10 col-9">
											<select class="form-control form-control-sm msc-max-width-1" data-ng-options="s.name for s in realSchemes"
													data-ng-model="newStockRequest.scheme" data-ng-class="{'is-invalid': newStockRequest.noScheme}" data-ng-change="onNewRequestChange()"></select>
										</div>
									</div>
								</div>
								<div class="col-12">

									<div class="border px-3 py-2 mt-1" data-ng-class="{'border-danger': groupObj.noTypes}" data-ng-repeat="groupObj in newStockRequest.requestedGroups">

										<div class="row align-items-center bg-light">
											<div class="col-10">
												<label class="col-form-label msc-label pr-2">Asset Type:</label>
												<select class="form-control form-control-sm msc-inline-block mb-1"
														id="{{'group' + $index}}" style="max-width: 200px;"
														data-ng-options="group for group in groups" data-ng-change="onGroupChange(groupObj)"
														data-ng-model="groupObj.name" data-ng-class="{'is-invalid': rpt.noProductType}">
												</select>
											</div>
											<div class="col-2 d-flex align-items-center justify-content-end">
												<button data-ng-click="removeGroupFromNewRequest()"
														class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
													<i class="material-icons md-18 text-white">clear</i>
												</button>
											</div>
										</div>

										<div class="row align-items-center"
											 data-ng-repeat="rpt in groupObj.types"
											 data-ng-if="newStockRequest">
											<label class="col-lg-1 col-sm-2 col-form-label">Item:</label>
											<div class="col-lg-7 col-sm-6 mt-1">
												<searchable-select name="'stock-request-product' + $parent.$parent.$index + $index" set-model="setProduct" add-params="[rpt]" array="typesOfAssetsGrouped[groupObj.name]" search-by-field="'fullName'" is-disabled="returnFalse"
																   is-valid="rpt.hasProductType" small="true"></searchable-select>
											</div>
											<div class="col-sm-3 mt-1">
												<input type="number" class="form-control form-control-sm"
													   pattern="[0-9]*" data-ng-model="rpt.orderValue" data-ng-class="{'is-invalid': rpt.noOrderValue}" data-ng-change="onRequestedTypeChange(rpt)">
											</div>
											<div class="col-1 mt-1">
												<button data-ng-click="removeAssetFromNewRequest(rpt, groupObj)"
														class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
													<i class="material-icons md-14 text-white">clear</i>
												</button>
											</div>
										</div>

										<button data-ng-click="addAssetToNewRequest(groupObj)"
												class="btn btn-success msc-round bigger-18" data-ng-disabled="!groupObj.name"
												style="color: #FFFFFF; padding-left: 1px; padding-top: 1px">
											<em class="material-icons md-14">add</em>
										</button>

									</div>

									<button data-ng-click="addGroupToNewRequest()"
											class="btn btn-success msc-round bigger-18"
											style="color: #FFFFFF; padding-left: 1px; padding-top: 1px">
										<em class="material-icons md-14">add</em>
									</button>
								</div>
							</div>

							<div class="row mx-1" style="margin: 15px 0 15px 0">
								<div class="col-md-2 col-lg-1">Notes:</div>
								<input class="form-control form-control-sm col-md-10 col-lg-11" data-ng-model="newStockRequest.notes">
							</div>

							<div class="d-flex flex-row-reverse mx-2 mb-2">
								<button class="btn btn-success ml-2" data-ng-click="openSaveNewRequestConfirmWindow()"
										data-ng-disabled="newStockRequest.requestedProductTypes.length <= 0">Save</button>
								<button class="btn btn-danger" data-ng-click="deleteNewRequest()">Delete</button>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div id="{{'accordion' + $index}}" class="" data-ng-repeat="request in requestsGrouped[currentScheme.name]" data-ng-show="needToShow($index, request)" data-ng-cloak>
				<div class="card">
					<div class="card-header">
						<div class="row">
							<div class="col-md-6">
								<h5 class="mb-0" style="display: inline-block;">
									<button class="btn btn-info msc-round bigger-18 mr-2"
											style="color: #FFFFFF; padding-left: 1px; padding-top: 1px"
											data-toggle="collapse"
											data-target="{{'#collapse' + $index}}" aria-expanded="false"
											aria-controls="{{'collapse' + $index}}">
										<em class="material-icons md-14">more_horiz</em>
									</button>
									{{(request.manual ? 'Manual ' : '') + 'Request No ' + request.id}}
								</h5>
								<div class="msc-inline-block">from {{request.creationDate}} <span data-ng-show="currentScheme === schemes[0]">{{request.scheme.name}}</span></div>
							</div>
							<div class="col-6 col-md-2" data-ng-if="!isDistributionReady[request.id] && !request.distribution"></div>
							<div class="text-success col-6 col-md-2" data-ng-if="isDistributionReady[request.id] && !request.distribution">Distribution is ready!</div>
							<div class="text-info col-6 col-md-2" data-ng-if="request.distribution && request.state.id !== 4">Distributing!</div>
							<div class="text-danger col-6 col-md-2" data-ng-if="request.distribution && request.state.id === 4">Distribution No.{{request.distribution.id}}</div>
							<div class="col-6 col-md-4 d-flex justify-content-end">
								<div class="msc-inline-block" data-ng-class="getStateTextClass(request.state)">
									<button class="btn btn-sm btn-outline-warning" title="Merge" data-ng-click="askToMergeNewRequests(request)"
											data-ng-disabled="merging" data-ng-hide="isUserOnlySchemeLeader() || request.state.id !== 1">M</button>
									<div class="msc-inline-block">{{request.state.name}}</div>
								</div>
								<div class="msc-inline-block ml-1" data-ng-show="request.state.id !== 1">at {{request.stateChangeDate}}</div>
							</div>
						</div>
					</div>

					<div id="{{'collapse' + $index}}" class="collapse"
						 aria-labelledby="{{'heading' + $index}}" data-parent="#accordion">
						<div class="card-body pt-0 px-3">
							<div class="" data-ng-repeat="group in request.prodGroups">
								<div class="row bg-light border-bottom">
									<div class="col-12 text-center pt-1"><h6>{{group}}</h6></div>
								</div>
								<div class="row py-1 border-bottom h6">
									<div class="col-xl-5 col-4">Asset Name</div>
									<div class="col-2">
										Delivery
									</div>
									<div class="col-2">
										Order Value
									</div>
									<div class="col-2">
										<span class="d-none d-md-block">Available</span><span class="d-block d-md-none">Avlb.</span>
									</div>
									<div class="col-xl-1 col-2" data-ng-show="request.state.id === 1" data-ng-hide="isUserOnlySchemeLeader()">
										<span class="d-none d-md-block">Transfer</span><span class="d-block d-md-none">Trns.</span>
									</div>
								</div>
								<div class="row py-1" data-ng-class="{'border-bottom':(($index + 1) < request.requestedProductTypes.length)}" data-ng-repeat="requestedType in request.reqProductTypesGrouped[group]">
									<div class="col-xl-5 col-4">{{requestedType.productType.fullName}}</div>
									<div class="col-2">
										{{requestedType.distributed ? requestedType.distributed.quantity : "-"}}
									</div>
									<div class="col-2">
										{{requestedType.orderValue}}
									</div>
									<div class="col-2" data-ng-class="{'text-danger': requestedType.orderValue > centralDepotProducts[requestedType.productType.id].amount}">
										{{centralDepotProducts[requestedType.productType.id].amount}}
									</div>
									<div class="col-xl-1 col-2" data-ng-show="request.state.id === 1" data-ng-hide="isUserOnlySchemeLeader()">
										<button type="button" class="btn btn-info btn-sm" style="height:25px; padding: 3px 8px;" data-ng-click="openTransferModal(requestedType, request)">
											<em class="material-icons md-18">swap_horiz</em>
										</button>
									</div>
								</div>
							</div>
							<div class="row mt-1" data-ng-hide="isUserOnlySchemeLeader()">
								<div class="col-md-3 col-lg-2">
									<button class="btn btn-success btn-sm" data-ng-click="createRealDistribtionModal(request)" data-ng-disabled="!canCreateDistribution(request) || request.distribution">Create Distribution</button>
								</div>
								<div class="col-md-9 col-lg-10"><input class="form-control form-control-sm msc-inline-block" data-ng-disabled="!canCreateDistribution(request) || request.distribution" data-ng-model="request.distributionNotes" placeholder="Notes"></div>
							</div>

							<div class="row mt-1" data-ng-show="isUserOnlySchemeLeader()">
								<div class="col-12" data-ng-show="canCreateDistribution(request) && !request.distribution"><input class="form-control form-control-sm msc-inline-block" data-ng-model="request.distributionNotes" placeholder="Notes" disabled></div>
							</div>

							<div class="mt-1" data-ng-show="request.manual && request.notes.length > 0">
								Manual request notes: {{request.notes}}
							</div>

						</div>
					</div>
				</div>
			</div>
			<pagination parameters="pagesParameters" data-ng-if="pagesParameters"
						class="msc-inline-block mt-1"></pagination>

			<confirm-modal data-modal-id="'mergeAllNewRequestsConfirmModal'"
						   data-on-confirm="mergeAllNewRequests"
						   data-title="'Merge?'"
						   data-text="'Are you sure you want to merge request(s)'"></confirm-modal>

			<confirm-modal data-modal-id="'saveNewRequestConfirmModal'"
						   data-on-confirm="saveNewRequest"
						   data-title="'Save new stock request?'"
						   data-text="''"></confirm-modal>


			<content-modal modal-id="'distributionModal'" max-width="'500px'"
						   on-confirm="createDistribution" on-confirm-text="'Create'"
						   data-title="distributionModalOptions.distributionModalTitle" hide-manually-on-confirm="true"
						   is-frozen="distributionModalOptions.isFrozen" confirm-disabled="distributionModalOptions.confirmDisabled" data-ng-cloak>

				<h6>{{transferRequest.productType.fullName}}</h6>
				<form data-ng-if="currentACVs">
					<div class="form-row">
						<div class="form-group col-md-6">
							Transfer <input type="number" class="form-control" style="display: inline-block; width: 100px;" autocomplete="off" data-ng-model="transferRequest.transferAmount"
											data-ng-class="{'is-invalid' : distributionModalOptions.transferQuantityInvalid}" data-ng-change="distributionModalOptions.transferQuantityInvalid = false" maxlength="7" required>
							from:
						</div>
						<div class="form-group col-md-6">
							<div class="form-check" data-ng-repeat="scheme in schemes" data-ng-if="scheme.name !== transferForStockRequest.scheme.name && scheme.name !== 'All' && currentACVs[scheme.name]">
								<input class="form-check-input" type="radio" id="{{'schemaRadio' + $index}}" data-ng-value="scheme" data-ng-model="transferRequest.transferFrom">
								<label class="form-check-label" for="{{'schemaRadio' + $index}}">
									{{scheme.name + " (" + currentACVs[scheme.name].quantity + ")"}}
								</label>
							</div>
						</div>
					</div>
				</form>

			</content-modal>

			<confirm-modal data-modal-id="'createRealDistribution'"
						   data-on-confirm="createRealDistribution"
						   data-title="'Create distribution'"
						   data-text="'Do you want to create distribution?'"></confirm-modal>
		</div>

	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
