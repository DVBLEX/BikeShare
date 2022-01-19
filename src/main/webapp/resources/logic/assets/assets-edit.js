"use strict";

app.controller('assetsEditCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$q',
	'$rootScope',
	function($scope, $http, $timeout, $q, $rootScope) {
		
		$scope.gridPageSizes = [20, 50, 100];
		$scope.gridPageSize = $scope.gridPageSizes[0];
		
		$scope.activeTab = 0;
		
		
		
		var loadData = function(){
			$http.get(projectRoot + "/msc-api/user/user-roles").then(function(response){
				$scope.userRoles = response.data;
				
				if($scope.userRoles[0] === 1){
					$scope.userIsAdmin = true;
				} else if($scope.userRoles[4] === 1){
					$scope.userIsFulfillmentOperator = true;
					$scope.userIsOnlyFulfillmentOperator = true;
				}
				for(var i = 0; i < $scope.userRoles.length; i++){
					if(i !== 4 && $scope.userRoles[i] === 1){
						$scope.userIsOnlyFulfillmentOperator = false;
					}
				}
			
				$http.get(projectRoot + "/msc-api/assets/products").then(function(response){
					$scope.allProducts = response.data;

					
					$http.get(projectRoot + "/msc-api/assets/groups-of-types").then(function(response){
						$scope.groups = response.data;
						
						if($scope.userIsOnlyFulfillmentOperator){
							for (var i = 0; i < $scope.groups.length; i++) {
								if($rootScope.belongsToFulfillment($scope.groups[i])){
									$scope.activeTab = i;
									break;
								}
							}
						}
						
						$http.get(projectRoot + "/msc-api/assets/suppliers").then(function(suppliersResponse){
							
							$scope.suppliers = suppliersResponse.data;
							
							$scope.productsByGroups = {};
							$scope.pagesParameters = {};
							
							for (var i = 0; i < $scope.groups.length; i++) {
								$scope.productsByGroups[$scope.groups[i]] = [];
								
								//Sorting by groups
								for (var j = 0; j < $scope.allProducts.length; j++) {
									if($scope.groups[i] === $scope.allProducts[j].type.assetGroup){
										$scope.allProducts[j].oldProduct = angular.copy($scope.allProducts[j]);
										$scope.productsByGroups[$scope.groups[i]].push($scope.allProducts[j]);
									}
								}
															
							}
							
							$scope.productsGridOptions.api.setRowData($scope.productsByGroups[$scope.getActiveGroupName()]);

						}, function(errorResponse){
							
						});
						
					}, function(errorResponse){
						
					});
					
				}, function(errorResponse){
					
				});
			}, function(errorResponse){
				
			});
		};

		
		$scope.getActiveGroupName = function(){
			return $scope.groups[$scope.activeTab];
		}
		
		$scope.openAddNewProductModal = function(){
			$scope.editableProduct = {
				productId: {},
				type: {
					assetGroup: $scope.groups[$scope.activeTab],
					typeName: ''
				}
			};
			
			$scope.openEditModal();
		}
		
		$scope.canEditProductName = function(){
			return $scope.editableProduct && !$scope.editableProduct.hasOwnProperty("id");
		}
		
		//--grid-related-stuff--
		
		var editCellRendererFunc = function(params){
			 params.$scope.editProduct = $scope.editProduct;//for editProduct function to be visible in row's scope
			 
			 params.$scope.editable = params.node.data;
			 params.$scope.rowId = params.node.id;
			 
			 return '<button type="button" class="btn btn-info btn-sm btn-block" style="height:25px; padding: 3px 8px;" data-ng-click="editProduct(editable, rowId)"><i class="material-icons md-18">edit</i></button>';
		}
		
		var deleteRowCellRendererFunc = function(params){
			params.$scope.deleteProduct = $scope.deleteProduct;//for deleteProduct function to be visible in row's scope
			 
			params.$scope.rowId = params.node.id;
			params.$scope.deletable = params.node.data;
			
			return '<button type="button" class="btn btn-danger btn-sm btn-block" style="height:25px; padding: 3px 8px;" data-ng-click="opedDeleteProductConfirmModal(deletable)"><i class="material-icons md-18">clear</i></button>';
		};
		
		// function numberFormatter(params) {
		//     if (typeof params.value === 'number') {
		//         return params.value.toFixed(2);
		//     } else {
		//         return params.value;
		//     }
		// }
		
		var columnDefs = [

			{headerName: "Asset Id", field: "id", filter: "agNumberColumnFilter", width: 87},
			{headerName: "Asset Name", field: "type.groupName", filter: "agTextColumnFilter", width: 240},
			{headerName: "Asset Description", field: "type.typeName", filter: "agTextColumnFilter", width: 240},
	        {headerName: "Parts Number", field: "productId.productName", filter: "agTextColumnFilter", width: 240},
	        //{headerName: "Type", field: "type.typeName", filter: "agTextColumnFilter", width: 200},
	        {headerName: "Supplier", field: "productId.supplier.name", filter: "agTextColumnFilter", width: 200},
	        // {headerName: "Price, €", field: "productId.price", cellClass:'text-right', filter: "agNumberColumnFilter", valueFormatter: numberFormatter, width: 80},
			{headerName: "Delivery time, days", field: "deliveryTime", cellClass:'text-right', filter: "agNumberColumnFilter", width: 80},
	        {headerName: "Minimal Order Quantity", field: "minOrder", cellClass:"text-right", filter: "agNumberColumnFilter", width: 170},
	        
	        {headerName: "Edit", field: "", width: 70, cellRenderer: editCellRendererFunc, pinned: "right", resizable: false, suppressSizeToFit: true},
	        {headerName: "Delete", field: "", width: 70, cellRenderer: deleteRowCellRendererFunc, pinned: "right", resizable: false, suppressSizeToFit: true},
	    ];


		$scope.productsGridOptions = {
				defaultColDef: {
			        resizable: true
			    },
		        columnDefs: columnDefs,
		        rowData: null,
		        pagination: true,
		        paginationPageSize: 20,
		        floatingFilter: true,
		        angularCompileRows: true,
		        domLayout: 'autoHeight',
//		        onGridReady: function(params) {
//		            params.api.sizeColumnsToFit();
//		        },
		    };
		
		
		$scope.setGridPageSize = function(pageSize){
			$scope.productsGridOptions.api.paginationSetPageSize(pageSize)
		}
		//--grid-related-stuff-end---
		
		$scope.openEditModal = function(){
			$scope.validitiyOfFieldsOfEditModal = {
					supplier: true,
					groupName: true,
					name: true,
					type: true,
					minOrder: true,
				}
			
			$('#editProductModal').modal('show');
		}
		
		$scope.editProduct = function(product, rowId){
			
			$scope.mainEditingProduct = product;
			$scope.editableProduct = angular.copy(product);
			$scope.editedRowId = rowId;
			
			$scope.openEditModal();
		};
		
		$scope.opedDeleteProductConfirmModal = function(product){
			$('#confirmDeleteModal').modal('show');
			
			$scope.deletableProduct = product;
		}
		
		$scope.deleteProduct = function(){
			$http.delete(projectRoot + "/msc-api/assets/product/" + $scope.deletableProduct.id).then(function(response){
				$scope.productsByGroups[$scope.getActiveGroupName()].splice($scope.productsByGroups[$scope.getActiveGroupName()].indexOf($scope.deletableProduct), 1);
				
				$scope.productsGridOptions.api.updateRowData({remove: [$scope.deletableProduct]});
				
				$rootScope.successNotification("Successful deleting of product");
			}, function(errorResponse){
				$rootScope.errorNotification("Error on product delete. Usually, product is used within the system and you can't just delete it");
			});
		}
				
		$scope.compareProductIds = function(pId1, pId2){
			
			return pId1.productName === pId2.productName
			// && pId1.price === pId2.price
			&& pId1.supplier.id === pId2.supplier.id;
		}
		
		$scope.findProductById = function(productId){
			for (var i = 0; i < $scope.productsByGroups[$scope.getActiveGroupName()].length; i++) {
				if($scope.compareProductIds(productId, $scope.productsByGroups[$scope.getActiveGroupName()][i].productId)){
					return $scope.productsByGroups[$scope.getActiveGroupName()][i];
				}
			}
			
			return null;
		}
		
		$scope.updateProductFromEdited = function(product){
			product.productId = $scope.editableProduct.productId;
			product.type = $scope.editableProduct.type;
			product.minOrder = $scope.editableProduct.minOrder;
			product.deliveryTime = $scope.editableProduct.deliveryTime;
		}
		
		$scope.isFrozen = function(){
			return $scope.saving;
		}
		
		$scope.saveEditedProduct = function(){
			if(!$scope.checkValiditiyOfFieldsOfEditModal()){
				$rootScope.validationErrorNotificationWithTitle();
				
				return;
			}
			
			
			$scope.saveProduct($scope.editableProduct, $scope.editableProduct.oldProduct).then(function(response){ //TODO there is no check, if a new product is the same, as the some old product from the database, we'll just get error on insert
				if(!$scope.editableProduct.oldProduct){
					$scope.editableProduct.id = response.id;
					$scope.editableProduct.oldProduct = angular.copy($scope.editableProduct);
					$scope.editableProduct.type = response.type;
					
					$scope.productsByGroups[$scope.getActiveGroupName()].splice(0, 0, $scope.editableProduct);
					
					$scope.productsGridOptions.api.setRowData($scope.productsByGroups[$scope.getActiveGroupName()]);
				} else {
//					var realProduct = $scope.findProductById($scope.editableProduct.oldProduct.productId);
//					
//					if(realProduct){
//						$scope.updateProductFromEdited(realProduct);
//						
//						realProduct.oldProduct = undefined;
//						realProduct.oldProduct = angular.copy(realProduct);
//					}
//					$scope.editableProduct.oldProduct = undefined;
//					$scope.editableProduct.oldProduct = angular.copy($scope.editableProduct);
					
					$scope.updateProductFromEdited($scope.mainEditingProduct);
					$scope.mainEditingProduct.oldProduct = undefined;
					$scope.mainEditingProduct.oldProduct = angular.copy($scope.mainEditingProduct);
					
					$scope.productsGridOptions.api.getRowNode($scope.editedRowId).setData($scope.mainEditingProduct);
					
					//$scope.productsGridOptions.api.setRowData($scope.productsByGroups[$scope.getActiveGroupName()]);//temp
				}
				
				$('#editProductModal').modal('hide');
			}, function(){
				
			});
		}
		
		$scope.saveProduct = function(product, oldProduct){
			
			var deferred = $q.defer();
			
			$scope.saving = true;
			
			$http.post(projectRoot + "/msc-api/assets/product", {savableProduct: product, oldProduct: oldProduct}).then(function(response){
				deferred.resolve(response.data);
				
				$scope.saving = false;
				
				$rootScope.successNotification("Successful saving of product");
				
			}, function(errorResponse){
				console.log("errorResponse", errorResponse);
				
				
				deferred.reject(errorResponse.data);
				$scope.saving = false;
				
				if(errorResponse.data.message.indexOf("trying to add a new product") > -1){
//					$scope.showErrorAddingExistingProduct = true;
//					
//					$timeout(function(){
//						$scope.showErrorAddingExistingProduct = false;
//					}, 4000);
					
					$rootScope.errorNotification("Error! Product already exists");
				} else {
					$rootScope.errorNotification("Error on product save");
				}
			});
			
			return deferred.promise;
		};
		
		$scope.checkValiditiyOfFieldsOfEditModal = function(){
			$scope.validitiyOfFieldsOfEditModal.supplier = $scope.editableProduct.productId.supplier !== null && $scope.editableProduct.productId.supplier !== undefined;
			$scope.validitiyOfFieldsOfEditModal.groupName = $scope.editableProduct.type.groupName !== null && $scope.editableProduct.type.groupName !== undefined && $scope.editableProduct.type.groupName.length > 0;
			$scope.validitiyOfFieldsOfEditModal.name = true;
			$scope.validitiyOfFieldsOfEditModal.type = true; // $scope.editableProduct.type !== null && $scope.editableProduct.type !== undefined && $scope.editableProduct.type.typeName !== undefined && $scope.editableProduct.type.typeName.length > 0;
			$scope.validitiyOfFieldsOfEditModal.minOrder = (typeof $scope.editableProduct.minOrder === "number") ? $scope.editableProduct.minOrder >= 0 : true;
			
			return $scope.validitiyOfFieldsOfEditModal.supplier && $scope.validitiyOfFieldsOfEditModal.groupName && $scope.validitiyOfFieldsOfEditModal.name && $scope.validitiyOfFieldsOfEditModal.type && $scope.validitiyOfFieldsOfEditModal.minOrder;
		};
		
		$scope.setActiveTab = function(index){
			$scope.activeTab = index;
			
			$scope.productsGridOptions.api.setRowData($scope.productsByGroups[$scope.getActiveGroupName()]);
		};
		
		
		
		//------LOAD-DATA----------------
		
		loadData();
	}
]);
