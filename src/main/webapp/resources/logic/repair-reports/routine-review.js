"use strict";

app.controller('routineReviewCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	function($scope, $http, $timeout, $rootScope) {	
		

		$scope.reviewModel = {
			reportStation: false,
			bikesAtStation: 0,
			graffiti: false,
			weeds: false
		}

		$scope.reviewValidation = {
			bikesAtStation: true
		}
		$scope.inactiveBollards = [];


		$scope.reportedStationModel = {
			vandalism: false,
			repairsNeeded: [],
			comments: ''
		}

		$scope.bikesReported = [];
		$scope.repairSelectApis = [];

		var loadData = function(){
			$http.get(projectRoot + "/msc-api/user/user-scheme").then(function(response){
				$scope.schemeOfUser = response.data;


				$http.get(projectRoot + "/msc-api/repair-reports/bike-stations/" + $scope.schemeOfUser.name).then(function(response){
					$scope.bikeStations = response.data;

					for (const station of $scope.bikeStations) {
						station.name = station.id + ' ' + station.location;
					}

				}, function(errorResponse){

				});

			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/repair-reports/report-reasons").then(function(response){
				$scope.reportReasons = response.data;

			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/repair-reports/repair-reasons/station").then(function(response){
				$scope.repairReasonsStation = response.data;

			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/repair-reports/repair-reasons/bike").then(function(response){
				$scope.repairReasonsBikes = response.data;

			}, function(errorResponse){
				
			});
		}
		
		loadData();

// 		Old functionality for geolocation was commented below
/*		$scope.checkIn = function(){
			var savePosition = function(position){
				$scope.myPosition = position;
				console.log("my position", $scope.myPosition);

				if(!$scope.myPosition){
					console.log("return");
					return;
				}

				$http.put(projectRoot + "/msc-api/repair-reports/bike-stations/by-geo", {latitude: $scope.myPosition.coords.latitude, longitude: $scope.myPosition.coords.longitude}).then(function(response){

					if(!response.data || response.data === ""){
						$rootScope.errorNotificationCustom("Could not find station");
					} else {
						$scope.bikeStation = response.data;
					}

				}, function(errorResponse){
					$rootScope.errorNotification("Could not find station");
				})
			}
			try{
				navigator.geolocation.getCurrentPosition(savePosition);
			} catch (e){
				$rootScope.errorNotificationCustom("Could not get position");
			}

		}*/

		$scope.setLocation = function(val) {
			let bikeStation = {};
			bikeStation.geoLat = val.geoLat;
			bikeStation.geoLong = val.geoLong;
			bikeStation.id = val.id;
			bikeStation.location = val.location;
			bikeStation.scheme =  val.scheme;
			bikeStation.bollardsTotalNumber =  val.bollardsTotalNumber;
			$scope.bikeStation = bikeStation;

			$scope.reviewModel = {
				reportStation: false,
				bikesAtStation: 0,
				graffiti: false,
				weeds: false
			}
			$scope.inactiveBollards = [];
		};

		$scope.addInactiveBollards = function() {
			if ($scope.inactiveBollards.length < $scope.bikeStation.bollardsTotalNumber) {
				$scope.inactiveBollards.push({
					bollardNo: 0,
					reason: ""
				})
			}
		}

		$scope.removeInactiveBollards = function() {
			$scope.inactiveBollards.splice(-1, 1);
		}


		$scope.requestProceed = function(){
			$('#proceed').modal('show');
		}

		$scope.reportABike = function() {
			$scope.bikesReported.push({
				bikeId: '',
				vandalism: false,
				pendingCollection: false,
				repairsNeeded: [],
				comments: ''
			})
		}

		$scope.removeABike = function(i) {
			$scope.bikesReported.splice(i, 1);
		}

		$scope.openRepairsNeededModal = function(bike) {
			if(!bike) {
				bike = $scope.reportedStationModel;
				$scope.tempRepairsNeeded = $scope.repairReasonsStation;
			} else {
				$scope.tempRepairsNeeded = $scope.repairReasonsBikes;
			}

			bike.repairsNotValid = false;

			$scope.selectedEntity = {
				original: bike,
				tempRepairsNeeded: bike.repairsNeeded ? angular.copy(bike.repairsNeeded) : [{}]
			}

			$('#repairsNeededModal').modal('show');
		}

		$scope.removeTempRepair = function(index) {
			$scope.selectedEntity.tempRepairsNeeded.splice(index, 1);
		}

		$scope.setRepair = function(val) {
			let repairNeeded = {};
			repairNeeded.id = val.id;
			repairNeeded.reason = val.reason;
			repairNeeded.forWhat = val.forWhat;
			$scope.selectedEntity.tempRepairsNeeded.push(repairNeeded);
		}

		$scope.setRepairsNeeded = function() {
			for (let i = 0; i < $scope.selectedEntity.tempRepairsNeeded.length; i++) {
				if(!$scope.selectedEntity.tempRepairsNeeded[i].id) {
					$scope.selectedEntity.tempRepairsNeeded.splice(i--, 1);
				}
			}

			$scope.selectedEntity.original.repairsNeeded = $scope.selectedEntity.tempRepairsNeeded;

			$scope.selectedEntity = undefined;
		}

		$scope.updateBikeOnChange = function(bike) {
			bike.invalidNumber = false;
		}


		$scope.requestComplete = function(){
			$('#complete').modal('show');
		}

		$scope.complete = function () {
			var validationError = false

			if(!$scope.reviewModel.bikesAtStation || $scope.reviewModel.bikesAtStation < 1) {
				validationError = true;
				$scope.reviewValidation.bikesAtStation = false;
			}

			for (const ib of $scope.inactiveBollards) {
				for (const ib2 of $scope.inactiveBollards) {
					if(ib !== ib2 && ib.bollardNo === ib2.bollardNo) {
						ib.invalidNo = true;
						ib2.invalidNo = true;

						validationError = true;
					} else if (!ib2.bollardNo || ib2.bollardNo < 1) {
						ib2.invalidNo = true;

						validationError = true;
					}
				}
			}

			if ($scope.reviewModel.reportStation && $scope.reportedStationModel.repairsNeeded.length < 1) {
				$scope.reportedStationModel.repairsNotValid = true;
				validationError = true;
			}

			for (const br of $scope.bikesReported) {
				if(br.repairsNeeded.length < 1) {
					br.repairsNotValid = true;
					validationError = true;
				}
			}


			if(validationError){
				$rootScope.errorNotificationCustom("Validation error");

				return;
			}
			//

			const bikeIds = [];

			for (const br of $scope.bikesReported) {
				bikeIds.push(br.bikeId);
			}

			$http.put(projectRoot + "/msc-api/repair-reports/bikes/" + $scope.bikeStation.scheme.name, bikeIds).then(function(resp){

				let hasInvalidNumbers = false;
				let bikesByNumber = {};

				for (const br of $scope.bikesReported) {
					let invalidNumber = true;

					for(const b of resp.data) {
						if(b.number === br.bikeId + '') {
							invalidNumber = false;
							bikesByNumber[b.number] = b;
							break;
						}
					}
					if(invalidNumber) {
						br.invalidNumber = true;
						hasInvalidNumbers = true;
					}

				}


				if(hasInvalidNumbers) {
					$rootScope.errorNotificationCustom("Validation error");

					return;
				}

				let reportsForSave = [];

				if($scope.reviewModel.reportStation) {
					reportsForSave.push({
						location: $scope.bikeStation,
						streetComments: $scope.reportedStationModel.comments,
						vandalism: $scope.reportedStationModel.vandalism,
						repairReason: $scope.reportedStationModel.repairsNeeded,
						routineReview: true,
					})
				}

				for (const br of $scope.bikesReported) {
					reportsForSave.push({
						location: $scope.bikeStation,
						bike: bikesByNumber[br.bikeId + ''],
						streetComments: br.comments,
						vandalism: br.vandalism,
						pendingCollection: br.pendingCollection,
						repairReason: br.repairsNeeded,
						routineReview: true,
					})
				}

				$scope.savingReports = true;

				let request = {
					review: {
						station: $scope.bikeStation,
						bikesAtStation: $scope.reviewModel.bikesAtStation,
						graffiti: $scope.reviewModel.graffiti,
						weeds: $scope.reviewModel.weeds,
						inactiveBollards: $scope.inactiveBollards
					},
					reports: reportsForSave
				}

				$http.post(projectRoot + "/msc-api/repair-reports/routine-review/with-reports", request).then(function (response) {

					//$rootScope.successNotification("Created reports");
					window.open(projectRoot + "/views/repair-reports", "_self");

				}, function (errorResponse) {
					$rootScope.errorNotification("Error on reports create");

					$scope.savingReports = false;
				});
			}, function (error) {

			});




		}

		$scope.returnTrue = function() {
			return true;
		}

		$scope.returnFalse = function() {
			return false;
		}
	}
]);
