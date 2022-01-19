"use strict";

app.controller('userListCtrl', [
		'$scope',
		'$http',
		'$timeout',
		'$rootScope',
		function($scope, $http, $timeout, $rootScope) {
			
			$scope.gridPageSizes = [20, 50, 100];
			$scope.gridPageSize = $scope.gridPageSizes[0];
			
			$scope.permissionsChecked = true;
			
			$scope.allUsers = undefined;
			
			$scope.loadUsers = function(){
				$http.get(projectRoot + "/msc-api/user/all").then(function(response) {
					$scope.allUsers = response.data;
					
					for (var i = 0; i < $scope.allUsers.length; i++) {
						updateUser($scope.allUsers[i]);	
					
					}
					
					$scope.usersGridOptions.api.setRowData($scope.allUsers);
					
				}, function(errorResponse) {

				});
			}
			
			$scope.addUser = function(){
				$scope.allUsers.push({
					firstName: "",
					lastName: "",
					roles : [],
					active: false,
					newlyCreated : true,
					oldUser: {}
				});
			}
			
			var userRolesToString = function(user){
				user.userRole = "";
				for (var i = 0; i < user.roles.length; i++) {
					if (user.roles[i]) {
						user.userRole += '1';
					} else {
						user.userRole += '0';
					}

					if (i + 1 < user.roles.length) {
						user.userRole += ",";
					}
				}
			}
			
			$scope.userRolesToLine = function(user){
				var userRoleLine = "";
				if(user.roles){
					for (var i = 0; i < user.roles.length; i++) {
						if (user.roles[i]) {
							switch (i) {
							case 0: userRoleLine += 'Admin, '; break;
							case 1: userRoleLine += 'Operator, '; break;
							case 2: userRoleLine += 'Scheme Leader, '; break;
							case 3: userRoleLine += 'Purchase Manager, '; break;
							case 4: userRoleLine += 'Fulfillment Operator  '; break;
							}
						}
					}
				}
				return userRoleLine.slice(0, -2);
			}
			
			$scope.toDateString = function(dateAsSeconds){
				//return new Date(dateAsSeconds).toUTCString();
				return new Date(dateAsSeconds).toTimeString();
			}
			
			var updateUser = function(user){
				user.oldUser = undefined;
				user.oldUser = angular.copy(user);
				user.fullName = user.firstName + " " + user.lastName;
				
				user.roles = [];
				for (var j = 0; j < user.userRole.length; j += 2) {
					if(user.userRole.charAt(j) === '1'){
						user.roles[j/2] = true;
					} else {
						user.roles[j/2] = false;
					}
				}
				
				user.prettyRoles = $scope.userRolesToLine(user);
			}
			
			
			//--grid-related-stuff--
			
			var editCellRendererFunc = function(params){				 
				 params.$scope.editable = params.node.data;
				 params.$scope.rowId = params.node.id;
				 
				 return '<button type="button" class="btn btn-info btn-sm btn-block" style="height:25px; padding: 3px 8px;" data-ng-click="openEditUserModal(editable)"><i class="material-icons md-18">edit</i></button>';
			}
			
			var deleteRowCellRendererFunc = function(params){
				params.$scope.rowId = params.node.id;
				params.$scope.deletable = params.node.data;
				
				return '<button type="button" class="btn btn-danger btn-sm btn-block" style="height:25px; padding: 3px 8px;" data-ng-click="setDeletableUser(deletable)"><i class="material-icons md-18">clear</i></button>';
			}
			
			var activeUserAndNameCellRendererFunc = function(params){
				params.$scope.user = params.node.data;
				
				return '<div><div class="msc-round msc-inline-block mr-2" data-ng-class="user.oldUser.active ? \'bg-success\' : \'bg-danger\'"></div>{{user.fullName}}</div>';
			}
			
			var lastLogInTimeCellRendererFunc = function(params){
				params.$scope.user = params.node.data;
				
				return '<div>{{user.lastLogInTime ? user.lastLogInTime : "Never"}}</div>';
			}
			
			function numberFormatter(params) {
			    if (typeof params.value === 'number') {
			        return params.value.toFixed(2);
			    } else {
			        return params.value;
			    }
			}
			
			var columnDefs = [
				{headerName: "Name", field: "fullName", filter: "agTextColumnFilter", cellRenderer: activeUserAndNameCellRendererFunc, width: 170},
		        {headerName: "Username", field: "userEmail", filter: "agTextColumnFilter", width: 190},
		        {headerName: "Scheme", field: "city", filter: "agTextColumnFilter", valueFormatter: numberFormatter, width: 120},
		        {headerName: "Role", field: "prettyRoles", filter: "agTextColumnFilter", width: 190},
		        {headerName: "Date Last Login", field: "lastLogInTime", cellRenderer: lastLogInTimeCellRendererFunc, filter: "agTextColumnFilter", width: 150},
		        {headerName: "Date Created", field: "creationDate", filter: "agTextColumnFilter", width: 150},
		        
		        {headerName: "Edit", field: "", width: 70, cellRenderer: editCellRendererFunc, pinned: 'right'},
//		        {headerName: "Reset Pass", field: "", width: 70, cellRenderer: resetPassRowCellRendererFunc, pinned: 'right'},
		        {headerName: "Delete", field: "", width: 70, cellRenderer: deleteRowCellRendererFunc, pinned: 'right'},
		    ];
		
			$scope.usersGridOptions = {
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
//			        onGridReady: function(params) {
//			            params.api.sizeColumnsToFit();
//			        },
			    };
			
			$scope.setGridPageSize = function(pageSize){
				$scope.usersGridOptions.api.paginationSetPageSize(pageSize)
			}
			
			//--grid-related-stuff-end---
			
			$scope.openEditUserModal = function(user){
				$scope.editableUser = angular.copy(user)
				$scope.editableUserRef = user;
				
				$('#addUserModal').modal('show');
			}
			
			$scope.openAddUserModal = function(){
				$scope.editableUser = {
						firstName: "",
						lastName: "",
						roles : [],
						active: false,
						newlyCreated : true,
						oldUser: {}
					};
				
				$('#addUserModal').modal('show');
			}
			
			$scope.isAddNewUserModalFrozen = function(){
				return $scope.saving;
			}
			
			$scope.saveEditedUser = function(user){
				if(user.newlyCreated){
					$scope.insertUser(user);
				} else {
					$scope.saveUser(user);
				}
			}
			
			$scope.saveNewUser = function(){
				$rootScope.$broadcast('makeSubmit', {formName: 'new-user-form'})
			}
			
			$scope.insertUser = function(user) {
				userRolesToString(user);
				$scope.saving = true;
				
				$http.post(projectRoot + "/msc-api/user/add", user)
					.then(function(response) {
						user = response.data;
	
						updateUser(user);
						user.newlyCreated = false;
						
						$scope.allUsers.push(user);
						
						$scope.usersGridOptions.api.setRowData($scope.allUsers);
						
						$scope.saving = false;
						
						$rootScope.successNotification("User added");
						
						$('#addUserModal').modal('hide');
					}, function(errorResponse) {
						
						$scope.saving = false;
						
						for (var i = 0; i < $scope.allUsers.length; i++) {
							if($scope.allUsers[i].userEmail === user.userEmail){
								$rootScope.errorNotification("Error. User with this email already exists");
								
								return;
							}
						}
						
						$rootScope.errorNotification("Error");
						
													
					});
			}
			
			$scope.updateEditable = function(user){
				$scope.editableUserRef.oldUser = user.oldUser;
				$scope.editableUserRef.fullName = user.firstName + " " + user.lastName;
				$scope.editableUserRef.firstName = user.firstName;
				$scope.editableUserRef.lastName = user.lastName;
				$scope.editableUserRef.userEmail = user.userEmail;
				$scope.editableUserRef.passwordHash = user.passwordHash;
				$scope.editableUserRef.userRole = user.userRole;
				$scope.editableUserRef.active = user.active;
				$scope.editableUserRef.city = user.city;
				$scope.editableUserRef.state = user.state;
				$scope.editableUserRef.roles = user.roles;
				$scope.editableUserRef.prettyRoles = user.prettyRoles;
				
			}
			
			$scope.saveUser = function(user) {
				userRolesToString(user);
				$scope.saving = true;
				$http.post(projectRoot + "/msc-api/user", user)
						.then(function(response) {
							updateUser(user);
							
							$scope.updateEditable(user);
							
							$scope.usersGridOptions.api.setRowData($scope.allUsers);
							
							$scope.saving = false;
							
							$rootScope.successNotification("User saved");
							
							$('#addUserModal').modal('hide');
						}, function(errorResponse) {
							$rootScope.errorNotification("Error. " + (errorResponse.data.message ? errorResponse.data.message : ''));
							
							$scope.saving = false;
						});
			}
			
			$scope.setDeletableUser = function(user){
				$scope.deletableUser = user;
				
				$('#confirmModal').modal('show');
			}
			
			$scope.deleteUser = function(user){
				if(user === undefined || user === null){
					user = $scope.deletableUser;
					
					if(user === undefined || user === null){
						return;
					}
				}
				
				$scope.deletableUser = undefined;
				$scope.deleting = true;
				if(user.newlyCreated){
					$scope.allUsers.splice($scope.allUsers.indexOf(user), 1);
					$scope.deleting = false;
				} else {

					$http.post(projectRoot + "/msc-api/user/delete", { userEmail: user.oldUser.userEmail}).then(function(response){
						$scope.allUsers.splice($scope.allUsers.indexOf(user), 1);
						
						$scope.usersGridOptions.api.updateRowData({remove: [user]});
						
						$rootScope.successNotification("User deleted");
						
						$scope.deleting = false;
					}, function(errorResponse){
						$rootScope.errorNotification("Error. " + (errorResponse.data.message ? errorResponse.data.message : ''));
						
						$scope.deleting = false;
					});
				}	
			}
			
			$scope.dropPassword = function(user){
				$scope.droppingPass = true;
				$http.put(projectRoot + "/msc-api/user/drop-password", {userEmail: user.oldUser.userEmail}).then(function(response){
					
					user.passwordHash = response.passwordHash;
					user.state = response.state;
					
					$scope.droppingPass = false;
					
					$rootScope.infoNotification("Recovery link was sent to " + user.oldUser.userEmail);
				}, function(errorResponse){
					$rootScope.errorNotification("Error on password reset");
					
					$scope.droppingPass = false;
				})
			}
			
			$scope.loadUsers();
		} ]);
