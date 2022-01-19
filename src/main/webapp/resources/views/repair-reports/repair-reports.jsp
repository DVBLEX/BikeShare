<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Repair Reports</title>

<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%> 

<script type="application/javascript"
	src="../resources/logic/repair-reports/repair-reports.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="repairReportsCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">
		
		<div data-ng-show="!reportToEdit" data-ng-cloak>
			<div class="d-inline-block">
				Scheme:
			</div>
			<div class="d-inline-block" style="width: 110px;" data-ng-if="userIsAdmin && schemeOfUser">
				<select class="form-control" ng-options="scheme.name as scheme.name for scheme in allSchemes"
						data-ng-model="schemeOfUser.name" data-ng-change="loadReportsData()"></select>
			</div>

			<div class="mx-2 mt-2 d-none d-md-block" data-ng-show="repairReports">
				<button class="mr-1 mb-1 btn btn-primary d-inline-block" data-ng-click="showBikesToRepair()">
					Bikes to repair: {{bikesToRepair.length}}
				</button>
				<button class="mr-1 mb-1 btn btn-info d-inline-block" data-ng-click="showStationsToRepair()">
					Stations to repair: {{stationsToRepair.length}}
				</button>
				<button class="mr-1 mb-1 btn btn-primary d-inline-block" data-ng-click="showBikesInRepair()">
					Bikes in repair: {{bikesInRepair.length}}
				</button>
				<button class="mr-1 mb-1 btn btn-info d-inline-block" data-ng-click="showStationsInRepair()">
					Stations in repair: {{stationsInRepair.length}}
				</button>
				<button class="mr-1 mb-1 btn btn-success d-inline-block" data-ng-click="showAll()">
					All
				</button>
				<div class="float-right mb-1 d-none d-lg-inline-block">
					<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in gridPageSizes" data-ng-model="gridPageSize" data-ng-change="setGridPageSize(gridPageSize)"></select> <span class="">entries</span>
				</div>
			</div>
			
			<div class="mx-2 d-block d-md-none" data-ng-show="repairReports"> <!-- for phones -->
				<button class="mr-1 mb-1 btn btn-primary btn-block" data-ng-click="showBikesToRepair()">
					Bikes to repair: {{bikesToRepair.length}}
				</button>
				<button class="mr-1 mb-1 btn btn-info btn-block" data-ng-click="showStationsToRepair()">
					Stations to repair: {{stationsToRepair.length}}
				</button>
				<button class="mr-1 mb-1 btn btn-primary btn-block" data-ng-click="showBikesInRepair()">
					Bikes in repair: {{bikesInRepair.length}}
				</button>
				<button class="mr-1 mb-1 btn btn-info btn-block" data-ng-click="showStationsInRepair()">
					Stations in repair: {{stationsInRepair.length}}
				</button>
				<button class="mr-1 mb-1 btn btn-success btn-block" data-ng-click="showAll()">
					All
				</button>
			</div>
			
			<div ag-grid="reportsGridOptions" class="ag-theme-balham mt-2"></div>	
			
			<div class="mb-1 d-flex justify-content-between">
				<h5>Routine Reviews</h5>
				<div class="">
					<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in routineGridPageSizes" data-ng-model="routineGridPageSize" data-ng-change="pagesParameters.currentPage = 1; loadRoutineReviewsInfoAndData()"></select> <span class="">entries</span>
				</div>
			</div>

            <div style="width: 100%">
                <div class="d-inline-block">
                    <div class="d-inline-block mr-1 h6">Date: </div>
                    <div class="btn-group my-1" role="group" aria-label="Sort" data-ng-cloak>
                        <button type="button" class="btn btn-secondary"
                                data-ng-click="reviewFilters.dateAsc = false" data-ng-disabled="!reviewFilters.dateAsc">Desc</button>
                        <button type="button" class="btn btn-secondary"
                                data-ng-click="reviewFilters.dateAsc = true" data-ng-disabled="reviewFilters.dateAsc">Asc</button>
                    </div>
                </div>
<%--                <div class="d-inline-block">--%>
<%--                    <div class="d-inline-block mr-1 h6">Scheme: </div>--%>
<%--                    <button type="button" class="btn btn-secondary" data-ng-click="reviewFilters.sortByScheme = !reviewFilters.sortByScheme"--%>
<%--                            data-toggle="button" aria-pressed="false" autocomplete="off">--%>
<%--						{{reviewFilters.sortByScheme ? 'YES' : 'NO'}}--%>
<%--                    </button>--%>
<%--                </div>--%>
                <div class="d-inline-block">
					<button class="btn" data-ng-click="reviewFilters.filterByBollards = !reviewFilters.filterByBollards"
							data-ng-class="{'btn-secondary': reviewFilters.filterByBollards, 'btn-outline-secondary': !reviewFilters.filterByBollards}">
						<div class="d-inline-block mr-0 pretty p-default p-curve">
							<input type="checkbox" ng-model="reviewFilters.withBollards" ng-click="$event.stopPropagation()">
							<div class="state p-primary">
								<label></label>
							</div>
						</div>
                        <span class="d-inline-block">Bollards</span>
					</button>

                </div>
                <div class="d-inline-block">
					<button class="btn" data-ng-click="reviewFilters.filterByGraffiti = !reviewFilters.filterByGraffiti"
							data-ng-class="{'btn-secondary': reviewFilters.filterByGraffiti, 'btn-outline-secondary': !reviewFilters.filterByGraffiti}">
						<div class="d-inline-block mr-0 pretty p-default p-curve">
							<input type="checkbox" data-ng-model="reviewFilters.withGraffiti" ng-click="$event.stopPropagation()">
							<div class="state p-primary">
								<label></label>
							</div>
						</div>
                        <span class="d-inline-block">Graffiti</span>
					</button>
                </div>
                <div class="d-inline-block">
					<button class="btn" data-ng-click="reviewFilters.filterByWeeds = !reviewFilters.filterByWeeds"
							data-ng-class="{'btn-secondary': reviewFilters.filterByWeeds, 'btn-outline-secondary': !reviewFilters.filterByWeeds}">
						<div class="d-inline-block mr-0 pretty p-default p-curve">
							<input type="checkbox" data-ng-model="reviewFilters.withWeeds" ng-click="$event.stopPropagation()">
							<div class="state p-primary">
								<label></label>
							</div>
						</div>
                        <span class="d-inline-block">Weeds/Debris</span>
					</button>
                </div>
                <div class="d-inline-block">
					<button class="btn" data-ng-click="reviewFilters.filterByBikes = !reviewFilters.filterByBikes"
							data-ng-class="{'btn-secondary': reviewFilters.filterByBikes, 'btn-outline-secondary': !reviewFilters.filterByBikes}">
						<div class="d-inline-block mr-0 pretty p-default p-curve">
							<input type="checkbox" data-ng-model="reviewFilters.withBikes" ng-click="$event.stopPropagation()">
							<div class="state p-primary">
								<label></label>
							</div>
						</div>
                        <span class="d-inline-block">Bikes</span>
					</button>
                </div>
                <div class="d-inline-block">
					<button class="btn" data-ng-click="reviewFilters.filterByStation = !reviewFilters.filterByStation"
							data-ng-class="{'btn-secondary': reviewFilters.filterByStation, 'btn-outline-secondary': !reviewFilters.filterByStation}">
						<div class="d-inline-block mr-0 pretty p-default p-curve">
							<input type="checkbox" data-ng-model="reviewFilters.withStation" ng-click="$event.stopPropagation()">
							<div class="state p-primary">
								<label></label>
							</div>
						</div>
                        <span class="d-inline-block">Station</span>
					</button>
                </div>
            </div>
<%--            <div class="d-block my-2">--%>
<%--                <button class="btn btn-primary" data-ng-click="loadRoutineReviewsInfoAndData()">Reload</button>--%>
<%--            </div>--%>

            <div id="{{'accordion' + $index}}" class="" data-ng-repeat="review in routineReviews" data-ng-show="needToShowReviewAccordion($index, review)" data-ng-cloak>
                <div class="card">
                    <div class="card-header">
                        <div class="row">
                            <div class="col-md-4 p-0 pl-md-2 pr-md-2">
								<button class="btn btn-info msc-round bigger-18 mr-2"
										style="color: #FFFFFF; padding-left: 1px; padding-top: 1px"
										data-toggle="collapse"
										data-target="{{'#collapse' + $index}}" aria-expanded="false"
										aria-controls="{{'collapse' + $index}}">
									<em class="material-icons md-14">more_horiz</em>
								</button>
								Station: <strong>{{review.station.location}} | {{review.station.scheme.name}}</strong>
                            </div>
                            <div class="text-danger col-12 col-md-3 p-0 pl-md-2 pr-md-2">{{getReviewHeaderMiddleText(review)}}</div>
                            <div class="col-12 col-md-5 d-flex justify-content-start justify-content-md-end p-0 pl-md-2 pr-md-2">
                                Operator: <strong class="pr-1">{{review.operator}}</strong>
                                Date: <strong>{{review.creationDate}}</strong>
                            </div>
                        </div>
                    </div>

                    <div id="{{'collapse' + $index}}" class="collapse"
                         aria-labelledby="{{'heading' + $index}}" data-parent="#accordion">

						<div class="row">
							<div class="col-md-4 col-lg-3 pl-4">
								<div class="d-flex justify-content-between">
									<div class="text-left">Bollards total:</div><div class="text-right">{{review.station.bollardsTotalNumber}}</div>
								</div>
								<div class="d-flex justify-content-between">
									<div class="text-left">Bikes at station:</div><div class="text-right">{{review.bikesAtStation}}</div>
								</div>
								<div class="d-flex justify-content-between">
									<div class="text-left">Graffiti:</div><div class="text-right">{{review.graffiti ? "YES" : "NO"}}</div>
								</div>
								<div class="d-flex justify-content-between">
									<div class="text-left">Weeds/Debris:</div><div class="text-right">{{review.weeds ? "YES" : "NO"}}</div>
								</div>
							</div>
							<div class="col-md-4 col-lg-5">
								<div class="row" data-ng-if="review.inactiveBollards && review.inactiveBollards.length > 0">
									<div class="col-5">
										Inactive bollards:
									</div>
									<div class="col-7 pl-0">
										<div data-ng-repeat="bollard in review.inactiveBollards">
											{{bollard.bollardNo}} - {{bollard.reason}}
										</div>
									</div>
								</div>
							</div>
							<div class="col-md-4">
								<div class="d-block" data-ng-if="review.sReportId">
									Station Report: - ID <a style='color: blue' href='#' data-ng-click='editReportById(review.sReportId)'>{{review.sReportId}}</a>
								</div>
								<div class="d-block">
                                    <div class="d-inline-block align-top" style="height: 100%" data-ng-if="review.bReportsIds">
                                        Reported Bikes:
                                    </div>
                                    <div class="d-inline-block" data-ng-if="review.bReportsIds">
                                        <div data-ng-repeat="br in review.myReports">{{br.bike.number}} - ID <a style='color: blue' href='#' data-ng-click='editReportById(br.id)'>{{br.id}}</a></div>
                                    </div>
                                </div>
							</div>
						</div>

                    </div>
                </div>
            </div>
			<pagination parameters="pagesParameters" data-ng-if="pagesParameters"
						class="msc-inline-block mt-1"></pagination>


        </div>

<%--        report editing--%>

		<div data-ng-if="reportToEdit" data-ng-cloak>
			<button class="btn btn-info" data-ng-click="backToReports()">< Back to reports</button>
			
			<div class="msc-line my-2"></div>
			
			<div class="bg-light my-2">
				<h6 class="msc-inline-block">ID: {{reportToEdit.id}} | Station: {{reportToEdit.location.location}} | {{reportToEdit.stationItself ? "" : ("Bike: " + reportToEdit.bike.number + " |")}}</h6>
				<h6 class="msc-inline-block">Report date: {{reportToEdit.reportDate}} | Status: {{reportToEdit.state.name}}</h6>
                <h6 class="msc-inline-block" data-ng-if="reportToEdit.collectedDate">(Collected on {{reportToEdit.collectedDate}})</h6>
			</div>
			
			<div class="row">
				<div class="col-md-3 d-inline-block d-md-none"> <!-- for phones -->
                    <button class="btn btn-info mb-2" data-ng-disabled="(reportToEdit.jobStarted || reportToEdit.state.id === STATE_DONE || !operators) || isUserOnlySchemeLeader()"
                            data-ng-if="reportToEdit.state.id !== STATE_PENDING" data-ng-click="startJob()">Start Job</button>
                    <button class="btn btn-info mb-2" data-ng-disabled="isUserOnlySchemeLeader()"
                            data-ng-if="reportToEdit.state.id === STATE_PENDING" data-ng-click="collect()">Collect</button>
                </div>
				<div class="col-md-9">
					<div class="row">
						<div class="col-md-5">
							<div class="card">
								<div class="card-body">
									<h6 class="card-title bg-light p-2">Repairs Needed</h6>
									<p class="card-text">{{reportToEdit.repairReasonString}}</p>
								</div>
							</div>
							<div class="card my-1">
								<div class="card-body">
									<h6 class="card-title bg-light p-2">Operator's Comment</h6>
									<p class="card-text">{{reportToEdit.streetComments}}</p>
								</div>
							</div>
							<div class="card my-1" data-ng-if="reportToEdit.bollardNumbers && reportToEdit.bollardNumbers.length > 0">
								<div class="card-body">
									<h6 class="card-title bg-light p-2">Reported bollards</h6>
									<div>
										<p class="card-text">{{reportToEdit.bollardNumbers}}</p>
										<p class="card-text">{{reportToEdit.bollardComments}}</p>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-7">
							<div class="card">
								<div class="card-body">
									<h6 class="card-title bg-light p-2">Repair History</h6>
									<p class="card-text"><div data-ng-repeat="report in reportToEdit.repairHistory track by $index">- {{report}}</div><p>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-3 d-none d-md-inline-block">
                    <button class="btn btn-info mb-2" data-ng-disabled="((reportToEdit.jobStarted || isUserOnlySchemeLeader()) && selectedOperator.value) || reportToEdit.state.id === STATE_DONE || !operators"
                            data-ng-if="reportToEdit.state.id !== STATE_PENDING" data-ng-click="startJob()">Start Job</button>
					<button class="btn btn-info mb-2"
                            data-ng-if="reportToEdit.state.id === STATE_PENDING" data-ng-click="collect()">Collect</button>
				</div>
			</div>
			
			<div class="msc-line my-2"></div>
			
			<div class="row" data-ng-if="reportToEdit.state.id === STATE_DONE || reportToEdit.jobStarted || selectedOperator.value || isUserOnlySchemeLeader()">
				<div class="col-lg-4 col-md-5" data-ng-if="(reportToEdit.state.id === STATE_IN_PROGRESS || reportToEdit.jobStarted) && !isUserOnlySchemeLeader()">
					<div class="card">
						<div class="card-body">
							<h6 class="card-title bg-light p-2">Repairs Complete</h6>

							<div class="card-text" data-ng-repeat="operator in reportToEdit.operators" data-ng-show="operator.jobsDone && operator.jobsDone.length && operator.id !== selectedOperator.value.id">
								<span><strong>{{operator.userName}}</strong>: {{operator.jobsDone}}</span>
							</div>

							<div class="row">
								<div class="col-12" data-ng-repeat="tempRJ in selectedOperator.tempRepairJobs">
									<div class="d-flex justify-content-between align-items-center mb-2 mr-2 p-2" style="border-radius: 4px; background: #5c8f94; width: 100%; color: #fff">
										<div>
											{{tempRJ.job}}
										</div>

										<div>
											<button data-ng-click="removeTempRepairJob($index)"
													class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
												<i class="material-icons md-18 text-white">clear</i>
											</button>
										</div>
									</div>
								</div>
								<div class="mt-2 col-12">
									<searchable-select name="'jobs-done'" set-model="setTempRepairJob" array="repairJobsToChoose"
													   search-by-field="'job'" is-disabled="returnFalse" is-valid="true" cleaned-string="true"></searchable-select>
								</div>
							</div>
						</div>
					</div>
				
					<br>
				
					<div class="card">
						<div class="card-body">
							<h6 class="card-title bg-light p-2">Parts Fit</h6>
							<div class="card-text" data-ng-repeat="oper in reportToEdit.operators" data-ng-show="oper.id !== selectedOperator.value.id && oper.usedSpareparts.length > 0">
								<span class="font-weight-bold">{{oper.userName}}</span>
								<div class="row align-items-center pb-2" data-ng-repeat="sp in oper.usedSpareparts">
									<div class="col-8" data-ng-class="{'text-muted': sp.forRemove}">{{sp.productType.fullName}}</div>
									<div class="col-2" data-ng-class="{'text-muted': sp.forRemove}">{{sp.amount}}</div>
									<div class="col-2">
										<button data-ng-click="askToRemoveSparepart(oper, $index)"
												class="btn btn-danger close-btn d-flex align-items-center justify-content-center"
												data-ng-disabled="sp.forRemove">
											<i class="material-icons md-14 text-white">clear</i>
										</button>
									</div>
								</div>
							</div>
							<div class="card-text new-spare-part-wrapper mb-1" data-ng-show="selectedOperator.sparePartsToUse.length > 0">
								<div class="row align-items-center pb-1 pt-1" data-ng-repeat="spPart in selectedOperator.sparePartsToUse">
									<div class="col-7">{{spPart.sparePart.showName}}</div>
									<div class="col-2">{{spPart.amount}}</div>
									<div class="col-3">
										<button data-ng-click="removeNewSparePart($index)"
												class="btn btn-danger close-btn d-flex align-items-center justify-content-center float-right">
											<i class="material-icons md-14 text-white">clear</i>
										</button>
									</div>
								</div>
							</div>

							<button class="btn btn-warning mb-3" data-ng-click="openUseSparePartsModal()">Use Spare Parts</button>
							<button class="btn btn-warning mb-3 float-right" data-ng-click="openAddQRModal()">Add QR</button>
						</div>
					</div>
					
					</div>
				<div class="col-lg-4 col-md-5" data-ng-if="reportToEdit.state.id === STATE_DONE || isUserOnlySchemeLeader()">
					<div class="card">
						<div class="card-body">
							<h6 class="card-title bg-light p-2">Repairs Complete</h6>
							<div class="card-text" data-ng-repeat="oper in reportToEdit.operators">
								<span data-ng-show="oper.jobsDone && oper.jobsDone.length"><strong>{{oper.userName}}</strong>: {{oper.jobsDone}}</span>
							</div>
						</div>
					</div>
					<div class="card my-1">
						<div class="card-body">
							<h6 class="card-title bg-light p-2">Parts Fit</h6>
							<div class="card-text" data-ng-repeat="oper in reportToEdit.operators" data-ng-show="oper.usedSpareparts.length > 0">
								<span class="font-weight-bold">{{oper.userName}}</span>
								<div class="row" data-ng-repeat="sp in oper.usedSpareparts">
									<div class="col-10">{{sp.productType.fullName}}</div>
									<div class="col-2">{{sp.amount}}</div>
								</div>
							</div>
						</div>
					</div>	
				</div>
				
				<div class="col-lg-5 col-md-4" data-ng-if="reportToEdit.state.id === STATE_DONE || isUserOnlySchemeLeader()">
					<div class="card">
						<div class="card-body">
							<h6 class="card-title bg-light p-2">Repair Comments</h6>
							<p class="card-text">{{reportToEdit.depotComments}}</p>
						</div>
					</div>
				</div>
				
				<div class="col-lg-5 col-md-4" data-ng-if="(reportToEdit.state.id === STATE_IN_PROGRESS  || reportToEdit.jobStarted) && !isUserOnlySchemeLeader()">
					Repair Comments
					<textarea class="form-control" id="repairComments" rows="6" data-ng-model="reportToEdit.depotComments"></textarea>
				</div>
				
				<div class="col-md-3 d-inline-block">
					<div class="row mt-2">
						<div class="col-5 col-sm-12" data-ng-hide="reportToEdit.state.id === STATE_DONE || isUserOnlySchemeLeader()">
							<button class="btn btn-primary" style="width: 124px" data-ng-click="saveEditedReport()">Save Report</button>
							<br>
							<button class="btn btn-success mt-1" data-ng-click="saveEditedReport(true)">Complete Job</button>
						</div>
						<div class=" col-7 col-sm-12">
							<div class="pretty p-default p-curve">
								<input type="checkbox" id="routineReview" data-ng-model="reportToEdit.routineReview" data-ng-disabled="reportToEdit.state.id === STATE_DONE || isUserOnlySchemeLeader()"/>
								<div class="state p-primary">
									<label>Routine Review</label>
								</div>
							</div>
							<div class="pretty p-default p-curve">
								<input type="checkbox" id="onStreetRepair" data-ng-model="reportToEdit.onStreetRepair" data-ng-disabled="reportToEdit.state.id === STATE_DONE || isUserOnlySchemeLeader()"/>
								<div class="state p-primary">
									<label>Repaired on-street</label>
								</div>
							</div>
						</div>
					</div>
				</div>				
			</div>
		</div>
		
		<content-modal modal-id="'useSparePartsModal'" max-width="'650px'" data-ng-cloak
			on-confirm="useSpareParts" on-confirm-text="'Use'"
			data-title="'Parts Fit'" hide-manually-on-confirm="true"
			is-frozen="isModalFrozen" confirm-disabled="isUseSparePartsConfirmDisabled">
			
				<div class="row align-items-center"
					data-ng-repeat="spToUse in selectedOperator.tempSparePartsToUse"
					data-ng-if="reportToEdit">
					<div class="col-8 mt-1">
						<searchable-select name="'sparepart-select' + $index" api="sparepartSelectApis[$index]" set-model="setSparePart" array="spareParts" add-params="[spToUse]" search-by-field="'showName'" is-disabled="returnFalse"
										   is-valid="!spToUse.invalid" small="true" showed-string="spToUse.sparePart.showName"></searchable-select>
					</div>
					<div class="col-3 mt-1">
						<input type="number" class="form-control form-control-sm"
							pattern="[0-9]*" data-ng-model="spToUse.amount" data-ng-class="{'is-invalid': spToUse.noAmount}" data-ng-change="onAmountChange(spToUse)">
					</div>
					<div class="col-1 mt-1 d-flex justify-content-center">
						<button data-ng-click="removeSparePartFromReport($index)"
								class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
							<i class="material-icons md-18 text-white">clear</i>
						</button>
					</div>
				</div>
							
				<button data-ng-click="addSparePartToReport()"
					class="btn btn-success msc-round bigger-18"
					style="color: #FFFFFF; padding-left: 1px; padding-top: 1px">
					<i class="material-icons md-14">add</i>
				</button>
												
		</content-modal>
		
		<content-modal modal-id="'addQRModal'" max-width="'450px'"
			max-height="'100%'" data-ng-cloak on-confirm="addQRSparePartToReport"
			on-confirm-text="'Submit'" data-title="'QR & Barcode Scanner'"
			hide-manually-on-confirm="false" is-frozen="isModalFrozen"
			confirm-disabled="isUseQRConfirmDisabled">
				<div id="qrVideo" data-ng-if="showQRVideo">
					<video id="preview" class="rounded" style="width: 100%"></video>
					<span ng-bind="camera"></span>
				</div>
				<div id="qrSparePart" data-ng-if="showQRSparePart">
					<p class="col-sm-10 col-form-label">{{scannedSparePart.productType.fullName}}</p>
					<p class="col-sm-10 col-form-label">Put the number of parts to be used</p>
					<div class="col-sm-10 mt-1">
						<input type="number" class="form-control form-control-sm"
							pattern="[0-9]*" data-ng-model="scannedSparePart.amount">
					</div>
				</div>
		</content-modal>

		<content-modal modal-id="'operatorSelection'" max-width="'350px'" data-ng-cloak
			on-confirm="chooseOperator" on-confirm-text="'OK'" hide-cancel="true"
			data-title="'Select an Operator'" hide-manually-on-confirm="false"
			is-frozen="isModalFrozen" confirm-disabled="isOperatorSelectionConfirmDisabled">

				<searchable-select name="'operator-select'" api="operatorSelectApi" set-model="setOperator" array="operators" add-params="[]" search-by-field="'fullName'" is-disabled="returnFalse"
							   is-valid="returnTrue"></searchable-select>
												
		</content-modal>
		
		<confirm-modal data-modal-id="'removeSparepartCheck'"
			data-on-confirm="rememberSparepartsToRemove"
			data-title="'Remove?'"
			data-text="''"></confirm-modal>
		
	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
