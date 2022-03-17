const passwordInput = document.getElementById("password");
const showPasswordBtn = document.getElementById("show-password-btn");
const showPasswordImg = document.getElementById("show-password-img");

function showPassword() {
    passwordInput.type = "text";
    showPasswordImg.src = "/static/img/hide_pass.svg";
}

function hidePassword() {
    passwordInput.type = "password";
    showPasswordImg.src = "/static/img/show_pass.svg";
}

showPasswordBtn.addEventListener("mousedown", showPassword);
showPasswordBtn.addEventListener("mouseup", hidePassword);