"use strict";

app.controller('repairReportsCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	function($scope, $http, $timeout, $rootScope) {

		$scope.STATE_NEW = 41;
		$scope.STATE_IN_PROGRESS = 42;
		$scope.STATE_DONE = 43;
		$scope.STATE_PENDING = 44;

		$scope.gridPageSizes = [20, 50, 100];
		$scope.gridPageSize = $scope.gridPageSizes[0];

		$scope.routineGridPageSizes = [20, 50, 100];
		$scope.routineGridPageSize = $scope.routineGridPageSizes[0];
		$scope.scanner = null;
		$scope.showQRVideo = true;
		$scope.showQRSparePart = false;
		$scope.scannedSparePart = null;

		$scope.operatorSelectApi = {};
		$scope.sparepartSelectApis = [{}];

		$scope.reviewFilters = {
			dateAsc: false,
			sortByScheme: false,
			withBollards: false,
			withGraffiti: false,
			withWeeds: false,
			withBikes: false,
			withStation: false,
			filterByBollards: false,
			filterByGraffiti: false,
			filterByWeeds: false,
			filterByBikes: false,
			filterByStation: false,
		};

		$scope.reviewFiltersWatcherInit = false;

		$scope.$watch('reviewFilters', function(){
			if($scope.reviewFiltersWatcherInit){
				$scope.loadRoutineReviewsInfoAndData();
			}

			$scope.reviewFiltersWatcherInit = true;
		}, true);

		const PAGES_TO_SHOW = 3;

		$scope.setOperator = function(operator) {
			$scope.selectedOperator.tempValue = operator;
		};

		let updateRepairLists = function(){
			$scope.bikesToRepair = [];
			$scope.bikesInRepair = [];

			$scope.stationsToRepair = [];
			$scope.stationsInRepair = [];

			for(let i = 0; i < $scope.repairReports.length; i++){
				if($scope.repairReports[i].state.id === $scope.STATE_NEW || $scope.repairReports[i].state.id === $scope.STATE_PENDING){
					$scope.isStationRepaired($scope.repairReports[i], $scope.bikesToRepair, $scope.stationsToRepair);
				} else if($scope.repairReports[i].state.id === $scope.STATE_IN_PROGRESS){
					$scope.isStationRepaired($scope.repairReports[i], $scope.bikesInRepair, $scope.stationsInRepair);
				}
			}
		};

		$scope.isStationRepaired = function(repairReport, bikes, stations) {
			if(repairReport.stationItself){
				stations.push(repairReport)
			} else {
				bikes.push(repairReport)
			}
		};

		let loadAssetCurrentValues_SpareParts = function(){
			$http.get(projectRoot + "/msc-api/assets/assets-current-values/by-scheme/" + $scope.schemeOfUser.name).then(function(response){
				$scope.acvs = response.data;
			}, function(errorResponse){

			});
		};

		let sortSparePartsForGroup = function(group){
			$scope.spareParts = [];

			for (let i = 0; i < $scope.acvs.length; i++) {

				if($scope.acvs[i].productType.assetGroup === group){
					$scope.acvs[i].showName = $scope.acvs[i].productType.fullName;
					$scope.spareParts.push($scope.acvs[i]);
				}

			}
		};

		$scope.isUserOnlySchemeLeader = function(){
			return $scope.userIsOnlySchemeLeader;
			//return true; //for testing
		};

		$scope.loadData = function () {
			$http.get(projectRoot + "/msc-api/user/user-roles").then(function (response) {
				$scope.userRoles = response.data;

				if ($scope.userRoles[0] === 1) {
					$scope.userIsAdmin = true;
				} else if ($scope.userRoles[2] === 1) {
					$scope.userIsSchemeLeader = true;
					$scope.userIsOnlySchemeLeader = true;
				}
				for (let i = 0; i < $scope.userRoles.length; i++) {
					if (i !== 2 && $scope.userRoles[i] === 1) {
						$scope.userIsOnlySchemeLeader = false;
					}
				}

				//if($scope.isUserOnlySchemeLeader()){
				if ($scope.userIsAdmin) {
					$http.get(projectRoot + "/msc-api/top-data/schemes").then(function (response) {
						$scope.allSchemes = response.data;
					});
				}

				$http.get(projectRoot + "/msc-api/user/user-scheme").then(function (response) {
					$scope.schemeOfUser = response.data;

					$scope.loadReportsData();
				});
			});
		};

		let getAllUsers = function (schemeName) {
			$http.get(projectRoot + "/msc-api/user/" + schemeName).then(function (response) {

				$scope.operators = [];
				for (let i = 0; i < response.data.length; i++) {
					for (let j = 0; j < response.data[i].userRole.length; j += 2) {//searching only for operators
						if (j === 2 && response.data[i].userRole.charAt(j) === '1') {
							let operator = response.data[i];
							operator.fullName = operator.firstName + ' ' + operator.lastName;
							$scope.operators.push(operator);
							break;
						}
					}
				}
			}, function (errorResponse) {
			});
		};

		let getRepairReasonString = function(index) {
			for (let j = 0; j < $scope.repairReports[index].repairReason.length; j++) {
				$scope.repairReports[index].repairReasonString += $scope.repairReports[index].repairReason[j].reason + "; ";
			}
		};

		let buildHistory = function(date, name, timeOfWork) {
			return date + " " + name + " " + msToTime(timeOfWork) + "\n";
		};

		let getSchemeRepairReports = function (schemeName) {
			$http.get(projectRoot + "/msc-api/repair-reports/" + schemeName).then(function(response){
				$scope.repairReports = response.data;

				let editReportId = localStorage.getItem("editReport");
				localStorage.removeItem("editReport");

				for(let i = 0; i < $scope.repairReports.length; i++){
					$scope.repairReports[i].repairReasonString = "";
					$scope.repairReports[i].timeSpent = "";
					stringToJobs($scope.repairReports[i]); //fills parameter $scope.repairReports[i].operators[j].repairJobs with $scope.repairJobs as obj, not as str
					getRepairReasonString(i);
					if ($scope.repairReports[i].operators !== null && $scope.repairReports[i].operators !== undefined) {
						$scope.repairReports[i].operators.sort(function(a, b){
							return a.id - b.id;
						});
					}

					if($scope.repairReports[i].operators){
						for (let j = 0; j < $scope.repairReports[i].operators.length; j++) {
							$scope.repairReports[i].timeSpent += buildHistory(
								$scope.repairReports[i].operators[j].jobDoneDatetime,
								$scope.repairReports[i].operators[j].userName,
								$scope.repairReports[i].operators[j].timeOfWorkMilis
							);
						}
					}

					if(editReportId && Number(editReportId) === $scope.repairReports[i].id){
						$scope.editReport($scope.repairReports[i]);
						editReportId = undefined;
					}
				}
				updateRepairLists();
				$scope.reportsGridOptions.api.setRowData($scope.repairReports);
				$scope.loadRoutineReviewsInfoAndData();

			}, function(errorResponse){

			});
		};

		let sortRepairJobsByStationsAndBykes = function() {
			$scope.stationRepairJobs = [];
			$scope.bikeRepairJobs = [];

			for (let i = 0; i < $scope.repairJobs.length; i++) {
				if($scope.repairJobs[i].forWhat === 1){
					$scope.stationRepairJobs.push($scope.repairJobs[i]);
				} else if($scope.repairJobs[i].forWhat === 2){
					$scope.bikeRepairJobs.push($scope.repairJobs[i]);
				}
			}
		};

		$scope.loadReportsData = function() {

			getAllUsers($scope.schemeOfUser.name); //build $scope.operators
			loadAssetCurrentValues_SpareParts();

			$http.get(projectRoot + "/msc-api/repair-reports/repair-jobs").then(function(response){
				$scope.repairJobs = response.data;
				sortRepairJobsByStationsAndBykes();
				getSchemeRepairReports($scope.schemeOfUser.name);
			})
		};

		$scope.loadRoutineReviewsInfoAndData = function() {
			let request = {
				dateAsc: $scope.reviewFilters.dateAsc,
				sortByScheme: $scope.reviewFilters.sortByScheme,
				withBollards: $scope.reviewFilters.withBollards,
				withGraffiti: $scope.reviewFilters.withGraffiti,
				withWeeds: $scope.reviewFilters.withWeeds,
				withBikes: $scope.reviewFilters.withBikes,
				withStation: $scope.reviewFilters.withStation,
				filterByBollards: $scope.reviewFilters.filterByBollards,
				filterByGraffiti: $scope.reviewFilters.filterByGraffiti,
				filterByWeeds: $scope.reviewFilters.filterByWeeds,
				filterByBikes: $scope.reviewFilters.filterByBikes,
				filterByStation: $scope.reviewFilters.filterByStation,
			};

			$http.put(projectRoot + "/msc-api/repair-reports/routine-review/" + $scope.schemeOfUser.name + "/count", request).then(function(response){
				$scope.loadRoutineReviews(request, response.data);
			});
		};

		$scope.pagesParameters = {
			currentPage: 1,
			pagesToShow: PAGES_TO_SHOW
		};

		$scope.loadRoutineReviews = function(nestedRequest, count) {
			$scope.pagesParameters.pagesNumber = Math.ceil(count / $scope.routineGridPageSize);

			if(typeof $scope.watchPageCancel === 'function') {
				$scope.watchPageCancel();
			}

			$scope.watchPageCancel = $scope.$watch("pagesParameters.currentPage", function(newVal, oldVal) {
				if(newVal !== oldVal) {
					$scope.loadRoutineReviewsInfoAndData();
				}
			});

			nestedRequest.page = $scope.pagesParameters.currentPage;
			nestedRequest.pageSize = $scope.routineGridPageSize;

			$http.put(projectRoot + "/msc-api/repair-reports/routine-review/" + $scope.schemeOfUser.name, nestedRequest).then(function(response){
				$scope.routineReviews = response.data;

				for (let i = 0; i < $scope.routineReviews.length; i++) {
					$scope.routineReviews[i].bikesString = "";

					// for (let j = 0; j < $scope.routineReviews[i].bikes.length; j++) {
					// 	$scope.routineReviews[i].bikesString += $scope.routineReviews[i].bikes[j].number + ", "
					// }
					//
					// $scope.routineReviews[i].bikesString = $scope.routineReviews[i].bikesString.slice(0, -2);

					if($scope.routineReviews[i].reports){
						$scope.routineReviews[i].myReports = [];
						if($scope.routineReviews[i].reports.indexOf("; ") > -1){
							let parts = $scope.routineReviews[i].reports.split("; ");
							$scope.routineReviews[i].sReportId = Number(parts[0].slice(3));
							if(parts[1]){
								let split = parts[1].slice(3).split(",");
								$scope.routineReviews[i].bReportsIds = [];

								for (let z = 0; z < split.length; z++) {
									const id = Number(split[z]);
									$scope.routineReviews[i].bReportsIds.push(id);
									$scope.routineReviews[i].myReports.push($scope.getReportById(id));
								}
							}
						} else {//in this case we assume, that reports where created only for bikes
							//$scope.routineReviews[i].bReportsIds = [];
							let split = $scope.routineReviews[i].reports.slice(3).split(",");
							$scope.routineReviews[i].bReportsIds = [];

							for (let z = 0; z < split.length; z++) {
								const id = Number(split[z]);
								$scope.routineReviews[i].bReportsIds.push(id);
								$scope.routineReviews[i].myReports.push($scope.getReportById(id));
							}
						}
					}
				}
			});
		};

		$scope.loadData();

		$scope.getReportById = function(id) {
			for (let i = 0; i < $scope.repairReports.length; i++) {
				if ($scope.repairReports[i].id === id) {
					return $scope.repairReports[i];
				}
			}
		};

		$scope.needToShowReviewAccordion = function() {
			return true;
		};

		$scope.showBikesToRepair = function(){
			$scope.reportsGridOptions.api.setRowData($scope.bikesToRepair);
		};

		$scope.showStationsToRepair = function(){
			$scope.reportsGridOptions.api.setRowData($scope.stationsToRepair);
		};

		$scope.showBikesInRepair = function(){
			$scope.reportsGridOptions.api.setRowData($scope.bikesInRepair);
		};

		$scope.showStationsInRepair = function(){
			$scope.reportsGridOptions.api.setRowData($scope.stationsInRepair);
		};

		$scope.showAll = function(){
			$scope.reportsGridOptions.api.setRowData($scope.repairReports);
		};

		$scope.getReviewHeaderMiddleText = function(review) {
			let text = "";
			if(review.graffiti) {
				text += "Graffiti! ";
			}
			if(review.weeds) {
				text += "Weeds/Debris! ";
			}
			if(review.inactiveBollards && review.inactiveBollards.length > 0) {
				text += "Bollards! ";
			}

			return text;
		};

		//--grid-related-stuff--

		let editCellRendererFunc = function(params){
			params.$scope.editable = params.node.data;

			return '<button type="button" class="btn btn-info btn-sm btn-block" style="height:25px; padding: 3px 8px;" data-ng-click="editReport(editable)"><i class="material-icons md-18">edit</i></button>';
		};

		let stateCellRendererFunc = function(params){
			if(params.node.data.state.id === $scope.STATE_IN_PROGRESS){
				params.$scope.textClass = "text-info";
			} else if(params.node.data.state.id === $scope.STATE_DONE){
				params.$scope.textClass = "text-danger";
			} else {
				params.$scope.textClass = "text-success";
			}

			return '<div class="' + params.$scope.textClass + '">' + params.node.data.state.name + '</div>';
		};

		let columnDefs = [
			{headerName: "Report Date", field: "reportDate", filter: "agTextColumnFilter", width: 120},
			{headerName: "Status", field: "state.name", filter: "agTextColumnFilter", cellRenderer: stateCellRendererFunc, width: 80},
			{headerName: "Station", field: "location.location", filter: "agTextColumnFilter", width: 140},
			{headerName: "Repairs Needed", field: "repairReasonString", filter: "agTextColumnFilter", width: 220},
			{headerName: "Time Spent", field: "timeSpent", filter: "agTextColumnFilter", cellStyle: {'white-space': 'pre-wrap'}, autoHeight: true, width: 250},

			{headerName: "Edit", field: "", width: 70, cellRenderer: editCellRendererFunc, pinned: 'right', suppressSizeToFit: true},
		];

		$scope.reportsGridOptions = {
			defaultColDef: {
				resizable: true,
				suppressMovable: true
			},
			columnDefs: columnDefs,
			rowData: null,
			pagination: true,
			paginationPageSize: $scope.gridPageSize,
			floatingFilter: true,
			angularCompileRows: true,
			domLayout: 'autoHeight',

			onGridReady: function(params) {
				if(window.innerWidth > 1000){
					params.api.sizeColumnsToFit();
				}
			},
		};

		$scope.setGridPageSize = function(pageSize){
			$scope.reportsGridOptions.api.paginationSetPageSize(pageSize)
		};
		//--grid-related-stuff--end--

		//--reviews-grid-related-stuff--

		let reportsCellRendererFunc = function(params){

			if(params.data.reportsHtml){
				return params.data.reportsHtml;
			}

			let element = "<div>";

			if(params.data.sReportId){
				element += "S: <a style='color: blue' href='#' data-ng-click='editReportById(" + params.data.sReportId + ")'>" + params.data.sReportId + "</a>; "
			}
			if(params.data.bReportsIds && params.data.bReportsIds.length > 0){
				element += "B: ";

				for (let i = 0; i < params.data.bReportsIds.length; i++) {
					element += "<a style='color: blue' href='#' data-ng-click='editReportById(" + params.data.bReportsIds[i] + ")'>" + params.data.bReportsIds[i] + "</a>, "
				}

				element = element.slice(0, -2);
			}
			element += "</div>";

			params.data.reportsHtml = element;

			return element;
		};

		let reviewsColumnDefs = [
			{headerName: "Review Date", field: "creationDate", filter: "agTextColumnFilter", width: 120},
			{headerName: "Station", field: "station.location", filter: "agTextColumnFilter", width: 170},
			{headerName: "Scheme", field: "station.scheme.name", filter: "agTextColumnFilter", width: 100},
			{headerName: "Operator", field: "operator", filter: "agTextColumnFilter", width: 100},
			{headerName: "Bikes", field: "bikesString", filter: "agTextColumnFilter", width: 220},
			{headerName: "Reports", field: "reports", filter: "agTextColumnFilter", cellRenderer: reportsCellRendererFunc, width: 220},
		];

		$scope.reviewsGridOptions = {
			defaultColDef: {
				resizable: true,
				suppressMovable: true
			},
			columnDefs: reviewsColumnDefs,
			rowData: null,
			pagination: true,
			paginationPageSize: $scope.routineGridPageSize,
			floatingFilter: true,
			angularCompileRows: true,
			domLayout: 'autoHeight',

			onGridReady: function(params) {
				if(window.innerWidth > 1000){
					params.api.sizeColumnsToFit();
				}
			},
		};

		$scope.setReviewsGridPageSize = function(pageSize){
			$scope.reviewsGridOptions.api.paginationSetPageSize(pageSize)
		};
		//--reviews-grid-related-stuff--

		$scope.editReportById = function(id){
			for (let i = 0; i < $scope.repairReports.length; i++) {
				if($scope.repairReports[i].id === id){
					$scope.editReport($scope.repairReports[i]);

					break;
				}
			}
		};

		$scope.updateOriginalEditable = function(){
			$scope.originalEditable = angular.copy($scope.reportToEdit);
		};

		let fillRepairJobsToChoose = function(stationItself) {
			if(stationItself){
				$scope.repairJobsToChoose = $scope.stationRepairJobs;
			} else {
				$scope.repairJobsToChoose = $scope.bikeRepairJobs;
			}
		};

		let buildCreatorString = function(date, operator, reportCreatorJob) {
			return date.toString() + " | " + operator + " | " + reportCreatorJob;
		};

		let performRepairHistoryBuild = function(reports) {
			$scope.reportToEdit.repairHistory = [];
			for (let i = 0; i < reports.length; i++) {
				if (reports[i].id !== $scope.reportToEdit.id) {
					continue; //do not show previously completed reports and first string of another NEW or IN_PROGRESS report for same bike or station
				}
				let creatorString;
				let reportCreatorJob = "Report has been created;";
				if (reports[i].onStreetOperator) {
					creatorString = buildCreatorString(reports[i].reportDate, reports[i].onStreetOperator, reportCreatorJob);
				}

				$scope.reportToEdit.repairHistory.push(creatorString);

				if (reports[i].operators && reports[i].operators.length) {
					for (let j = 0; j < reports[i].operators.length; j++) {
						creatorString = buildCreatorString (reports[i].operators[j].jobDoneDatetime,
							reports[i].operators[j].userName,
							reports[i].operators[j].jobsDone);
						$scope.reportToEdit.repairHistory.push(creatorString);
					}
				}
			}
		};

		$scope.editReport = function(editable){
			$scope.reportToEdit = angular.copy(editable);

			fillRepairJobsToChoose(editable.stationItself); //fills $scope.repairJobsToChoose with desired jobs

			$http.put(projectRoot + "/msc-api/repair-reports/repair-history", {
				schemeName: editable.location.scheme.name,
				bikeNumber: editable.bike ? editable.bike.number : "Station",
				location: editable.location.location}
			).then(function(response){
				performRepairHistoryBuild(response.data);
				$scope.updateOriginalEditable();
			}, function(errorResponse){
			});

			if(editable.state.id === $scope.STATE_DONE){
				sparePartsToString();
			}

			sortSparePartsForGroup($scope.reportToEdit.stationItself ? "Station" : "Bike");
			arrayToSpareParts();
		};

		// function buildRepairHistoryString(jobsDone, operator, repairDate, workPlace) {
		// 	if (!operator) operator = "";
		// 	if (!repairDate) repairDate = "";
		// 	return repairDate + " | " + operator + "(" + workPlace + ") | " + jobsDone;
		// 	// repairReason + " | " +
		// }

		function ignoreHashKey(key,value) {
			if (key==="$$hashKey") return undefined;
			else return value;
		}

		$scope.backToReports = function(){
//			$scope.reportToEdit.jobStarted = false;
//			$scope.reportToEdit = undefined;

			if($scope.originalEditable && JSON.stringify($scope.reportToEdit, ignoreHashKey) !== JSON.stringify($scope.originalEditable, ignoreHashKey)){//checking, if changes where made to the object
				if(!confirm("You may lose changed data in the report")){
					return;
				}
			}
			window.open(projectRoot + "/views/repair-reports", "_self");
		};

		let jobsToStringInSelectedOperator = function(){
			$scope.selectedOperator.value.jobsDone = "";

			if(!$scope.selectedOperator.repairJobs){
				return;
			}

			for (let i = 0; i < $scope.selectedOperator.repairJobs.length; i++) {
				$scope.selectedOperator.value.jobsDone += $scope.selectedOperator.repairJobs[i].job + ", ";
			}
			if($scope.selectedOperator.value.jobsDone.length > 0){
				$scope.selectedOperator.value.jobsDone = $scope.selectedOperator.value.jobsDone.slice(0, -2);//removing the last comma
			}
		};

		let stringToJobs = function (report) {
			if (report.operators !== null && report.operators !== undefined) {
				for (let j = 0; j < report.operators.length; j++) {
					report.operators[j].repairJobs = [];

					if (report.operators[j].jobsDone && report.operators[j].jobsDone.length > 0) {
						let splitString = report.operators[j].jobsDone.split(", ");

						for (let i = 0; i < $scope.repairJobs.length; i++) {
							if (splitString.includes($scope.repairJobs[i].job)) {
								report.operators[j].repairJobs.push($scope.repairJobs[i]);
							}
						}
					}
				}
			}
		};

		$scope.openUseSparePartsModal = function(){

			if(!$scope.selectedOperator.sparePartsToUse){
				$scope.selectedOperator.tempSparePartsToUse = [
					{amount: 1}
				];
			} else {
				$scope.selectedOperator.tempSparePartsToUse = $scope.selectedOperator.sparePartsToUse;
			}

			$('#useSparePartsModal').modal('show');
		};

		$scope.openAddQRModal = function(){
			$('#addQRModal').modal('show');
			document.getElementById("qrVideo").style.display = "block";
			$scope.showQRSparePart = false;
			initializeQRScanner();
		};

		let initializeQRScanner = function() {
			$scope.showQRVideo = true;
			if ($scope.scanner == null) {
				$scope.scanner = new Instascan.Scanner({ video: document.getElementById('preview'), scanPeriod: 5 });
				$scope.scanner.addListener('scan', scanQR);
				startCameras();
			} else {
				startCameras();
			}
		};

		let startCameras = function() {
			Instascan.Camera.getCameras().then(function (cameras) {
				if (cameras.length > 0) {
					$scope.scanner.start(cameras[cameras.length-1]);
				} else {
					console.error('No cameras found.');
				}
			}).catch(function (e) {
				console.error(e);
			});
		};

		let scanQR = function(sparePartId) {
			$rootScope.$apply(function(){
				$scope.scannedSparePart = $scope.spareParts.find(sp => sp.productType.id === sparePartId);
				if ($scope.scannedSparePart != null) {
					$scope.showQRSparePart = true;
					$scope.scanner.stop();
					document.getElementById("qrVideo").style.display = "none";
				}
			});
		};

		$scope.addQRSparePartToReport = function(){
			if ($scope.scannedSparePart != null) {
				if (typeof $scope.selectedOperator.sparePartsToUse === 'undefined') {
					$scope.selectedOperator.sparePartsToUse = [];
				}
				$scope.selectedOperator.sparePartsToUse.push({
					amount: $scope.scannedSparePart.amount,
					sparePart: $scope.scannedSparePart
				});
				$scope.validationError = false;
				$scope.useSpareParts();
				$scope.scannedSparePart = null;
			}
		};

		let validateSparePartsInReport = function(){
			$scope.validationError = false;

			for (let i = 0; i < $scope.selectedOperator.tempSparePartsToUse.length; i++) {
				$scope.selectedOperator.tempSparePartsToUse[i].noPart = !$scope.selectedOperator.tempSparePartsToUse[i].sparePart;
				$scope.selectedOperator.tempSparePartsToUse[i].noAmount = !$scope.selectedOperator.tempSparePartsToUse[i].amount;

				if($scope.selectedOperator.tempSparePartsToUse[i].noPart || $scope.selectedOperator.tempSparePartsToUse[i].noAmount){
					$scope.validationError = true;
					$scope.selectedOperator.tempSparePartsToUse[i].invalid = true;
				}
			}
		};

		let sparePartsToArray = function(){
			$scope.selectedOperator.value.usedSpareparts = [];

			if($scope.selectedOperator.sparePartsToUse){
				for (let i = 0; i < $scope.selectedOperator.sparePartsToUse.length; i++) {
					if(!$scope.selectedOperator.sparePartsToUse[i].sparePart){
						continue;
					}
					for (let j = 0; j < $scope.selectedOperator.sparePartsToUse.length; j++) {//we need to sum up duplicates
						if($scope.selectedOperator.sparePartsToUse[i] !== $scope.selectedOperator.sparePartsToUse[j]
							&& $scope.selectedOperator.sparePartsToUse[i].sparePart.productType.id === $scope.selectedOperator.sparePartsToUse[j].sparePart.productType.id){
							$scope.selectedOperator.sparePartsToUse[i].amount += $scope.selectedOperator.sparePartsToUse[j].amount;
							$scope.selectedOperator.sparePartsToUse.splice(j--, 1);
						}
					}

					$scope.selectedOperator.value.usedSpareparts.push({repairReportId: $scope.selectedOperator.id, productType:$scope.selectedOperator.sparePartsToUse[i].sparePart.productType, amount:  $scope.selectedOperator.sparePartsToUse[i].amount});
				}
			}
			if(!$scope.reportToEdit.operators){
				$scope.reportToEdit.operators = [];
			}


		};

		let sparePartsToString = function(){
			$scope.reportToEdit.sparepartsUsed = "";

			for(let j = 0; j < $scope.reportToEdit.operators.length; j++){
				for (let i = 0; i < $scope.reportToEdit.operators[j].usedSpareparts.length; i++) {
					$scope.reportToEdit.sparepartsUsed += $scope.reportToEdit.operators[j].usedSpareparts[i].productType.typeName + " - " + $scope.reportToEdit.operators[j].usedSpareparts[i].amount + "; ";
				}

			}

			if($scope.reportToEdit.sparepartsUsed.length > 0){
				$scope.reportToEdit.sparepartsUsed = $scope.reportToEdit.sparepartsUsed.slice(0, -2);
			}

		};

		let arrayToSpareParts = function(){
			if(!$scope.selectedOperator.value || !$scope.selectedOperator.value.usedSpareparts || $scope.selectedOperator.value.usedSpareparts.length <= 0 || $scope.selectedOperator.value.sparePartsToUse){
				return;
			}

			$scope.selectedOperator.sparePartsToUse = [];

			for (let i = 0; i < $scope.selectedOperator.value.usedSpareparts.length; i++) {
				let sp = {
					amount: $scope.selectedOperator.value.usedSpareparts[i].amount
				};

				for (let j = 0; j < $scope.spareParts.length; j++) {
					if($scope.spareParts[i].productType.typeName === $scope.selectedOperator.value.usedSpareparts[i].productType.typeName){
						sp.sparePart = $scope.spareParts[i];

						break;
					}
				}

				$scope.selectedOperator.sparePartsToUse.push(sp);
			}
		};

		$scope.useSpareParts = function(){
			validateSparePartsInReport();

			if($scope.validationError){
				return;
			}

			$scope.selectedOperator.sparePartsToUse = angular.copy($scope.selectedOperator.tempSparePartsToUse);

			sparePartsToArray();

			$('#useSparePartsModal').modal('hide');

		};

		$scope.isUseSparePartsConfirmDisabled = function(){
			return $scope.validationError;
		};

		$scope.isModalFrozen = function(){
			return false;
		};

		$scope.setSparePart = function(sp, spToUse) {
			spToUse.sparePart = sp;
			spToUse.invalid = false;
			validateSparePartsInReport() ;
		};

		$scope.addSparePartToReport = function(){
			$scope.selectedOperator.tempSparePartsToUse.push({
				amount: 1
			});

			$scope.validationError = false;

			$scope.sparepartSelectApis.push({});
		};

		$scope.removeSparePartFromReport = function(index){
			$scope.selectedOperator.tempSparePartsToUse.splice(index, 1);

			$scope.validationError = false;
			$scope.sparepartSelectApis.splice(index, 1);
		};

		$scope.onSparePartChange = function(sp){
			sp.noPart = false;
			sp.amount = 1;

			$scope.validationError = false;
		};

		$scope.onAmountChange = function(sp){
			sp.noAmount = false;

			$scope.validationError = false;
		};

		$scope.askToRemoveSparepart = function(operator, spIndex){
			$scope.removeUsedSparepartsData = {
				operator: operator,
				spIndex: spIndex
			};

			$('#removeSparepartCheck').modal('show');
		};

		$scope.removeNewSparePart = function (idx){
			$scope.selectedOperator.sparePartsToUse.splice(idx, 1);
		}


		$scope.rememberSparepartsToRemove = function(){
			if(!$scope.reportToEdit.sparepartsToRemove){
				$scope.reportToEdit.sparepartsToRemove = []
			}

			$scope.removeUsedSparepartsData.operator.usedSpareparts[$scope.removeUsedSparepartsData.spIndex].forRemove = true;

			$scope.reportToEdit.sparepartsToRemove.push($scope.removeUsedSparepartsData.operator.usedSpareparts[$scope.removeUsedSparepartsData.spIndex]);
		};

		$scope.setTempRepairJob = function (val) {
			if (!$scope.selectedOperator.tempRepairJobs) {
				$scope.selectedOperator.tempRepairJobs = [];
			}

			let tempRJ = {};
			tempRJ.id = val.id;
			tempRJ.job = val.job;
			tempRJ.forWhat = val.forWhat;
			tempRJ.invalid = false;
			$scope.selectedOperator.tempRepairJobs.push(tempRJ);
			$scope.selectedOperator.repairJobs = $scope.selectedOperator.tempRepairJobs;
		};

		$scope.removeTempRepairJob = function(index) {
			$scope.selectedOperator.tempRepairJobs.splice(index, 1);
		};

		//TODO доробити стосовно Pending статуса і добавити інформаційний блок з номерами боллардів і коментами до них

		$scope.collect = function() {
			$http.post(projectRoot + "/msc-api/repair-reports/collect", $scope.reportToEdit).then(function(response){
				$scope.reportToEdit.state = response.data.state;
				$scope.reportToEdit.collectedDate = response.data.collectedDate;

				$rootScope.successNotification("Collected");

				$scope.updateOriginalEditable();
			}, function(){
				$rootScope.errorNotification("Collected");

			});
		};

		$scope.startJob = function() {

			$('#operatorSelection').modal('show');
//			$http.post(projectRoot + "/msc-api/repair-reports/start-job", $scope.reportToEdit).then(function(response){
//				$scope.reportToEdit.state = response.data.state;
//
//				updateRepairLists();
//
//				$rootScope.successNotification("Job Started");
//
//			}, function(errorResponse){
//				$rootScope.errorNotification("Error on Job Start");
//
//			});
		};

		$scope.chooseOperator = function(){
			$scope.reportToEdit.jobStarted = true;
			$scope.milisOfJobStart = Date.now();
			$scope.selectedOperator.value = $scope.selectedOperator.tempValue;
		};

		$scope.selectedOperator={};

		$scope.isOperatorSelectionConfirmDisabled = function(){
			return !$scope.selectedOperator.tempValue;
		};

		let addNewOperatorToReport = function(){
			// if($scope.selectedOperator.value.id !== undefined && $scope.selectedOperator.value.id !== null){
			// 	for (let i = 0; i < $scope.reportToEdit.operators.length; i++) {
			// 		if($scope.reportToEdit.operators[i].id === $scope.selectedOperator.value.id){
			// 			$scope.reportToEdit.operators[i].userName = 'new';
			// 			$scope.reportToEdit.operators[i].usedSpareparts = $scope.selectedOperator.value.usedSpareparts;
			// 			$scope.reportToEdit.operators[i].jobsDone = $scope.selectedOperator.value.jobsDone;
			// 		} //end if
			// 	} //end for
			// } else {
				$scope.reportToEdit.operators.push({
					userName: "new",
					usedSpareparts: $scope.selectedOperator.value.usedSpareparts,
					jobsDone: $scope.selectedOperator.value.jobsDone
				});
			// } //end else
		};

		$scope.saveEditedReport = function(complete){
			// $scope.responseReport = null;
			jobsToStringInSelectedOperator(); //build string in $scope.selectedOperator.value.jobsDone = all done jobs, separated with comma
			sparePartsToArray();

			let millisNow = Date.now();

			$scope.selectedOperator.value.userName = "new";

			addNewOperatorToReport(); //add temporary operator to $scope.reportToEdit.operators array with filled jobsDone & usedSpareParts
            if (complete) {
                $scope.reportToEdit.completeAfter = true;
            }
			$http.post(projectRoot + "/msc-api/repair-reports/" + $scope.selectedOperator.value.userEmail + "/" + (millisNow - $scope.milisOfJobStart), $scope.reportToEdit)
				.then(function(response){
					$scope.milisOfJobStart = millisNow;
					$scope.reportToEdit.state = response.data.state;
					$scope.reportToEdit.operators = response.data.operators;
					// $scope.selectedOperator.value.id = response.data.theLatestOperator.id;
					// $scope.responseReport = response.data;

					$scope.reportToEdit.operators.sort(function(a, b){
						return a.id - b.id;
					});

					// for (let i = 0; i < $scope.repairReports.length; i++) {
					// 	if ($scope.repairReports[i].getId() === $scope.reportToEdit.getId()) {
					// 		$scope.repairReports[i].operators = $scope.reportToEdit.operators;
					// 	}
					// }

					updateRepairLists(); //move $scope.repairReports to $scope.bikesInRepair if state is IN_PROGRESS
					//or to bikesToRepair in other states

					$scope.reportsGridOptions.api.setRowData($scope.repairReports);

					// $scope.updateOriginalEditable();

					$rootScope.successNotification("Report saved");

					let reports = [];
					reports.push($scope.reportToEdit);
					performRepairHistoryBuild(reports);
					// $scope.updateOriginalEditable();
					let jobsDoneStr = getJobsDoneAsString($scope.reportToEdit);

					// для незакінчених робіт response.data[i].repairDate == null, тому
					if (!$scope.reportToEdit.repairDate) $scope.reportToEdit.repairDate = $scope.reportToEdit.reportDate;

                    if ($scope.selectedOperator.tempRepairJobs) {
                        $scope.selectedOperator.tempRepairJobs = [];
                    }

                    if (complete) {
                        $rootScope.successNotification("Job Completed");
                        $scope.reportToEdit = null;
						$scope.updateOriginalEditable();
                    } else {
						$rootScope.successNotification("Saved");
						$scope.updateOriginalEditable();
					}
					window.open(projectRoot + "/views/repair-reports", "_self");

				}, function(){
					$rootScope.errorNotification("Error on save report");

				});
		};

		let getJobsDoneAsString = function(reportToEdit) {
			let jd = "";
			for (let i = 0; i < reportToEdit.operators.length; i++) {
				if (reportToEdit.operators[i].jobsDone && reportToEdit.operators[i].jobsDone.length) {
					jd += reportToEdit.operators[i].jobsDone + ", ";
				}
			}
			if (jd.length > 2) {
				jd = jd.slice(0, -2);
			}
			// reportToEdit.repairHistory.push(jd);
			return jd;
		};

		// $scope.completeJob = function(){
        //     $scope.saveEditedReport(true);
		// 	// let savedResponseId = $rootScope.responseId;
		// 	let millisNow = Date.now();
        //     $scope.reportToEdit.completeAfter = true;
		// 	// $http.post(projectRoot + "/msc-api/repair-reports/complete-job/" + $scope.selectedOperator.value.userEmail + "/" + (millisNow - $scope.milisOfJobStart), saveResponse.id)
		// 	// $http.post(projectRoot + "/msc-api/repair-reports/complete-job/" + savedResponseId, saveResponseId)
        //     $http.post(projectRoot + "/msc-api/repair-reports/" + $scope.selectedOperator.value.userEmail + "/" + (millisNow - $scope.milisOfJobStart), $scope.reportToEdit)
		// 		.then(function(){
		// 			//$scope.reportToEdit.state = response.data.state;
		//
		// 			//updateRepairLists();
		//
		// 			// $scope.milisOfJobStart = millisNow;
		//
		// 			$rootScope.successNotification("Job Completed");
		//
		// 			window.open(projectRoot + "/views/repair-reports", "_self");
		//
		// 		}, function(){
		// 			$rootScope.errorNotification("Error on complete job");
		//
		// 		});
		// };

		$scope.returnTrue = function() {
			return true;
		};

		$scope.returnFalse = function() {
			return false;
		};

	}

]);
