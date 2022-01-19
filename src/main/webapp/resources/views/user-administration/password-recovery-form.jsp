<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Password recovery</title>

<%@ include file="/resources/views/html-headers/header.html" %>

<script type="application/javascript"
	src="/msc/resources/logic/user-administration/password-recovery-logic.js"></script>

</head>
<body data-ng-app="mainModule" style="height: 100%;"
	data-ng-controller="passwordRecoveryCtrl">
	
	<%@ include file="/resources/views/navbar.html"%>

	<div class="container pt-2 pb-2" style="background-color: white; max-width: 550px;">
		<form data-ng-submit="sendRecoverRequest()">
			<div class="form-group row">
				<label for="new-password" class="col-sm-5 col-form-label">New password</label>
			    <div class="col-sm-7">
			      <input type="password" class="form-control" id="new-password" placeholder="" data-ng-model="requestBody.newPassword">
			    </div>
			</div>
			<div class="form-group row">
				<label for="confirm-password" class="col-sm-5 col-form-label">Confirm password</label>
			    <div class="col-sm-7">
			      <input type="password" class="form-control" data-ng-class="{'is-invalid' : requestBody.newPassword !== confirmPassword}" 
			      		id="confirm-password" placeholder="" data-ng-model="confirmPassword">
			      <div class="invalid-feedback" data-ng-if="requestBody.newPassword !== confirmPassword" data-ng-cloak>
		          	Passwords don't match.
		        	</div>
			    </div>
			    
			</div>
			<div class="form-group row">
			    <div class="col-sm-7">
			      <button type="submit" class="btn btn-primary" data-ng-disabled="requestBody.newPassword !== confirmPassword">Submit</button>
			    </div>
	 		</div>
		</form>
	</div>
	
	<%@ include file="/resources/views/footer-viewport-bottom.html"%>
</body>
</html>