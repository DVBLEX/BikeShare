"use strict";

app.controller('stockRequestsCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	function($scope, $http, $timeout, $rootScope) {
		
		$scope.pageSizes = [10, 20, 50];
		$scope.pageSize = $scope.pageSizes[0];
		
		const PAGES_TO_SHOW = 5;
		
		$scope.setCurrentScheme = function(scheme){
			$scope.currentScheme = scheme;	
			
			$scope.hasNewRequests = false;
			if($scope.requestsGrouped[$scope.currentScheme.name]){
				for (var i = 0; i < $scope.requestsGrouped[$scope.currentScheme.name].length; i++) {
					if($scope.requestsGrouped[$scope.currentScheme.name][i].state.id === 1){
						$scope.hasNewRequests = true;
					}
				}
			}
			
			$scope.updatePageable();
		}
		
		$scope.isUserOnlySchemeLeader = function(){
			return $scope.userIsOnlySchemeLeader;
		}
		
		var loadData = function(){
			$http.get(projectRoot + "/msc-api/user/user-roles").then(function(response){
				$scope.userRoles = response.data;
				
				if($scope.userRoles[0] === 1){
					$scope.userIsAdmin = true;
				} else if($scope.userRoles[2] === 1){
					$scope.userIsSchemeLeader = true;
					$scope.userIsOnlySchemeLeader = true;
				} else if($scope.userRoles[4] === 1){
					$scope.userIsFulfillmentOperator = true;
					$scope.userIsOnlyFulfillmentOperator = true;
				}
				
				
				for(var i = 0; i < $scope.userRoles.length; i++){
					if(i != 2 && $scope.userRoles[i] === 1){
						$scope.userIsOnlySchemeLeader = false;
					}
					if(i !== 4 && $scope.userRoles[i] === 1){
						$scope.userIsOnlyFulfillmentOperator = false;
					}
				}
				
				if($scope.isUserOnlySchemeLeader()){
					$http.get(projectRoot + "/msc-api/user/user-scheme").then(function(response){
						$scope.schemeOfUser = response.data;
						
						$scope.currentScheme = $scope.schemeOfUser;
					}, function(errorResponse){
						
					});
				}
				
				
				$http.get(projectRoot + "/msc-api/top-data/schemes").then(function(response){
					$scope.schemes = response.data;
					$scope.realSchemes = angular.copy(response.data);
					$scope.schemes.splice(0, 0, {name: "All"});//adding fake 'All' scheme
					
					$http.get(projectRoot + "/msc-api/distribution/stock-requests/scheme-sorted").then(function(response){
						$scope.requestsGrouped = response.data;
						
						if($scope.userIsOnlyFulfillmentOperator){
							for(var scheme in $scope.requestsGrouped){
								for (var i = 0; i < $scope.requestsGrouped[scheme].length; i++) {
									if(!$rootScope.belongsToFulfillment($scope.requestsGrouped[scheme][i].requestedProductTypes[0].productType.assetGroup)){
										$scope.requestsGrouped[scheme].splice(i--, 1);
										
										continue;
									}
								}
							}
						}

						//multiply array just for testing
//						$scope.requestsGrouped[$scope.currentScheme.name].push.apply($scope.requestsGrouped[$scope.currentScheme.name], angular.copy($scope.requestsGrouped[$scope.currentScheme.name]));
//						$scope.requestsGrouped[$scope.currentScheme.name].push.apply($scope.requestsGrouped[$scope.currentScheme.name], angular.copy($scope.requestsGrouped[$scope.currentScheme.name]));
//						$scope.requestsGrouped[$scope.currentScheme.name].push.apply($scope.requestsGrouped[$scope.currentScheme.name], angular.copy($scope.requestsGrouped[$scope.currentScheme.name]));
//						$scope.requestsGrouped[$scope.currentScheme.name].push.apply($scope.requestsGrouped[$scope.currentScheme.name], angular.copy($scope.requestsGrouped[$scope.currentScheme.name]));
						
						
						$http.get(projectRoot + "/msc-api/assets/central-depot/product-type-id-sorted").then(function(response){
							$scope.centralDepotProducts = response.data;

							$scope.isDistributionReady = {};
							
							//start from 1, because under index 0 there is fake 'All' scheme
							for (var i = 1; i < $scope.schemes.length; i++) {
								var requestsInScheme = $scope.requestsGrouped[$scope.schemes[i].name];
								
								
								if(!requestsInScheme){
									continue;
								}
								
								//iterate over requests in one scheme
								for (var j = 0; j < requestsInScheme.length; j++) {
									
									$scope.isDistributionReady[requestsInScheme[j].id] = true;
									
									var ptg = {}
									
									requestsInScheme[j].prodGroups = []
									
									//iterate over requested products and add a product to it's group
									for (var k = 0; k < requestsInScheme[j].requestedProductTypes.length; k++) {
										
										var reqPT = requestsInScheme[j].requestedProductTypes[k];
										
										var groupName = reqPT.productType.assetGroup;
										if(!ptg.hasOwnProperty(groupName)){
											ptg[groupName] = [];
											
											//lets form a separate array of group names for some easy use
											requestsInScheme[j].prodGroups.push(groupName);
										}
										ptg[groupName].push(reqPT);
										
										if(requestsInScheme[j].distribution){
											for (var z = 0; z < requestsInScheme[j].distribution.assets.length; z++) {
												if(requestsInScheme[j].distribution.assets[z].typeOfAssets.id == reqPT.productType.id){
													reqPT.distributed = requestsInScheme[j].distribution.assets[z];
												}
											}
										}
										
										if((!$scope.centralDepotProducts[reqPT.productType.id] || reqPT.orderValue > $scope.centralDepotProducts[reqPT.productType.id].amount) &&
												!requestsInScheme[j].distribution && requestsInScheme[j].state.id < 3){
											$scope.isDistributionReady[requestsInScheme[j].id] = false;
										}
									}
									
									requestsInScheme[j].reqProductTypesGrouped = ptg;
								}
															
							}
							
							//Add all requests to fake 'All' scheme
							$scope.requestsGrouped[$scope.schemes[0].name] = []
							$scope.currentSort[$scope.schemes[0].name] = 2;
							for (i = 1; i < $scope.schemes.length; i++) {
								$scope.currentSort[$scope.schemes[i].name] = 2;
								
								requestsInScheme = $scope.requestsGrouped[$scope.schemes[i].name];
								
								if(!requestsInScheme){
									continue;
								}
								
								for (j = 0; j < requestsInScheme.length; j++) {
									$scope.requestsGrouped[$scope.schemes[0].name].push(requestsInScheme[j]);
								}
							}
							
							$scope.requestsGrouped[$scope.schemes[0].name].sort(function (a, b){
								return b.id - a.id;
							})
							
							if(!$scope.schemeOfUser){
								$scope.setCurrentScheme($scope.schemes[0]);
							}
							
							$scope.updatePageable();
							
						}, function(errorResponse){
							
						})

					}, function(errorResponse){
						
					})
					
				}, function(errorResponse){
					
				});
				
				
			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/top-data/states/stock-requests").then(function (response){
				$scope.states = response.data;
				$scope.states.splice(2, 1);//removing 'Distributed' state
				$scope.states.splice(0, 0, {name: "All"});
				$scope.currentState = $scope.states[0];
			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/assets/groups-of-types").then(function(response){
				$scope.groups = response.data;

				loadAllTypesOfAssets();

			}, function(errorResponse){
				
			});
			
			
		}
		
		var loadAllTypesOfAssets = function(){
			$http.get(projectRoot + "/msc-api/assets/types-of-assets").then(function(response){
				$scope.allTypesOfAssets = response.data;

				$scope.typesOfAssetsGrouped = {};

				for (const group of $scope.groups) {
					let types = [];

					for(const type of $scope.allTypesOfAssets) {
						if(type.assetGroup === group) {
							types.push(type);
						}
					}

					$scope.typesOfAssetsGrouped[group] = types;
				}

			}, function(errorResponse){
				
			})
		}
		

		
		$scope.checkForDistributionReady = function(){
			for (var i = 0; i < $scope.requestsGrouped[$scope.currentScheme.name].length; i++) {
				for (var k = 0; k < $scope.requestsGrouped[$scope.currentScheme.name][i].requestedProductTypes.length; k++) {
					
					var reqPT = $scope.requestsGrouped[$scope.currentScheme.name][i].requestedProductTypes[k];

					if(!$scope.centralDepotProducts[reqPT.productType.id] || reqPT.orderValue > $scope.centralDepotProducts[reqPT.productType.id].amount){
						$scope.isDistributionReady[$scope.requestsGrouped[$scope.currentScheme.name][i].id] = false;
					}
				}
			}
			
		}
		
		$scope.currentSort = {};
		$scope.sortCurrentRequests = function(rs){
			if(!$scope.requestsGrouped[$scope.currentScheme.name]){
				return;
			}
			
			$scope.requestsGrouped[$scope.currentScheme.name].sort(function (a, b){
				var result = b.id - a.id;
				if(rs === 1){
					result *= -1;
				} else if(rs === 3){
					if(a.scheme.name < b.scheme.name) { result = -1; }
				    if(a.scheme.name > b.scheme.name) { result = 1; }
				}
				
				return result;
			});
			
			$scope.currentSort[$scope.currentScheme.name] = rs;
		}
		
		function checkStockRequestState(sr){
			return sr.state.id === $scope.currentState.id || $scope.currentState.id === undefined
		}
		
		$scope.needToShow = function(index, sr){
			if($scope.pagesParameters){
				return (index < $scope.pagesParameters.currentPage * $scope.pageSize && index >= ($scope.pagesParameters.currentPage - 1) * $scope.pageSize) 
					&& checkStockRequestState(sr);
			} else {
				return false;
			}
		}
		
		$scope.setCurrentState = function(state){
			$scope.currentState = state;
			
			$scope.updatePageable();
		}
		
		$scope.updatePageable = function(){

			if (!!$scope.requestsGrouped[$scope.currentScheme.name]) {
				var count = 0;
				for (var i = 0; i < $scope.requestsGrouped[$scope.currentScheme.name].length; i++) {
					if(checkStockRequestState($scope.requestsGrouped[$scope.currentScheme.name][i])){
						count++;
					}
				}
			}
			
			$scope.pagesParameters = {
					currentPage: 1,
					pagesNumber: Math.ceil(count / $scope.pageSize),
					pagesToShow: PAGES_TO_SHOW
			}
		}
		
		$scope.getStateTextClass = function(state){
			switch (state.id) {
			case 1:
				return "text-success"
				break;
				
			case 2:
				return "text-warning"
				break;
				
			case 3:
				return "text-info"
				break;
				
			case 4:
				return "text-danger"
				break;

			default:
				break;
			}
		}
		
		$scope.getStateButtonClass = function(state){
			switch(state.id){
			case undefined: return "btn-secondary"; break;
			case 1: return "btn-success"; break;
			case 2: return "btn-warning"; break;
			case 3: return "btn-info"; break;
			case 4: return "btn-danger"; break;
			}
		}
		
		$scope.addNewRequest = function(){
			$scope.newStockRequest = {
				//requestedProductTypes: [{}],
				requestedGroups: [{types: []}],
				manual: true,
			};
			
			if($scope.schemeOfUser){
				$scope.newStockRequest.scheme = $scope.schemeOfUser;
			}
		}
		
		$scope.addGroupToNewRequest = function(){
			$scope.newStockRequest.requestedGroups.push({types: [], stockRequestProductSelectApis: []});
		}
		
		$scope.removeGroupFromNewRequest = function(groupObj){
			$scope.newStockRequest.requestedGroups.splice($scope.newStockRequest.requestedGroups.indexOf(groupObj), 1);
		}
		
		$scope.onGroupChange = function(groupObj){
			groupObj.types = [];
			groupObj.stockRequestProductSelectApis = [];
		}
		
		$scope.deleteNewRequest = function(){
			$scope.newStockRequest = undefined;
		}
		
		$scope.openSaveNewRequestConfirmWindow = function(){
			$('#saveNewRequestConfirmModal').modal('show');
		}

		$scope.setProduct = function(product, rpt) {
			rpt.productType = product;
		}

		$scope.saveNewRequest = function(){
			var validationError = false;
			
			$scope.newStockRequest.requestedProductTypes = [];
			for (var i = 0; i < $scope.newStockRequest.requestedGroups.length; i++) {
				if($scope.newStockRequest.requestedGroups[i].types.length === 0){
					$scope.newStockRequest.requestedGroups[i].noTypes = true;
					
					validationError = true;
				}
				
				for (var j = 0; j < $scope.newStockRequest.requestedGroups[i].types.length; j++) {
					$scope.newStockRequest.requestedProductTypes.push($scope.newStockRequest.requestedGroups[i].types[j]);
				}
			}
			
			var requestedTypes = $scope.newStockRequest.requestedProductTypes;//for convenience

			if(!$scope.newStockRequest.scheme){
				$scope.newStockRequest.noScheme = true;
				
				validationError = true;
			}
			
			for(i = 0; i < requestedTypes.length; i++){
				//checking for duplicates
				for(j = 0; j < requestedTypes.length; j++){
					if(requestedTypes[i] !== requestedTypes[j] && requestedTypes[i].productType && requestedTypes[j].productType && requestedTypes[i].productType.id === requestedTypes[j].productType.id){
						requestedTypes[i].orderValue += requestedTypes[j].orderValue;
						requestedTypes.splice(j--, 1);
					}
				}

				if(!requestedTypes[i].productType){
					requestedTypes[i].hasProductType = false;

					validationError = true;
				}
				if(!requestedTypes[i].orderValue){
					requestedTypes[i].noOrderValue = true;
					
					validationError = true;
				}
			}
			
			if(validationError) {
				return;
			}
			
			if(!$scope.newStockRequest.notes){
				$scope.newStockRequest.notes = "";
			}
			
			$scope.saveRequest($scope.newStockRequest,
				function(){
					$scope.newStockRequest = undefined;
				}
			);
			
		}
		
		$scope.onNewRequestChange = function(){
			if($scope.newStockRequest.scheme){
				$scope.newStockRequest.noScheme = false;
			}
		}
		
		$scope.onRequestedTypeChange = function(rpt){
			if(rpt.productType){
				rpt.hasProductType = true;
			}
			if(rpt.orderValue){
				rpt.noOrderValue = false;
			}
		}
		
		$scope.removeAssetFromNewRequest = function(rpt, groupObj){
			groupObj.types.splice(groupObj.types.indexOf(rpt), 1);
			groupObj.stockRequestProductSelectApis.splice(groupObj.types.indexOf(rpt), 1);
		}
		
		$scope.addAssetToNewRequest = function(groupObj){
			groupObj.noTypes = false
			groupObj.types.push({hasProductType: true});
			groupObj.stockRequestProductSelectApis.push({});
		}
		
//		$scope.addNewStockRequest = function(){
//			
//			//$('#addStockRequestModal').modal('hide');
//		}
		
		
		$scope.askToMergeNewRequests = function(request){
			$('#mergeAllNewRequestsConfirmModal').modal('show');
			
			$scope.singleRequestForMerge = request;
		}
		
		$scope.mergeAllNewRequests = function(){
			if(!$scope.singleRequestForMerge){
				var newRequests = [];
				for (var i = 0; i < $scope.requestsGrouped[$scope.currentScheme.name].length; i++) {
					if($scope.requestsGrouped[$scope.currentScheme.name][i].state.id === 1){
//						if(!$scope.userIsOnlyFulfillmentOperator){
//							
//						} else {
//							
//						}
						newRequests.push($scope.requestsGrouped[$scope.currentScheme.name][i]);
					}
				}

				mergeRequests(newRequests);
			} else {
				mergeRequests([$scope.singleRequestForMerge]);
				//$scope.singleRequestForMerge = undefined;
			}
		}
		
		var mergeRequests = function (requests){//TODO якщо фулфілмент, то мерджити тільки фулфілмент реквести
			$scope.merging = true;
			
			$http.post(projectRoot + "/msc-api/distribution/stock-requests/merge-requests", requests).then(function(response){
				$scope.merging = false;
				for (var i = 0; i < requests.length; i++) {
					requests[i].state = $scope.states[2]; //state with id 2
					requests[i].stateChangeDate = response.data.datetime;
				}
				$scope.singleRequestForMerge = undefined;
				
				$rootScope.successNotification("Merged");
			}, function(errorResponse){
				$rootScope.errorNotification("Error on merge");
				
				$scope.merging = false;
			});
		}
		
		$scope.distributionModalOptions = {
			distributionModalTitle: function(){
				if($scope.transferForStockRequest){
					return 'Transfer to ' + $scope.transferForStockRequest.scheme.name;
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
		
		$scope.openTransferModal = function(requestedType, stockRequest){
			
			$scope.transferForStockRequest = stockRequest;
			
			//loading here marginal values (for product type and scheme) and list of current values (for product type in each scheme)
			$http.put(projectRoot + "/msc-api/assets/assets-margin-values/get-by-complex-id", 
					{productTypeId: requestedType.productType.id, schemeName: $scope.transferForStockRequest.scheme.name}).then(function(response){
						$scope.currentAMV = response.data;
						
						$http.get(projectRoot + "/msc-api/assets/assets-current-values/" + requestedType.productType.id).then(function(response){
								var temp = response.data;
								
								$scope.currentACVs = {};
								for (var i = 0; i < temp.length; i++) {
									$scope.currentACVs[temp[i].scheme.name] = temp[i];
								}
								
								$scope.transferRequest = {
										productType: requestedType.productType,
									}
								
								if($scope.currentACVs){
									$scope.transferRequest.transferAmount = $scope.currentAMV.orderValue;
								} else {
									$scope.transferRequest.transferAmount = 0;
								}
									
								$scope.distributionModalOptions.transferQuantityInvalid = false;
								
							}, function(errorResponse){
								
							});
						
					}, function(errorResponse){
						
					});
			
			$('#distributionModal').modal('show');
		}
		
		//create transfer
		$scope.createDistribution = function(){
			if($scope.transferRequest.transferAmount <= 0 || 
					$scope.transferRequest.transferAmount > $scope.currentACVs[$scope.transferRequest.transferFrom.name].quantity){
				
				$scope.distributionModalOptions.transferQuantityInvalid = true;
				return;
			}
			
			$scope.transferRequest.transferTo = $scope.transferForStockRequest.scheme;
			$scope.transferRequest.fromStockRequest = true;
			
			$http.post(projectRoot + "/msc-api/assets/request-transfer", $scope.transferRequest).then(function(response){
				var savedATQ = response.data.saved;

				$('#distributionModal').modal('hide');
				
				$rootScope.successNotification("Transfer created");
			}, function(errorResponse){

				$rootScope.errorNotification("Error on transfer create. " + (errorResponse.data.errorMessage ? errorResponse.data.errorMessage : ''));

				console.log(errorResponse);
				
			});
		}
		
		$scope.canCreateDistribution = function(request){
			if(!$scope.centralDepotProducts){
				return false;
			}
			for (var i = 0; i < request.requestedProductTypes.length; i++) {
				//need to check, if there are at least one asset in a request, that is on the central depot and can be delivered
				if($scope.centralDepotProducts[request.requestedProductTypes[i].productType.id] && $scope.centralDepotProducts[request.requestedProductTypes[i].productType.id].amount > 0){
					return true;
				}
			}
			
			return false;
		}
		
		$scope.createRealDistribtionModal = function(request){
			$scope.requestForDistribution = request;
			
			$('#createRealDistribution').modal('show');
		}
		
		$scope.saveRequest = function(savableRequest, onSaveSuccess, onSaveFailure){
			$http.post(projectRoot + "/msc-api/distribution/stock-requests", savableRequest).then(function(response){//TODO тут додати створені реквести
				
				for(var i = 0; i < response.data.length; i++){
					var ptg = {}
					
					var request = response.data[i];
					
					request.prodGroups = []
					
					//iterate over requested products and add a product to it's group
					for (var k = 0; k < request.requestedProductTypes.length; k++) {
						
						var reqPT = request.requestedProductTypes[k];
						
						var groupName = reqPT.productType.assetGroup;
						if(!ptg.hasOwnProperty(groupName)){
							ptg[groupName] = [];
							
							//lets form a separate array of group names for some easy use
							request.prodGroups.push(groupName);
						}
						ptg[groupName].push(reqPT);
						
						if(request.distribution){
							for (var z = 0; z < request.distribution.assets.length; z++) {
								if(request.distribution.assets[z].typeOfAssets.id == reqPT.productType.id){
									reqPT.distributed = request.distribution.assets[z];
								}
							}
						}
						
						
						$scope.isDistributionReady[request.id] = $scope.centralDepotProducts[reqPT.productType.id] && reqPT.orderValue <= $scope.centralDepotProducts[reqPT.productType.id].amount;
					}
					
					request.reqProductTypesGrouped = ptg;
					
					
					$scope.requestsGrouped[request.scheme.name].splice(0, 0, request);
					$scope.requestsGrouped[$scope.schemes[0].name].splice(0, 0, request);//Add new request to fake 'All' scheme
				}
				
				
				
				$rootScope.successNotification("Saved");
				
				$('#createRealDistribution').modal('hide');
				
				if(typeof onSaveSuccess === 'function'){
					onSaveSuccess();
				}
			}, function(errorResponse){
				
				$rootScope.errorNotification("Error on save new request. " + (errorResponse.data.errorMessage ? errorResponse.data.errorMessage : ''));
				
				$('#createRealDistribution').modal('hide');
				
				if(typeof onSaveFailure === 'function'){
					onSaveFailure();
				}
			});
		}
		
		$scope.createRealDistribution = function(){
			if($scope.creatingDistribution){
				return;
			}
			
			$scope.creatingDistribution = true;
			$http.post(projectRoot + "/msc-api/distribution/create/from-stock-request", $scope.requestForDistribution).then(function(response){
				$scope.requestForDistribution.distribution = response.data;
				
				var newStockRequest = {
						requestedProductTypes: [],
						scheme: $scope.requestForDistribution.scheme,
						state: {id: 1, name: "New", type: 1},
				}
				
				for (var i = 0; i < $scope.requestForDistribution.requestedProductTypes.length; i++) {
					for (var z = 0; z < $scope.requestForDistribution.distribution.assets.length; z++) {
						if($scope.requestForDistribution.distribution.assets[z].typeOfAssets.id == $scope.requestForDistribution.requestedProductTypes[i].productType.id){
							$scope.requestForDistribution.requestedProductTypes[i].distributed = $scope.requestForDistribution.distribution.assets[z];
							if($scope.requestForDistribution.requestedProductTypes[i].orderValue > $scope.centralDepotProducts[$scope.requestForDistribution.requestedProductTypes[i].productType.id].amount){
								newStockRequest.requestedProductTypes.push({
									productType: $scope.requestForDistribution.requestedProductTypes[i].productType,
									orderValue: $scope.requestForDistribution.requestedProductTypes[i].orderValue - $scope.centralDepotProducts[$scope.requestForDistribution.requestedProductTypes[i].productType.id].amount
								})
							}
							$scope.centralDepotProducts[$scope.requestForDistribution.requestedProductTypes[i].productType.id].amount -= $scope.requestForDistribution.distribution.assets[z].quantity;
						}
					}
				}
				
				if(newStockRequest.requestedProductTypes.length > 0 && $scope.requestForDistribution.state.id === 2){
					$scope.saveRequest(newStockRequest, 
						function(){
							$scope.creatingDistribution = false;
							
							$rootScope.successNotification("Distribution created");
						},
						function(){
							$rootScope.successNotification("Error on distribution create");
							
							$scope.creatingDistribution = false;
						}
					);
				}
				
				$scope.checkForDistributionReady();
				
			}, function(errorResponse){
				$scope.creatingDistribution = false;
				$('#createRealDistribution').modal('hide');
			});
		}
		
		loadData();

		$scope.returnTrue = function() {
			return true;
		}

		$scope.returnFalse = function() {
			return false;
		}
	}
	]);
