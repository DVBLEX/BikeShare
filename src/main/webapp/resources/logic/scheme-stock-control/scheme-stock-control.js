"use strict";

app.controller('schemeStockControlCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$q',
	'requestsQueueService',
	'$rootScope',
	function($scope, $http, $timeout, $q, requestsQueueService, $rootScope) {
		
		$scope.STATE_USED = 31;
		$scope.STATE_PLACED = 32;
		$scope.currentStateId = 0;
		
		$scope.pageSizes = [10, 20, 50];
		$scope.pageSize = $scope.pageSizes[0];
		
		const PAGES_TO_SHOW = 5;
		
		$scope.lowStockPercent = 0;
		
		var loadACVData = function(){
			$http.get(projectRoot + "/msc-api/assets/low-stock-percentage").then(function(response){
				$scope.lowStockPercent = response.data;
			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/assets/assets-current-values").then(function(response){
				
				var acvBySchemes = {};
				for (var i = 0; i < $scope.schemes.length; i++) {
					acvBySchemes[$scope.schemes[i].name] = [];
					
					//sorting by schemes
					for (var j = 0; j < response.data.length; j++) {
						if(response.data[j].scheme.name === $scope.schemes[i].name){
							acvBySchemes[$scope.schemes[i].name].push(response.data[j])
						}
					}
				}
				
				$scope.acvGrouped = {};
				
				for (i = 0; i < $scope.schemes.length; i++) {
					$scope.acvGrouped[$scope.schemes[i].name] = {};
					for (var k = 0; k < $scope.groups.length; k++) {
						$scope.acvGrouped[$scope.schemes[i].name][$scope.groups[k]] = [];
						
						//sorting by groups in range of one scheme
						for (j = 0; j < acvBySchemes[$scope.schemes[i].name].length; j++) {
							if(acvBySchemes[$scope.schemes[i].name][j].productType.assetGroup === $scope.groups[k]){
								$scope.acvGrouped[$scope.schemes[i].name][$scope.groups[k]].push(acvBySchemes[$scope.schemes[i].name][j]);
							}
						}
					}
				}
				
				$scope.updateCVGridData();
				
			}, function(errorResponse){
				
			});
		}
		
		var loadData = function(){
			$http.get(projectRoot + "/msc-api/top-data/schemes").then(function(response){
				$scope.schemes = response.data;
				$scope.currentScheme = $scope.schemes[0]
				
				$http.get(projectRoot + "/msc-api/user/user-roles").then(function(response){
					
					$scope.userRoles = response.data;
					
					if($scope.userRoles[0] === 1){
						$scope.userIsAdmin = true;
					} else if($scope.userRoles[4] === 1){
						$scope.userIsFulfillmentOperator = true;
						$scope.userIsOnlyFulfillmentOperator = true;
					}
					for(var i = 0; i < $scope.userRoles.length; i++){
						if(i !== 4 && $scope.userRoles[i] === 1){
							$scope.userIsOnlyFulfillmentOperator = false;
						}
					}
				
					$http.get(projectRoot + "/msc-api/assets/groups-of-types").then(function(response){
						$scope.groups = response.data;
						
						if($scope.userIsOnlyFulfillmentOperator){
							for (var i = 0; i < $scope.groups.length; i++) {
								if($rootScope.belongsToFulfillment($scope.groups[i])){
									$scope.activeTabCV = i;
									$scope.activeTabMV = i;
									break;
								}
							}
						}
						
						
						$http.get(projectRoot + "/msc-api/assets/assets-margin-values").then(function(response){
							
							var amvBySchemes = {};
							for (var i = 0; i < $scope.schemes.length; i++) {
								amvBySchemes[$scope.schemes[i].name] = [];
								
								//sorting by schemes
								for (var j = 0; j < response.data.length; j++) {
									if(response.data[j].scheme.name === $scope.schemes[i].name){
										amvBySchemes[$scope.schemes[i].name].push(response.data[j])
									}
								}
							}
							
							$scope.amvGrouped = {};
							
							for (i = 0; i < $scope.schemes.length; i++) {
								$scope.amvGrouped[$scope.schemes[i].name] = {};
								for (var k = 0; k < $scope.groups.length; k++) {
									$scope.amvGrouped[$scope.schemes[i].name][$scope.groups[k]] = [];
									
									//sorting by groups in range of one scheme
									for (j = 0; j < amvBySchemes[$scope.schemes[i].name].length; j++) {
										if(amvBySchemes[$scope.schemes[i].name][j].productType.assetGroup === $scope.groups[k]){
											$scope.amvGrouped[$scope.schemes[i].name][$scope.groups[k]].push(amvBySchemes[$scope.schemes[i].name][j]);
										}
									}
								}
							}
							
							$scope.updateMVGridData();
							
						}, function(errorResponse){
							
						});
						
						loadACVData();
						
						$http.get(projectRoot + "/msc-api/assets/assets-transfer-queue").then(function(response){
							$scope.transferQueueBySchemes = {};
							
							for (var i = 0; i < $scope.schemes.length; i++) {
								$scope.transferQueueBySchemes[$scope.schemes[i].name] = [];
								
								//sorting by schemes
								for (var j = 0; j < response.data.length; j++) {
									if(response.data[j].transferToScheme.name === $scope.schemes[i].name){
										if($scope.userIsOnlyFulfillmentOperator && !$rootScope.belongsToFulfillment(response.data[j].productType.assetGroup)){
											response.data.splice(j--, 1);
											continue;
										}
										$scope.transferQueueBySchemes[$scope.schemes[i].name].push(response.data[j])
									}
								}
							}
							
							$scope.updateTransferQueueGrid();
						}, function(errorResponse){
							
						});
						
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
						
						$http.get(projectRoot + "/msc-api/assets/stock-usage-reports").then(function(response){
							$scope.stockUsageReports = response.data;
							
							for (var i = 0; i < $scope.stockUsageReports.length; i++) {
								reportCreateGroups($scope.stockUsageReports[i]);
								
							}
							
							$scope.updateSBPageParameters();
						}, function(errorResponse){
							
						});
					}, function(errorResponse){
						
					})
				
				}, function(errorResponse){
					
				})
				
			}, function(errorResponse){
				
			});
		}
		
		$scope.updateSBPageParameters = function(){
			$scope.currentRecords = [];
			for (var i = 0; i < $scope.stockUsageReports.length; i++) {
				if($scope.stockUsageReports[i].scheme.name === $scope.currentScheme.name && ($scope.currentStateId === 0 || $scope.currentStateId === $scope.stockUsageReports[i].state.id)){
					$scope.currentRecords.push($scope.stockUsageReports[i]);
				}
			}
			
			$scope.pagesParameters = {
					currentPage: 1,
					pagesNumber: Math.ceil($scope.currentRecords.length / $scope.pageSize),
					pagesToShow: PAGES_TO_SHOW
				}
		}
		
		$scope.updateMVGridData = function(){
			$scope.assetsMinValGridOptions.api.setRowData($scope.amvGrouped[$scope.currentScheme.name][$scope.groups[$scope.activeTabMV]]);
			
			$scope.assetsMinValGridOptions.api.refreshCells({force: true});
		}
		
		$scope.updateCVGridData = function(){
			$scope.acvGridOptions.api.setRowData($scope.acvGrouped[$scope.currentScheme.name][$scope.groups[$scope.activeTabCV]]);
			
			$scope.acvGridOptions.api.refreshCells({columns: ['quantity'], force: true});
		}
		
		$scope.updateTransferQueueGrid = function(){
			$scope.transferQueueGridOptions.api.setRowData($scope.transferQueueBySchemes[$scope.currentScheme.name]);
			
			$scope.transferQueueGridOptions.api.refreshCells({columns: ['quantity'], force: true});
		}
		
		
		$scope.setCurrentScheme = function(scheme){
			$scope.currentScheme = scheme;
			
			$scope.updateMVGridData();
			$scope.updateCVGridData();
			$scope.updateTransferQueueGrid();
			$scope.updateSBPageParameters();
		}
		
		$scope.activeTabMV = 0;
		$scope.setActiveTabMV = function(index){
			$scope.activeTabMV = index;
			
			$scope.updateMVGridData();
		}
		
		$scope.activeTabCV = 0;
		$scope.setActiveTabCV = function(index){
			$scope.activeTabCV = index;
			
			$scope.updateCVGridData();
		}
		
		$scope.setStockBalanceCurrentState = function(currentStateId){
			$scope.currentStateId = currentStateId;
			
			$scope.updateSBPageParameters();
		}
		
		
		$scope.needToShowSB = function(index, r){
			if($scope.pagesParameters){
				return (index < $scope.pagesParameters.currentPage * $scope.pageSize && index >= ($scope.pagesParameters.currentPage - 1) * $scope.pageSize)
			} else {
				return false;
			}
		}
		
		$scope.getCurrentValuesBySchemeAndProductType = function(scheme, productType){
			if(scheme && productType){
				for (var i = 0; i < $scope.acvGrouped[scheme.name][$scope.groups[$scope.activeTabCV]].length; i++) {
					if($scope.acvGrouped[scheme.name][$scope.groups[$scope.activeTabCV]][i].productType.id === productType.id){
						return $scope.acvGrouped[scheme.name][$scope.groups[$scope.activeTabCV]][i];
					}
				}
			}
			
			return null;
		};
		
		$scope.getQuantityBySchemeAndProductType = function(scheme, productType){
			var acv = $scope.getCurrentValuesBySchemeAndProductType(scheme, productType);
			if(acv){
				return acv.quantity;
			}
			
			return 0;
		}
		
		//--amv(assets minimal values)--grid-related-stuff--

		const WARN_CHAR = "!";

		$scope.doTriangleWarning = function(titleString) {
			return '<div class="msc-warning-border msc-triangle-up bigger" title="' +
				titleString +
				'" style="font-weight: bolder; font-size: 16px; text-align: center;">' +
				'<span style="margin-left: -2.5px;">' +
				WARN_CHAR +
				'</span></div> '
		};

		$scope.doCircleWarning = function(titleString, color) {
			return '<div class="msc-border-box msc-round bigger" title="' +
				titleString +
				'" style="background-color: ' +
				color +
				'; margin-right: 5px; color: white; font-weight: bolder; font-size: 16px; text-align: center;">' +
				WARN_CHAR +
				'</div> '
		};

		let WARN_COLOR_DELIVERY_TIME_APPLIED = "#ffc107";

		let assetNameCellRendererFunc = function(params){
			params.$scope.rowData = params.node.data;

			if($scope.isDeliveryTimeApplied(params.node.data)){
				return $scope.doCircleWarning("Delivery time is applied to some products", WARN_COLOR_DELIVERY_TIME_APPLIED) + params.node.data.productType.fullName;
			} else {
				return params.node.data.productType.fullName;
			}
		};

		let assetsMinValColumnDefs = [
			{headerName: "Asset Name", sortable: true, field: "productType.fullName", filter: "agTextColumnFilter", cellRenderer: assetNameCellRendererFunc, width: 200},
			{headerName: "Point of Order", sortable: true, field: "orderValue", editable: true, cellClass:'text-right', cellEditor: 'numericCellEditor', filter: "agNumberColumnFilter", width: 80},
			{headerName: "Lower Value", sortable: true, field: "lowerValue", editable: true, cellClass:'text-right', cellEditor: 'numericCellEditor', filter: "agNumberColumnFilter", width: 80},
			{headerName: "Trigger", sortable: true, field: "trigger", editable: true, cellClass:'text-right', cellEditor: 'numericCellEditor', filter: "agNumberColumnFilter", width: 80},
		];

		$scope.assetsMinValGridOptions = {
			columnDefs: assetsMinValColumnDefs,
			onCellEditingStopped: function(event) {
				
				requestsQueueService.appendRequest('POST', projectRoot + "/msc-api/assets/assets-margin-values", null, event.data, 
						function(){
					
							$scope.assetsMinValGridOptions.api.refreshCells({force: true});
							$scope.acvGridOptions.api.refreshCells({columns: ['quantity'], force: true});
							
							$rootScope.successNotification("Updated");
						}, 
						function(){
							$rootScope.errorNotification("Error");
						});
			},
			rowData: null,
	        pagination: true,
	        paginationPageSize: 50,
	        floatingFilter: true,
	        angularCompileRows: true,
	        components:{
		        numericCellEditor: NumericCellEditor,
		    },
	        onGridReady: function(params) {
	            params.api.sizeColumnsToFit();
	        },
		};

		$scope.isDeliveryTimeApplied = function(currentValuesRow) {
			let result = false;

			for (let i = 0; i < currentValuesRow.productType.products.length; i++) {
				result = result || (currentValuesRow.productType.products[i].deliveryTime > 0); //need to check this logic during the next call or meeting
			}
			return result;
		}
		
		//--amv--grid-related-stuff-end--
		
		//--acv--grid-related-stuff--
		
		$scope.isLackOfSuppliesSoon = function(currentValuesRow){
			var group;

			if(!$scope.amvGrouped || !$scope.amvGrouped[$scope.currentScheme.name]) {
				return false;
			}

			try{
				group = $scope.amvGrouped[$scope.currentScheme.name][$scope.groups[$scope.activeTabCV]];
			} catch (e){
				
				$timeout(function(){
					$scope.acvGridOptions.api.refreshCells({columns: ['quantity'], force: true});
				}, 100);
				
				return false;
			}
			
			if(!group){
				return false;
			}
			
			for (var i = 0; i < group.length; i++) {
				if(group[i].productType.id === currentValuesRow.productType.id){
					var fraction = (currentValuesRow.quantity - group[i].lowerValue) / group[i].lowerValue;
					return  fraction <= $scope.lowStockPercent && fraction >= 0
				}
			}
		}
		
		$scope.isLackOfSupplies = function(currentValuesRow){
			var group;

			if(!$scope.amvGrouped || !$scope.amvGrouped[$scope.currentScheme.name]) {
				return false;
			}
			try{
				group = $scope.amvGrouped[$scope.currentScheme.name][$scope.groups[$scope.activeTabCV]];
			} catch (e){
				
				$timeout(function(){
					$scope.acvGridOptions.api.refreshCells({columns: ['quantity'], force: true});
				}, 100);
				
				return false;
			}
			
			if(!group){
				return false;
			}
			
			for (var i = 0; i < group.length; i++) {
				if(group[i].productType.id === currentValuesRow.productType.id){
					return currentValuesRow.quantity / group[i].lowerValue  < 1 
				}
			}
		}
		
		
		var quantityCellRendererFunc = function(params){
			params.$scope.rowData = params.node.data;
			//data-container="body" data-toggle="popover" data-placement="top" data-trigger="hover" data-content="Vivamus sagittis lacus vel augue laoreet rutrum faucibus."

			if($scope.isLackOfSuppliesSoon(params.node.data)){
				return $scope.doTriangleWarning("You will have a lack of supplies soon") + params.node.data.quantity;
			} else if($scope.isLackOfSupplies(params.node.data)){
				return $scope.doCircleWarning("You have a lack of supplies!", "red") + params.node.data.quantity;
			} else {
				return params.node.data.quantity;
			}
		}
		
		$scope.openTransferModal = function(rowData){
			var orderValue = 0;
			
			for (var i = 0; i < $scope.amvGrouped[$scope.currentScheme.name][$scope.groups[$scope.activeTabCV]].length; i++) {
				if($scope.amvGrouped[$scope.currentScheme.name][$scope.groups[$scope.activeTabCV]][i].productType.id === rowData.productType.id){
					orderValue = $scope.amvGrouped[$scope.currentScheme.name][$scope.groups[$scope.activeTabCV]][i].orderValue;
				}
			}
			
			$scope.transferRequest = {
				productType: rowData.productType,
				transferAmount: orderValue,
			}
			
			$scope.distributionModalOptions.transferQuantityInvalid = false;
			
			$('#distributionModal').modal('show');
		}
		
		var createDistributionCellRendererFunc = function(params){
			//params.$scope.openTransferModal = $scope.openTransferModal;
			params.$scope.rowData = params.node.data
			 
			params.$scope.rowId = params.node.id;
			
			return '<button type="button" class="btn btn-info btn-sm float-right" style="height:25px; padding: 3px 8px;" ng-click="openTransferModal(rowData)"><i class="material-icons md-18">swap_horiz</i></button>';
		}
		
		var acvColumnDefs = [
	        {headerName: "Asset Name", sortable: true, field: "productType.fullName", filter: "agTextColumnFilter", width: 190},
	        {headerName: "Quantity", sortable: true, field: "quantity", editable: false, cellClass:'text-right', cellEditor: 'numericCellEditor', filter: "agNumberColumnFilter",
	        		cellRenderer: quantityCellRendererFunc ,width: 80},
	        {headerName: "Transfer", field: "", cellRenderer: createDistributionCellRendererFunc, width: 70, suppressNavigable: true,
	        		cellClass: 'no-border'},
	    ];
		
		$scope.acvGridOptions = {
			columnDefs: acvColumnDefs,
			onCellEditingStopped: function(event) {
				
				requestsQueueService.appendRequest('POST', projectRoot + "/msc-api/assets/assets-current-values", null, event.data, 
						function(){
							$scope.assetsMinValGridOptions.api.refreshCells({force: true});
							$scope.acvGridOptions.api.refreshCells({columns: ['quantity'], force: true});
						}, 
						function(){
							alert("Error in updating data");
						});
			},
			rowData: null,
		    pagination: true,
		    paginationPageSize: 50,
		    floatingFilter: true,
		    angularCompileRows: true,
		    components:{
		        numericCellEditor: NumericCellEditor,
		    },
		    onGridReady: function(params) {
		        params.api.sizeColumnsToFit();
		    },
		};
		
		//--acv--grid-related-stuff-end--
		
		$scope.opedDeleteTransferConfirmModal = function(deletable){
			$('#confirmDeleteTransferModal').modal('show');
			
			$scope.deletableTransfer = deletable;
		}
		
		$scope.deleteTransfer = function(){
			$http.delete(projectRoot + "/msc-api/assets/transfer-request/" + $scope.deletableTransfer.id).then(function(response){
				$scope.transferQueueBySchemes[$scope.currentScheme.name].splice($scope.transferQueueBySchemes[$scope.currentScheme.name].indexOf($scope.deletableTransfer), 1);
				
				$scope.transferQueueGridOptions.api.updateRowData({remove: [$scope.deletableTransfer]});
				
				$rootScope.successNotification("Transfer deleted");
			}, function(errorResponse){
				$rootScope.errorNotification("Error on transfer delete");
			});
		}
		
		
		//--transfer--grid-related-stuff--
		
		var deleteRowCellRendererFunc = function(params){
			
			params.$scope.rowId = params.node.id;
			params.$scope.deletable = params.node.data;
			
			return '<button type="button" class="btn btn-danger btn-sm float-right" style="height:25px; padding: 3px 8px;" data-ng-click="opedDeleteTransferConfirmModal(deletable)"><i class="material-icons md-18">clear</i></button>';
		}
		
		
		var transferQeueColumnDefs = [
	        {headerName: "Asset Name", field: "productType.fullName", filter: "agTextColumnFilter", width: 200},
	        {headerName: "Quantity", field: "quantity", editable: true, cellClass:'text-right', cellEditor: 'transferQuantityCellEditor',
	        	filter: "agNumberColumnFilter", width: 80},
	        {headerName: "Transfer from", field: "transferFromScheme.name", filter: "agTextColumnFilter", width: 150},
	        {headerName: "Delete", field: "", width: 70, cellRenderer: deleteRowCellRendererFunc, pinned: 'right'},
	    ];
		
		function TransferQuantityCellEditor() {
		}

		TransferQuantityCellEditor.prototype.init = function (params) {
		    // create the cell
		    this.eInput = document.createElement('input');
		    this.eInput.style.width = '100%';
		    this.eInput.style.height = 'calc(100% - 2px)';

		    $scope.editedTransferRow = params.data;
		    	
		    if (isCharNumeric(params.charPress)) {
		        this.eInput.value = params.charPress;
		    } else {
		        if (params.value !== undefined && params.value !== null) {
		            this.eInput.value = params.value;
		        }
		    }

		    var that = this;
		    this.eInput.addEventListener('keypress', function (event) {
		        if (!isKeyPressedNumeric(event)) {
		            that.eInput.focus();
		            if (event.preventDefault) event.preventDefault();
		        } else if (that.isKeyPressedNavigation(event)){
		            event.stopPropagation();
		        }
		    });

		    // only start edit if key pressed is a number, not a letter
		    var charPressIsNotANumber = params.charPress && ('1234567890'.indexOf(params.charPress) < 0);
		    this.cancelBeforeStart = charPressIsNotANumber;
		};

		TransferQuantityCellEditor.prototype.isKeyPressedNavigation = function (event){
		    return event.keyCode===39
		        || event.keyCode===37;
		};


		// gets called once when grid ready to insert the element
		TransferQuantityCellEditor.prototype.getGui = function () {
			return this.eInput;
		};

		// focus and select can be done after the gui is attached
		TransferQuantityCellEditor.prototype.afterGuiAttached = function () {
		    this.eInput.focus();
		};

		// returns the new value after editing
		TransferQuantityCellEditor.prototype.isCancelBeforeStart = function () {
		    return this.cancelBeforeStart;
		};

		
		// returns the new value after editing
		TransferQuantityCellEditor.prototype.getValue = function (params) {
			var quantity = $scope.getQuantityBySchemeAndProductType($scope.editedTransferRow.transferFromScheme, $scope.editedTransferRow.productType);
			if(quantity >= this.eInput.value){
				return this.eInput.value;
			} else {
				return $scope.editedTransferRow.quantity;
			}
		};


		
		$scope.transferQueueGridOptions = {
			columnDefs: transferQeueColumnDefs,
			onCellEditingStopped: function(event) {

				var request = {
					id: event.data.id,
					quantity: event.data.quantity
				}

				requestsQueueService.appendRequest('PUT', projectRoot + "/msc-api/assets/update-transfer-quantity", null, request, 
						function(){
							$scope.transferQueueGridOptions.api.refreshCells({columns: ['quantity'], force: true});
							
							$rootScope.successNotification("Updated");
						}, 
						function(){
							$rootScope.errorNotification("Error");
						});
			},
			rowData: null,
			floatingFilter: true,
			angularCompileRows: true,
			components:{
				transferQuantityCellEditor: TransferQuantityCellEditor,
		    },
			onGridReady: function(params) {
			   params.api.sizeColumnsToFit();
			},
		};
		
		//--transfer--grid-related-stuff-end--
		
		$scope.openClearQueueModal = function(){
			$('#confirmClearQueueModal').modal('show');
		}
		
		$scope.getClearQueueModalTitle = function(){
			if($scope.currentScheme){
				return "Clear transfer queue for " + $scope.currentScheme.name;
			} else {
				return "";
			}
		}
		
		$scope.clearQueue = function(){
			$http.delete(projectRoot + "/msc-api/assets/transfer-request/by-transfer-to-scheme/" + $scope.currentScheme.name).then(function(response){
				$scope.transferQueueBySchemes[$scope.currentScheme.name] = [];
				
				$scope.updateTransferQueueGrid();
				
				$rootScope.successNotification("Queue cleared");
			}, function(errorResponse){
				$rootScope.errorNotification("Error on queue clear");
			});
		}
		
		$scope.distributionModalOptions = {
			distributionModalTitle: function(){
				if($scope.currentScheme){
					return 'Transfer to ' + $scope.currentScheme.name;
				} else {
					return '';
				}
			},
			isFrozen: function(){
				return false;
			},
			confirmDisabled: function(){
				return $scope.transferRequest && !$scope.transferRequest.transferFrom;
			},
			transferQuantityInvalid: false
			
		}
		
		$scope.createDistribution = function(){
			if($scope.transferRequest.transferAmount <= 0 || 
					$scope.transferRequest.transferAmount > $scope.getQuantityBySchemeAndProductType($scope.transferRequest.transferFrom, $scope.transferRequest.productType)){
				
				$scope.distributionModalOptions.transferQuantityInvalid = true;
				return;
			}
			
			$scope.transferRequest.transferTo = $scope.currentScheme;
			
			$http.post(projectRoot + "/msc-api/assets/request-transfer", $scope.transferRequest).then(function(response){
				var savedATQ = response.data.saved;
				
				$scope.transferQueueBySchemes[$scope.currentScheme.name].push(savedATQ);
				$scope.updateTransferQueueGrid();
				
				$rootScope.successNotification("Transfer created");
				
				$('#distributionModal').modal('hide');
			}, function(errorResponse){
				$rootScope.errorNotificationWithMessage("Error on transfer create. " + (errorResponse.data.errorMessage ? errorResponse.data.errorMessage : ''));
				
				$scope.showAddTransferError = true;
				console.log("errorResponse", errorResponse);
				$scope.addTransferErrorMsg = errorResponse.data.errorMessage;
				
				$timeout(function(){
					$scope.showAddTransferError = false;
				}, 5000) //5 sec.
			});
		}
		
		$scope.checkQueueButtonsForDisabled = function(){
			return !$scope.transferQueueBySchemes || !$scope.transferQueueBySchemes[$scope.currentScheme.name] || $scope.transferQueueBySchemes[$scope.currentScheme.name].length === 0 || $scope.creatingDistribution
		}
		
		$scope.openCreateDistributionModal = function(){
			$('#confirmCreateDistributionModal').modal('show');
		}
		
		$scope.createRealDistribution = function(){//TODO тут, якщо фулфілмент оператор, то робити дистрибуцію тільки для фулфілмент товарів (воно вроді вже так працює)
			$scope.creatingDistribution = true;
			$http.post(projectRoot + "/msc-api/distribution/create/from-transfer-queue", $scope.transferQueueBySchemes[$scope.currentScheme.name]).then(function(response){
				$scope.transferQueueBySchemes[$scope.currentScheme.name] = [];
				$scope.updateTransferQueueGrid();
				$scope.acvGridOptions.api.setRowData(null);
				
				$rootScope.successNotification("Distribution created");
				
				loadACVData();
				$scope.creatingDistribution = false;
			}, function(errorResponse){
				$rootScope.errorNotification("Error on distribution create");
				
				$scope.creatingDistribution = false;
			});
		}
		
		loadData();
	}
	]);
