<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>User Management</title>

<script src="/msc/resources/components/ag-grid/ag-grid-community.js"></script>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="../resources/logic/user-administration/user-list.js"></script>

<script type="application/javascript"
	src="../resources/directives/confirm-modal/confirm-modal.js"></script>

<script type="application/javascript"
	src="/msc/resources/directives/content-modal/content-modal.js"></script>

</head>
<body data-ng-app="mainModule" style="height: 100%;"
	data-ng-controller="userListCtrl">

	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2"
		style="background-color: white; min-height: 100%;">


		<div class="d-flex justify-content-between">
			<div class="btn-group mb-2" role="group" aria-label="Basic example">
				<button type="button" class="btn btn-secondary"
					data-ng-click="openAddUserModal()">Add</button>
			</div>
			<div>
				<span class="">Show </span><select class="form-control form-control-sm" style="width: auto; display: inline-block;" data-ng-options="size for size in gridPageSizes" data-ng-model="gridPageSize" data-ng-change="setGridPageSize(gridPageSize)"></select> <span class="">entries</span>
			</div>
		</div>

		<div ag-grid="usersGridOptions" class="ag-theme-balham"></div>

		
		<content-modal modal-id="'addUserModal'" on-confirm="saveNewUser"
			on-confirm-text="'Save'" data-title="'User Edit'" max-width="'500px'"
			hide-manually-on-confirm="true" is-frozen="isAddNewUserModalFrozen" data-ng-cloak>

			<form name="new-user-form"
				msc-ext-submit data-ng-submit="saveEditedUser(editableUser)">
				<div class="pretty p-default p-curve">
					<input type="checkbox" id="user-active"
						   data-ng-model="editableUser.active">
					<div class="state p-primary">
						<label>Active</label>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-md-6">
						<label for="first-name">First Name</label> <input
							class="form-control" id="first-name"
							data-ng-model="editableUser.firstName" placeholder="First Name"
							maxlength="30" required>
					</div>
					<div class="form-group col-md-6">
						<label for="last-name">Last Name</label> <input
							class="form-control" id="last-name" placeholder="Last Name"
							data-ng-model="editableUser.lastName" maxlength="30" required>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-md-6">
						<label for="city">City / Scheme</label> <select
							class="form-control" id="city" placeholder="City"
							data-ng-model="editableUser.city" required>
							<option>Dublin</option>
							<option>Cork</option>
							<option>Galway</option>
							<option>Limerick</option>
						</select>
					</div>
					<div class="form-group col-md-6">
						<label for="user-email">Email</label> <input type="email"
							class="form-control" id="user-email" ng-model="editableUser.userEmail"
							data-ng-disabled="!editableUser.newlyCreated"
							placeholder="username@telclic.net" required>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col-md-6">
						<div class="form-check">
							<label>Roles</label>
							<br>
							<div class="pretty p-default p-curve">
								<input type="checkbox" id="role-admin" ng-model="editableUser.roles[0]">
								<div class="state p-primary">
									<label>Admin</label>
								</div>
							</div>
							<div class="pretty p-default p-curve">
								<input type="checkbox" id="role-operator" ng-model="editableUser.roles[1]">
								<div class="state p-primary">
									<label>Operator</label>
								</div>
							</div>
							<div class="pretty p-default p-curve">
								<input type="checkbox" ng-model="editableUser.roles[2]" id="role-scheme-leader">
								<div class="state p-primary">
									<label>Scheme Leader</label>
								</div>
							</div>
							<div class="pretty p-default p-curve">
								<input type="checkbox" ng-model="editableUser.roles[3]" id="role-purchase-manager">
								<div class="state p-primary">
									<label>Purchase	Manager</label>
								</div>
							</div>
							<div class="pretty p-default p-curve">
								<input type="checkbox" ng-model="editableUser.roles[4]" id="role-fulfillment-operator">
								<div class="state p-primary">
									<label>Fulfillment Operator</label>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group col-md-6">
						<button type="button" class="btn btn-info btn-block"
									data-ng-click="dropPassword(editableUserRef)"
									data-ng-show="!user.newlyCreated"
									data-ng-disabled="droppingPass || isAddNewUserModalFrozen()">Reset password</button>
					</div>
				</div>
			</form>

		</content-modal>

		<confirm-modal data-modal-id="'confirmModal'"
			data-on-confirm="deleteUser"
			data-title="'Delete user ' + deletableUser.userName"
			data-text="'Are you sure you want to delete this user?'"></confirm-modal>

		
	</div>
	<%@ include file="/resources/views/footer.html"%>
</body>
</html>
