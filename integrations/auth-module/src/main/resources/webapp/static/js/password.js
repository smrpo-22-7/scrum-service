const passwordInput = document.getElementById("password");
const newPasswordInput = document.getElementById("new-password");
const alert = document.getElementById("validation-alert");
const submitButton = document.getElementById("submit-btn");

function PasswordMatchElement(elementId) {
    this.root = document.getElementById(elementId);
    this.weakBar = this.root.querySelector(".weak-password");
    this.mediumBar = this.root.querySelector(".medium-password");
    this.strongBar = this.root.querySelector(".strong-password");
    this.passwordMeter = new PasswordMeterModule.PasswordMeter({}, {
        "80": "weak",
        "120": "medium",
        "180": "strong",
        "_": "strong",
    });
}
PasswordMatchElement.prototype.applyColors = function(status) {
    if (status === "weak") {
        this.weakBar.classList.add("active");
    } else if (status === "medium") {
        this.weakBar.classList.add("active");
        this.mediumBar.classList.add("active");
    } else if (status === "strong") {
        this.weakBar.classList.add("active");
        this.mediumBar.classList.add("active");
        this.strongBar.classList.add("active");
    }
}
PasswordMatchElement.prototype.clearColors = function() {
    this.weakBar.classList.remove("active");
    this.mediumBar.classList.remove("active");
    this.strongBar.classList.remove("active");
}
PasswordMatchElement.prototype.checkPassword = function(password) {
    const result = this.passwordMeter.getResult(password);
    this.applyColors(result.status);
    return result.status !== "weak";
}
function displayValidationError(error) {
    alert.style.display = "block";
    alert.innerText = error;
    submitButton.disabled = true;
}
function clearValidationError() {
    alert.style.display = "none";
    submitButton.disabled = false;
}

meter = new PasswordMatchElement("password-meter");
function verifyPasswordMatch(pass1, pass2) {
    return () => {
        if (pass1.value) {
            const validPassword = meter.checkPassword(pass1.value);
            if (!validPassword) {
                displayValidationError("Choose stronger password!");
                return;
            }
            if (pass1.value.length < 12) {
                displayValidationError("Password must be at least 12 characters long");
                return;
            }
            if (pass1.value.length > 128) {
                displayValidationError("Password must be at most 128 characters long");
                return;
            }
            if (pass2.value) {
                if (pass1.value !== pass2.value) {
                    displayValidationError("Passwords do not match!");
                    return;
                }
            } else {
                displayValidationError("Please confirm new password!");
                return;
            }
        } else {
            displayValidationError("Please enter new password!");
            return;
        }
        clearValidationError();
    };
}
passwordInput.addEventListener("input", verifyPasswordMatch(passwordInput, newPasswordInput));
newPasswordInput.addEventListener("input", verifyPasswordMatch(passwordInput, newPasswordInput));
