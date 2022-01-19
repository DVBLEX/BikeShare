"use strict";

app.controller('assetsReportCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	function($scope, $http, $timeout, $rootScope) {
		
		var loadData = function(){
			$http.get(projectRoot + "/msc-api/top-data/schemes").then(function(response){
				$scope.schemes = response.data;
				
				$scope.currentScheme = null;
				
			}, function(errorResponse){
				
			});
		}
		
		$scope.loadACVs = function(){
			
			$scope.acvs = undefined;
			$http.get(projectRoot + "/msc-api/assets/assets-current-values/by-scheme/" + $scope.currentScheme.name).then(function(response){
				$scope.acvs = response.data;
				
				$scope.usedACVs = [{}];
			}, function(errorResponse){
				
			})
		}
		
		loadData();
		
		
		$scope.openSavedAssetsInfoModal = function() {
			$('#savedAssetsInfoModal').modal('show');
		};
		
		$scope.savedAssetsInfo = function(){
			
			$scope.updating = true;
			
			var updateRequest = [];
			
			for (var i = 0; i < $scope.usedACVs.length; i++) {
				updateRequest.push({
					productTypeId: $scope.usedACVs[i].acv.productType.id,
					schemeName: $scope.usedACVs[i].acv.scheme.name,
					minusQuantity: $scope.usedACVs[i].usedAmount
				})
				
				if(updateRequest[updateRequest.length - 1].minusQuantity >  $scope.usedACVs[i].acv.quantity){
					updateRequest[updateRequest.length - 1].minusQuantity = $scope.usedACVs[i].acv.quantity;
				}
			}
			
			$http.put(projectRoot + "/msc-api/assets/assets-current-values/reduce-values", updateRequest).then(function(response){
				$scope.usedACVs = [{}];
				
				$scope.updating = false;
				
				$rootScope.successNotification("Reported");
			}, function(errorResponse){
				$rootScope.errorNotification("Error on report create");
				
				$scope.updating = false;
			});
		}
		
		$scope.validateUsedACVs = function(){
			if(!$scope.usedACVs || $scope.usedACVs.length === 0){
				return false;
			}
			
			for (var i = 0; i < $scope.usedACVs.length; i++) {
				if(!$scope.usedACVs[i].acv || !$scope.usedACVs[i].usedAmount || $scope.usedACVs[i].usedAmount < 0){
					return false;
				}
			}
			
			return true;
		}
		
		$scope.addNewUsedACV = function(){
			$scope.usedACVs.push({});
		}
		
		$scope.removeUsedACV = function(index){
			$scope.usedACVs.splice(index, 1);
		}
	}
]);
