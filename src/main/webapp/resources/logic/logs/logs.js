"use strict";

app.controller('logsCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$q',
	'$rootScope',
	function($scope, $http, $timeout, $q, $rootScope) {
		
		function correctLogsData(logsData){
			
			var objectToString = function(obj, hideBrackets){
				var temp = "";
				if(Array.isArray(obj)){
					for(var k in obj){
						temp += (typeof obj[k] === 'object'? objectToString(obj[k]) : obj[k]) + '; ';//here should not be infinite recursion, because it is a valid JSON string
					}
					
					return "[" + temp + "]";
				} else {
					for(k in obj){
						temp += k + ': ' + (typeof obj[k] === 'object'? objectToString(obj[k]) : obj[k]) + '; ';//here should not be infinite recursion, because it is a valid JSON string
					}
				}
				
				if(!hideBrackets){
					return "{" + temp + "}";
				} else {
					return temp;
				}
			}
			
			for (var i = 0; i < logsData.length; i++) {
				var dataObject = JSON.parse(logsData[i].dataObject);
				
				
				switch($scope.logsData[i].id){
				case 18002:{//placeholder in case we need some special representation of log
					
				}
				break;
				default:{
					if(dataObject.value != null || dataObject.value != undefined){
						 dataObject = dataObject.value

					} else if(dataObject.newValue || dataObject.oldValue){
						if(dataObject.newValue && !dataObject.oldValue){
							dataObject = objectToString(dataObject.newValue.value);

						} else if(!dataObject.newValue && dataObject.oldValue){
							dataObject = objectToString(dataObject.oldValue.value);

						} else if(dataObject.newValue && dataObject.oldValue){
							var string = "";
							for(var key in dataObject.newValue.value){
								if(dataObject.newValue.value[key] != dataObject.oldValue.value[key]){
									var oldValue = dataObject.oldValue.value[key];
									var newValue = dataObject.newValue.value[key];

									if(typeof oldValue === 'object'){
										oldValue = objectToString(oldValue);
									}
									
									if(typeof newValue === 'object'){
										newValue = objectToString(newValue);
									}
									
									string += key + ": " + oldValue  + " -> " + newValue + " | \n";
								}
							}
							
							string = string.substring(0, string.length - 3);
							
//							for(var key in dataObject.oldValue.value){
//								if(dataObject.newValue.value[key] != dataObject.oldValue.value[key]){
//									console.log("new " + key, dataObject.newValue.value[key]);
//									console.log("old " + key, dataObject.oldValue.value[key]);
//									string += key + ": " + dataObject.oldValue.value[key] + " -> " + dataObject.newValue.value[key] + " |\n ";
//								}
//							}
							
							for(key in dataObject){
								if(key !== 'oldValue' && key !== 'newValue'){
									break;
								}
							}
							
							dataObject = key + ": " + dataObject[key] + "; \n" + string;
						}
					} else {
						dataObject = objectToString(dataObject, true);
					}
				}
				}
				
				
				logsData[i].dataObject = dataObject;
			}
			
			return logsData;
		}
		
		var loadData = function(){
			$http.get(projectRoot + "/msc-api/user/all").then(function(response){
				$scope.allUsers = response.data;
								
				$http.get(projectRoot + "/msc-api/logs/action-groups").then(function(response){
					$scope.actionGroups = response.data;
				}, function(errorResponse){
					$rootScope.errorNotification("Error on logs data load")
				})
				
			}, function(errorResponse){
				$rootScope.errorNotification("Error on users data load")
			})
			$http.get(projectRoot + "/msc-api/logs").then(function(response){
				$scope.logsData = response.data;
				
				correctLogsData($scope.logsData);
				
				$scope.logsGridOptions.api.setRowData($scope.logsData);
			}, function(errorResponse){
				$rootScope.errorNotification("Error on logs data load")
			});
		}
		
		
		$scope.onDateChange = function(){
			$scope.dateRange = "";
			
			if($scope.startDate){
				var s = $scope.startDate.split("/")
				$scope.endMinDate = s[1] + "." + s[0] + "." + s[2];
			} else {
				$scope.endMinDate = "";
			}
			
			if($scope.endDate){
				s = $scope.endDate.split("/")
				$scope.startMaxDate = s[1] + "." + s[0] + "." + s[2];
			} else {
				$scope.startMaxDate = "";
			}
			
		}
		
		//--grid-related-stuff--
		
		$scope.openShowInfoModal = function(rowData){
			$scope.currentShowingLog = rowData;
			
			$('#logShowModal').modal('show');
		}
		
		var showBodyCellRendererFunc = function(params){
			//params.$scope.openTransferModal = $scope.openTransferModal;
			params.$scope.rowData = params.node.data
			 
			params.$scope.rowId = params.node.id;
			
			return '<button type="button" class="btn btn-info btn-sm float-right" style="height:25px; padding: 3px 8px;" ng-click="openShowInfoModal(rowData)"><i class="material-icons md-18">calendar_view_day</i></button>';
		}
		
		var columnDefs = [
			{headerName: "Time", field: "timeStamp", filter: "agTextColumnFilter", width: 150},
			{headerName: "User", field: "userEmail", filter: "agTextColumnFilter", width: 140},
			{headerName: "Action", field: "operation.actionName", filter: "agTextColumnFilter", width: 150},
			{headerName: "Changes", field: "dataObject", filter: "agTextColumnFilter", width: 300},
			{headerName: "Full Log", field: "", cellRenderer: showBodyCellRendererFunc, width: 70, suppressNavigable: true,
        		cellClass: 'no-border', pinned: 'right'},
	    ];
		
		$scope.textAreaMinHeight = function(text){
			return ($scope.textAreaRowsNumber(text) * 26) + "px";

		}
		
		$scope.textAreaRowsNumber = function(text){
			if(!text){
				return 3;
			}
			return Math.ceil(text.length/85);
		}
		
		$scope.logsGridOptions = {
			defaultColDef: {
			    resizable: true
			},
		    columnDefs: columnDefs,
		    rowData: null,
		    pagination: true,
		    paginationPageSize: 40,
		    floatingFilter: true,
		    angularCompileRows: true
		};
		
		//--grid-related-stuff--end--
		
		$scope.onDateRangeChange = function(){
			var dr = Number($scope.dateRange);
			
			var date = new Date();
			
			var dd = date.getDate() < 10 ? ("0" + date.getDate()) : (date.getDate() + "");
			
			var mm = date.getMonth() + 1;
			mm = mm < 10 ? ("0" + mm) : (mm + "");
			
			$scope.endDate = dd + "/" + mm + "/" + date.getFullYear();
			
			switch(dr){
			case 1:{
 				$scope.startDate = $scope.endDate;
			}
			break;
			case 2:{ 							
 				date.setDate(date.getDate() - 7);
 				
 				dd = date.getDate() < 10 ? ("0" + date.getDate()) : (date.getDate() + "");
 				
 				mm = date.getMonth() + 1;
 				mm = mm < 10 ? ("0" + mm) : (mm + "");
 				
 				$scope.startDate = dd + "/" + mm + "/" + date.getFullYear();
			}
			break;
			case 3:{ 				
 				date.setDate(date.getDate() - 30);
 				
 				dd = date.getDate() < 10 ? ("0" + date.getDate()) : (date.getDate() + "");
 				
 				mm = date.getMonth() + 1;
 				mm = mm < 10 ? ("0" + mm) : (mm + "");
 				
 				$scope.startDate = dd + "/" + mm + "/" + date.getFullYear();
			}
			break;
			}
		}
		
		$scope.onUserSelect = function(){
			//console.log("users for search", $scope.usersForSearch);
		}
		
		$scope.onGroupSelect = function(){
			//console.log("groups for search", $scope.groupsForSearch);
		}
		
		$scope.isFrozen = function(){
			return false;
		}
		
		$scope.usersForSearch = [];
		$scope.groupsForSearch = [];
		
		$scope.clearSelection = function(){
			$scope.usersForSearch = [];
			$scope.groupsForSearch = [];
			
			$scope.dateRange = "";
			$scope.startDate = "";
			$scope.endDate = "";			
			
			$scope.orderNumber = undefined;
		}
		
		$scope.search = function(){
			var request = {
				userEmails: $scope.usersForSearch,
				groups: $scope.groupsForSearch,
				orderNumber: $scope.orderNumber
			}
			if($scope.startDate){
				var s = $scope.startDate.split("/");
				request.startDateMilis = (new Date(s[1] + "." + s[0] + "." + s[2])).getTime();
			}
			if($scope.endDate){
				s = $scope.endDate.split("/");
				
				var date = new Date(s[1] + "." + s[0] + "." + s[2]);
				date.setDate(date.getDate() + 1);
				
				request.endDateMilis = (date).getTime();
			}
			
			
			
			
			$scope.searching = true;
			
			$http.put(projectRoot + "/msc-api/logs", request).then(function(response){
				$scope.logsData = response.data;
				
				correctLogsData($scope.logsData);
				
				$scope.logsGridOptions.api.setRowData($scope.logsData);
				
				$rootScope.successNotification("Logs loaded");
				
				$scope.searching = false;
			}, function(errorResponse){
				$scope.searching = false;
				$rootScope.errorNotification("Error on logs data load")
			});
		}
		
		$scope.hideLogShowModal = function(){
			$('#logShowModal').modal('hide');
		}
		
		loadData();
	}]
);
