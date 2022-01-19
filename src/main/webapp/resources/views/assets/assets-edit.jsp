<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>Assets Profiles</title>

<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="/msc/resources/logic/assets/assets-edit.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="assetsEditCtrl">

	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">

		<ul class="nav nav-tabs" role="tablist" data-ng-cloak>
			<li class="nav-item" data-ng-repeat="groupName in groups" data-ng-show="!userIsOnlyFulfillmentOperator || (userIsOnlyFulfillmentOperator && belongsToFulfillment(groupName))"><a
				class="nav-link" data-ng-class="{'active': activeTab === $index}"
				id="{{'tab-' + $index}}" href="#" data-toggle="tab" role="tab"
				aria-selected="{{activeTab === $index ? 'true' : 'false'}}"
				data-ng-click="setActiveTab($index)">{{groupName}}</a></li>
		</ul>
		<div class="d-flex justify-content-between">
			<button class="btn btn-sm btn-success my-1"
				data-ng-click="openAddNewProductModal()">Add</button>
			
			<div class="mt-1">
				<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in gridPageSizes" data-ng-model="gridPageSize" data-ng-change="setGridPageSize(gridPageSize)"></select> <span class="">entries</span>
			</div>
		</div>
		
		<div ag-grid="productsGridOptions" class="ag-theme-balham"></div>


		<content-modal modal-id="'editProductModal'" max-width="'500px'"
			on-confirm="saveEditedProduct" on-confirm-text="'Save'"
			title="'Edit Asset'" hide-manually-on-confirm="true"
			is-frozen="isFrozen" data-ng-cloak>

			<form>
				<div class="form-row">
					<div class="form-group col-md-12">
						<label for="product-name">Asset Name</label>
						<input type="text" class="form-control" id="full-name" data-ng-model="editableProduct.type.groupName"
						   placeholder="Asset Name" data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.groupName}" required>
					</div>
					<div class="form-group col-md-12">
						<label for="product-name">Asset Description</label>
						<input type="text" class="form-control" id="product-name" data-ng-model="editableProduct.type.typeName"
							placeholder="Asset Description" data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.type}">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-md-6">
						<label for="product-name">Parts Number</label>
						<input type="text" class="form-control" id="name-classifier" data-ng-model="editableProduct.productId.productName"
							placeholder="Parts Number" data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.name}">
					</div>
					<div class="form-group col-md-6">
						<label for="product-supplier">Supplier</label>
						<select class="form-control"
							id="product-supplier" placeholder="Supplier"
							data-ng-model="editableProduct.productId.supplier"
							data-ng-options="supplier.name for supplier in suppliers track by supplier.id"
							data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.supplier}"
							required></select>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-md-6">
						<label for="product-name">Minimal Order Quantity</label> <input type="number"
							class="form-control" id="minimal-order" data-ng-model="editableProduct.minOrder"
							placeholder="Minimal Order Quantity" data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.minOrder}" required>
					</div>
					<div class="form-group col-md-6">
						<label for="product-delivery-time">Delivery time</label> <input type="number"
							class="form-control" id="product-delivery-time" data-ng-model="editableProduct.deliveryTime"
							placeholder="">
					</div>
				</div>
				<div class="alert alert-danger mt-2" role="alert"
							data-ng-show="showErrorAddingExistingProduct">Error! Asset already exists</div>
			</form>
		</content-modal>
		
		
		<confirm-modal data-modal-id="'confirmDeleteModal'"
			data-on-confirm="deleteProduct"
			data-title="'Delete product'"
			data-text="'Are you sure you want to delete this product: ' + deletableProduct.type.typeName + ' ' + deletableProduct.productId.productName + '?'"></confirm-modal>
		
	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
