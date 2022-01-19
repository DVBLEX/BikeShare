"use strict";

app.controller('repairHistoryCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	function($scope, $http, $timeout) {
		
		$scope.gridPageSizes = [20, 50, 100];
		$scope.gridPageSize = $scope.gridPageSizes[0];
		$scope.showGrid = false;
		$scope.cleanStringBikeNumber = false;
		$scope.cleanStringStation = false;
		$scope.showCleanBtnBikeNumber = false;
		$scope.showCleanBtnStation = false;
		$scope.newRequest = {};
		$scope.currentScheme = {
			name: "All"
		}

		const SIMPLE_DATE = 'YYYY-MM-DD';

		let initialDate = {startDate: moment().subtract(1, 'month'), endDate: moment()};
		let datePickerCommonOptions = {
			locale: {format: 'DD/MM/YYYY'},
			autoApply: true,
			showDropdowns: true
		}

		$scope.datePickerRepairHistory = {
			date: initialDate,
			options: datePickerCommonOptions
		}

		$scope.selectBikeNumber = function (bike) {
			$scope.newRequest.bikeNumber = bike.number;
			$scope.cleanStringBikeNumber = false;
			$scope.showCleanBtnBikeNumber = true;
		};

		$scope.clearBikeNumber = function () {
			if ($scope.newRequest.bikeNumber) {
				$scope.cleanStringBikeNumber = true;
				$scope.showCleanBtnBikeNumber = false;
				$scope.newRequest.bikeNumber = null;
			}
		}

		$scope.selectStation = function (station) {
			$scope.newRequest.location = station.id;
			$scope.cleanStringStation = false;
			$scope.showCleanBtnStation = true;
		};

		$scope.clearStation = function () {
			if ($scope.newRequest.location) {
				$scope.cleanStringStation = true;
				$scope.showCleanBtnStation = false;
				$scope.newRequest.location = null;
			}
		}

		$scope.selectRepairHistory = function () {

			if ($scope.datePickerRepairHistory.date.startDate === null || $scope.datePickerRepairHistory.date.endDate === null) {
				$scope.datePickerRepairHistory.date = initialDate;
			}

			$scope.newRequest.startDate = $scope.datePickerRepairHistory.date.startDate.format(SIMPLE_DATE);
			$scope.newRequest.endDate = $scope.datePickerRepairHistory.date.endDate.format(SIMPLE_DATE);

			$scope.showGrid = true;
			$http.get(projectRoot + "/msc-api/repair-reports/done", {
				params: {...$scope.newRequest}

			}).then(function(response){

				$scope.repairReports = response.data.filter(function (report) {
					return !!report.operators.length;
				});

				for(var i = 0; i < $scope.repairReports.length; i++){
					$scope.repairReports[i].repairReasonString = "";

					//stringToJobs($scope.repairReports[i]);

					if($scope.repairReports[i].streetComments){
						$scope.repairReports[i].streetComments = $scope.repairReports[i].onStreetOperator + ": " + $scope.repairReports[i].streetComments;
					} else {
						$scope.repairReports[i].streetComments = ""
					}
					if($scope.repairReports[i].depotComments){
						$scope.repairReports[i].depotComments = $scope.repairReports[i].onDepotOperator + ": " + $scope.repairReports[i].depotComments;
					} else {
						$scope.repairReports[i].depotComments = ""
					}

					$scope.repairReports[i].repairsComplete = "";
					if ($scope.repairReports[i].operators.length > 0) {
						$scope.repairReports[i].operator = $scope.repairReports[i].operators[0].userName;
						for (var operatorNum = 0; operatorNum < $scope.repairReports[i].operators.length; operatorNum++) {
							if ($scope.repairReports[i].operators[operatorNum].jobsDone !== "") {
								$scope.repairReports[i].repairsComplete += $scope.repairReports[i].operators[operatorNum].jobsDone + "; ";
							}
						}
					}

					for (var j = 0; j < $scope.repairReports[i].repairReason.length; j++) {
						$scope.repairReports[i].repairReasonString += $scope.repairReports[i].repairReason[j].reason + "; ";

					}

					sparePartsToString($scope.repairReports[i]);
				}
				$scope.setCurrentScheme($scope.currentScheme);

			}, function(errorResponse){
				console.error('error: ', errorResponse)
			});
		};

		$scope.setCurrentScheme = function(scheme){
			$scope.currentScheme = scheme;

			let filteredRepairHistory = $scope.repairReports.filter( function (report) {
				if ($scope.currentScheme.name === "All") {
					return report;
				} else {
					return report.location.scheme.name === $scope.currentScheme.name;
				}
			}  )

			$scope.reportsGridOptions.api.setRowData(filteredRepairHistory);

		}

		var stringToJobs = function(report){
			report.repairJobs = [];
			
			if(report.jobsDone && report.jobsDone.length > 0){
				var splitString = report.jobsDone.split(", ");
				
				for (var i = 0; i < $scope.repairJobs.length; i++) {
					if(splitString.includes($scope.repairJobs[i].job)){
						report.repairJobs.push($scope.repairJobs[i]);
					}
				}
			}
		}
		
		var loadData = function(){

			$http.get(projectRoot + "/msc-api/top-data/schemes").then(function(response) {
				$scope.schemes = response.data.filter(function (scheme) {
					return scheme.name !== 'Dublin';
				});
				$scope.schemes.splice(0, 0, {name: "All"});

			}, function (errorResponse) {

			});

			// get bike and station
			$http.get(projectRoot + "/msc-api/repair-reports/bike-stations").then(function (response) {
				$scope.bikeStations = response.data;

				for (const station of $scope.bikeStations) {
					station.name = station.id + ' ' + station.location;
				}

			}, function (errorResponse) {

			});

			$http.get(projectRoot + "/msc-api/repair-reports/bikes").then(function (response) {
				$scope.bikes = response.data;

			}, function (errorResponse) {

			});
		}
		
		loadData();
		
		var sparePartsToString = function(report){
			report.sparepartsUsed = "";
			
			for(let j = 0; j < report.operators.length; j++){
				for (let i = 0; i < report.operators[j].usedSpareparts.length; i++) {
					report.sparepartsUsed += report.operators[j].usedSpareparts[i].productType.fullName + " - " + report.operators[j].usedSpareparts[i].amount + "; ";
				}
			}
			
			if(report.sparepartsUsed.length > 0){
				report.sparepartsUsed = report.sparepartsUsed.slice(0, -2);
			}
		}

		//--grid-related-stuff--

		var columnDefs = [
			{headerName: "Report Date", field: "reportDate", filter: "agTextColumnFilter", width: 140},
			{headerName: "Operator", field: "operator", filter: "agTextColumnFilter", width: 180},
			{headerName: "Repairs Needed", field: "repairReasonString", filter: "agTextColumnFilter", cellStyle: {'white-space': 'normal'}, autoHeight: true, width: 120},
			{headerName: "Parts Fit", field: "sparepartsUsed", filter: "agTextColumnFilter", cellStyle: {'white-space': 'normal'}, autoHeight: true, width: 120},
			{headerName: "Repairs Complete", field: "repairsComplete", filter: "agTextColumnFilter", cellStyle: {'white-space': 'normal'}, autoHeight: true, width: 180},
			{headerName: "Station", field: "location.location", filter: "agTextColumnFilter", cellStyle: {'white-space': 'normal'}, autoHeight: true, width: 120},
			{headerName: "Bike", field: "bike.number", filter: "agTextColumnFilter", cellStyle: {'white-space': 'normal'}, autoHeight: true, width: 120},
		];

		$scope.reportsGridOptions = {
			defaultColDef: {
			    resizable: true,
			    suppressMovable: true
			},
		    columnDefs: columnDefs,
		    rowData: null,
		    pagination: true,
		    paginationPageSize: 25,
		    floatingFilter: true,
		    angularCompileRows: true,
		    domLayout: 'autoHeight',
		    
		    onGridReady: function(params) {
		        if(window.innerWidth > 1000){
		        	params.api.sizeColumnsToFit();
		        }
		    }
		    
		};
		
		$scope.setGridPageSize = function(pageSize){
			$scope.reportsGridOptions.api.paginationSetPageSize(pageSize)
		}

		//--grid-related-stuff--end--

		$scope.exportRepairHistoryToCSV = function () {
			var downloadLink = document.createElement("a");
			var blob = new Blob(["\ufeff", $scope.reportsGridOptions.api.getDataAsCsv()]);
			downloadLink.href = URL.createObjectURL(blob);
			downloadLink.download = "Repair History.csv";
			document.body.appendChild(downloadLink);
			$timeout(function() {
				downloadLink.click();
			}, 1);
			document.body.removeChild(downloadLink);
		}
	}
	]
);
