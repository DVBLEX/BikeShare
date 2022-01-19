"use strict";

app.directive('pagination', function () {
    return {
    	restrict: 'E',
        replace: true,
        transclude: true,
        scope: {
        	parameters: "=parameters",
        	//We need these parameters:
        	//currentPage
        	//pagesNumber
        	//pagesToShow
        },
        link: function (scope, element, attrs) {
        	scope.setCurrentPageNumber = function(num){
        		scope.parameters.currentPage = num;
        		
        		if(scope.parameters.currentPage < 1){
        			scope.parameters.currentPage = 1;
        		} else if(scope.parameters.currentPage > scope.parameters.pagesNumber){
        			scope.parameters.currentPage = scope.parameters.pagesNumber;
        		}
        	}
        	
        	scope.shouldPageBeShown = function(pageNum){
        		if(!scope.parameters){
        			return false;
        		}
        		
        		var minPage = scope.parameters.currentPage - Math.floor(scope.parameters.pagesToShow / 2);
        		if(minPage <= 0) {
        			minPage = 1;
        		}
        		
        		var maxPage = minPage + scope.parameters.pagesToShow - 1;
        		if(maxPage > scope.parameters.pagesNumber){
        			maxPage = scope.parameters.pagesNumber;
        		}
        		
        		minPage = maxPage - scope.parameters.pagesToShow + 1;
        		
        		if(minPage <= 0) {
        			minPage = 1;
        		}
        		
        		return pageNum >= minPage && pageNum <= maxPage;
        	}
        },
    	templateUrl: projectRoot + "/resources/directives/pagination/pagination.html"
    }
});
