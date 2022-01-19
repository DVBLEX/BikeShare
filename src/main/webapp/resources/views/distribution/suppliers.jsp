<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>Suppliers</title>

<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="/msc/resources/logic/distribution/suppliers.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="suppliersCtrl">

	<%@ include file="/resources/views/navbar.html"%>
	
	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">
		
		<div class="d-flex justify-content-between">
			<button class="btn btn-sm btn-success my-1"
				data-ng-click="openAddNewSupplierModal()">Add</button>
				
			<div>
				<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in gridPageSizes" data-ng-model="gridPageSize" data-ng-change="setGridPageSize(gridPageSize)"></select> <span class="">entries</span>
			</div>
		</div>
		
		<div ag-grid="suppliersGridOptions" class="ag-theme-balham"></div>		
		
		<content-modal modal-id="'editSupplierModal'" max-width="'500px'"
			on-confirm="saveEditedSupplier" on-confirm-text="'Save'"
			title="'Edit Supplier'" hide-manually-on-confirm="true"
			is-frozen="isFrozen" data-ng-cloak>

			<form>
				<div class="form-row">
					<div class="form-group col-md-12">
						<label for="supplier-name">Name</label> <input type="text"
							class="form-control" id="supplier-name" data-ng-model="editableSupplier.name"
							placeholder="Name" data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.name}" required>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-md-5">
						<label for="supplier-phone">Phone</label> <input type="text"
							class="form-control" id="supplier-phone" data-ng-model="editableSupplier.phone"
							placeholder="Phone" data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.phone}">
					</div>
					<div class="form-group col-md-7">
						<label for="supplier-website">Website</label> <input type="text"
							class="form-control" id="supplier-website" data-ng-model="editableSupplier.website"
							placeholder="www.website.com" data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.website}">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-md-4">
						<label for="supplier-contact">Address</label> <input type="text"
							class="form-control" id="supplier-contact" data-ng-model="editableSupplier.contact"
							placeholder="Address" data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.contact}">
					</div>
					<div class="form-group col-md-8">
						<label for="supplier-email">Email</label> <input type="text"
							class="form-control" id="supplier-email" data-ng-model="editableSupplier.email"
							placeholder="supplier@email.com" data-ng-class="{'is-invalid' : !validitiyOfFieldsOfEditModal.email}">
					</div>
				</div>
				<div class="pretty p-default p-curve">
					<input type="checkbox" id="supplier-miscellaneous" data-ng-model="editableSupplier.miscellaneous">
					<div class="state p-primary">
						<label>Miscellaneous</label>
					</div>
				</div>
			</form>
		</content-modal>
		
		
		<confirm-modal data-modal-id="'confirmDeleteModal'"
			data-on-confirm="deleteSupplier"
			data-title="'Delete supplier'"
			data-text="'Are you sure you want to delete this supplier ' + deletableSupplier.name + '?'"></confirm-modal>
	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
