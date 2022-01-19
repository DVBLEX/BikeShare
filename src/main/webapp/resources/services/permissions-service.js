"use strict";

app.service("permissionsService", function($http, $q){
	
	this.returnPromiseOfPermissionCheck = function(pageName){
		var deffered = $q.defer();
		
		if(!localStorage["userEmail"]){
			window.open("/msc/views/login", "_self");
			
			return;
		}
		
		$http.put("/msc/msc-api/user/check-permission/user-list", {userEmail: localStorage["userEmail"]}).then(function(response){
			if(response.data){
				deffered.resolve();
			} else {
				deffered.reject();
			}
		}, function(errorResponse){
			deffered.reject({errorMessage: errorResponse.data.message});
		});
		
		return deffered.promise;
	}
});