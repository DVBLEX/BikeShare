<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Create Routine Review</title>

<%@ include file="/resources/views/html-headers/header.html"%> 

<script type="application/javascript"
	src="../resources/logic/repair-reports/routine-review.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="routineReviewCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">

		<div ng-if="!bikeStations" class="text-center mt-5">
			<div class="spinner-border size-spinner" role="status">
				<span class="sr-only">Loading...</span>
			</div>
		</div>

		<div ng-if="bikeStations">

			<%--	Old functionality for geolocation was commented below   --%>
			<%--	<div style="width: 100%" class="d-flex justify-content-center" data-ng-show="!bikeStation">
                        <button class="btn btn-info" data-ng-click="checkIn()">Check in</button>
                    </div>--%>

			<div class="form-group">
				<label>Location</label>
				<searchable-select name="'locations-select'" api="locationSelectApi" set-model="setLocation" array="bikeStations" add-params="[]" search-by-field="'name'" is-disabled="returnFalse"
								   is-valid="returnTrue"></searchable-select>
			</div>

			<div data-ng-show="bikeStation" data-ng-cloak>

				<%--		Old functionality for geolocation was commented below   --%>
				<%--		<div class="alert alert-info d-flex justify-content-center" role="alert">
                                  Station: {{bikeStation.location}}
                            </div>--%>

				<div class="row">
					<div class="col-12 col-md-7 col-xl-5">
						<div class="row">
							<div class="col-6">
								<div class="pt-2 d-inline-block">Bollards Total</div>
								<input type="number" class="form-control d-inline-block" style="max-width: 75px" ng-model="bikeStation.bollardsTotalNumber" disabled>
							</div>
							<div class="col-6 pl-0 pl-lg-3">
								<div class="pt-2 d-inline-block">Bikes at Station</div>
								<input type="number" min="0" class="form-control d-inline-block" style="max-width: 75px" ng-model="reviewModel.bikesAtStation"
									   data-ng-class="{'is-invalid': !reviewValidation.bikesAtStation}" data-ng-change="reviewValidation.bikesAtStation = true;">
							</div>
						</div>
					</div>
					<div class="col-12 col-md-5 col-xl-3">
						<div class="row">
							<div class="col-5  pt-2">
								<div class="pretty p-default p-curve">
									<input type="checkbox" ng-model="reviewModel.graffiti">
									<div class="state p-primary">
										<label>Graffiti</label>
									</div>
								</div>
							</div>
							<div class="col-7  pt-2">
								<div class="pretty p-default p-curve">
									<input type="checkbox" ng-model="reviewModel.weeds">
									<div class="state p-primary">
										<label>Weeds/Debris</label>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-12 col-xl-4 d-flex  mt-md-2 mt-xl-0">
						<div class="pt-3 pt-sm-2" style="width: 180px">Bollard Inactive</div>
						<div class="input-group pt-2 pt-sm-0" style="width: 100%">
							<input type="number" class="form-control" style="max-width: 60px; height: 100%" data-ng-model="inactiveBollards.length" disabled>
							<div class="input-group-append" id="button-addon4">
								<button class="btn btn-outline-secondary" type="button" ng-click="removeInactiveBollards()">-</button>
								<button class="btn btn-outline-secondary" type="button" ng-click="addInactiveBollards()">+</button>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-12 col-md-7 col-xl-6">
						<div class="d-flex border-info mt-2" data-ng-repeat="ib in inactiveBollards">
							<div class="d-inline-block mt-2" style="width: 115px">Bollard No</div>
							<input type="number" min="0" class="form-control d-inline-block mr-2"  style="max-width: 60px" data-ng-model="ib.bollardNo"
								   data-ng-class="{'is-invalid': ib.invalidNo}" data-ng-change="ib.invalidNo = false">
							<input class="form-control d-inline-block" data-ng-model="ib.reason">
						</div>
					</div>
				</div>
				<hr>

				<div class="row d-none d-md-flex">
					<div class="col-6 col-md-3 col-xl-2 pt-2 pb-2">
						<div class="pretty p-default p-curve">
							<input type="checkbox" ng-model="reviewModel.reportStation">
							<div class="state p-primary">
								<label>Report a Station</label>
							</div>
						</div>
					</div>
					<div class="col-6 col-md-2 padding-x-5px pt-2" data-ng-if="reviewModel.reportStation">
						<div class="pretty p-default p-curve">
							<input type="checkbox" ng-model="reportedStationModel.vandalism">
							<div class="state p-primary">
								<label>Vandalism</label>
							</div>
						</div>
					</div>
					<button class="btn btn-primary col-4 col-md-2" data-ng-if="reviewModel.reportStation" data-ng-click="openRepairsNeededModal()"
							data-ng-class="{'btn-danger': reportedStationModel.repairsNotValid}">Repairs Needed</button>
					<div class="col-8 col-md-5 col-xl-6"><input class="form-control" data-ng-if="reviewModel.reportStation" data-ng-model="reportedStationModel.comments" placeholder="Comments"></div>
				</div>

				<div class="row my-2 d-none d-md-flex" ng-repeat="rb in bikesReported">
					<div class="col-6 col-md-2">
						<div class="d-flex align-items-center">
							<div>
								<button data-ng-click="removeABike($index)"
										class="mr-2 btn btn-danger close-btn d-flex align-items-center justify-content-center">
									<i class="material-icons md-14 text-white">clear</i>
								</button>
							</div>
							<div>
								<input class="form-control d-inline-block" data-ng-class="{'is-invalid': rb.invalidNumber}"
									   style="width: calc(100% - 30px)" data-ng-model="rb.bikeId" data-ng-change="updateBikeOnChange(rb)"
									   placeholder="Bike ID">
							</div>
						</div>

					</div>
					<div class="col-6 col-md-2 padding-x-5px pt-2">
						<div class="pretty p-default p-curve">
							<input type="checkbox" ng-model="rb.vandalism">
							<div class="state p-primary">
								<label>Vandalism</label>
							</div>
						</div>
					</div>
					<div class="col-6 col-md-3 col-xl-2 padding-x-5px pt-2">
						<div class="pretty p-default p-curve">
							<input type="checkbox" ng-model="rb.pendingCollection">
							<div class="state p-primary">
								<label>Pending Collection</label>
							</div>
						</div>
					</div>
					<button class="btn btn-primary col-6 col-md-2" data-ng-click="openRepairsNeededModal(rb)"
							data-ng-class="{'btn-danger': rb.repairsNotValid}">Repairs Needed</button>
					<div class="col-12 col-md-3 col-xl-4"><input class="form-control" data-ng-model="rb.comments" placeholder="Comments"></div>
				</div>


				<%--for phones--%>
				<div class="row d-flex d-md-none mb-2">
					<div class="col-6">
						<div class="col-12 pt-2 px-0">
							<div class="pretty p-default p-curve">
								<input type="checkbox" ng-model="reviewModel.reportStation">
								<div class="state p-primary">
									<label>Report a Station</label>
								</div>
							</div>
						</div>
						<div class="col-12 pt-2 px-0" data-ng-if="reviewModel.reportStation">
							<div class="pretty p-default p-curve">
								<input type="checkbox" ng-model="reportedStationModel.vandalism">
								<div class="state p-primary">
									<label>Vandalism</label>
								</div>
							</div>
						</div>
						<div class="col-12 px-0">
							<button class="btn btn-primary" data-ng-if="reviewModel.reportStation" data-ng-click="openRepairsNeededModal()"
									data-ng-class="{'btn-danger': reportedStationModel.repairsNotValid}">Repairs Needed</button>
						</div>
					</div>
					<div class="col-md-6 col-12 pl-md-0 pt-2 pt-md-0">
						<textarea class="form-control" style="height: 100%" data-ng-if="reviewModel.reportStation" data-ng-model="reportedStationModel.comments" placeholder="Comments"></textarea>
					</div>
				</div>

				<div class="row my-2 d-flex d-md-none" ng-repeat="rb in bikesReported">
					<div class="col-6">
						<div class="col-12 px-0">
							<div class="d-flex align-items-center">
								<div>
									<button data-ng-click="removeABike($index)"
											class="mr-2 btn btn-danger close-btn d-flex align-items-center justify-content-center">
										<i class="material-icons md-14 text-white">clear</i>
									</button>
								</div>
								<div>
									<input class="form-control d-inline-block" data-ng-class="{'is-invalid': rb.invalidNumber}" style="width: calc(100% - 30px)"
										   data-ng-model="rb.bikeId" data-ng-change="updateBikeOnChange(rb)" placeholder="Bike ID">
								</div>
							</div>
						</div>
						<div class="col-12 pt-2 px-0">
							<div class="pretty p-default p-curve">
								<input type="checkbox" ng-model="rb.vandalism">
								<div class="state p-primary">
									<label>Vandalism</label>
								</div>
							</div>
						</div>
						<div class="col-12 pt-2 px-0">
							<div class="pretty p-default p-curve">
								<input type="checkbox" ng-model="rb.pendingCollection">
								<div class="state p-primary">
									<label>Pending Collection</label>
								</div>
							</div>
						</div>
						<div class="col-12 px-0">
							<button class="btn btn-primary" data-ng-click="openRepairsNeededModal(rb)"
									data-ng-class="{'btn-danger': rb.repairsNotValid}">Repairs Needed</button>
						</div>
					</div>
					<div class="col-md-6 col-12 pl-md-0 pt-2 pt-md-0">
						<textarea class="form-control" style="height: 100%" data-ng-model="rb.comments" placeholder="Comments"></textarea>
					</div>
				</div>



				<button class="btn btn-info d-block mt-1" data-ng-click="reportABike()">Report a Bike</button>

				<div class="row d-flex justify-content-center mt-2 my-2" data-ng-if="bikeStation">
					<div class="col-12 col-md-5 mx-2">
						<button class="btn btn-success w-100" data-ng-click="requestComplete()">Complete</button>
					</div>
				</div>
			</div>



			<confirm-modal data-modal-id="'proceed'"
						   data-on-confirm="proceed"
						   data-title="'Proceed?'"
						   data-text="''"></confirm-modal>

			<confirm-modal data-modal-id="'complete'"
						   data-on-confirm="complete"
						   data-title="'Complete?'"
						   data-text="''"></confirm-modal>


			<content-modal modal-id="'repairsNeededModal'" max-width="'650px'" data-ng-cloak
						   on-confirm="setRepairsNeeded" on-confirm-text="'Set'"
						   data-title="'Set Repairs Needed'" hide-manually-on-confirm="false"
			>

				<div class="row">
					<div class="col-md-6 col-12" data-ng-repeat="repair in selectedEntity.tempRepairsNeeded">
						<div class="d-flex justify-content-between align-items-center mb-2 mr-2 p-2" style="border-radius: 4px; background: #5c8f94; width: 100%; color: #fff">
							<div>
								{{repair.reason}}
							</div>

							<div>
								<button data-ng-click="removeTempRepair($index)"
										class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
									<i class="material-icons md-18 text-white">clear</i>
								</button>
							</div>
						</div>
					</div>
					<div class="mt-2 col-12">
						<searchable-select name="'repair-select'" set-model="setRepair" array="tempRepairsNeeded"
										   search-by-field="'reason'" is-disabled="returnFalse" is-valid="true" cleaned-string="true"></searchable-select>
					</div>
				</div>

			</content-modal>
		</div>
		</div>

	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
