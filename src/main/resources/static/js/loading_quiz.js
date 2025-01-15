function showLoading() {
    document.getElementById('loading').style.display = 'block';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

function displayError(data) {

    const errorMessageDiv = document.getElementById('errorMessage');
    document.getElementById('errorText').innerText = data.message;
    errorMessageDiv.style.display = 'block';

    let countdown = 5;
    const countdownText = document.getElementById('countdownText');
    countdownText.innerText = `5초 후에\n퀴즈 생성 페이지로 이동합니다...`;

    const countdownInterval = setInterval(() => {
        countdown--;
        countdownText.innerText = `${countdown}초 후에\n퀴즈 생성 페이지로 이동합니다...`;
        if (countdown === 0) {
            clearInterval(countdownInterval);
            window.location.href = data.nextUrl;
        }
    }, 1000);
}

function submitAndReload() {
    showLoading();
    let responseData; // data 변수를 상위 범위에서 선언

    fetch('/api/quiz', {
        method: 'POST',
    })
        .then(response => {
            return response.json().then(data => {

                if (!response.ok) {
                    responseData = data; // data를 상위 변수에 저장
                    throw new Error(data);
                }
                return data;
            });
        })
        .then(data => {
            hideLoading();
            window.location.href = data.nextUrl;
        })
        .catch(error => {
            hideLoading();
            displayError(responseData);
        });
}

document.addEventListener("DOMContentLoaded", function() {
    submitAndReload();
});