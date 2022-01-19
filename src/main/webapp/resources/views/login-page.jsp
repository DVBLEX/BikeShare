<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<title>Log In</title>

<%@ include file="/resources/views/html-headers/header.html"%>

<script type="application/javascript"
	src="/msc/resources/logic/login-page-logic.js"></script>


<script type="application/javascript"
	src="/msc/resources/directives/confirm-modal/confirm-modal.js"></script>

</head>
<body data-ng-app="mainModule"
	style="height: 100%;"
	data-ng-controller="loginPageCtrl">

	<div
		class="container-fluid d-flex align-items-center justify-content-center"
		style="height: 100vh">


		<form data-ng-submit="signIn()"
			style="background-color: white; width: 550px; box-shadow: 0 30px 60px 0 rgba(0, 0, 0, 0.3); border-radius: 5px;'">
			<div class="row justify-content-center my-3">
				<h3 class="display-4">Sign In</h3>
			</div>
			<div class="form-row justify-content-center mb-3">
				<div class="col-9">
					<input type="email" id="username" class="form-control" name="userEmail"
						placeholder="login" data-ng-model="signInRequest.userEmail"
						required>
				</div>
			</div>
			<div class="form-row justify-content-center mb-3">
				<div class="col-9">
					<input type="password" id="password" class="form-control"
						name="rawPassword" placeholder="password"
						data-ng-model="signInRequest.rawPassword" required>
				</div>
			</div>

			<div class="row justify-content-center mb-3">
				<div class="msc-text-invalid" data-ng-cloak
					data-ng-show="showWrongCredentialsMessage">Wrong user name
					and/or password</div>
			</div>

			<div class="row justify-content-center mb-3">
				<button type="submit" class="btn btn-primary col-5"
					data-ng-disabled="loggingIn || droppingPass">Sign In</button>
			</div>
			<div class="row justify-content-center mb-3" id="formFooter">
				<a class="" href="#" data-ng-click="askToDropPassword()" data-ng-disabled="droppingPass">Forgot Password?</a>
			</div>

			<div class="alert alert-primary mt-2 mx-1" role="alert"
				data-ng-show="showSuccessDropMsgVar" data-ng-cloak>Password dropped! Please,
				check your email for the recovery link.</div>
			<div class="alert alert-danger mt-2 mx-1" role="alert"
				data-ng-show="showErrorMsgVar" data-ng-cloak>{{'Error! ' + errorMsg}}</div>
		</form>

		<confirm-modal data-modal-id="'infoModal'"
			data-on-confirm="redirectToChangePassword" data-title="'Info'"
			data-text="'You need to change your password'"
			data-hide-cancel="true"></confirm-modal>
			
		<confirm-modal data-modal-id="'dropPasswordModal'"
			data-on-confirm="dropPassword" data-title="'Password Reset'"
			data-text="'Do you want to reset your password? Recovery link will be sent to your email.'"
			></confirm-modal>
	</div>
	<%@ include file="/resources/views/footer-viewport-bottom.html"%>
</body>
</html>
