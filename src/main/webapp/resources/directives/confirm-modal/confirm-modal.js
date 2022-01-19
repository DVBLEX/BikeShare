"use strict";

app.directive('confirmModal', function () {
    return {
    	restrict: 'E',
        replace: true,
        transclude: true,
        scope: {
        	modalId: "=modalId",
        	onConfirm: "=onConfirm",
        	hideCancel: "=hideCancel",
        	onHide: "=onHide",
        	title: "=title",
        	text: "=text"
        },
        link: function (scope, element, attrs) {
        	scope.constructTitle = function(){
        		if(typeof scope.title === 'function'){
        			return scope.title();
        		}
        		
        		return scope.title;
        	}
        },
    	templateUrl: projectRoot + "/resources/directives/confirm-modal/confirm-modal.html"
    }
});