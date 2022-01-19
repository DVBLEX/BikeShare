"use strict";

app.controller('passwordRecoveryCtrl', [
		'$scope',
		'$http',
		'$timeout',
		function($scope, $http, $timeout) {
			
			var splitURL = window.location.href.split("/");			
			
			$scope.requestBody = {
				token: splitURL[splitURL.length - 1]
			}
			
			$scope.confirmPassword = "";
			
			$scope.sendRecoverRequest = function(){
				$http.put(projectRoot + "/msc-api/user/recover-password", $scope.requestBody).then(function(response){
					window.open(projectRoot + "/login", "_self");
				}, function(errorResponse){
					
				});
			}
		}
	]
);