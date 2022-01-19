"use strict";

app.directive('searchableSelect', function () {
    return {
    	restrict: 'E',
        replace: true,
        transclude: false,
        scope: {
        	name: "=name", //functions as id
        	setModel: "=setModel",
			addParams: "=?addParams",
        	array: "=array",
        	searchByField: "=searchByField", //if this parameter === null, undefined or "", then we consider array element as string
        	isDisabled: "=isDisabled",
        	isValid: "=isValid",
        	api: "=?api",
			small: "=?small",
			showedString: "=?showedString",
			cleanedString: "=?cleanedString"
        },
        link: function (scope, element, attrs) {
        	scope.myArrayFiltered = scope.array;

			 scope.callIsDisabled = function() {
				if(typeof scope.isDisabled !== 'function'){
					return scope.isDisabled;
				}
				return scope.isDisabled();
			}

        	scope.callIsValid = function() {
				if(typeof scope.isValid !== 'function'){
					return scope.isValid;
				}
				return scope.isValid();
			}

        	if(scope.api){
        		scope.api.refresh = function(){
        			scope.searchString = scope.getShowedString();
        			scope.chosenRow = undefined;
        		}
        	}

        	scope.getShowedString = function() {

				return typeof scope.showedString === 'string' && scope.showedString.length > 0 ? scope.showedString : '';
			}

        	scope.searchString = scope.getShowedString();

        	scope.getArrayValue = function(index){
        		if(isStringBad(scope.searchByField)){
        			return scope.myArrayFiltered[index];
        		} else {
        			return scope.myArrayFiltered[index][scope.searchByField];
        		}
        	}
        	
        	scope.changeInput = function(){
        		scope.showDropdown = !isStringBad(scope.searchString);
        		
        		scope.myArrayFiltered = [];

				if(!scope.searchString || scope.searchString.length === 0) {
					scope.myArrayFiltered = scope.array;

					return;
				}

        		for (var i = 0; i < scope.array.length; i++) {
					let myString = scope.searchString.toLowerCase();
        			if(isStringBad(scope.searchByField)){
						let itemString = scope.array[i].toLowerCase();
						if(itemString.indexOf(myString) > -1){
							scope.myArrayFiltered.push(scope.array[i]);
						}
					} else {
						let itemString = scope.array[i][scope.searchByField].toLowerCase();
						if(itemString.indexOf(myString) > -1){
							scope.myArrayFiltered.push(scope.array[i]);
						}
					}
				}
        	}

        	
        	scope.chooseRow = function(item){
        		let params = [item];

        		if(scope.addParams){
        			for(let param of scope.addParams) {
						params.push(param);
					}
				}

        		scope.setModel.apply(null, params);
        	
        		scope.chosenRow = item;
        		
        		if(isStringBad(scope.searchByField)){
        			scope.searchString = item;
        		} else {
        			scope.searchString = item[scope.searchByField];
        		}
        		
        		scope.showDropdown = false;
        	}
        	
        	window.addEventListener('click', function(e){
        		if(!scope.searchString || scope.searchString.length === 0) {
        			scope.myArrayFiltered = scope.array;
				}
        		let component = document.getElementById(scope.name + "-input");

        		  if (component && component.contains(e.target) && scope.myArrayFiltered.length > 0 && !scope.callIsDisabled()){
        			  scope.showDropdown = true;
        		  } else{
        			  scope.showDropdown = false;
        			  
        			  if(!scope.chosenRow){
        				  //scope.searchString = "";
        			  } else {
        				  if(isStringBad(scope.searchByField)){
        	        		scope.searchString = scope.chosenRow;
        				  } else {
        	        		scope.searchString = scope.chosenRow[scope.searchByField];
        				  }
						  if (scope.cleanedString) {
							  scope.searchString = "";
						  }
        			  }
        		  }
        		  
        		  scope.$apply()
        		});
        	
        },
        templateUrl: projectRoot + "/resources/directives/searchable-select/searchable-select.html"
    }
}
);
