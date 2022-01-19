<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>System Setup</title>

<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="/msc/resources/services/requests-queue.js"></script>

<script type="application/javascript"
	src="../resources/logic/system/edit-tables-data.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

<style>
    #txtPassword{
    	-webkit-text-security:disc;
	}
</style>
</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="editTablesDataCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">
	
		<div class="row mb-1">
			<div class="col-md-12">
				<div class="d-flex justify-content-between mb-1"> 
					<h5>Locations</h5>
					<button class="btn btn-sm btn-info" data-ng-click="openAddLocationWindow()" data-ng-disabled="!canStartEdit()">Add</button>
				</div>
				<div ag-grid="bikeStationsGridOptions"  style="height: 400px;" class="ag-theme-balham"></div>
			</div>
		</div>

		<div class="row mb-1">
			<div class="col-md-12">
				<div class="d-flex justify-content-between mb-1">
					<h5>Bikes</h5>
					<button class="btn btn-sm btn-info" data-ng-click="openAddBikeWindow()" data-ng-disabled="!canStartEdit()">Add</button>
				</div>
				<div ag-grid="bikesGridOptions" style="height: 400px;" class="ag-theme-balham"></div>
			</div>
		</div>
		
		<div class="row">
			<div class="col-md-6">
				<div class="d-flex justify-content-between mb-1"> 
					<h5>Repairs Complete</h5>
					<button class="btn btn-sm btn-info" data-ng-click="openAddJobWindow()" data-ng-disabled="!canStartEdit()">Add</button>
				</div>
				<div ag-grid="jobsGridOptions" style="height: 400px;" class="ag-theme-balham"></div>
			</div>
			<div class="col-md-6">
				<div class="d-flex justify-content-between mb-1">
					<h5>Repairs Needed</h5>
					<button class="btn btn-sm btn-info" data-ng-click="openAddRepairReasonWindow()" data-ng-disabled="!canStartEdit()">Add</button>
				</div>
				<div ag-grid="repairReasonsGridOptions" style="height: 400px;" class="ag-theme-balham"></div>
			</div>
		</div>


		<div class="row mt-3 mb-1">
			<h5 class="col-12">Email server properties</h5>
		</div>
		<div class="row my-1">
			<div class="col-md-6">
				Email host
				<input class="form-control" data-ng-model="emailServerProperties.email_host" data-ng-disabled="!canEdit(emailServerBlock)">
			</div>
			<div class="col-md-6">
				Email port
				<input class="form-control" data-ng-model="emailServerProperties.email_port" data-ng-disabled="!canEdit(emailServerBlock)">
			</div>
		</div>
		
		<div class="row my-1">
			<div class="col-md-6">
				Email username
				<input class="form-control" data-ng-model="emailServerProperties.email_username" data-ng-disabled="!canEdit(emailServerBlock)">
			</div>
			<div class="col-md-6">
				Email password
				<input type="password" class="form-control" data-ng-model="emailServerProperties.email_password" data-ng-disabled="!canEdit(emailServerBlock)">
			</div>
		</div>
		
		<div class="row my-1">
			<div class="col-md-6">
				Email starttls
				<input type="checkbox" data-ng-model="emailServerProperties.email_starttls" data-ng-disabled="!canEdit(emailServerBlock)">
			</div>
		</div>
		<div class="d-flex flex-row-reverse" data-ng-if="!canEdit(emailServerBlock)">
			<button class="btn btn-info" data-ng-click="editBlock(emailServerBlock, emailServerProperties)" data-ng-disabled="!canStartEdit()">Edit</button>
		</div>
		<div class="d-flex flex-row-reverse" data-ng-if="canEdit(emailServerBlock)">
			<button class="btn btn-success" data-ng-click="saveBlock(emailServerProperties)">Save</button>
			<button class="btn btn-danger mr-1" data-ng-click="cancelEditBlock(emailServerProperties)">Cancel</button>
		</div>
	
		<div class="msc-line my-2"></div>
	
		<div class="row mt-3 mb-1">
			<h5 class="col-12">General</h5>
		</div>
		<div class="row my-1">
			<div class="col-md-6">
				Domain link
				<small class="text-muted d-block">Used in emails, when there is need to show a link to the site</small>
				<input class="form-control" data-ng-model="generalProperties.domain_link" data-ng-disabled="!canEdit(generalBlock)">
			</div>
		</div>
		<div class="d-flex flex-row-reverse" data-ng-if="!canEdit(generalBlock)">
			<button class="btn btn-info" data-ng-click="editBlock(generalBlock, generalProperties)" data-ng-disabled="!canStartEdit()">Edit</button>
		</div>
		<div class="d-flex flex-row-reverse" data-ng-if="canEdit(generalBlock)">
			<button class="btn btn-success" data-ng-click="saveBlock(generalProperties)">Save</button>
			<button class="btn btn-danger mr-1" data-ng-click="cancelEditBlock(generalProperties)">Cancel</button>
		</div>
		
		<div class="msc-line my-2"></div>

		<div class="row mt-3 mb-1">
			<h5 class="col-12">Stock Request Triggers</h5>
		</div>
		<div class="row my-1">
<%--			<div class="col-md-6">--%>
<%--				<small class="text-muted d-block">Enter a percentage value for an automatic Stock Request generation trigger</small>--%>
<%--				<input class="form-control" maxlength="2" data-ng-model="stockRequestProperties.stock_request_generation_trigger" data-ng-disabled="!canEdit(stockRequestBlock)">--%>
<%--			</div>--%>
			<div class="col-md-6">
				<small class="text-muted d-block">Enter a percentage value for an alert notification trigger</small>
				<input class="form-control" maxlength="2" data-ng-model="stockRequestProperties.low_stock_percentage" data-ng-disabled="!canEdit(stockRequestBlock)">
			</div>
		</div>
		<div class="d-flex flex-row-reverse" data-ng-if="!canEdit(stockRequestBlock)">
			<button class="btn btn-info" data-ng-click="editBlock(stockRequestBlock, stockRequestProperties)" data-ng-disabled="!canStartEdit()">Edit</button>
		</div>
		<div class="d-flex flex-row-reverse" data-ng-if="canEdit(stockRequestBlock)">
			<button class="btn btn-success" data-ng-click="saveBlock(stockRequestProperties)">Save</button>
			<button class="btn btn-danger mr-1" data-ng-click="cancelEditBlock(stockRequestProperties)">Cancel</button>
		</div>

		<div class="msc-line my-2"></div>

		<div class="row mt-3 mb-1">
			<h5 class="col-12">Info Block for Purchase Order PDF file</h5>
		</div>
		<div class="row my-1">
			<div class="col-12">
				<small class="text-muted d-block">Free text information to be included in the pdf file and e-mail of the Purchase Order</small>
				<textarea class="form-control" rows="10" maxlength="1500" data-ng-model="infoBlockForPDF.pdf_text" data-ng-disabled="!canEdit(infoForPDFBlock)"></textarea>
			</div>
		</div>
		<div class="d-flex flex-row-reverse" data-ng-if="!canEdit(infoForPDFBlock)">
			<button class="btn btn-info" data-ng-click="editBlock(infoForPDFBlock, infoBlockForPDF)" data-ng-disabled="!canStartEdit()">Edit</button>
		</div>
		<div class="d-flex flex-row-reverse" data-ng-if="canEdit(infoForPDFBlock)">
			<button class="btn btn-success" data-ng-click="saveBlock(infoBlockForPDF)">Save</button>
			<button class="btn btn-danger mr-1" data-ng-click="cancelEditBlock(infoBlockForPDF)">Cancel</button>
		</div>
		
	
		<content-modal modal-id="'addNewLocationModal'" max-width="'500px'"
			on-confirm="saveNewLocation" on-confirm-text="'Add'"
			data-title="'Add New Location'" hide-manually-on-confirm="true"
			is-frozen="isModalFrozen" confirm-disabled="locationModalConfirmDisabled" data-ng-cloak>
			
				<div class="form-row">
					<div class="form-group col-12">
						<label for="id">Id</label> 
						<input type="text"
							class="form-control" id="id" data-ng-model="newLocation.id"
							placeholder="">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-12">
						<label for="location">Location</label> 
						<input type="text"
							class="form-control" id="location" data-ng-model="newLocation.location"
							placeholder="Location">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-12 col-md-6">
						<label for="latitude">Latitude</label> 
						<input type="number"
							class="form-control" id="latitude" data-ng-model="newLocation.geoLat"
							placeholder="">
					</div>
					<div class="form-group col-12 col-md-6">
						<label for="longitude">Longitude</label> 
						<input type="number"
							class="form-control" id="longitude" data-ng-model="newLocation.geoLong"
							placeholder="">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-12">
						<label for="location-scheme">Scheme</label> 
						<select class="form-control" id="location-scheme" data-ng-options="scheme.name for scheme in schemes" data-ng-model="newLocation.scheme">
						</select>
					</div>
				</div>
		
		</content-modal>
		
		<content-modal modal-id="'addNewBikeModal'" max-width="'500px'"
			on-confirm="saveNewBike" on-confirm-text="'Add'"
			data-title="'Add New Bike'" hide-manually-on-confirm="true"
			is-frozen="isModalFrozen" confirm-disabled="bikeModalConfirmDisabled" data-ng-cloak>
			
				<div class="form-row">
					<div class="form-group col-12">
						<label for="number">Number</label> 
						<input type="text"
							class="form-control" id="number" data-ng-model="newBike.number"
							placeholder="Number">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-12">
						<label for="bike-scheme">Scheme</label> 
						<select class="form-control" id="bike-scheme" data-ng-options="scheme.name for scheme in schemes" data-ng-model="newBike.scheme">
						</select>
					</div>
				</div>
		
		</content-modal>
		
		<content-modal modal-id="'addNewJobModal'" max-width="'500px'"
			on-confirm="saveNewJob" on-confirm-text="'Add'"
			data-title="'Add New Job'" hide-manually-on-confirm="true"
			is-frozen="isModalFrozen" confirm-disabled="jobModalConfirmDisabled" data-ng-cloak>
			
				<div class="form-row">
					<div class="form-group col-12">
						<label for="job">Job Name</label> 
						<input type="text"
							class="form-control" id="job" data-ng-model="newJob.job"
							placeholder="Name">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-12">
						<label for="job-type">Job Type</label> 
						<select class="form-control" id="job-type" data-ng-options="fw for fw in forWhatOptions" data-ng-model="newJob.forWhatString">
						</select>
					</div>
				</div>
		
		</content-modal>
		
		<content-modal modal-id="'addNewRepairReasonModal'" max-width="'500px'"
			on-confirm="saveNewRepairReason" on-confirm-text="'Add'"
			data-title="'Add New Repair Reason'" hide-manually-on-confirm="true"
			is-frozen="isModalFrozen" confirm-disabled="repairReasonModalConfirmDisabled" data-ng-cloak>
			
				<div class="form-row">
					<div class="form-group col-12">
						<label for="reason">Reason</label> 
						<input type="text"
							class="form-control" id="reason" data-ng-model="newReason.reason"
							placeholder="Reason">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-12">
						<label for="job-type">Job Type</label> 
						<select class="form-control" id="job-type" data-ng-options="fw for fw in forWhatOptions" data-ng-model="newReason.forWhatString">
						</select>
					</div>
				</div>
		
		</content-modal>
	</div>
	<%@ include file="/resources/views/footer.html"%>

</body>
</html>
