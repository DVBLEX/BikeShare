"use strict";

app.controller('loginPageCtrl', [
		'$scope',
		'$http',
		'$timeout',
		'$rootScope',
		'Notification',
		function($scope, $http, $timeout, $rootScope, Notification) {
			
			$scope.signInRequest = {};
			
			
			$scope.signIn = function(){
				$scope.loggingIn = true;
				
				$scope.showWrongCredentialsMessage = false;
				
				$http.put(projectRoot + "/login", $scope.signInRequest).then(function(response){

					if(response.data.passwordRecoveryToken){
						$('#infoModal').modal('show');
						$scope.recoveryToken = response.data.passwordRecoveryToken;

					} else {
						window.open(projectRoot + "/views/home", "_self");
					}
					
					$scope.loggingIn = false;
				}, function(errorResponse){

					console.log('error resp', errorResponse);
					
					$scope.loggingIn = false;
					
					if(errorResponse.status === 511) {
						$scope.showWrongCredentialsMessage = true;
					}
				})
			}
			
			$scope.redirectToChangePassword = function(){
				window.open(projectRoot + "/views/users/password-recovery/" + $scope.recoveryToken, "_self");
				$scope.loggingIn = true;
			}
			
			$scope.showSuccessDropMsg = function(){
				$scope.showSuccessDropMsgVar = true;
				
				$timeout(function(){
					$scope.showSuccessDropMsgVar = false;
				}, 3000);
			}
			
			$scope.showErrorMsg = function(message){
				$scope.errorMessage = message;
				$scope.showErrorMsgVar = true;
				
				$timeout(function(){
					$scope.showErrorMsgVar = false;
				}, 3000);
			}
			
			$scope.askToDropPassword = function(){
				$('#dropPasswordModal').modal('show');
			}
			
			$scope.dropPassword = function(){
				$scope.droppingPass = true;

				$http.put(projectRoot + "/msc-api/user/drop-password", $scope.signInRequest).then(function(response){
					//$scope.showSuccessDropMsg();
					
					Notification.info({message: '<span>Recovery link was sent to your email</span>'});
					
					$scope.droppingPass = false;
				}, function(errorResponse){
					//$scope.showErrorMsg("");
					console.log('error resp', errorResponse);
					Notification.error({message: '<span>No user with this email</span>'});
					
					$scope.droppingPass = false;
				})
			}
		}
	]
);
