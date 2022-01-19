<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Purchase Orders</title>

<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="../resources/logic/distribution/purchase-orders.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>
	
<script type="application/javascript"
	src="/msc/resources/services/load-user-roles-service.js"></script>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/select2/3.4.5/select2.css">    
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.8.5/css/selectize.default.css">

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.9/dist/css/bootstrap-select.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.9/dist/js/bootstrap-select.min.js"></script>
    

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="purchaseOrdersCtrl">

	<%@ include file="/resources/views/navbar.html"%>
	
	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">

		<div ng-if="!purchaseOrders" class="text-center mt-5">
			<div class="spinner-border size-spinner" role="status">
				<span class="sr-only">Loading...</span>
			</div>
		</div>

		<div ng-if="purchaseOrders">
			<div class="msc-inline-block mr-1 h6">State: </div>
			<div class="btn-group mb-2 mr-3 msc-inline-block" role="group" aria-label="States" data-ng-cloak>
				<button type="button" class="btn" data-ng-class="getStateButtonClass(state)" data-ng-repeat="state in states"
						data-ng-click="setCurrentState(state)" data-ng-disabled="state.id === currentState.id">{{state.name}}</button>
			</div>

			<div class="msc-inline-block mr-3" >
				<div class="msc-inline-block mr-1 h6">Sorting: </div>
				<div class="btn-group mb-1" role="group" aria-label="Sort" data-ng-cloak>
					<button type="button" class="btn btn-secondary"
							data-ng-click="sortCurrentRequests(1)" data-ng-disabled="currentSort[currentState.name] === 1">Asc</button>
					<button type="button" class="btn btn-secondary"
							data-ng-click="sortCurrentRequests(2)" data-ng-disabled="currentSort[currentState.name] === 2">Desc</button>
					<button type="button" class="btn btn-secondary"
							data-ng-click="sortCurrentRequests(3)" data-ng-disabled="currentSort[currentState.name] === 3" data-ng-if="currentState.name === 'All'">State</button>
				</div>
			</div>

			<div class="msc-inline-block mr-3">
				<h6 class="msc-inline-block">Search by Supplier:</h6>
				<select class="form-control form-control-sm msc-inline-block col-lg-2 col-4" style="min-width: fit-content" data-ng-if="allSuppliers" data-ng-model="currentSupplier.val" data-ng-options="supplier.name for supplier in allSuppliers" data-ng-change="initPages()"></select>
			</div>

			<div class="my-1 msc-inline-block">
				<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in pageSizes" data-ng-model="pageSize" data-ng-change="initPages()"></select> <span class="">entries</span>
			</div>
			<br>
			<button class="btn btn-success btn-sm msc-inline-block" data-ng-click="addNewOrder()" data-ng-disabled="newOrder">Add New Order</button>

			<!-- placeholder for not loaded orders -->
			<div class="row mx-2 mt-1" data-ng-if="!purchaseOrders || creatingOrdersFromQueue">
				<div class="col-12" style="height:300px; background-color: #f8f9fa"></div>
			</div>

			<div id="accordion-new" class="mt-1" data-ng-cloak data-ng-show="!creatingOrdersFromQueue">

				<div class="card" data-ng-if="newOrder">
					<div class="card-header">
						<div class="">
							<h5 class="mb-0" style="display: inline-block;">
								<button class="btn btn-info msc-round bigger-18 mr-2"
										style="color: #FFFFFF; padding-left: 1px; padding-top: 1px"
										data-toggle="collapse"
										data-target="#collapse-new" aria-expanded="false"
										aria-controls="collapse-new">
									<i class="material-icons md-14">more_horiz</i>
								</button>
								New Purchase order
							</h5>
						</div>
					</div>
					<div id="collapse-new" class="collapse show"
						 aria-labelledby="heading-new" data-parent="#accordion">
						<div class="card-body pt-0 px-3">
							<div class="row mt-1">
								<div class="col-sm-4">
									<div class="row">
										<label class="col-sm-4 col-form-label">Supplier:</label>
										<div class="col-8 mt-1">
											<select class="form-control form-control-sm" data-ng-options="s.name for s in realSuppliers" data-ng-model="newOrder.supplier" data-ng-change="onChangeSupplierInNewOrder()"></select>
										</div>
									</div>
								</div>
								<div class="col-sm-8">
									<div class="row align-items-center" data-ng-repeat="op in newOrder.orderedProducts" data-ng-if="newOrder.supplier">
										<label class="col-md-2 col-form-label">Asset:</label>
										<div class="col-md-6 mt-1">
											<searchable-select
													name="'new-order-product' + $index"
													api="newOrderProductSelectApis[$index]"
													set-model="setProduct"
													add-params="[op]"
													array="productsBySupplierForNO[newOrder.supplier.name]"
													search-by-field="'fullName'"
													is-disabled="returnFalse"
													is-valid="isAssetInputValid($index)"
													small="true">
											</searchable-select>
										</div>
										<div class="col-sm-3 mt-1 ammount-input">
											<input
													type="number"
													name="'new-order-product' + $index + 'amount'"
													class="form-control form-control-sm"
													pattern="[0-9]*"
													data-ng-model="op.amount"
													data-ng-class="{'is-invalid': !isAmmountInputValid($index)}"
											>
										</div>
										<div class="col-1 mt-1">
											<button data-ng-click="removeProductFromNewOrder($index)"
													class="btn btn-danger close-btn d-flex align-items-center justify-content-center">
												<i class="material-icons md-18 text-white">clear</i>
											</button>
										</div>
									</div>

									<button data-ng-click="addProductToNewOrder()" data-ng-show="newOrder.supplier"
											class="btn btn-success msc-round bigger-18"
											style="color: #FFFFFF; padding-left: 1px; padding-top: 1px">
										<i class="material-icons md-14">add</i>
									</button>

								</div>
							</div>
							<div class="row mx-1" data-ng-if="newOrder.supplier"  style="margin: 15px 0px 15px 0px">
								<input class="form-control form-control-sm col-md-12 col-lg-12" placeholder="comment" data-ng-model="newOrder.comment">
							</div>
							<div class="row">
								<div class="col-6 col-md-7"></div>
							</div>
							<div class="d-flex flex-row-reverse">
								<button class="btn btn-success btn-sm" data-ng-click="saveNewOrder()" data-ng-disabled="!newOrder.supplier && !newOrder.orderedProducts[0].product">Save</button>
								<button class="btn btn-danger btn-sm mr-2" data-ng-click="deleteNewOrder()">Delete</button>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div id="{{'accordion' + $index}}" class="" data-ng-cloak data-ng-repeat="order in purchaseOrders" data-ng-show="needToShow($index) && !creatingOrdersFromQueue">

				<div class="card">
					<div class="card-header">
						<div class="d-flex justify-content-between">
							<div class="">
								<h5 class="mb-0" style="display: inline-block;">
									<button class="btn btn-info msc-round bigger-18 mt-2 mr-2"
											style="color: #FFFFFF; padding-left: 1px; padding-top: 1px"
											data-toggle="collapse"
											data-target="{{'#collapse' + $index}}" aria-expanded="false"
											aria-controls="{{'collapse' + $index}}">
										<i class="material-icons md-14">more_horiz</i>
									</button>

									{{'Purchase order No ' + order.id}}
								</h5>
								<div class="msc-inline-block">{{"(" + order.supplier.name + ")"}}</div>
							</div>
							<div class="">
								<div class="msc-inline-block" data-ng-class="getStateTextClass(order.state)">
									<div class="msc-inline-block mt-1">{{order.state.name}}</div>
								</div>
								<div class="msc-inline-block">at {{order.stateChangeDate}}</div>
							</div>
						</div>
					</div>

					<div id="{{'collapse' + $index}}" class="collapse"
						 aria-labelledby="{{'heading' + $index}}" data-parent="#accordion">
						<div class="card-body pt-0 px-3">
							<span data-ng-include="htmlOrderBody(order)"></span>
						</div>
					</div>
				</div>
			</div>
			<pagination parameters="pagesParameters" data-ng-if="pagesParameters"
						class="msc-inline-block mt-1"></pagination>

			<confirm-modal data-modal-id="'removeProductModal'"
						   data-on-confirm="removeProductFromOrder"
						   data-title="'Remove product from order?'"
						   data-text="removeProductModalText"></confirm-modal>

			<content-modal modal-id="'addItemToOrder'" max-width="'500px'"
						   on-confirm="addItemToOrder" on-confirm-text="'Add'"
						   title="'Add Item to Order'" hide-manually-on-confirm="true"
						   is-frozen="addItemModalOptions.isFrozen" confirm-disabled="addItemModalOptions.confirmDisabled" data-ng-cloak>
				<form>
					<div class="form-group row" data-ng-if="addItemModalOptions">
						<label class="col-sm-2 col-form-label">Asset:</label>
						<div class="col-sm-10">
							<searchable-select name="'add-product-select'" api="addItemModalOptions.addProductSelectApi" set-model="setAddedProduct" array="productsToAdd" search-by-field="'fullName'" is-disabled="returnFalse"
											   is-valid="returnTrue" small="false"></searchable-select>
						</div>
					</div>
					<div class="form-group row">
						<label for="amount" class="col-sm-2 col-form-label">Amount:</label>
						<div class="col-sm-10">
							<input type="number" class="form-control" id="amount" pattern="[0-9]*" data-ng-model="addItemModalOptions.amount">
						</div>
					</div>
				</form>
			</content-modal>

			<content-modal modal-id="'editOrderedProduct'" max-width="'500px'"
						   on-confirm="editOrderedProduct" on-confirm-text="'Edit'"
						   title="'Edit Ordered Asset'" hide-manually-on-confirm="true"
						   is-frozen="editOrderedProductModalOptions.isFrozen" confirm-disabled="editOrderedProductModalOptions.confirmDisabled" data-ng-cloak>
				<form>
					<div class="form-group row">
						<label for="amount" class="col-sm-2 col-form-label">Amount:</label>
						<div class="col-sm-10">
							<input type="number" class="form-control" id="new-amount" pattern="[0-9]*" data-ng-model="orderedProductForEdit.newAmount">
						</div>
					</div>
				</form>
			</content-modal>
		</div>

	</div>
	<%@ include file="/resources/views/footer.html"%>


	<confirm-modal data-modal-id="'sendOrder'"
		data-on-confirm="sendOrder"
		data-title="'Send order'"
		data-text="''"></confirm-modal>

	<confirm-modal data-modal-id="'replenish'"
		data-on-confirm="replenish"
		data-title="'Replenish'"
		data-text="''"></confirm-modal>

</body>
</html>
