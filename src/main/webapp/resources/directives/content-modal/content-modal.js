"use strict";

app.directive('contentModal', function($timeout) {
	return {
		restrict : 'E',
		replace : true,
		transclude : true,
		scope : {
			modalId : "=modalId",
			onConfirm : "=onConfirm",
			onConfirmText : "=onConfirmText",
			hideCancel : "=hideCancel",
			cancelText : "=cancelText",
			onHide : "=onHide",
			hideManuallyOnConfirm : "=hideManuallyOnConfirm",
			isFrozen : "=?isFrozen",
			confirmDisabled: "=?confirmDisabled",
			title : "=title",
			text : "=text",
			maxWidth: '=maxWidth'
		},
		link : function(scope, element, attrs) {

			if(typeof scope.isFrozen !== 'function'){
				scope.isFrozen = function(){
					return false;
				}
			}

			if(typeof scope.confirmDisabled !== 'function'){
				scope.confirmDisabled = function(){
					return false;
				}
			}
			
			var callAfterDigest = function(func) {
				$timeout(function() {
					func();

					callAfterDigest(func);
				}, 0, false); // third argument for not to trigger new digest by calling $apply
			}

			scope.checkBackdrop = function() {
				if (scope.isFrozen()) {
					return 'static';
				}

				return true;
			}
			
			scope.constructTitle = function(){
        		if(typeof scope.title === 'function'){
        			return scope.title();
        		}
        		
        		return scope.title;
        	}
			if(!scope.maxWidth){
				scope.maxWidth = '500px';
			}
//			callAfterDigest(function() {
//				if(typeof modalId !== 'undefined'){
//					$('#' + modalId).modal({backdrop: scope.checkBackdrop(), keyboard: scope.isFrozen()})  //doesn't work. maybe bug in bootstrap 4
//				}
//			});

		},
		templateUrl : projectRoot + "/resources/directives/content-modal/content-modal.html"
	}
});
