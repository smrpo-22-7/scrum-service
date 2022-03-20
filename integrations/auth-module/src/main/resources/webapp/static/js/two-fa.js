const codeInput = document.getElementById("code_2fa");

function trimCode() {
    codeInput.value = codeInput.value.trim();
    return true;
}