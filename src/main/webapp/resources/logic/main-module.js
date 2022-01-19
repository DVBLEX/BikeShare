"use strict";

var projectRoot = "/msc";

//To open window with post request
var postWindowOpen = function(verb, url, data, target) {
	  var form = document.createElement("form");
	  form.action = url;
	  form.method = verb;
	  form.target = target || "_self";
	  if (data) {
	    for (var key in data) {
	      var input = document.createElement("textarea");
	      input.name = key;
	      input.value = typeof data[key] === "object" ? JSON.stringify(data[key]) : data[key];
	      form.appendChild(input);
	    }
	  }
	  form.style.display = 'none';
	  document.body.appendChild(form);
	  form.submit();
	};

var attachedModules = ['ui-notification', '720kb.datepicker', 'ui.mask', 'daterangepicker', 'chart.js'];
try{
	agGrid.initialiseAgGridWithAngular1(angular);
	angular.module('agGrid');
	
	attachedModules.push('agGrid');
} catch(err){
	console.log("no grid module registered");
}


var app = angular.module('mainModule', attachedModules);

app.config(function ($httpProvider, $controllerProvider, $provide, $compileProvider, NotificationProvider) {

	 NotificationProvider.setOptions({
         delay: 2000,
         startTop: 20,
         startRight: 10,
         verticalSpacing: 20,
         horizontalSpacing: 20,
         positionX: 'right',
         positionY: 'bottom'
     });
	
    //controllers registering configuration
    app.controller = function (name, constructor) {
        $controllerProvider.register(name, constructor);
        return (this);
    };
    //services registering configuration
    app.service = function (name, constructor) {
        $provide.service(name, constructor);
        return (this);
    };
    //factories registering configuration
    app.factory = function (name, factory) {
        $provide.factory(name, factory);
        return (this);
    };
    //values registering configuration
    app.value = function (name, value) {
        $provide.value(name, value);
        return (this);
    };
    //directives registering configuration
    app.directive = function (name, factory) {
        $compileProvider.directive(name, factory);
        return (this);
    };

    function getParameterByName(name, url) {
        if (!url) url = window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }

    var token = localStorage['token'];

//    if (!token) {
//        alert("(Temp) Access is denied!");
//    }
    
    $httpProvider.defaults.headers.common["X-Security-Token"] = token;
});


app.controller('headerCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	'Notification',
	function($scope, $http, $timeout, $rootScope, Notification) {
		
//		$rootScope.successNotificationWithTitle = function(title, message){
//			Notification.success({message: '<span>' + message + '</span>', title: '<h5>' + title + '</h5>'});
//		}
//		
//		$rootScope.errorNotificationWithTitle = function(title, message){
//			Notification.error({message: '<span>' + message + '</span>', title: '<h5>' + title + '</h5>'});
//		}
//		
//		$rootScope.infoNotificationWithTitle = function(title, message){
//			Notification.info({message: '<span>' + message + '</span>', title: '<h5>' + title + '</h5>'});
//		}
//		
//		
//		$rootScope.successNotification = function(message){
//			Notification.success({message: '<span>' + message + '</span>'});
//		}
//		
//		$rootScope.errorNotification = function(message){
//			Notification.error({message: '<span>' + message + '</span>'});
//		}
//		
//		$rootScope.infoNotification = function(message){
//			Notification.info({message: '<span>' + message + '</span>'});
//		}

		//for not to change every single call of notifications in system, we'll just redefine the functions

		$rootScope.successNotificationWithTitle = function(title, message){
			Notification.success({message: '<span>Success!</span>'});
		}

		$rootScope.errorNotificationWithTitle = function(title, message){
			Notification.error({message: '<span>Please try again later or refer to the system administrator</span>', title: '<h6>Something went wrong</h6>'});
		}

		$rootScope.validationErrorNotificationWithTitle = function(title, message){
			Notification.error({message: '<span>Please check the data</span>', title: '<h6>Error</h6>'});
		}

		$rootScope.infoNotificationWithTitle = function(title, message){
			Notification.info({message: '<span>' + message + '</span>', title: '<h6>' + title + '</h6>'});
		}


		$rootScope.successNotification = function(message){
			Notification.success({message: '<span>Success!</span>'});
		}

		$rootScope.errorNotification = function(message){
			Notification.error({message: '<span>Please try again later or refer to the system administrator</span>', title: '<h6>Something went wrong</h6>'});
		}

		$rootScope.errorNotificationCustom = function(message){
			Notification.error({message: '<span>' + message + '</span>'});
		}

		$rootScope.infoNotification = function(message){
			Notification.info({message: '<span>' + message + '</span>'});
		}

		$rootScope.errorNotificationWithMessage = function(message){
			Notification.error({message: '<span>' + message + '</span>', title: '<h6>Something went wrong</h6>'});
		}


		$http.get(projectRoot + "/msc-api/user/permitted-pages").then(function(response){
			$scope.permittedPages = response.data;

		}, function(errorResponse){

		});

		function loadUserCreds(){
			$http.get(projectRoot + "/msc-api/top-data/get-user-creds").then(function(response){
				$scope.currentUser = response.data;
			}, function(errorResponse){

			});
		}

		loadUserCreds();

		var container = document.getElementsByClassName("container")[0];

		var changeContainer = function(){
			if(window.innerWidth > 1000){
				container.className = "container pt-2 pb-2";
			} else {
				container.className = "container-fluid pt-2 pb-2";
			}
		}

		//here we are changing type of container, based in viewport width
		changeContainer();

		window.addEventListener('resize', function(){
			changeContainer();
		});

//		window.onload = function(){
//			$timeout(function(){
//				document.getElementById("titleForBigPage").className += " d-lg-inline-block";
//				
//				console.log('class name', document.getElementById("titleForBigPage").className);
//			}, 500)
//			
//		}
		
		$scope.isPagePermitted = function(pageName){
			if(!$scope.permittedPages){
				return false;
			}
			
			for (var i = 0; i < $scope.permittedPages.length; i++) {
				if($scope.permittedPages[i] === pageName){
					return true;
				}
				if($scope.permittedPages[i] === 'all'){
					return true;
				}
			}
			
			return false;
		}
		
		$scope.getPageTitle = function(){
			return document.title;
		}
		
		$scope.isPasswordRecoveryPage = function(){
			return $scope.getPageTitle() === "Password recovery";
		}

		$rootScope.roundNumber = function(number, digitsCount){
			return number.toFixed(digitsCount);
		}

		$scope.askToDropPassword = function(){
			$('#dropPasswordModal').modal('show');
		}

		$scope.dropPassword = function(){
			$scope.droppingPass = true;

			$http.get(projectRoot + "/msc-api/top-data/get-user-creds").then(function(response){
				$http.put(projectRoot + "/msc-api/user/drop-password", {userEmail: response.data.email}).then(function(response){
					$scope.droppingPass = false;

					window.open(projectRoot + "/views/login", "_self");
				}, function(errorResponse){
					//$scope.showErrorMsg("");

					$rootScope.errorNotificationWithTitle("Error", "Something went wrong");

					$scope.droppingPass = false;
				})
			}, function(errorResponse){

			});

		}

		//root scope stuff

		$rootScope.belongsToFulfillment = function(string){
			if(string.indexOf("Fulfillment") > -1){
				return true;
			}

			return false;
		}

	}
]);

app.controller('footerCtrl', ['$scope', function($scope) {
	let currentDate = new Date();
	$scope.currentYear = currentDate.getFullYear();
}]);

//allows to do things like  ng-repeat="i in 5 | range"
app.filter('range', function(){
    return function(n) {
    	 var res = [];
	     for (var i = 0; i < n; i++) {
	       res.push(i);
	     }
	     return res;
    }
});


app.directive("mscExtSubmit", ['$timeout',function($timeout){
    return {
        link: function($scope, $el, $attr) {
            $scope.$on('makeSubmit', function(event, data){
              if(data.formName === $attr.name) {
                $timeout(function() {
                  $el.triggerHandler('submit'); //<<< This is Important
                  //$el[0].dispatchEvent(new Event('submit')) //equivalent with native event
                }, 0, false);   
              }
            })
        }
    };
}]);

app.directive('mscElastic', [
    '$timeout',
    function($timeout) {
        return {
            restrict: 'A',
            link: function($scope, element) {
                $scope.initialHeight = $scope.initialHeight || element[0].style.height;
                var resize = function() {
                    element[0].style.height = $scope.initialHeight;
                    element[0].style.height = "" + element[0].scrollHeight + "px";
                };
                element.on("input change", resize);
                $timeout(resize, 0);
            }
        };
    }
]);

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

function getCharCodeFromEvent(event) {
    event = event || window.event;
    return (typeof event.which == "undefined") ? event.keyCode : event.which;
}

function isCharNumeric(charStr) {
    return !!/\d/.test(charStr) || charStr === '.' || charStr === "-";
}

function isKeyPressedNumeric(event) {
    var charCode = getCharCodeFromEvent(event);
    var charStr = String.fromCharCode(charCode);
    return isCharNumeric(charStr);
}

function isStringBad(string){
	return string === undefined || string === null || string === "";
}

function safeFuncCall(func, ...args) {
	if(!args) {
		args = [];
	}
	if(typeof func === 'function') {
		return func.apply(this, args);
	}

	return undefined;
}

function arrayToPageable(array, pageSize){
	var pageable = [];
	
	var tempPage = 0;
	var currPageArray;
	for (var i = 0; i < array.length; i++) {
		
		if((i + 1 == pageSize * tempPage) || i == 0){
			currPageArray = [];
			pageable[++tempPage] = currPageArray;
		}
		
		currPageArray.push(array[i]);
	}
	
	return pageable;
}

function msToTime(s) {

	  // Pad to 2 or 3 digits, default is 2
	  function pad(n, z) {
	    z = z || 2;
	    return ('00' + n).slice(-z);
	  }

	  var ms = s % 1000;
	  s = (s - ms) / 1000;
	  var secs = s % 60;
	  s = (s - secs) / 60;
	  var mins = s % 60;
	  var hrs = (s - mins) / 60;

	  return pad(hrs) + ':' + pad(mins);
	  //return pad(hrs) + ':' + pad(mins) + ':' + pad(secs) + '.' + pad(ms, 3);
}

Function.prototype.clone = function() {
    var that = this;
    var temp = function temporary() { return that.apply(this, arguments); };
    for(var key in this) {
        if (this.hasOwnProperty(key)) {
            temp[key] = this[key];
        }
    }
    return temp;
};






//This is for ag grid

//function to act as a class
function NumericCellEditor() {
}

// gets called once before the renderer is used
NumericCellEditor.prototype.init = function (params) {
    // create the cell
    this.eInput = document.createElement('input');
    this.eInput.style.width = '100%';
    this.eInput.style.height = 'calc(100% - 2px)'
    
    if (isCharNumeric(params.charPress)) {
        this.eInput.value = params.charPress;
    } else {
        if (params.value !== undefined && params.value !== null) {
            this.eInput.value = params.value;
        }
    }

    var that = this;
    this.eInput.addEventListener('keypress', function (event) {
        if (!isKeyPressedNumeric(event)) {
            that.eInput.focus();
            if (event.preventDefault) event.preventDefault();
        } else if (that.isKeyPressedNavigation(event)){
            event.stopPropagation();
        }
    });

    // only start edit if key pressed is a number, not a letter
    var charPressIsNotANumber = params.charPress && ('-.1234567890'.indexOf(params.charPress) < 0);//повинне пропускати мінус і крапку
    this.cancelBeforeStart = charPressIsNotANumber;
};

NumericCellEditor.prototype.isKeyPressedNavigation = function (event){
    return event.keyCode===39
        || event.keyCode===37;
};


// gets called once when grid ready to insert the element
NumericCellEditor.prototype.getGui = function () {
    return this.eInput;
};

// focus and select can be done after the gui is attached
NumericCellEditor.prototype.afterGuiAttached = function () {
    this.eInput.focus();
};

// returns the new value after editing
NumericCellEditor.prototype.isCancelBeforeStart = function () {
    return this.cancelBeforeStart;
};

// example - will reject the number if it contains the value 007
// - not very practical, but demonstrates the method.
NumericCellEditor.prototype.isCancelAfterEnd = function () {
//    var value = this.getValue();
//    return value.indexOf('007') >= 0;
};

// returns the new value after editing
NumericCellEditor.prototype.getValue = function () {
    return this.eInput.value;
};

// any cleanup we need to be done here
NumericCellEditor.prototype.destroy = function () {
    // but this example is simple, no cleanup, we could  even leave this method out as it's optional
};

// if true, then this editor will appear in a popup 
NumericCellEditor.prototype.isPopup = function () {
    // and we could leave this method out also, false is the default
    return false;
};
