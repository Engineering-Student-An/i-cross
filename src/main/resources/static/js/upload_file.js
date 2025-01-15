document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById('file-input');
    const fileInfo = document.getElementById('file-info');

    fileInput.addEventListener('change', function() {
        const files = fileInput.files;
        fileInfo.innerHTML = ''; // 기존 내용을 지웁니다.

        if (files.length > 1) {
            fileInfo.innerHTML = '하나의 파일만 선택할 수 있습니다.';
            fileInput.value = ''; // 선택된 파일 초기화
        } else if (files.length === 1) {
            const fileName = files[0].name; // 첫 번째 파일의 이름
            fileInfo.innerHTML = `선택한 파일: ${fileName}`;
        } else {
            fileInfo.innerHTML = '선택한 파일이 없습니다.';
        }
    });
});

$(document).ready(function() {
    $('form').on('submit', function(event) {
        event.preventDefault(); // 폼 기본 동작 방지

        const fileInput = document.getElementById('file-input');
        if (fileInput.files.length === 0) {
            alert('파일을 선택해야 합니다.');
            return; // 파일이 선택되지 않으면 폼 제출 중단
        }

        // 현재 폼 요소를 변수에 저장
        const formElement = this; // `this`는 폼 요소를 가리킵니다.

        // 폼 데이터를 FormData 객체로 생성
        var formData = new FormData(formElement);

        // AJAX 요청 보내기
        $.ajax({
            url: $(formElement).attr('action'), // 폼의 action 속성에서 URL 가져오기
            type: 'POST',
            data: formData,
            processData: false, // FormData를 처리하지 않음
            contentType: false, // 콘텐츠 타입을 자동으로 설정

            success: function(response, textStatus, xhr) {
                // 상태 코드가 CREATED인 경우
                if (xhr.status === 200) {
                    // quizRequestDto의 값을 쿼리 파라미터로 URL에 추가
                    console.log(response);
                    window.location.href=response;
                }
            },
            error: function(xhr) {
                // 에러 발생 시 (BAD_REQUEST 포함)
                alert(xhr.responseText);
            }
        });
    });
});


function checkTotal() {
    const ox = parseInt(document.getElementById('ox').value) || 0;
    const multipleChoice = parseInt(document.getElementById('multipleChoice').value) || 0;
    const shortAnswer = parseInt(document.getElementById('shortAnswer').value) || 0;
    const fileInput = document.getElementById('file-input');

    const total = ox + multipleChoice + shortAnswer;

    const error0 = document.getElementById('error-0');
    const error20 = document.getElementById('error-20');
    const errorFile = document.getElementById('error-file');
    const submitButton = document.getElementById('submit-button');

    var bool = false;

    if (fileInput.files.length === 0) {
        bool = false;
        errorFile.classList.remove('hidden');
    } else {
        bool = true;
        errorFile.classList.add('hidden');
    }

    if (total < 1) {
        error0.classList.remove('hidden');
        error20.classList.add('hidden');
        bool = false;
    } else if(total > 20) {
        error0.classList.add('hidden');
        error20.classList.remove('hidden');
        bool = false;
    } else {
        error0.classList.add('hidden');
        error20.classList.add('hidden');
    }

    submitButton.disabled = !bool;

}