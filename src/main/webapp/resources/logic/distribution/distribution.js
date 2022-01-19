"use strict";

app.controller('distributionCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	'loadUserRolesService',
	function($scope, $http, $timeout, $rootScope, loadUserRolesService) {
		
		$scope.pageSizes = [10, 20, 50];
		$scope.pageSize = $scope.pageSizes[0];
		
		const PAGES_TO_SHOW = 8;
		
		var loadData = function(){
			
			$http.get(projectRoot + "/msc-api/top-data/states/distribution").then(function (response){
				$scope.states = response.data;
				$scope.states.splice(0, 0, {name: "All"});
				$scope.currentState = $scope.states[0];
			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/distribution/central-depot-scheme").then(function(response){
				$scope.centralDepotScheme = response.data;
			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/top-data/schemes").then(function(response){
				$scope.schemes = response.data;
				$scope.schemes.splice(0, 0, {name: "All"});
				//$scope.currentScheme = $scope.schemes[0];
			}, function(errorResponse){
				
			});
			
			$http.get(projectRoot + "/msc-api/user/user-scheme").then(function(response){
				$scope.userScheme = response.data;
				$scope.currentScheme = $scope.userScheme;
				
				loadUserRolesService.load($scope, function(){
					$http.get(projectRoot + "/msc-api/distribution/all").then(function(response){
						$scope.distributions = response.data;
						//$scope.distributionsByPages = arrayToPageable($scope.distributions, $scope.pageSize);
						
						//grouping products
						for (var i = 0; i < $scope.distributions.length; i++) {
							if($scope.userIsOnlyFulfillmentOperator){
								if($scope.distributions[i].assets[0].typeOfAssets.assetGroup.indexOf("Fulfillment") < 0){
									$scope.distributions.splice(i--, 1);
									continue;
								}
							}
							
							var ptg = {}
							
							//lets form a separate array of group names just for easy use
							$scope.distributions[i].assetGroups = [];

							for (var j = 0; j < $scope.distributions[i].assets.length; j++) {
								var asset = $scope.distributions[i].assets[j];
								var groupName = asset.typeOfAssets.assetGroup;
								
								if(!ptg.hasOwnProperty(groupName)){
									ptg[groupName] = [];
									
									$scope.distributions[i].assetGroups.push(groupName);
								}
								ptg[groupName].push(asset);
							}
							
							$scope.distributions[i].assetsGrouped = ptg;
						}

						$scope.updatePageable()

						// Settle default values for sorting buttons
						$scope.currentSort[$scope.schemes[0].name] = 2;
						for (i = 1; i < $scope.schemes.length; i++) {
							$scope.currentSort[$scope.schemes[i].name] = 2;
						}

					}, function(errorResponse){
						
					});
				}, function(){})
				
			}, function(errorResponse){
				
			});
		}
		
		$scope.canCloseDistribution = function(){
			return $scope.userRoles[0] === 1 || $scope.userRoles[2] === 1;
//			return $scope.userRoles[2] === 1;
		}
		
		$scope.canShipDistribution = function(){
			return $scope.userRoles[0] === 1 || $scope.userRoles[3] === 1 || $scope.userRoles[4] === 1;
//			return $scope.userRoles[2] === 1;
		}
		
		$scope.isUserASchemeLeader = function(){
			return $scope.userRoles[2] === 1;
		}
		
		function checkDistributionSchemeTo(distribution){
			return distribution.schemeTo.name === $scope.currentScheme.name || $scope.currentScheme.name === 'All'
		}
		
		function checkDistributionState(distribution){
			return distribution.state.id === $scope.currentState.id || $scope.currentState.id === undefined
		}
		
		$scope.needToShow = function(index, distribution){
			if($scope.pagesParameters){
				var pp =  $scope.pagesParameters;
				return (index < pp.currentPage * $scope.pageSize && index >= (pp.currentPage - 1) * $scope.pageSize)//pages part
						&& checkDistributionSchemeTo(distribution)
						&& checkDistributionState(distribution);
			} else {
				return false;
			}
		}
		
		$scope.updatePageable = function(){
			var count = 0;
			for (var i = 0; i < $scope.distributions.length; i++) {
				if(checkDistributionSchemeTo($scope.distributions[i]) && checkDistributionState($scope.distributions[i])){
					count++;
				}
			}
			
			$scope.pagesParameters = {
					currentPage: 1,
					pagesNumber: Math.ceil(count / $scope.pageSize),
					pagesToShow: PAGES_TO_SHOW
			}
		}
		
		$scope.setCurrentScheme = function(scheme){
			$scope.currentScheme = scheme;

			$scope.updatePageable();

			$scope.sortCurrentRequests($scope.currentSort[scheme.name]);
		}
		
		$scope.setCurrentState = function(state){
			$scope.currentState = state;
			
			$scope.updatePageable();
		}

		$scope.currentSort = {};
		$scope.sortCurrentRequests = function(rs){

			$scope.distributions.sort(function (a, b){
				var result = b.id - a.id;
				if(rs === 1){
					result *= -1;
				} else if(rs === 3){
					if(a.schemeTo.name < b.schemeTo.name) { result = -1; }
					if(a.schemeTo.name > b.schemeTo.name) { result = 1; }
				}

				return result;
			});

			$scope.currentSort[$scope.currentScheme.name] = rs;
		}
		
		$scope.getStateTextClass = function(state){
			switch(state.id){
			case 21: return "text-info"; break;
			case 22: return "text-danger"; break;
			case 23: return "text-success"; break;
			}
		}
		
		$scope.getStateButtonClass = function(state){
			switch(state.id){
			case undefined: return "btn-secondary"; break;
			case 21: return "btn-info"; break;
			case 22: return "btn-danger"; break;
			case 23: return "btn-success"; break;
			}
		}
		
		$scope.openCloseDistributionModal = function(distribution){
			$('#closeDistribution').modal('show');
			
			$scope.closableDistribution = distribution;
		};
		
		$scope.closeDistribution = function(){
			$scope.closingDistribution = true;
			$http.put(projectRoot + "/msc-api/distribution/" + $scope.closableDistribution.id + "/close-distribution").then(function(response){
				$scope.closableDistribution.state = response.data;
				$scope.closableDistribution.stateChangeDate = response.data.stateChangeDate;
				$scope.closableDistribution = undefined;
				$scope.closingDistribution = false;
				
				$rootScope.successNotification("Distribution closed");
			}, function(errorResponse){
				$scope.closableDistribution = undefined;
				$scope.closingDistribution = false;
				
				$rootScope.errorNotification("Error on distribution close");
			});
		}
		
		$scope.openShipDistributionModal = function(distribution){
			$('#shipDistribution').modal('show');
			
			$scope.distributionForShipping = distribution;
		};
		
		$scope.shipDistribution = function(){
			$scope.shippingDistribution = true;
			$http.put(projectRoot + "/msc-api/distribution/" + $scope.distributionForShipping.id + "/change-state/23").then(function(response){
				$scope.distributionForShipping.state = response.data;
				$scope.distributionForShipping.stateChangeDate = response.data.stateChangeDate;
				$scope.distributionForShipping = undefined;
				$scope.shippingDistribution = false;
				
				$rootScope.successNotification("Distribution shipped");
			}, function(errorResponse){
				$scope.distributionForShipping = undefined;
				$scope.shippingDistribution = false;
				
				$rootScope.errorNotification("Error on distribution ship");
			});
		}
		
		$scope.previewDistributionPDF = function(distribution){
			window.open(projectRoot + "/msc-api/distribution/get-distribution-pdf/" + distribution.id, "_blank");
		}
		
		loadData();
	}
]);
