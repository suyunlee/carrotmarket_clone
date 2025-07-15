document.addEventListener('DOMContentLoaded', () => {
    const {currentUser, postOwner, postId} = window.chatConfig || {};
    const previewImage = document.querySelector('.previewImage');
    const fileInput = document.getElementById('fileInput');
    const chatBtn = document.getElementById('chatButton');


    if(previewImage && fileInput){
        previewImage.addEventListener('click', () => { fileInput.click(); });
        fileInput.addEventListener('change', async () => {
            const files = fileInput.files;
            if(files.length > 0) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    previewImage.src = e.target.result; };
                reader.readAsDataURL(files[0]);

                const formData = new FormData();
                for(let i = 0; i < files.length; i++) {
                    const file = files[i];
                    formData.append('files', file);
                }
                const response = await fetch('/posts', {
                    method: 'POST',
                    body: formData
                });
                if (response.ok) {
                    const urls = await response.json();
                    console.log(urls); // S3에 올라간 URL 리스트!
                    alert('업로드 성공!');
                } else {
                    alert('업로드 실패!');
                }
            }
        });
    }

    chatBtn.addEventListener('click', async () => {
            if (currentUser === postOwner) {
              // 판매자 모드 → 이미 있는 방이 있으면 입장, 없으면 안내
              const res = await fetch(
                `/chat/post/${postId}/rooms?username=${encodeURIComponent(currentUser)}`
              );
              if (!res.ok) {
                return alert('채팅방 목록 조회 실패: ' + await res.text());
              }
              const rooms = await res.json();
              if (rooms.length === 0) {
                return alert('아직 구매자가 채팅을 신청하지 않았습니다.');
              }
              // 첫 번째 방으로 입장
              location.href = `/chat/room/${rooms[0].id}?username=${encodeURIComponent(currentUser)}`;
            } else {
              // 구매자 모드 → 방 생성 or 기존 방 입장
              const res = await fetch(
                `/chat/post/${postId}/rooms?username=${encodeURIComponent(currentUser)}`,
                {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({ username: currentUser })
                }
              );
              if (!res.ok) {
                return alert('채팅방 생성 실패: ' + await res.text());
              }
              const dto = await res.json();
              location.href = `/chat/room/${dto.id}?username=${encodeURIComponent(currentUser)}`;
            }
    });
});
