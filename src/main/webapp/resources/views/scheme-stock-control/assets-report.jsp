<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Assets report</title>

<%@ include file="/resources/views/html-headers/header.html"%> 

<script type="application/javascript"
	src="../resources/logic/scheme-stock-control/assets-report.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="assetsReportCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">
			<div>This page is used for daily report.</div>
			<div>Select all used spare parts and supplies from the list  and save in the end of day.</div>
			<div class="mb-2">This will update current Scheme stock amounts.</div>   
		
		<h5 class="msc-inline-block">Your scheme: </h5>
		<select class="msc-inline-block form-control" style="max-width:220px;" data-ng-model="currentScheme" data-ng-options="scheme.name for scheme in schemes" data-ng-change="loadACVs()"></select>
		
		<h5 data-ng-show="currentScheme" class="mt-2" data-ng-cloak>Enter list of used assets:</h5>
		<div data-ng-if="currentScheme && acvs && usedACVs" data-ng-repeat="usedACV in usedACVs" class="row mt-1" data-ng-cloak>
			<select class="form-control col-5 col-md-4 col-lg-3 ml-3 mt-1" data-ng-model="usedACV.acv" data-ng-options="acv.productType.typeName for acv in acvs" placeholder="Asset"></select>
			<input type="number" class="form-control col-4 col-md-3 col-lg-2 ml-3 mt-1" data-ng-model="usedACV.usedAmount" placeholder="Used amount">
			<button data-ng-click="removeUsedACV($index)"
				class="btn btn-danger msc-round bigger-18 mt-3 ml-2"
				style="color: #FFFFFF; padding-left: 1px; padding-top: 1px">
				<em class="material-icons md-14">clear</em>
			</button>
		</div>
		<button data-ng-click="addNewUsedACV()" data-ng-show="currentScheme && acvs && usedACVs" data-ng-cloak
				class="btn btn-success msc-round bigger-18 mt-2"
				style="color: #FFFFFF; padding-left: 1px; padding-top: 1px">
				<em class="material-icons md-14">add</em>
			</button>
		<button class="btn btn-success mt-3 float-right" data-ng-show="currentScheme && acvs && usedACVs.length > 0" data-ng-click="openSavedAssetsInfoModal()" data-ng-disabled="!validateUsedACVs() || updating" data-ng-cloak>Save</button>
		
		
		
		<confirm-modal data-modal-id="'savedAssetsInfoModal'"
			data-on-confirm="savedAssetsInfo"
			data-title="'Save used assets amount?'"
			data-text="''"></confirm-modal>
	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>