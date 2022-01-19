"use strict";

app.controller('assetsValuesCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$q',
	function($scope, $http, $timeout, $q) {
		
		$http.get(projectRoot + "/msc-api/top-data/schemes").then(function(response){
			$scope.schemes = response.data;
		}, function(errorResponse){
			
		});
		
		$http.get(projectRoot + "/msc-api/assets/types-of-assets").then(function(response){
			$scope.typesOfAssets = response.data;
		}, function(errorResponse){
			
		});
		
		$scope.amv = {}

		$scope.setAmvAsset = function(asset) {
			$scope.amv.productType = asset;
		}
		
		$scope.addAmv = function(){
			
			$http.post(projectRoot + "/msc-api/assets/assets-margin-values", $scope.amv).then(function(response){
				alert("Success!");
				
				$scope.amv = {};
			}, function(errorResponse){
				alert("Error! " + errorResponse.data.message);
			})
		}
		
		
		$scope.acv = {}

		$scope.setAcvAsset = function(asset) {
			$scope.acv.productType = asset;
		}
		
		$scope.addAcv = function(){
			
			$http.post(projectRoot + "/msc-api/assets/assets-current-values", $scope.acv).then(function(response){
				alert("Success!");
				
				$scope.acv = {};
			}, function(errorResponse){
				alert("Error! " + errorResponse.data.message);
			})
		}
	}
	]);
