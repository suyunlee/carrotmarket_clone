  document.addEventListener('DOMContentLoaded', () => {
    const {currentUser, roomId, postId, postOwner} = window.chatConfig;
    const chatArea    = document.getElementById('chatArea');
    const chatList  = document.getElementById('chatList');
    const renderIds   = new Set();
    const renderedRooms = new Set();

  // 이미 렌더된 메시지 id 수집
  document.querySelectorAll('#chatArea [data-id]')
    .forEach(el => renderIds.add(+el.dataset.id));

  // 읽음 처리 함수
  function sendReadReceipt() {
    const toMark = Array.from(chatArea.children)
      .filter(el =>
        el.dataset.sender !== currentUser
        && !el.classList.contains('read')
      )
      .map(el => +el.dataset.id);

    if (!toMark.length) return;

    client.send(
      `/app/room/${roomId}/read`,
      {'content-type':'application/json'},
      JSON.stringify({ readerUsername: currentUser, messageIds: toMark })
    );
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
        new Date(msg.createdAt).toLocaleTimeString([], {hour:'2-digit',minute:'2-digit'})
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

    // 읽음 이벤트 수신
    client.subscribe(`/topic/chat/${roomId}/read`, frame => {
      const { messageIds, readerUsername } = JSON.parse(frame.body);

      if (readerUsername === currentUser) return;

      messageIds.forEach(id => {
        const el = document.querySelector(`#chatArea li[data-id='${id}']`);
        if (
          el
          && !el.classList.contains('read')
          && el.dataset.sender === currentUser
        ) {
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

  const unreadToggle = document.querySelector('.unread-toggle input[type="checkbox"]');

  unreadToggle.addEventListener('change', ()=>{
    const showUnreadOnly = unreadToggle.checked;
    document.querySelectorAll('.chat-list .chat-item')
    .forEach(li => {
        const isUnread = li.dataset.unread === 'true';
        if(showUnreadOnly && !isUnread){
            li.style.display = 'none';
        }
        else{
            li.style.display = '';
        }
    });
  });

 // “다른 채팅방” 목록 로드 (AI 챗봇 밑에 li로 추가)
   async function loadOtherRooms() {
     const res = await fetch(
       `/chat/post/${postId}/rooms?username=${encodeURIComponent(currentUser)}`
     );
     if (!res.ok) {
       console.error('방 목록 로드 실패');
       return;
     }
     const list = await res.json();

    // 판매자면 전부, 구매자면 본인 방만
    const roomsToShow = (currentUser === postOwner)
      ? list
      : list.filter(r => r.username === currentUser);

     roomsToShow
      .filter(r => r.id !== +roomId)
       .forEach(r => {
        if (renderedRooms.has(r.id)) return;  // 이미 붙였다면 스킵
        renderedRooms.add(r.id);

         const li = document.createElement('li');
         li.className = 'chat-item';

         li.dataset.unread = r.unreadCount > 0 ? 'true' : 'false';

         // info
         const info = document.createElement('div');
         info.className = 'chat-info';

         // 이름 + 시간 표기
         const header = document.createElement('div');
         header.className = 'chat-header';

         const who = document.createElement('strong');
         const lastMessageTime = document.createElement('span');
         lastMessageTime.className='chat-time'

         who.textContent = r.username;
         lastMessageTime.textContent = r.lastMessageAt // 마지막 메세지 시간
                    ? new Date(r.lastMessageAt)
                    .toLocaleTimeString([],{hour: '2-digit', minute: '2-digit'}) : '';
         header.append(who, lastMessageTime);

         const footer = document.createElement("div");
         footer.className = 'chat-footer';
         // unread badge
         if (r.unreadCount > 0) {
           const badge = document.createElement('span');
           badge.className = 'unread-badge';
           badge.textContent = r.unreadCount;
           footer.append(badge);
         }
         const lastMessage = document.createElement('p');
         lastMessage.textContent = r.lastMessage || ''; // 마지막 메세지
         footer.append(lastMessage);

         info.append(header,footer);
         li.append(info);
         // 클릭 시 해당 방으로 이동
         li.addEventListener('click', () => {
           location.href = `/chat/room/${r.id}?username=${encodeURIComponent(currentUser)}`;
         });

         chatList.append(li);
       });
   }
   // 페이지 로드 시 한 번 실행
   loadOtherRooms();
});
