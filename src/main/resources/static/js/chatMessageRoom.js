document.addEventListener('DOMContentLoaded', () => {
  const {currentUser, roomId, postId, postOwner} = window.chatConfig;
  const chatArea   = document.getElementById('chatArea');
  const chatList   = document.getElementById('chatList');
  const confirmBtn = document.getElementById('confirm-btn');
  const renderIds  = new Set();
  const renderedRooms = new Set();

  // 이미 렌더된 메시지 id 수집
  document.querySelectorAll('#chatArea [data-id]')
    .forEach(el => renderIds.add(+el.dataset.id));

  // 읽음 처리 함수
    function sendReadReceipt() {
        const toMark = Array.from(chatArea.children)
        .filter(el =>
            el.dataset.sender !== currentUser &&
            !el.classList.contains('read')
        )
        .map(el => +el.dataset.id);

        if (!toMark.length) return;

        client.send(
            `/app/room/${roomId}/read`,
            {'content-type':'application/json'},
            JSON.stringify({
                readerUsername: currentUser,
                messageIds: toMark
            })
        );
    }

    // 거래 확정
    if(confirmBtn){
        confirmBtn.addEventListener('click', ()=>{
            if(confirmBtn.disabled) return;

            fetch(`/chat/post/${window.chatConfig.postId}/confirm`, {
                method : 'POST',
                headers : {
                    'Content-Type' : 'application/json'
                     // [window.chatConfig.csrfHeader]: window.chatConfig.csrfToken
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('거래확정에 실패했습니다.');
                }
                // UI 업데이트
                confirmBtn.textContent = '거래 완료';
                confirmBtn.disabled = true;
                confirmBtn.classList.add('chat-room__button--completed');
            })
            .catch(err => {
                console.error(err);
                alert('거래확정 중 오류가 발생했습니다.');
            });
      });
  }

  // 메시지 렌더링
  function renderMessage(msg) {
    if (renderIds.has(msg.id)) return;
    renderIds.add(msg.id);

    const isMine = msg.senderUsername === currentUser;
    const li     = document.createElement('li');
    li.className = 'message ' + (isMine ? 'buyer' : 'seller');
    li.dataset.id     = msg.id;
    li.dataset.sender = msg.senderUsername;

    if (!isMine) {
      const av = document.createElement('div');
      av.className = 'avatar';
      av.innerHTML = '<img src="" alt="상대방"/>';
      li.append(av);
    }

    const bubble = document.createElement('div');
    bubble.className = 'bubble';
    bubble.innerHTML = `
      <p>${msg.content}</p>
      <span class="time">${
        new Date(msg.createdAt)
          .toLocaleTimeString([], {hour:'2-digit',minute:'2-digit'})
      }</span>
      ${isMine
        ? (msg.read
            ? '<span class="read-badge">읽음</span>'
            : '<span class="unread-badge">1</span>')
        : ''}
    `;
    li.append(bubble);

    chatArea.append(li);
    chatArea.scrollTop = chatArea.scrollHeight;
  }

  // STOMP 연결
  const client = Stomp.over(new SockJS('/ws-chat'));
  client.connect({}, () => {
    client.subscribe(`/topic/chat/${roomId}`, frame => {
      const msg = JSON.parse(frame.body);
      renderMessage(msg);

      if (msg.senderUsername !== currentUser) {
        sendReadReceipt();
      }
    });

    client.subscribe(`/topic/chat/${roomId}/read`, frame => {
      const { messageIds, readerUsername } = JSON.parse(frame.body);
      if (readerUsername === currentUser) return;

      messageIds.forEach(id => {
        const el = document.querySelector(
          `#chatArea li[data-id='${id}']`
        );
        if (el &&
            !el.classList.contains('read') &&
            el.dataset.sender === currentUser) {
          el.classList.add('read');
          const ub = el.querySelector('.unread-badge');
          if (ub) ub.remove();
          const rb = document.createElement('span');
          rb.className = 'read-badge';
          rb.textContent = '읽음';
          el.querySelector('.bubble').append(rb);
        }
      });
    });

    // 초기 메시지 가져오기 + 읽음 전송
    fetch(`/chat/room/${roomId}/messages?username=${encodeURIComponent(currentUser)}`)
      .then(r => r.json())
      .then(list => {
        list.forEach(renderMessage);
        sendReadReceipt();
      })
      .catch(console.error);
  });

  // 메시지 보내기
  document.getElementById('chat-form').addEventListener('submit', e => {
    e.preventDefault();
    const input = document.getElementById('chat-input');
    const text  = input.value.trim();
    if (!text) return;

    client.send(
      `/app/room/${roomId}/send`,
      {'content-type':'application/json'},
      JSON.stringify({
        content: text,
        senderUsername: currentUser
      })
    );
    input.value = '';
  });

  // "읽지 않은 항목" 토글
  const unreadToggle = document.querySelector('.chat-room__switch-input');
  unreadToggle.addEventListener('change', () => {
    const showUnreadOnly = unreadToggle.checked;
    document.querySelectorAll('.chat-room__list .chat-room__item')
      .forEach(li => {
        const isUnread = li.dataset.unread === 'true';
        li.style.display = (showUnreadOnly && !isUnread) ? 'none' : '';
      });
  });

  // 다른 채팅방 목록 로드
  async function loadOtherRooms() {
    const endpoint = postId
      ? `/chat/post/${postId}/rooms?username=${encodeURIComponent(currentUser)}&full=true`
      : `/chat/post/rooms?username=${encodeURIComponent(currentUser)}&full=true`;
    const res = await fetch(endpoint);

    if (!res.ok) {
      console.error('방 목록 로드 실패');
      return;
    }
    const list = await res.json();
     const roomsToShow = postId
          ? ((currentUser === postOwner)
              ? list
              : list.filter(r => r.username === currentUser))
          : list;

    roomsToShow
      .filter(r => r.id !== +roomId)
      .forEach(r => {
        if (renderedRooms.has(r.id)) return;
        renderedRooms.add(r.id);

        const li = document.createElement('li');
        li.className = 'chat-room__item';
        li.dataset.unread = r.unreadCount > 0 ? 'true' : 'false';

        // info
        const info = document.createElement('div');
        info.className = 'chat-room__info';

        const header = document.createElement('div');
        header.className = 'chat-room__header';

        const who = document.createElement('strong');
        const lastTime = document.createElement('span');
        lastTime.className = 'chat-room__time';

        who.textContent = r.username;
        lastTime.textContent = r.lastMessageAt
          ? new Date(r.lastMessageAt)
              .toLocaleTimeString([], {hour:'2-digit',minute:'2-digit'})
          : '';
        header.append(who, lastTime);

        const footer = document.createElement('div');
        footer.className = 'chat-room__footer';
        if (r.unreadCount > 0) {
          const badge = document.createElement('span');
          badge.className = 'unread-badge';
          badge.textContent = r.unreadCount;
          footer.append(badge);
        }
        const lastMsg = document.createElement('p');
        lastMsg.textContent = r.lastMessage || '';
        footer.append(lastMsg);

        info.append(header, footer);
        li.append(info);

        li.addEventListener('click', () => {
          location.href = `/chat/room/${r.id}?username=${encodeURIComponent(currentUser)}`;
        });

        chatList.append(li);
      });
  }
  loadOtherRooms();
});