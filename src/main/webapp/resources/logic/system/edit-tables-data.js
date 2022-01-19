"use strict";

app.controller('editTablesDataCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$q',
	'requestsQueueService',
	'$rootScope',
	function($scope, $http, $timeout, $q, requestsQueueService, $rootScope) {
		
		
		var loadData = function(){
			$http.get(projectRoot + "/msc-api/top-data/schemes").then(function(response){
				$scope.schemes = response.data;
			
				$scope.schemeNames = [];
				
				for (var i = 0; i < $scope.schemes.length; i++) {
					$scope.schemeNames.push($scope.schemes[i].name);
				}

				$http.get(projectRoot + "/msc-api/repair-reports/bike-stations").then(function(response){
					$scope.bikeStations = response.data;
					
					$scope.bikeStationsGridOptions.api.setRowData($scope.bikeStations);
					
					
				}, function(errorResponse){
					
				});
					
				$http.get(projectRoot + "/msc-api/repair-reports/bikes").then(function(response){
					$scope.bikes = response.data;
					
					//$scope.bikesGridOptions.api.setColumnDefs(bColumnDefs); //we need to update columns definitions when we loaded schemes for dropdown select to work
					$scope.bikesGridOptions.api.setRowData($scope.bikes);
					
				}, function(errorResponse){
					
				});
				
				$http.get(projectRoot + "/msc-api/repair-reports/repair-jobs").then(function(response){
					$scope.repairJobs = response.data;
					
					for (var i = 0; i < $scope.repairJobs.length; i++) {
						if($scope.repairJobs[i].forWhat === 1){
							$scope.repairJobs[i].forWhatString = "Station";
						} else if($scope.repairJobs[i].forWhat === 2){
							$scope.repairJobs[i].forWhatString = "Bike";
						}
					}
					
					$scope.jobsGridOptions.api.setRowData($scope.repairJobs);
					
				}, function(errorResponse){
					
				});	
				
				$http.get(projectRoot + "/msc-api/repair-reports/repair-reasons").then(function(response){
					$scope.repairReasons = response.data;
					
					for (var i = 0; i < $scope.repairReasons.length; i++) {
						if($scope.repairReasons[i].forWhat === 1){
							$scope.repairReasons[i].forWhatString = "Station";
						} else if($scope.repairReasons[i].forWhat === 2){
							$scope.repairReasons[i].forWhatString = "Bike";
						}
					}
					
					$scope.repairReasonsGridOptions.api.setRowData($scope.repairReasons);
					
				}, function(errorResponse){
					
				});
				
				$http.get(projectRoot + "/msc-api/top-data/system-parameters").then(function(response){
					$scope.parameters = response.data;
					
					for(var key in $scope.parameters){
						if($scope.parameters[key] === "true"){
							$scope.parameters[key] = true;
						}
					}
					
					$scope.emailServerProperties.email_host = $scope.parameters.email_host;
					$scope.emailServerProperties.email_port = $scope.parameters.email_port;
					$scope.emailServerProperties.email_username = $scope.parameters.email_username;
					$scope.emailServerProperties.email_password = $scope.parameters.email_password;
					$scope.emailServerProperties.email_starttls = $scope.parameters.email_starttls;
					
					$scope.generalProperties.domain_link = $scope.parameters.domain_link;
					
					$scope.stockRequestProperties.stock_request_generation_trigger = $scope.parameters.stock_request_generation_trigger;
					$scope.stockRequestProperties.low_stock_percentage = $scope.parameters.low_stock_percentage;
					
					$scope.infoBlockForPDF.pdf_text = $scope.parameters.pdf_text;
				}, function(errorResponse){
					
				});
				
			}, function(errorResponse){
				
			});
			
		}
		
		loadData();
		
		$scope.forWhatOptions = ['Station', 'Bike'];
		
		//--bike-stations--grid-related-stuff--
		
		var bsColumnDefs = [
			{headerName: "Id", field: "id", filter: "agNumberColumnFilter", editable: false, width: 100},
	        {headerName: "Location", field: "location", filter: "agTextColumnFilter", editable: true, width: 150},
	        {headerName: "Geo Latitude", field: "geoLat", cellEditor: 'numericCellEditor', editable: true, filter: "agTextColumnFilter", width: 125},
	        {headerName: "Geo Longitude", field: "geoLong", cellEditor: 'numericCellEditor', editable: true, filter: "agTextColumnFilter", width: 125},
	        {headerName: "Bollards", field: "bollardsTotalNumber", cellEditor: 'numericCellEditor', editable: true, width: 125},
	        {headerName: "Scheme", field: "scheme.name", filter: "agTextColumnFilter", editable: false, width: 95},
	    ];
		
		$scope.bikeStationsGridOptions = {
			columnDefs: bsColumnDefs,
			rowData: null,
	        pagination: true,
	        paginationPageSize: 25,
	        floatingFilter: true,
	        angularCompileRows: true,
	        onGridReady: function(params) {
	            params.api.sizeColumnsToFit();
	        },
			onCellValueChanged: function (params) {
				// trigger filtering on cell edits
				if (params.colDef.field === "bollardsTotalNumber" && (+params.newValue > 100 || +params.newValue < 1 || params.newValue.toString().includes('.'))) {

						params.data.bollardsTotalNumber = params.oldValue;
						$rootScope.errorNotificationCustom("Bollards must be in the range 0-100");

				} else {
					requestsQueueService.appendRequest('POST', projectRoot + "/msc-api/repair-reports/bike-stations", null, params.data,
						function(){

							$scope.bikeStationsGridOptions.api.refreshCells({force: true});
							$rootScope.successNotification("Updated");

						},
						function(){

							$scope.bikeStationsGridOptions.api.refreshCells({force: true});
							$rootScope.errorNotification("Error");

						});
				}
				$scope.bikeStationsGridOptions.api.refreshCells({force: true});
			},
	        components:{
		        numericCellEditor: NumericCellEditor,
		    },
		};
		
		//--bike-stations--grid-related-stuff--end--
		
		//--bikes--grid-related-stuff--
		
		var bColumnDefs = [
	        {headerName: "ID", field: "number", editable: true, filter: "agTextColumnFilter", width: 200},
	        {headerName: "Scheme", field: "scheme.name", editable: true, filter: "agTextColumnFilter", cellEditor: 'agSelectCellEditor', 
	        	cellEditorParams: {values: ['Cork', 'Dublin', 'Galway', 'Limerick']} /*temporary setting schemes explicitly*/, width: 195},
	    ];
		
		$scope.bikesGridOptions = {
			columnDefs: bColumnDefs,
			onCellEditingStopped: function(event) {
				
				requestsQueueService.appendRequest('POST', projectRoot + "/msc-api/repair-reports/bikes", null, event.data, 
						function(){
					
							$scope.bikesGridOptions.api.refreshCells({force: true});
							
							$rootScope.successNotification("Updated");
						}, 
						function(){
							$rootScope.errorNotification("Error");
						});
			},
			rowData: null,
	        pagination: true,
	        paginationPageSize: 25,
	        floatingFilter: true,
	        angularCompileRows: true,
	        onGridReady: function(params) {
	            params.api.sizeColumnsToFit();
	        },
		};
		
		//--bikes--grid-related-stuff--end--
		
		$scope.openAddLocationWindow = function(){
			$scope.newLocation = {};
			
			$('#addNewLocationModal').modal('show');
		}
		
		$scope.isModalFrozen = function(){
			return $scope.saving;
		}
		
		$scope.locationModalConfirmDisabled = function(){
			return $scope.newLocation && (isStringBad($scope.newLocation.location) || !$scope.newLocation.scheme || !$scope.newLocation.geoLat || !$scope.newLocation.geoLong); 
		}
		
		$scope.saveNewLocation = function(){
			$scope.saving = true;
			
			$http.post(projectRoot + "/msc-api/repair-reports/bike-stations", $scope.newLocation).then(function(response){
				$scope.bikeStations.push(response.data);
				
				$scope.bikeStationsGridOptions.api.setRowData($scope.bikeStations);
				
				$scope.saving = false;
				
				$('#addNewLocationModal').modal('hide');
				
				$rootScope.successNotification("Added");
			}, function(errorResponse){
				$scope.saving = false;
				
				$('#addNewLocationModal').modal('hide');
				
				$rootScope.errorNotification("Error");
			})
		}
		
		
		$scope.openAddBikeWindow = function(){
			$scope.newBike = {};
			
			$('#addNewBikeModal').modal('show');
		}
		
		$scope.bikeModalConfirmDisabled = function(){
			return $scope.newBike && (isStringBad($scope.newBike.number) || !$scope.newBike.scheme); 
		}
		
		$scope.saveNewBike = function(){
			$scope.saving = true;
			
			$http.post(projectRoot + "/msc-api/repair-reports/bikes", $scope.newBike).then(function(response){
				$scope.bikes.push(response.data);
				
				$scope.bikesGridOptions.api.setRowData($scope.bikes);
				
				$scope.saving = false;
				
				$('#addNewBikeModal').modal('hide');
				
				$rootScope.successNotification("Added");
			}, function(errorResponse){
				$scope.saving = false;
				
				$('#addNewBikeModal').modal('hide');
				
				$rootScope.errorNotification("Error");
			})
		}
		
		
		//--jobs--grid-related-stuff--
		
		var jobsColumnDefs = [
	        {headerName: "Job Name", field: "job", editable: true, filter: "agTextColumnFilter", width: 120},
	        {headerName: "Job Type", field: "forWhatString", editable: true, filter: "agTextColumnFilter", cellEditor: 'agSelectCellEditor', 
	        	cellEditorParams: {values: $scope.forWhatOptions}, width: 70},
	    ];
		
		$scope.jobsGridOptions = {
			columnDefs: jobsColumnDefs,
			onCellEditingStopped: function(event) {
				
				if(event.data.forWhatString === 'Station'){
					event.data.forWhat = 1;
				} else if(event.data.forWhatString === 'Bike'){
					event.data.forWhat = 2
				}
				
				requestsQueueService.appendRequest('POST', projectRoot + "/msc-api/repair-reports/repair-jobs", null, event.data, 
						function(){
							$scope.jobsGridOptions.api.refreshCells({force: true});
							
							$rootScope.successNotification("Updated");
						}, 
						function(){
							$rootScope.errorNotification("Error");
						});
			},
			rowData: null,
	        pagination: true,
	        paginationPageSize: 25,
	        floatingFilter: true,
	        angularCompileRows: true,
	        onGridReady: function(params) {
	            params.api.sizeColumnsToFit();
	        },
		};
		
		//--jobs--grid-related-stuff--end--
		
		//--repair-reasons--grid-related-stuff--
		
		var repairReasonsColumnDefs = [
	        {headerName: "Reason Name", field: "reason", editable: true, filter: "agTextColumnFilter", width: 120},
	        {headerName: "Reason Type", field: "forWhatString", editable: true, filter: "agTextColumnFilter", cellEditor: 'agSelectCellEditor', 
	        	cellEditorParams: {values: $scope.forWhatOptions}, width: 70},
	    ];
		
		$scope.repairReasonsGridOptions = {
			columnDefs: repairReasonsColumnDefs,
			onCellEditingStopped: function(event) {
				
				if(event.data.forWhatString === 'Station'){
					event.data.forWhat = 1;
				} else if(event.data.forWhatString === 'Bike'){
					event.data.forWhat = 2
				}
				
				requestsQueueService.appendRequest('POST', projectRoot + "/msc-api/repair-reports/repair-reasons", null, event.data, 
						function(){
							$scope.repairReasonsGridOptions.api.refreshCells({force: true});
							
							$rootScope.successNotification("Updated");
						}, 
						function(){
							$rootScope.errorNotification("Error");
						});
			},
			rowData: null,
	        pagination: true,
	        paginationPageSize: 25,
	        floatingFilter: true,
	        angularCompileRows: true,
	        onGridReady: function(params) {
	            params.api.sizeColumnsToFit();
	        },
		};
		
		//--repair-reasons--grid-related-stuff--end--
		
		
		$scope.openAddJobWindow = function(){
			$scope.newJob = {};
			
			$('#addNewJobModal').modal('show');
		}
		
		$scope.jobModalConfirmDisabled = function(){
			return $scope.newJob && (isStringBad($scope.newJob.job) || !$scope.newJob.forWhatString); 
		}
		
		$scope.saveNewJob = function(){
			$scope.saving = true;
			
			if($scope.newJob.forWhatString === 'Station'){
				$scope.newJob.forWhat = 1;
			} else if($scope.newJob.forWhatString === 'Bike'){
				$scope.newJob.forWhat = 2
			}
			
			$http.post(projectRoot + "/msc-api/repair-reports/repair-jobs", $scope.newJob).then(function(response){
				if(response.data.forWhat === 1){
					response.data.forWhatString = "Station";
				} else if(response.data.forWhat === 2){
					response.data.forWhatString = "Bike";
				}
				
				
				$scope.repairJobs.push(response.data);
				
				$scope.jobsGridOptions.api.setRowData($scope.repairJobs);
				
				$scope.saving = false;
				
				$('#addNewJobModal').modal('hide');
				
				$rootScope.successNotification("Added");
			}, function(errorResponse){
				$scope.saving = false;
				
				$('#addNewJobModal').modal('hide');
				
				$rootScope.errorNotification("Error");
			})
		}
		
		
		$scope.openAddRepairReasonWindow = function(){
			$scope.newReason = {};
			
			$('#addNewRepairReasonModal').modal('show');
		}
		
		$scope.repairReasonModalConfirmDisabled = function(){
			return $scope.newReason && (isStringBad($scope.newReason.reason) || !$scope.newReason.forWhatString); 
		}
		
		$scope.saveNewRepairReason = function(){
			$scope.saving = true;
			
			if($scope.newReason.forWhatString === 'Station'){
				$scope.newReason.forWhat = 1;
			} else if($scope.newReason.forWhatString === 'Bike'){
				$scope.newReason.forWhat = 2
			}
			
			$http.post(projectRoot + "/msc-api/repair-reports/repair-reasons", $scope.newReason).then(function(response){
				if(response.data.forWhat === 1){
					response.data.forWhatString = "Station";
				} else if(response.data.forWhat === 2){
					response.data.forWhatString = "Bike";
				}
				
				
				$scope.repairReasons.push(response.data);
				
				$scope.repairReasonsGridOptions.api.setRowData($scope.repairReasons);
				
				$scope.saving = false;
				
				$('#addNewRepairReasonModal').modal('hide');
				
				$rootScope.successNotification("Added");
			}, function(errorResponse){
				$scope.saving = false;
				
				$('#addNewRepairReasonModal').modal('hide');
				
				$rootScope.errorNotification("Error");
			})
		}
		
		//----BLOCK-EDITING
		
		$scope.emailServerProperties = {};
		$scope.generalProperties = {};
		$scope.stockRequestProperties = {};
		$scope.infoBlockForPDF = {}
		
		
		$scope.emailServerBlock = {};
		$scope.generalBlock = {};
		$scope.stockRequestBlock = {};
		$scope.infoForPDFBlock = {};
		
		$scope.editBlock = function(block, properties){
			$scope.editableBlock = block;
			$scope.oldProperties = angular.copy(properties);
		}
		
		$scope.saveBlock = function(savableParameters){
			var parameters = [];
			
			for(var key in savableParameters){
				parameters.push({parameterName: key, parameterValue: savableParameters[key]});
			}
			
			$http.post(projectRoot + "/msc-api/top-data/system-parameters/list", parameters).then(function(response){
				$scope.stopEditBlock();
				
				$rootScope.successNotification("Saved");
			}, function(errorResponse){
				$rootScope.errorNotification("Error");
			})
		}
		
		$scope.canEdit = function(block){
			return $scope.editableBlock === block;
		}
		
		$scope.canStartEdit = function(){
			return $scope.editableBlock === undefined;
		}
		
		$scope.cancelEditBlock = function(properties){
			for(var key in properties){
				properties[key] = $scope.oldProperties[key];
			}
			
			$scope.stopEditBlock();
		}
		
		$scope.stopEditBlock = function(){
			$scope.editableBlock = undefined;
			$scope.oldProperties = undefined;
		}
		
		//----------------------
		
		$scope.saveParameter = function(paramName, paramValue){
			$http.post(projectRoot + "/msc-api/top-data/system-parameters", {parameterName: paramName, parameterValue: paramValue}).then(function(response){
				$rootScope.successNotification("Saved");
			}, function(errorResponse){
				$rootScope.errorNotification("Error");
			})
		}
		
		$scope.saveAllParameters = function(){
			var parameters = [];
			
			for(var key in $scope.parameters){
				parameters.push({parameterName: key, parameterValue: $scope.parameters[key]});
			}
			
			$http.post(projectRoot + "/msc-api/top-data/system-parameters/list", parameters).then(function(response){
				$rootScope.successNotification("Saved");
			}, function(errorResponse){
				$rootScope.errorNotification("Error");
			})
		}
	}
]);
