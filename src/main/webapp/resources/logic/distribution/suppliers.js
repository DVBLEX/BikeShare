"use strict";

app.controller('suppliersCtrl', [
	'$scope',
	'$http',
	'$timeout',
	'$q',
	'$rootScope',
	function($scope, $http, $timeout, $q, $rootScope) {
		$scope.gridPageSizes = [20, 50, 100];
		$scope.gridPageSize = $scope.gridPageSizes[0];
		
		var loadData = function(){
			$http.get(projectRoot + "/msc-api/assets/suppliers").then(function(suppliersResponse){
				
				$scope.suppliers = suppliersResponse.data;
				
				$scope.suppliersGridOptions.api.setRowData($scope.suppliers);
			}, function(errorResponse){
				
			});
		}
		
		loadData();
		
		$scope.openAddNewSupplierModal = function(){
			$scope.editableSupplier = {
				newlyCreated: true
			};
			
			$scope.openEditModal();
		}
		
		$scope.openEditModal = function(){
			$scope.validitiyOfFieldsOfEditModal = {
					name: true,
					email: true,
					phone: true,
					contact: true,
					website: true,
				}
			
			$('#editSupplierModal').modal('show');
		}
		
		$scope.editSupplier = function(supplier, rowId){
			
			$scope.mainEditingSupplier = supplier;
			$scope.editableSupplier = angular.copy(supplier);
			$scope.editedRowId = rowId;
			
			$scope.openEditModal();
		};
		
		
		//--grid-related-stuff--
		
		var editCellRendererFunc = function(params){
			 //params.$scope.editSupplier = $scope.editSupplier;
			 
			 params.$scope.editable = params.node.data;
			 params.$scope.rowId = params.node.id;
			 
			 return '<button type="button" class="btn btn-info btn-sm btn-block" style="height:25px; padding: 3px 8px;" data-ng-click="editSupplier(editable, rowId)"><i class="material-icons md-18">edit</i></button>';
		}
		
		var deleteRowCellRendererFunc = function(params){
			//params.$scope.deleteSupplier = $scope.deleteSupplier;
			 
			params.$scope.rowId = params.node.id;
			params.$scope.deletable = params.node.data;
			
			return '<button type="button" class="btn btn-danger btn-sm btn-block" style="height:25px; padding: 3px 8px;" data-ng-click="opedDeleteSupplierConfirmModal(deletable)"><i class="material-icons md-18">clear</i></button>';
		}
		
		var columnDefs = [
			{headerName: "Name", field: "name", filter: "agTextColumnFilter", width: 130},
			{headerName: "Email", field: "email", filter: "agTextColumnFilter", width: 220},
			{headerName: "Phone", field: "phone", filter: "agTextColumnFilter", width: 130},
			{headerName: "Address", field: "contact", filter: "agTextColumnFilter", width: 150},
			{headerName: "Website", field: "website", filter: "agTextColumnFilter", width: 185},
			{headerName: "Miscellaneous", field: "miscellaneous", filter: "agTextColumnFilter", width: 150},
	        
	        {headerName: "Edit", field: "", width: 70, cellRenderer: editCellRendererFunc, pinned: "'right"},
	        {headerName: "Delete", field: "", width: 70, cellRenderer: deleteRowCellRendererFunc, pinned: "right"},
	    ];
		
		$scope.suppliersGridOptions = {
			defaultColDef: {
			    resizable: true
			},
		    columnDefs: columnDefs,
		    rowData: null,
		    pagination: true,
		    paginationPageSize: 40,
		    floatingFilter: true,
		    angularCompileRows: true,
		    domLayout: 'autoHeight',
		};
		
		$scope.setGridPageSize = function(pageSize){
			$scope.suppliersGridOptions.api.paginationSetPageSize(pageSize)
		}
		//--grid-related-stuff--end--
		
		
		$scope.opedDeleteSupplierConfirmModal = function(supplier){
			$('#confirmDeleteModal').modal('show');
			
			$scope.deletableSupplier = supplier;
		}
		
		$scope.deleteSupplier = function(){
			$http.delete(projectRoot + "/msc-api/assets/suppliers/" + $scope.deletableSupplier.id).then(function(response){
				$scope.suppliers.splice($scope.suppliers.indexOf($scope.deletableSupplier), 1);
				
				$scope.suppliersGridOptions.api.updateRowData({remove: [$scope.deletableSupplier]});
				
				$rootScope.successNotification("Supplier deleted");
			}, function(errorResponse){
				$rootScope.errorNotification("Error on supplier delete. Usually, suppliers are used across the whole system and can't be just deleted");
			});
		}
		
		$scope.updateSupplierFromEdited = function(supplier){
			supplier.name = $scope.editableSupplier.name;
			supplier.phone = $scope.editableSupplier.phone;
			supplier.email = $scope.editableSupplier.email;
			supplier.contact = $scope.editableSupplier.contact;
			supplier.website = $scope.editableSupplier.website;
			supplier.miscellaneous = $scope.editableSupplier.miscellaneous;
		}
		
		$scope.isFrozen = function(){
			return $scope.saving;
		}
		
		$scope.checkValiditiyOfFieldsOfEditModal = function(){
			$scope.validitiyOfFieldsOfEditModal.name = $scope.editableSupplier.name !== null && $scope.editableSupplier.name !== undefined && $scope.editableSupplier.name.length > 0;
			$scope.validitiyOfFieldsOfEditModal.phone = $scope.editableSupplier.phone !== null && $scope.editableSupplier.phone !== undefined && $scope.editableSupplier.phone.length > 0 ? /^[0-9- ()]{2,15}$/.test($scope.editableSupplier.phone) : true;
			$scope.validitiyOfFieldsOfEditModal.email = $scope.editableSupplier.email !== null && $scope.editableSupplier.email !== undefined && $scope.editableSupplier.email.length > 0;
			$scope.validitiyOfFieldsOfEditModal.contact = true;
			$scope.validitiyOfFieldsOfEditModal.website = true;
			$scope.validitiyOfFieldsOfEditModal.miscellaneous = true;
			
			
			return $scope.validitiyOfFieldsOfEditModal.name && $scope.validitiyOfFieldsOfEditModal.phone && $scope.validitiyOfFieldsOfEditModal.email && $scope.validitiyOfFieldsOfEditModal.contact && $scope.validitiyOfFieldsOfEditModal.website;
		}
		
		
		$scope.saveSupplier = function(supplier){
			
			var deferred = $q.defer();
			
			$scope.saving = true;
			
			$http.post(projectRoot + "/msc-api/assets/suppliers", supplier).then(function(response){
				deferred.resolve(response);
				
				$scope.saving = false;
			}, function(errorResponse){
				deferred.reject(errorResponse.data);
				if(errorResponse.data.indexOf("Data too long for column 'phone'") > -1){
					$rootScope.errorNotificationWithTitle("Phone is too long", "Maximum length is 15");
				} else {
					$rootScope.errorNotification("Error on supplier save");
				}
				$scope.saving = false;
			});
			
			return deferred.promise;
		}
		
		$scope.saveEditedSupplier = function(){
			if(!$scope.checkValiditiyOfFieldsOfEditModal()){
				
				$rootScope.validationErrorNotificationWithTitle();
				
				if($scope.editableSupplier.phone.length > 15){
					$rootScope.infoNotificationWithTitle("Phone is too long", "Maximum length is 15");
				}
				
				return;
			}
			
			
			$scope.saveSupplier($scope.editableSupplier).then(function(response){
				if($scope.editableSupplier.newlyCreated){
					$scope.editableSupplier.newlyCreated = false;
					$scope.editableSupplier.id = response.data;
					$scope.suppliers.push($scope.editableSupplier);
					
					$scope.suppliersGridOptions.api.setRowData($scope.suppliers);
				} else {
					$scope.updateSupplierFromEdited($scope.mainEditingSupplier);
					
					$scope.suppliersGridOptions.api.getRowNode($scope.editedRowId).setData($scope.mainEditingSupplier);
				}
				
				$rootScope.successNotification("Supplier saved");
				
				$('#editSupplierModal').modal('hide');
			}, function(){
			});
		}
	}]
);
