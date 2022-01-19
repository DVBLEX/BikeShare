<%@ page language="java" contentType="text/html; UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>Assets values (only for developers)</title>

<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="/msc/resources/dev-scope/assets-values.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>
</head>
<body data-ng-app="mainModule"
	style="background-color: #ccf9d1; height: 100%;"
	data-ng-controller="assetsValuesCtrl">

		<div class="container pt-2 pb-2"
				style="background-color: white; min-height: 100%;">
			<h6>Add Assets Margin Value</h6>
			<div class="row">
				<div class="col-md-3">
					<searchable-select name="'asset' + $index"
									   set-model="setAmvAsset"
									   array="typesOfAssets"
									   search-by-field="'fullName'"
									   is-disabled="false"
									   is-valid="true"
									   small="true">
					</searchable-select>
				</div>
				<div class="col-md-3">
					<select data-ng-options="sch.name for sch in schemes" data-ng-model="amv.scheme"></select>
				</div>
				<div class="col-md-2">
					<input type="number" data-ng-model="amv.orderValue" placeholder="Order value">
				</div>
				<div class="col-md-2">
					<input type="number" data-ng-model="amv.lowerValue" placeholder="Lower value">
				</div>
				<div class="col-md-2">
					<button class="btn btn-success"
						data-ng-click="addAmv()">Add</button>
				</div>
			</div>
			
			<h6>Add Assets Сurrent Value</h6>
			<div class="row">
				<div class="col-md-3">
					<searchable-select name="'asset' + $index"
									   set-model="setAcvAsset"
									   array="typesOfAssets"
									   search-by-field="'fullName'"
									   is-disabled="false"
									   is-valid="true"
									   small="true">
					</searchable-select>
				</div>
				<div class="col-md-3">
					<select data-ng-options="sch.name for sch in schemes" data-ng-model="acv.scheme"></select>
				</div>
				<div class="col-md-4">
					<input type="number" data-ng-model="acv.quantity" placeholder="Quantity">
				</div>
				<div class="col-md-2">
					<button class="btn btn-success"
						data-ng-click="addAcv()">Add</button>
				</div>
			</div>	
		</div>
</body>
</html>
