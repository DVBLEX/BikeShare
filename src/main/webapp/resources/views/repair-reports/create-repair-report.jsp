<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Create Repair Report</title>

<%@ include file="/resources/views/html-headers/header.html"%> 

<script type="application/javascript"
	src="../resources/logic/repair-reports/create-repair-report.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="createRepairReportCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">

		<div ng-if="!bikeStations && !bikes" class="text-center mt-5">
			<div class="spinner-border size-spinner" role="status">
				<span class="sr-only">Loading...</span>
			</div>
		</div>

		<div ng-if="bikeStations && bikes">
			<div data-ng-if="newReport && bikeStations && bikes && stationRepairReasons && bikeRepairReasons" data-ng-cloak>
				<div class="form-group">
					<label>Location</label>
					<searchable-select name="'locations-select'" api="locationSelectApi" set-model="setLocation" array="bikeStations" add-params="[]" search-by-field="'name'" is-disabled="returnFalse"
									   is-valid="returnTrue"></searchable-select>
				</div>
				<div class="pretty p-default p-curve mb-2">
					<input type="checkbox" id="station-report"
						   data-ng-change="onBikeNumberChange()" ng-model="newReport.stationItself">
					<div class="state p-primary">
						<label>Station</label>
					</div>
				</div>
				<div class="form-group mb-2" data-ng-show="!newReport.stationItself">
					<label>Bike ID</label>
					<searchable-select name="'bike-number-select'" api="bikeSelectApi" set-model="setBike" array="bikes" add-params="[]" search-by-field="'number'" is-disabled="isStationReport"
									   is-valid="isBikeGood"></searchable-select>
				</div>
				<div class="row mb-2" data-ng-if="newReport.stationItself">
					<div class="col-6 col-lg-6">
						<input class="form-control" data-ng-model="newReport.bollardNumbers" placeholder="Bollard Numbers">
					</div>
					<div class="col-6">
						<input class="form-control" data-ng-model="newReport.bollardComments" placeholder="Comments">
					</div>
				</div>
				<div class="pretty p-default p-curve">
					<input type="checkbox" ng-model="newReport.vandalism">
					<div class="state p-primary">
						<label>Vandalism</label>
					</div>
				</div>
				<div class="pretty p-default p-curve" data-ng-if="!newReport.stationItself">
					<input type="checkbox" ng-model="newReport.pendingCollection">
					<div class="state p-primary">
						<label>Pending Collection</label>
					</div>
				</div>

				<div class="form-group mt-2">
					<label data-ng-class="{'text-danger': newReport.noRepairReason}">Repairs Needed</label>
					<div class="row">
						<div class="col-lg-auto col-md-6 col-12" data-ng-repeat="report in newReport.repairReason">
							<div class="d-flex justify-content-between align-items-center mb-2 mr-2 p-2" style="border-radius: 4px; background: #5c8f94; width: 100%; color: #fff">
								<div>
									{{report.reason}}
								</div>

								<div>
									<button data-ng-click="removeRepair($index)"
											class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
										<i class="material-icons md-18 text-white">clear</i>
									</button>
								</div>
							</div>

						</div>
					</div>

					<div class="mt-2">
						<searchable-select name="'repair-select'"
										   set-model="setRepair" array="repairReasonsToChoose"
										   search-by-field="'reason'" is-disabled="returnFalse" cleaned-string="true"
										   is-valid="true"></searchable-select>
					</div>

				</div>

				<div class="form-group mb-2">
					<label for="operatorComments">Operator Comments</label>
					<textarea class="form-control" id="operatorComments" rows="3" data-ng-model="newReport.streetComments"></textarea>
				</div>

				<button class="btn btn-success mt-1 mt-sm-0" data-ng-click="saveReport()" data-ng-disabled="savingReport">Save</button>
				<button class="btn btn-info mt-1 mt-sm-0" data-ng-click="saveReport(true)" data-ng-disabled="savingReport">Save&Repair</button>
				<button class="btn btn-dark mt-1 mt-sm-0" data-ng-click="saveReportAndComplete()" data-ng-disabled="savingReport">Repair Done</button>
			</div>
		</div>

	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
