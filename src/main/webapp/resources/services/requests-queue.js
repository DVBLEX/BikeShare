"use strict";

app.service("requestsQueueService", function($http, $q){
	var queue = [];
	
	var onAnyResponse = function(success, response){
		var req = queue.splice(0, 1)[0];
		
		if(success){
			if(typeof req.successFunc === 'function'){
				req.successFunc(response);
			}
		} else {
			if(typeof req.errorFunc === 'function'){
				req.errorFunc(response);
			}
		}
		
		if(queue.length > 0){
			$http(queue[0]).then(function(response){
				onAnyResponse(true, response)
			}, function(errorResponse){
				onAnyResponse(false, errorResponse)
			})
		}
	}
	
	this.appendRequest = function(method, url, headers, body, successFunc, errorFunc){
		var request = {
				method: method,
				url: url,
				headers: headers,
				data: body,
				successFunc: successFunc,
				errorFunc: errorFunc
		}
		
		queue.push(request);
		
		if(queue.length === 1){
			$http(request).then(function(response){
				onAnyResponse(true, response);
			}, function(errorResponse){
				onAnyResponse(false, errorResponse);
			})
		}
	}
})