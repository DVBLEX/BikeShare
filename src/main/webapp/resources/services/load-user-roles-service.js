"use strict"

app.service("loadUserRolesService", function($http, $q){
	
	this.load = function(scope, onSuccessFunction, onErrorFunction){
		$http.get(projectRoot + "/msc-api/user/user-roles").then(function(response){
			scope.userRoles = response.data;
			
			if(scope.userRoles[0] === 1){
				scope.userIsAdmin = true;
			} else if(scope.userRoles[2] === 1){
				scope.userIsSchemeLeader = true;
				scope.userIsOnlySchemeLeader = true;
			} else if(scope.userRoles[4] === 1){
				scope.userIsFulfillmentOperator = true;
				scope.userIsOnlyFulfillmentOperator = true;
			}
			
			
			for(var i = 0; i < scope.userRoles.length; i++){
				if(i != 2 && scope.userRoles[i] === 1){
					scope.userIsOnlySchemeLeader = false;
				}
				if(i !== 4 && scope.userRoles[i] === 1){
					scope.userIsOnlyFulfillmentOperator = false;
				}
			}
			
			if(typeof onSuccessFunction === 'function'){
				onSuccessFunction(response)
			}
			
		}, function(errorResponse){
			if(typeof onErrorFunction === 'function'){
				onErrorFunction(errorResponse);
			}
		});
	}
});
