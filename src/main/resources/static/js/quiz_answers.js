const modal = document.getElementById("myModal");

function openModal(button) {
    // 모달 표시
    modal.style.display = "flex"; // 모달 열기

    // 버튼의 data 속성에서 질문 객체 가져오기
    const answers = button.dataset.answers.replace(/[\[\]]/g, '').split(', ').join('\n');

    console.log(answers);

    // 질문과 정답을 모달에 표시
    document.getElementById('answerText').innerText = answers;
}

// 모달 닫기
document.querySelector(".close").onclick = function() {
    modal.style.display = "none";
};

// 모달 바깥 클릭 시 닫기
window.onclick = function(event) {
    if (event.target === modal) {
        modal.style.display = "none";
    }
};