"use strict";

app.controller('purchaseOrdersCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$rootScope',
	'loadUserRolesService',
	function($scope, $http, $timeout, $rootScope, loadUserRolesService) {
		//Enabling tooltips
//		$(function () {
//		  $('[data-toggle="tooltip"]').tooltip()
//		})

		$scope.pageSizes = [10, 20, 50];
		$scope.pageSize = $scope.pageSizes[0];
		$scope.newOrderProductSelectApis = [];
		
		$scope.currentSupplier = {
			val: {}
		}
		
		var loadData = function(){
            $scope.doNotMarkInputsAsInvalidFlag = true; //do not validate newly added inputs after page load
			loadUserRolesService.load($scope, function(){
				
				$http.get(projectRoot + "/msc-api/top-data/states/purchase-orders").then(function (response){
					$scope.states = response.data;
					$scope.states.splice(0, 0, {name: "All"});
					$scope.currentState = $scope.states[0];
				}, function(errorResponse){

				});
				
				$http.get(projectRoot + "/msc-api/assets/products/by-suppliers").then(function(response){

					$scope.productsBySuppliers = response.data;

				}, function(errorResponse){
					
				});
				
				$http.get(projectRoot + "/msc-api/distribution/split-purchase-order-queue").then(function(response){
					$scope.splitPurchaseOrderQueue = response.data;
					
					if($scope.userIsOnlyFulfillmentOperator){
						for (var i = 0; i < $scope.splitPurchaseOrderQueue.length; i++) {
							if($scope.splitPurchaseOrderQueue[i].product.type.assetGroup.indexOf("fulfillment") < 0){
								$scope.splitPurchaseOrderQueue.splice(i--, 1);
							}
						}
					}
					
					// $scope.splitPurchaseOrderQueueGridOptions.api.setRowData($scope.splitPurchaseOrderQueue);
					// $scope.splitPurchaseOrderQueueGridOptions.api.sizeColumnsToFit();
				}, function(errorResponse){
					
				});
				
				$http.get(projectRoot + "/msc-api/assets/suppliers").then(function(response){
					$scope.realSuppliers = angular.copy(response.data);

					if($scope.userIsOnlyFulfillmentOperator){
						for (var i = 0; i < $scope.realSuppliers.length; i++) {
							if(!$scope.realSuppliers[i].fulfillment){
								$scope.realSuppliers.splice(i--, 1);
							}
						}
					}
					
					$scope.allSuppliers = angular.copy($scope.realSuppliers);
					
					$scope.allSuppliers.splice(0, 0, {id: -1, name: "All Suppliers"});
					
					$scope.currentSupplier.val = $scope.allSuppliers[0];
					
					$http.get(projectRoot + "/msc-api/distribution/purchase-orders").then(function(response){
						$scope.purchaseOrders = response.data;

						for (var i = 0; i < $scope.purchaseOrders.length; i++) {
							
							if($scope.userIsOnlyFulfillmentOperator){
								if(!$scope.purchaseOrders[i].supplier.fulfillment){
									$scope.purchaseOrders.splice(i--, 1);
									
									continue;
								}
							}	
							
							if($scope.purchaseOrders[i].state.id === 13){
								$scope.addCredsToOrder($scope.purchaseOrders[i]);
								$scope.fixateProductsAmounts($scope.purchaseOrders[i]);
							}
							
							for (var j = 0; j < $scope.purchaseOrders[i].orderedProducts.length; j++) {
								$scope.checkIsOrderProductExclusive($scope.purchaseOrders[i].orderedProducts[j], $scope.purchaseOrders[i]);
							}
							
						}
						
						//grouping products by type groups
						groupProductsInOrders($scope.purchaseOrders)
						
						$scope.allPurchaseOrders = $scope.purchaseOrders;
						
						$scope.updatePageable();

						// Settle default values for sorting buttons
						$scope.currentSort[$scope.states[0].name] = 2;
						for (i = 1; i < $scope.states.length; i++) {
							$scope.currentSort[$scope.states[i].name] = 2;
						}
						$scope.sortCurrentRequests($scope.currentSort[$scope.states[0].name])

					}, function(errorResponse){
						
					});
				}, function(errorResponse){
					
				});
			}, function(){})
			
			
		}

		$scope.fixateProductsAmounts = function(order){
			for (var i = 0; i < order.orderedProducts.length; i++) {
				order.orderedProducts[i].prevAmount = order.orderedProducts[i].confirmed;
			}
		}
		
		$scope.checkIsOrderProductExclusive = function(orderedProduct, purchaseOrder){
			orderedProduct.exclusive = true;
			
			if(!orderedProduct.product.type.products){
				return;
			}
			
			
			for (var z = 0; z < orderedProduct.product.type.products.length; z++) {
				if(orderedProduct.product.type.products[z].productId.supplier.name !== purchaseOrder.supplier.name 
						&& orderedProduct.product.type.products[z].productId.productName === orderedProduct.product.productId.productName){
					orderedProduct.exclusive = false;
					
					break;
				}
			}
		}
		
		function groupProductsInOrders(purchaseOrders){
			for (var i = 0; i < purchaseOrders.length; i++) {
				purchaseOrders[i].productsGroups = {};
				
				for (var j = 0; j < purchaseOrders[i].orderedProducts.length; j++) {
					var orderedProduct = purchaseOrders[i].orderedProducts[j];
					
					if(!purchaseOrders[i].productsGroups[orderedProduct.product.type.assetGroup]){
						purchaseOrders[i].productsGroups[orderedProduct.product.type.assetGroup] = [];
					}
					
					purchaseOrders[i].productsGroups[orderedProduct.product.type.assetGroup].push(orderedProduct);
				}
			}
			
			return purchaseOrders;
		}

		$scope.initPages = function () {
			$scope.updatePageable();
		}
		
		$scope.getStateTextClass = function(state){
			switch (state.id) {
			case 11:
				return "text-success"
				break;
				
			case 12:
				return "text-warning"
				break;
				
			case 13:
				return "text-info"
				break;
				
			case 14:
				return "text-danger"
				break;

			default:
				break;
			}
		}
		
		$scope.needToShow = function(index){
			if($scope.pagesParameters){
				return index < $scope.pagesParameters.currentPage * $scope.pageSize && index >= ($scope.pagesParameters.currentPage - 1) * $scope.pageSize
						&& $scope.checkState($scope.purchaseOrders[index]);
			} else {
				return false;
			}
		}
		
		$scope.checkState = function(order){
			return $scope.currentState.name === 'All' || order.state.id === $scope.currentState.id
		}
		
		$scope.updatePageable = function() {

			$scope.purchaseOrders = [];
			
			for (var i = 0; i < $scope.allPurchaseOrders.length; i++) {
				if($scope.checkState($scope.allPurchaseOrders[i]) && ($scope.currentSupplier.val.id === -1 || $scope.allPurchaseOrders[i].supplier.id === $scope.currentSupplier.val.id)){
					$scope.purchaseOrders.push($scope.allPurchaseOrders[i]);
				}
			}
			
			
			$scope.pagesParameters = {
					currentPage: 1,
					pagesNumber: Math.ceil($scope.purchaseOrders.length / $scope.pageSize),
					pagesToShow: 5
				}
		}
		
		$scope.getStateButtonClass = function(state){
			switch(state.id){
			case undefined: return "btn-secondary"; break;
			case 11: return "btn-success"; break;
			case 12: return "btn-warning"; break;
			case 13: return "btn-info"; break;
			case 14: return "btn-danger"; break;
			}
		}
		
		$scope.setCurrentState = function(state){
			$scope.currentState = state;

			$scope.updatePageable();

			$scope.sortCurrentRequests($scope.currentSort[state.name]);
		}

		$scope.currentSort = {};
		$scope.sortCurrentRequests = function(rs){

			$scope.purchaseOrders.sort(function (a, b){
				var result = b.id - a.id;
				if(rs === 1){
					result *= -1;
				} else if(rs === 3){
					if(a.state.name < b.state.name) { result = -1; }
					if(a.state.name > b.state.name) { result = 1; }
				}

				return result;
			});

			$scope.currentSort[$scope.currentState.name] = rs;
		}
		
		$scope.addNewOrder = function(){
			$scope.newOrder = {};
			$scope.newOrder.orderedProducts = [{}]
			
			$scope.productsBySupplierForNO = angular.copy($scope.productsBySuppliers);
		}
		
		$scope.onChangeSupplierInNewOrder = function(){
			//we need to clear all ordered products after supplier change
			$scope.newOrder.orderedProducts = [{}]

			$scope.doNotMarkInputsAsInvalidFlag = true;
		}
		
		$scope.deleteNewOrder = function(){
			$scope.newOrder = undefined;
		}
		
		$scope.removeProductFromNewOrder = function(index){
			$scope.newOrder.orderedProducts.splice(index, 1);
			$scope.newOrderProductSelectApis.splice(index, 1);

			//set doNotMarkInputsAsInvalidFlag as newOrder validation result (clear error input show after deleting last error asset line)
            $scope.doNotMarkInputsAsInvalidFlag = $scope.isNewOrderValid($scope.newOrder);
		}
		
		$scope.addProductToNewOrder = function(){
		    //before adding new asset line new order will be validated to know how to show new asset lines
            $scope.doNotMarkInputsAsInvalidFlag = $scope.isNewOrderValid($scope.newOrder);

			$scope.newOrder.orderedProducts.push({});
			$scope.newOrderProductSelectApis.push({});
		}

		$scope.setProduct = function(product, op) {
			op.product = product;
		}

		$scope.saveNewOrder = function(){
            $scope.doNotMarkInputsAsInvalidFlag = false; //can mark invalid inputs
		    if ($scope.isNewOrderValid($scope.newOrder)) {
                $http.post(projectRoot + "/msc-api/distribution/purchase-orders", $scope.newOrder).then(function(response){
                    $scope.newOrder = undefined;
                    var savedOrder  = response.data;

                    groupProductsInOrders([savedOrder])

                    var present = false;

                    for (var i = 0; i < $scope.allPurchaseOrders.length; i++) {
                        if($scope.allPurchaseOrders[i].id === savedOrder.id){
                            $scope.allPurchaseOrders[i] = savedOrder;
                            present = true;
                            break;
                        }
                    }
                    if(!present){
                        $scope.allPurchaseOrders.splice(0, 0, savedOrder);
                    }

                    if($scope.allPurchaseOrders !== $scope.purchaseOrders){
                        present = false;

                        for (i = 0; i < $scope.purchaseOrders.length; i++) {
                            if($scope.purchaseOrders[i].id === savedOrder.id){
                                $scope.purchaseOrders[i] = savedOrder;
                                present = true;
                                break;
                            }
                        }
                        if(!present){
                            $scope.purchaseOrders.splice(0, 0, savedOrder);
                        }

                    }


                    $rootScope.successNotification("Order saved");
                }, function(errorResponse){
                    console.log("error response, save purchase order", errorResponse);

                    $rootScope.errorNotification("Error on order save");
                });
            }
		}
		
		//basically, sorting by supplier
		$scope.changeSupplier = function(){
		}
		
		$scope.numberFormatter = function(number) {
			return number.toFixed(2);
		}
		
		$scope.productsToAdd = []

		$scope.openAddItemModal = function(order){
			$scope.orderToAddItem = order;

			$scope.productsToAdd.length = 0;

			$scope.productsToAdd.push(...$scope.productsBySuppliers[order.supplier.name]); //for not to loose reference to the original array
			
			//removing from select array products, that already are in order
			for (var i = 0; i < $scope.productsToAdd.length; i++) {
				for (var j = 0; j < $scope.orderToAddItem.orderedProducts.length; j++) {
					if($scope.orderToAddItem.orderedProducts[j].product.id === $scope.productsToAdd[i].id){
						$scope.productsToAdd.splice(i--, 1);
						
						break;
					}
				}
			}

			$('#addItemToOrder').modal('show');
		}

		$scope.setAddedProduct = function(product) {
			$scope.addItemModalOptions.productToAdd = product;
		}
		
		$scope.addItemModalOptions = {
			isFrozen: function(){
				return $scope.addingItemToOrder;
			},
			confirmDisabled: function(){
				return !$scope.addItemModalOptions.productToAdd || !$scope.addItemModalOptions.amount;
			},
			addProductSelectApi: {}
		};
		
		$scope.addItemToOrder = function(item){
			var request = item;
			if(!request){
				request = {
					purchaseOrder: $scope.orderToAddItem,
					product: $scope.addItemModalOptions.productToAdd,
					amount: $scope.addItemModalOptions.amount
				}
			}
			
			$scope.addingItemToOrder = true;
			
			$http.post(projectRoot + "/msc-api/distribution/add-product-to-order", request).then(function(response){

				request.purchaseOrder = undefined;
				$scope.orderToAddItem.orderedProducts.push(request);
				
				if(!$scope.orderToAddItem.productsGroups[request.product.type.assetGroup]){
					$scope.orderToAddItem.productsGroups[request.product.type.assetGroup] = [];
				}
				
				$scope.orderToAddItem.productsGroups[request.product.type.assetGroup].push(request);
				
				$scope.checkIsOrderProductExclusive(request, $scope.orderToAddItem);
				
				$scope.addingItemToOrder = false;
				
				$('#addItemToOrder').modal('hide');
				
				$scope.addItemModalOptions.productToAdd = null;
				$scope.addItemModalOptions.amount = null;
				
				$rootScope.successNotification("Product added to the order");

				$scope.addItemModalOptions.addProductSelectApi.refresh();
			}, function(errorResponse){
				console.log("error to add item", errorResponse);
				
				$rootScope.errorNotification("Error on adding product to the order");
				
				$scope.addingItemToOrder = false;
				
				$('#addItemToOrder').modal('hide');
				
				$scope.addItemModalOptions.productToAdd = null;
				$scope.addItemModalOptions.amount = null;

				$scope.addItemModalOptions.addProductSelectApi.refresh();
			})
			
		}
		
		$scope.openRemoveProductModal = function(orderedProduct, order){
			$scope.orderedProductToRemove = orderedProduct;
			$scope.orderToRemoveProductFrom = order;
			
			$scope.removeProductModalText = "Do you want to remove " + orderedProduct.product.type.fullName + " " + (orderedProduct.product.productId.name ? orderedProduct.product.productId.name : "") + " from order No " + order.id + "?";
			
			$('#removeProductModal').modal('show');
			
		}
		
		$scope.removeProductFromOrder = function(onSuccFunc, onFailFunc){
			$scope.removingProduct = true;
			$http.delete(projectRoot + "/msc-api/distribution/ordered-pruduct/" + $scope.orderedProductToRemove.product.id  + "/" + $scope.orderToRemoveProductFrom.id).then(function(response){
				var group = $scope.orderToRemoveProductFrom.productsGroups[$scope.orderedProductToRemove.product.type.assetGroup];
				group.splice(group.indexOf($scope.orderedProductToRemove), 1);
				
				$scope.orderToRemoveProductFrom.orderedProducts.splice($scope.orderToRemoveProductFrom.orderedProducts.indexOf($scope.orderedProductToRemove), 1);
				
				$scope.orderedProductToRemove = undefined;
				$scope.orderToRemoveProductFrom = undefined;
				
				$scope.removingProduct = false;
				
				if(typeof onSuccFunc === 'function'){
					onSuccFunc()
				}
				
				$rootScope.successNotification("Product removed from the order");
			}, function(errorResponse){
				$rootScope.errorNotification("Error on product remove from the order");
				
				$scope.orderedProductToRemove = undefined;
				$scope.orderToRemoveProductFrom = undefined;
				$scope.removingProduct = false;
				
				if(typeof onFailFunc === 'function'){
					onFailFunc()
				}
			});
			
			$('#removeProductModal').modal('hide');
		}

		$scope.editOrderedProductModalOptions = {
			isFrozen: function(){
				return $scope.editingOrderedProduct;
			},
			confirmDisabled: function(){
				return !$scope.orderedProductForEdit || !$scope.orderedProductForEdit.newAmount;
			}
		};
		
		$scope.openEditOrderedProduct = function(orderedProduct, order) {
			$scope.orderedProductForEdit = orderedProduct
			$scope.orderToEditProduct = order;
			
			$scope.orderedProductForEdit.newAmount = $scope.orderedProductForEdit.amount;
			
			$('#editOrderedProduct').modal('show');
		}
		
		$scope.editOrderedProduct = function(product) {
			$scope.editingOrderedProduct = true;
			
			var request = product;
			
			if(!request){
				request = {
					purchaseOrder: $scope.orderToEditProduct,
					product: $scope.orderedProductForEdit.product,
					amount: $scope.orderedProductForEdit.newAmount
				}
			}
			
			$http.post(projectRoot + "/msc-api/distribution/ordered-product", request).then(function(response){
				$scope.orderedProductForEdit.amount = request.amount;
				 
				 $scope.editingOrderedProduct = false;
				 $('#editOrderedProduct').modal('hide');
				 
				 $rootScope.successNotification("Order edited");
			}, function(errorResponse){
				$rootScope.errorNotification("Error on order edit");
				
				$scope.editingOrderedProduct = false;
			});
		}

		$scope.splitProductModalOptions = {
			isFrozen: function(){
				return $scope.splittingProduct;
			},
			confirmDisabled: function(){
				return !$scope.splitRequest || !$scope.splitRequest.quantity || !$scope.splitRequest.product || !$scope.isValidSplitQuantity();
			}
		};
		
		$scope.isValidSplitQuantity = function(){
			return $scope.splitRequest && $scope.productToSplit && $scope.splitRequest.quantity <= $scope.productToSplit.amount && $scope.splitRequest.quantity !== null;
		}
		
		$scope.openCreateOrdersModal = function(){
			$('#createOrdersFromQueue').modal('show');
		}
		
		$scope.openSendModal = function(order){
			$scope.orderToSend = order;
			
			$('#sendOrder').modal('show');
		}
		
		$scope.addCredsToOrder = function(order){
			if(!order.creds){
				order.creds = [];
			}
			
			order.creds.push({invoice: "", notes: ""});
		}
		
		$scope.sendOrder = function(){
			$scope.sendingOrder = true;
			
			$http.put(projectRoot + "/msc-api/distribution/send-order/" + $scope.orderToSend.supplier.id, $scope.orderToSend).then(function(response){
				$http.put(projectRoot + "/msc-api/distribution/purchase-orders/" + $scope.orderToSend.id + "/change-state/12").then(function(response){
					$scope.orderToSend.state = response.data.state;
					$scope.orderToSend.stateChangeDate = response.data.stateChangeDate;
					
					for (var i = 0; i < $scope.orderToSend.orderedProducts.length; i++) {
						$scope.orderToSend.orderedProducts[i].confirmed = $scope.orderToSend.orderedProducts[i].amount;
					}
					
					if(!$scope.orderToSend.creds || $scope.orderToSend.creds.length === 0){
						$scope.addCredsToOrder($scope.orderToSend);
					}
					
					$scope.sendingOrder = false;

				}, function(errorResponse){
					$scope.sendingOrder = false;
				});
				
				$rootScope.successNotification("Order sent");
			}, function(errorResponse){
				console.log("error response", errorResponse);
				$rootScope.errorNotification("Error on order sending");
				
				$scope.sendingOrder = false;
			});
		}
		
		$scope.isInvoiceValid = function(invoice){
			return invoice && invoice.length > 0;
		}
		
		$scope.isConfirmedValid = function(confirmed){
			return typeof confirmed === 'number' && confirmed >= 0 && Number.isInteger(confirmed);
		}
		
		$scope.isConfirmedValidForReplenish = function(confirmed, prevAmount){
			return typeof confirmed === 'number' && confirmed >= 0 && Number.isInteger(confirmed) && (prevAmount === undefined ? true : confirmed >= prevAmount);
		}
		
		$scope.openReplenishModal = function(order){

			for (var i = 0; i < order.creds.length; i++) {
				if(!$scope.isInvoiceValid(order.creds[i].invoice)){
					return;
				}
			}
			
			for (i = 0; i < order.orderedProducts.length; i++) {
				if(order.orderedProducts[i].prevAmount && !$scope.isConfirmedValidForReplenish(order.orderedProducts[i].confirmed, order.orderedProducts[i].prevAmount)){
					return;
				}
			}
			
			$scope.orderToReplenish = order;
			
			$('#replenish').modal('show');
		}
		
		$scope.replenish = function(){
			$http.put(projectRoot + "/msc-api/distribution/replenish", $scope.orderToReplenish).then(function(response){
//				if(response.data){ //this is used for new order creation logic
//					groupProductsInOrders([response.data]);
//					
//					$scope.purchaseOrders.splice(0, 0, response.data);
//				}
				
				$scope.orderToReplenish.state = response.data.state;
				$scope.orderToReplenish.stateChangeDate = response.data.stateChangeDate;
				
				if($scope.orderToReplenish.state.id === 13){
					$scope.addCredsToOrder($scope.orderToReplenish);
				}
				
				$scope.fixateProductsAmounts($scope.orderToReplenish);
				//$scope.orderToClose.creds
				
				$rootScope.successNotification("Order replenished");
			}, function(errorResponse){
				$rootScope.errorNotification("Error on order replenish");
			});
		}

		function loadUserCreds(){
			$http.get(projectRoot + "/msc-api/top-data/get-user-creds").then(function(response){
				$scope.currentUser = response.data;
			}, function(errorResponse){
				
			});
		}
		
		loadUserCreds();
		
		$scope.previewOrderPDF = function(order){
			
			window.open(projectRoot + "/msc-api/distribution/get-order-pdf/" + order.id, "_blank");
		}
		
		loadData();
		
		$scope.htmlOrderBody = function(order){
			switch(order.state.id){
			case 11: {
				return "/msc/resources/views/distribution/purchase-request-new.html";
			}
			break;
			case 12: {
				return "/msc/resources/views/distribution/purchase-request-sent.html";
			}
			break;
			case 14: {
				return "/msc/resources/views/distribution/purchase-request-fulfilled.html";
			}
			case 13: {
				return "/msc/resources/views/distribution/purchase-request-partially-fulfilled.html";
			}
			}
		}

		$scope.returnTrue = function() {
			return true;
		}

		$scope.returnFalse = function() {
			return false;
		}

        $scope.isAssetInputValid = function(index) {
		    return !!$scope.newOrder.orderedProducts[index].product || $scope.doNotMarkInputsAsInvalidFlag;
        }

        $scope.isAmmountInputValid = function(index) {
            return !!$scope.newOrder.orderedProducts[index].amount || $scope.doNotMarkInputsAsInvalidFlag;
        }

        $scope.isNewOrderValid = function (order) {
            let result = true;
            for (let i = 0; i < order.orderedProducts.length; i++) {
                //if there is any empty field, then order is not valid
                if (!order.orderedProducts[i].product || !order.orderedProducts[i].amount) {
                    result = false;
                }
            }
		    return result;
        }



	}]
);
