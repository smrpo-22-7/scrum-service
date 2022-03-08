const passwordInput = document.getElementById("password");
const usernameInput = document.getElementById("username");
const showPasswordBtn = document.getElementById("show-password-btn");
const showPasswordImg = document.getElementById("show-password-img");

function togglePasswordVisibility() {
    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        showPasswordImg.src = "/static/img/hide_pass.svg";
    } else {
        passwordInput.type = "password";
        showPasswordImg.src = "/static/img/show_pass.svg";
    }
}

showPasswordBtn.addEventListener("click", togglePasswordVisibility);