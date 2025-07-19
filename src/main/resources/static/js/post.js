document.addEventListener('DOMContentLoaded', () => {
    const {currentUser, postOwner, postId} = window.chatConfig || {};
    const previewImage = document.querySelector('.post-form__image-preview');
    const fileInput = document.getElementById('fileInput');
    const chatBtn = document.getElementById('post-detail__chat-button');

    // 이미지
    if(previewImage && fileInput){
        previewImage.addEventListener('click', () => { fileInput.click(); });
        fileInput.addEventListener('change', async () => {
            const files = fileInput.files;
            if(files.length > 0) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    previewImage.src = e.target.result;
                    previewImage.style.objectFit = 'cover';
                };
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

    // 채팅방 연결
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

    // 무한스크롤
    let page = 0;
    const size = 12;
    let isLoading = false;
    let hasNext = true;

    const grid = document.getElementById('post-grid');
    const sentinel = document.createElement('div');
    sentinel.id = 'scroll-sentinel';
    grid.after(sentinel);

    const observer = new IntersectionObserver(async (entries) => {
        if (entries[0].isIntersecting && !isLoading && hasNext) {
            isLoading = true;
            page++;

            try {
                const res = await fetch(`/posts?page=${page}&fragment=true`);
                const html = await res.text();

                const temp = document.createElement('div');
                temp.innerHTML = html;

                const cards = temp.querySelectorAll('.post-list__card');
                if(cards.length === 0) {
                    hasNext = false;
                    observer.unobserve(sentinel);
                }

                cards.forEach(card => grid.appendChild(card));
            } catch (e) {
                console.error('게시글 로딩 실패', e);
            }

            isLoading = false;

        }
    }, { threshold: 1 });
    observer.observe(sentinel);
