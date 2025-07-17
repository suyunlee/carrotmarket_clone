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
                alert('거래확정은 판매자만 가능합니다.');
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

        const bubble = document.createElement('div');
        bubble.className = 'bubble';
        bubble.innerHTML = `
          <span>${msg.content}</span>
        `;
        const read = document.createElement('div');
        read.className = 'read';
        if(isMine){
            read.innerHTML = msg.read
           ? '<span class="read-badge">읽음</span>'
           : '<span class="unread-badge">1</span>';
        }

        const timeEl = document.createElement('div');
        timeEl.textContent = new Date(msg.createdAt)
                          .toLocaleTimeString([], {hour:'2-digit',minute:'2-digit'});
        timeEl.className = 'time';

        if (!isMine) {
          const av = document.createElement('div');
          av.className = 'avatar';
          av.innerHTML = msg.senderNickname;
          li.append(av);
        }
        else{
            li.append(read);
            li.append(timeEl);
        }

        li.append(bubble);
        if(!isMine){
            li.append(timeEl);
            li.append(read);
        }

        chatArea.append(li);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    // STOMP 연결
    const client = Stomp.over(new SockJS('/ws-chat'));
    client.debug = () => {};
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
                el.dataset.sender === currentUser)
            {
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
          return;
        }

        const list = await res.json();
        const roomsToShow = postId
          ? ((currentUser === postOwner)
              ? list
              : list.filter(r => r.username === currentUser))
          : list;

        roomsToShow
            .forEach(r => {

                if (renderedRooms.has(r.id)) return;
                renderedRooms.add(r.id);

                if (r.chatBot) {
                    const li = document.createElement('li');
                    li.className = 'chat-room__item'
                     + (r.id === +roomId ? ' chat-room__item--active' : '');

                     const avatar = document.createElement('div');
                     avatar.className = 'chat-room__avatar';
                     avatar.innerHTML = `
                       <img src="/images/icons/chatbot_icon.svg" alt="AI 챗봇"/>
                     `;
                     li.append(avatar);

                     const info = document.createElement('div');
                     info.className = 'chat-room__info';
                     info.innerHTML = `
                       <strong>AI 챗봇</strong>
                       <span>궁금한 내용을 물어보세요!</span>
                     `;
                     li.append(info);

                     li.addEventListener('click', () => {
                       location.href =
                         `/chat/room/${r.id}?username=${encodeURIComponent(currentUser)}`;
                     });
                     chatList.prepend(li);
                     return;
                }

                const li = document.createElement('li');
                li.className = 'chat-room__item'
                +  (r.id === +roomId ? ' chat-room__item--active' : '') ;
                li.dataset.unread = r.unreadCount > 0 ? 'true' : 'false';

                // info
                const info = document.createElement('div');
                info.className = 'chat-room__info';

                const avatar = document.createElement('div');
                avatar.className = 'chat-room__avatar';
                const thumb = document.createElement('img');

                thumb.src = r.postImageUrl || '';
                thumb.alt = '상품 썸네일';
                avatar.appendChild(thumb);
                li.appendChild(avatar);

                const header = document.createElement('div');
                header.className = 'chat-room__header';

                const who = document.createElement('strong');
                const lastTime = document.createElement('span');
                lastTime.className = 'chat-room__time';

                who.textContent = r.nickname + '  ';
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
                  header.append(badge);
                }
                const lastMsg = document.createElement('span');
                lastMsg.className = "chat-room__last-message";
                lastMsg.textContent = '  마지막 메세지 : ' +  r.lastMessage || '';
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