"use strict";

app.controller('createRepairReportCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	function($scope, $http, $timeout, $rootScope) {	

		$scope.repairSelectApis = [];
		
		let loadData = function(){
			$http.get(projectRoot + "/msc-api/user/username").then(function(response){
				$scope.userEmail = response.data;
				
				refreshNewReport();
			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/user/user-roles").then(function(response){
				$scope.userRoles = response.data;
				
				if($scope.userRoles[0] === 1){
					$scope.userIsAdmin = true;
				} else if($scope.userRoles[2] === 1){
					$scope.userIsSchemeLeader = true;
				}
				
				//if($scope.isUserOnlySchemeLeader()){
					$http.get(projectRoot + "/msc-api/user/user-scheme").then(function(response){
						$scope.schemeOfUser = response.data;
						
						$scope.currentScheme = $scope.schemeOfUser;
						
						
						$http.get(projectRoot + "/msc-api/repair-reports/bike-stations/" + $scope.schemeOfUser.name).then(function(response){
							$scope.bikeStations = response.data;

							for (const station of $scope.bikeStations) {
								station.name = station.id + ' ' + station.location;
							}
							
						}, function(errorResponse){
							
						});
						
						$http.get(projectRoot + "/msc-api/repair-reports/bikes/" + $scope.schemeOfUser.name).then(function(response){
							$scope.bikes = response.data;
							
							
						}, function(errorResponse){
							
						});
						
						$http.get(projectRoot + "/msc-api/repair-reports/repair-reasons/station").then(function(response){
							$scope.stationRepairReasons = response.data;
							
							$scope.repairReasonsToChoose = [];
						}, function(errorResponse){
							
						});
						
						$http.get(projectRoot + "/msc-api/repair-reports/repair-reasons/bike").then(function(response){
							$scope.bikeRepairReasons = response.data;
							
							
						}, function(errorResponse){
							
						});
						
					}, function(errorResponse){
						
					});
				//}
			}, function(errorResponse){
				
			});
		};

		loadData();

		$scope.locationSelectApi = {};
		$scope.bikeSelectApi = {};
		
		var refreshNewReport = function(){
			$scope.newReport = {
				onStreetOperator: $scope.userEmail
			};
			
			safeFuncCall($scope.bikeSelectApi.refresh);
			safeFuncCall($scope.locationSelectApi.refresh)
		};

		$scope.onLocationChange = function() {
			$scope.validateLocation();
		};

		$scope.onBikeNumberChange = function(){
			$scope.validateBike();

			//to not loose a reference to the array
			$scope.repairReasonsToChoose.length = 0;

			if($scope.newReport.stationItself){
				$scope.repairReasonsToChoose.push(...$scope.stationRepairReasons);
			} else {
				$scope.repairReasonsToChoose.push(...$scope.bikeRepairReasons);
			}
			
			$scope.newReport.repairReason = [];
		};
		
		$scope.isStationReport = function(){
			return $scope.newReport.stationItself;
		};
		
		$scope.setBike = function(bike){
			$scope.newReport.bike = bike;
			
			$scope.onBikeNumberChange();
		};

		$scope.setLocation = function(location) {
			$scope.newReport.location = location;
		};

		$scope.setRepair = function (val) {
			let repair = {};
			repair.id = val.id;
			repair.reason = val.reason;
			repair.forWhat = val.forWhat;
			repair.invalid = false;
			$scope.newReport.repairReason.push(repair);
			$scope.newReport.noRepairReason = false;
		};

		$scope.removeRepair = function(index) {
			$scope.newReport.repairReason.splice(index, 1);
			$scope.repairSelectApis.splice(index, 1);
		};

		$scope.validateLocation = function(){
			$scope.newReport.noLocation = !$scope.newReport.location;
		};
		
		$scope.validateBike = function(){
			$scope.newReport.noBike = !$scope.newReport.bike && !$scope.newReport.stationItself;
		};
		
		$scope.isBikeGood = function(){
			return !$scope.newReport.noBike;
		};
		
		$scope.validateReportReason = function(){
			$scope.newReport.noReportReason = false;
		};
		
		$scope.validateRepairReason = function(){
			$scope.newReport.noRepairReason = !$scope.newReport.repairReason || $scope.newReport.repairReason.length === 0;

			for(let rr of $scope.newReport.repairReason) {
				if(!rr.id) {
					rr.invalid = true;
					$scope.newReport.noRepairReason = true;
				}
			}
		};
		
		$scope.validate = function(){
			$scope.validateLocation();
			$scope.validateBike();
			$scope.validateReportReason();
			$scope.validateRepairReason();
			
			return !$scope.newReport.noLocation && !$scope.newReport.noBike && !$scope.newReport.noReportReason && !$scope.newReport.noRepairReason;
		};
		
		$scope.dropValidation = function(){
			$scope.newReport.noLocation = false;
			$scope.newReport.noBike = false;
			$scope.newReport.noReportReason = false;
			$scope.newReport.noRepairReason = false;
		};

		$scope.saveReport = function(repairAfter){
			if(!$scope.validate()){
				return;
			}
			if($scope.newReport.stationItself) {
				$scope.newReport.bike = null;
				$scope.newReport.pendingCollection = false;
			}

			$scope.savingReport = true;

			// onStreetRepair flag must be in "checked" mode by pressing "save&repair" button on report creation
			if(repairAfter){
				$scope.newReport.onStreetRepair = true;
			}
			
			$http.post(projectRoot + "/msc-api/repair-reports/create-form", $scope.newReport).then(function(response){
				$scope.dropValidation();
				refreshNewReport();
				$rootScope.successNotification("Reported");
				$scope.savingReport = false;

				if(repairAfter){
					$scope.reportToEdit = response.data;
					window.open(projectRoot + "/views/repair-reports", "_self");
					localStorage.setItem("editReport", response.data.id);
				}
			}, function(){
				$rootScope.errorNotification("Error on report create");
				$scope.savingReport = false;
			});
			
		};

		$scope.saveReportAndComplete = function(){
			if(!$scope.validate()){
				return;
			}
			if($scope.newReport.stationItself) {
				$scope.newReport.bike = null;
				$scope.newReport.pendingCollection = false;
			}
			$scope.savingReport = true;
			$scope.newReport.onStreetRepair = true;

			$http.post(projectRoot + "/msc-api/repair-reports/create-completed-report", $scope.newReport).then(function(response){
				$scope.dropValidation();
				refreshNewReport();
				$rootScope.successNotification("Reported");
				$scope.savingReport = false;
			}, function(){
				$rootScope.errorNotification("Error on report create");
				$scope.savingReport = false;
			});

		};

		$scope.returnTrue = function() {
			return true;
		};

		$scope.returnFalse = function() {
			return false;
		}
	}
]);
