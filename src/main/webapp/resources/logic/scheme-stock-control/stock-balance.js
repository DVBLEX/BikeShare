"use strict";

app.controller('stockBalanceCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	function($scope, $http, $timeout, $rootScope) {
		
		$scope.STATE_USED = 31;
		$scope.STATE_PLACED = 32;
		$scope.currentStateId = 0;
		
		$scope.pageSizes = [10, 20, 50];
		$scope.pageSize = $scope.pageSizes[0];
		
		const PAGES_TO_SHOW = 5;
		
		
		var loadACV = function(){
			$http.get(projectRoot + "/msc-api/assets/assets-current-values/by-scheme/" + $scope.schemeOfUser.name).then(function(response){
				$scope.assetsCurrentValues = response.data;
				$scope.assetsCurrentValuesArray = response.data;
				
				var tempAcv = {};
				
				//sorting by groups
				for (var k = 0; k < $scope.groups.length; k++) {
					tempAcv[$scope.groups[k]] = [];
				
					for (var j = 0; j < $scope.assetsCurrentValues.length; j++) {
						if($scope.assetsCurrentValues[j].productType.assetGroup === $scope.groups[k]){
							tempAcv[$scope.groups[k]].push($scope.assetsCurrentValues[j]);
						}
					}
				}
				
				$scope.assetsCurrentValues = tempAcv;
				
				$scope.updateCVGridData();
			}, function(errorResponse){
				
			});
		}
		
		var loadAllTypesOfAssets = function(){
			$http.get(projectRoot + "/msc-api/assets/types-of-assets").then(function(response){
				$scope.allTypesOfAssets = response.data;

			}, function(errorResponse){
				
			})
		}
		
		var reportCreateGroups = function(report){
			report.prodGroupsNames = [];
			report.prodGroups = {};
			
			for (var j = 0; j < report.assets.length; j++) {
				var usedAsset = report.assets[j];
				
				if(!report.prodGroups.hasOwnProperty(usedAsset.typeOfAssets.assetGroup)){
					report.prodGroups[usedAsset.typeOfAssets.assetGroup] = [];
					
					report.prodGroupsNames.push(usedAsset.typeOfAssets.assetGroup);
				}
				
				report.prodGroups[usedAsset.typeOfAssets.assetGroup].push(usedAsset);
			}
		}
		
		var loadData = function(){
			$http.get(projectRoot + "/msc-api/user/user-scheme").then(function(response){
				$scope.schemeOfUser = response.data;
				
				$http.get(projectRoot + "/msc-api/assets/stock-usage-reports/" + $scope.schemeOfUser.name).then(function(response){
					$scope.stockUsageReports = response.data;
					
					for (var i = 0; i < $scope.stockUsageReports.length; i++) {
						reportCreateGroups($scope.stockUsageReports[i]);
					}
					
					$scope.updateRecords();
				}, function(errorResponse){
					
				});
				
				$http.get(projectRoot + "/msc-api/assets/groups-of-types").then(function(response){
					$scope.groups = response.data;
				
					loadACV();
					
				}, function(errorResponse){
					
				})
				
				loadAllTypesOfAssets();
			}, function(errorResponse){
				
			})	
		}
		
		loadData();
		
		$scope.updateRecords = function(){
			$scope.currentRecords = [];
			
			for (var i = 0; i < $scope.stockUsageReports.length; i++) {
				if($scope.stockUsageReports[i].state.id === $scope.currentStateId || $scope.currentStateId === 0){
					$scope.currentRecords.push($scope.stockUsageReports[i]);
				}
			}
			
			$scope.pagesParameters = {
					currentPage: 1,
					pagesNumber: Math.ceil($scope.currentRecords.length / $scope.pageSize),
					pagesToShow: PAGES_TO_SHOW
				}
		}
		
		$scope.setCurrentState = function(currentStateId){
			$scope.currentStateId = currentStateId;
			
			$scope.updateRecords();
		}
		
		$scope.activeTabCV = 0;
		$scope.setActiveTabCV = function(index){
			$scope.activeTabCV = index;
			
			$scope.updateCVGridData();
		}
		
		$scope.updateCVGridData = function(){
			
			$scope.acvGridOptions.api.setRowData($scope.assetsCurrentValues[$scope.groups[$scope.activeTabCV]]);
			
			//$scope.acvGridOptions.api.refreshCells({columns: ['quantity'], force: true});
		}

		$scope.needToShow = function(index, r){
			if($scope.pagesParameters){
				return (index < $scope.pagesParameters.currentPage * $scope.pageSize && index >= ($scope.pagesParameters.currentPage - 1) * $scope.pageSize)
			} else {
				return false;
			}
		}
		
		//--acv--grid-related-stuff--
		
		var qrCellRendererFunc = function(params){
			 params.$scope.productId = params.node.data.productType.id;
			 return '<button type="button" class="btn btn-info btn-sm btn-block" style="height:25px; padding: 3px 8px;" data-ng-click="printQR(productId)"><i class="material-icons md-18">print</i></button>';
		}
		
		var acvColumnDefs = [
	        {headerName: "Asset Name", field: "productType.fullName", filter: "agTextColumnFilter", width: 190},
	        {headerName: "Quantity", field: "quantity", cellClass:'text-right', filter: "agNumberColumnFilter", width: 80},
	        {headerName: "QR Code", field: "", width: 80, cellRenderer: qrCellRendererFunc, pinned: 'right', suppressSizeToFit: true}
	    ];
		
		$scope.acvGridOptions = {
			columnDefs: acvColumnDefs,
			rowData: null,
		    pagination: true,
		    paginationPageSize: 30,
		    floatingFilter: true,
		    angularCompileRows: true,
		    components:{
		        numericCellEditor: NumericCellEditor,
		    },
		    onGridReady: function(params) {
		        params.api.sizeColumnsToFit();
		    },
		};
		
		window.addEventListener('resize', function(){
			$scope.acvGridOptions.api.sizeColumnsToFit();
		}, true);
		
		$scope.printQR = function(id) {
			window.open(projectRoot + "/msc-api/assets/qr-pdf/" + id, "_blank");
		}
		
		//--acv--grid-related-stuff-end--
		
		$scope.assetsToChooseFrom = [];
		$scope.assetsToChooseFromGrouped = {};
		$scope.groupsToChooseFrom = [];
		
		$scope.openUseStocksWindow = function(useStocks) {

			$scope.newReport = {
				reportedGroups: [{types: []}],
			}

			if (useStocks) {
				$scope.usingStocks = true;

				$scope.assetsToChooseFrom = [];
				$scope.groupsToChooseFrom = [];
				for (let i = 0; i < $scope.assetsCurrentValuesArray.length; i++) {
					$scope.assetsToChooseFrom.push($scope.assetsCurrentValuesArray[i].productType);

					if (!$scope.groupsToChooseFrom.find(function (element) {
						return element === $scope.assetsCurrentValuesArray[i].productType.assetGroup
					})) {
						$scope.groupsToChooseFrom.push($scope.assetsCurrentValuesArray[i].productType.assetGroup);
					}
				}

				$scope.newReport.state = {
					id: $scope.STATE_USED,
					name: "Used",
					type: 4
				}

				$scope.createReportModalOptions.title = "Use Stocks";
			} else {
				$scope.usingStocks = false;
				$scope.groupsToChooseFrom = $scope.groups;
				$scope.assetsToChooseFrom = $scope.allTypesOfAssets;
				$scope.newReport.state = {
					id: $scope.STATE_PLACED,
					name: "Placed",
					type: 4
				}
				$scope.createReportModalOptions.title = "Place Stocks";
			}

			$scope.assetsToChooseFromGrouped = {};

			for (let asset of $scope.assetsToChooseFrom) {
				if($scope.assetsToChooseFromGrouped[asset.assetGroup]) {
					$scope.assetsToChooseFromGrouped[asset.assetGroup].push(asset)
				} else {
					$scope.assetsToChooseFromGrouped[asset.assetGroup] = [asset];
				}
			}
			
			$('#createReportModal').modal('show');
				
		}
		
		$scope.createReportModalOptions = {
			isFrozen: function(){
				return $scope.savingReport;
			},
			confirmDisabled: function(){
				return $scope.newReport && $scope.newReport.reportedGroups.length <= 0;
			}
		}
		
		$scope.addGroupToNewReport = function(){
			$scope.newReport.reportedGroups.push({types: []});
		}
		
		$scope.removeGroupFromNewReport = function(groupObj){
			$scope.newReport.reportedGroups.splice($scope.newReport.reportedGroups.indexOf(groupObj), 1);
		}
		
		$scope.onGroupChange = function(groupObj){
			groupObj.types = [];
		}
		
		$scope.deleteNewReport = function(){
			$scope.newReport = undefined;
		}

		$scope.setAsset = function(asset, rpt) {
			rpt.typeOfAssets = asset;
			$scope.onReportedTypeChange(rpt);
		}

		$scope.onReportedTypeChange = function(rpt){
			if(rpt.typeOfAssets){
				rpt.noType = false;
			}
			if(rpt.amount){
				rpt.noAmount = false;
			}
		}
		
		$scope.removeAssetFromNewReport = function(rpt, groupObj){
			groupObj.types.splice(groupObj.types.indexOf(rpt), 1);
		}
		
		$scope.addAssetToNewReport = function(groupObj){
			groupObj.noTypes = false
			groupObj.types.push({});
		}
		
		$scope.saveReport = function(){
		
			var validationError = false;
			
			$scope.newReport.scheme = $scope.schemeOfUser;
			
			$scope.newReport.assets = [];
			for (var i = 0; i < $scope.newReport.reportedGroups.length; i++) {
				if($scope.newReport.reportedGroups[i].types.length === 0){
					$scope.newReport.reportedGroups[i].noTypes = true;
					
					validationError = true;
				}
				
				for (var j = 0; j < $scope.newReport.reportedGroups[i].types.length; j++) {
					$scope.newReport.assets.push(angular.copy($scope.newReport.reportedGroups[i].types[j]));
					
					if(!$scope.newReport.reportedGroups[i].types[j].typeOfAssets){
						$scope.newReport.reportedGroups[i].types[j].noType = true;
						
						validationError = true;
					}
					if(!$scope.newReport.reportedGroups[i].types[j].amount){
						$scope.newReport.reportedGroups[i].types[j].noAmount = true;
						
						validationError = true;
					}
				}
			}
			
			
			for(i = 0; i < $scope.newReport.assets.length; i++){
				//checking for duplicates
				for(j = 0; j < $scope.newReport.assets.length; j++){
					if($scope.newReport.assets[i] !== $scope.newReport.assets[j] && $scope.newReport.assets[i].typeOfAssets && $scope.newReport.assets[j].typeOfAssets && $scope.newReport.assets[i].typeOfAssets.id === $scope.newReport.assets[j].typeOfAssets.id){
						$scope.newReport.assets[i].amount += $scope.newReport.assets[j].amount;
						$scope.newReport.assets.splice(j--, 1);
					}
				}
				
			}
			
			if(validationError) {
				return;
			}
			
			$scope.savingReport = true;
			
			//---saving---
			$http.post(projectRoot + "/msc-api/assets/stock-usage-reports", $scope.newReport).then(function(response){

				reportCreateGroups(response.data);
				$scope.stockUsageReports.splice(0, 0, response.data);
				
				$scope.updateRecords();
				
				loadACV();
				
				$rootScope.successNotification("Saved");
				
				$scope.savingReport = false;
				
				$('#createReportModal').modal('hide');
				
				$scope.newReport = undefined;
			}, function(errorResponse){
				$rootScope.errorNotification("Error on save report. " + (errorResponse.data.errorMessage ? errorResponse.data.errorMessage : ''));
				
				$scope.savingReport = false;
				
				$('#createReportModal').modal('hide');
			});
			
		}
	}
]);
