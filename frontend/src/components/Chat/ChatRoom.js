import React, { useEffect, useState } from 'react';
import WebSocketService from '../../services/WebSocketService';

const ChatRoom = () => {
    const [messages, setMessages] = useState([]);
    const [messageInput, setMessageInput] = useState('');
    const [chatId, setChatId] = useState(null);
    const [userId, setUserId] = useState('');
    const [vetId, setVetId] = useState('');

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await fetch('/user/me');
                const data = await response.json();

                if (response.ok) {
                    setUserId(data.user.userId);
                    setVetId(data.user.vetId);
                } else {
                    console.error('사용자 정보를 가져오는 데 실패했습니다:', data.message);
                }
            } catch (error) {
                console.error('에러 발생:', error);
            }
        };

        fetchUserInfo();
    }, []);

    useEffect(() => {
        if (userId && vetId) {
            fetch(`/chat/rooms?userId=${userId}&vetId=${vetId}`)
                .then(response => response.json())
                .then(data => {
                    if (data) {
                        setChatId(data.chatId);
                        WebSocketService.connect(data.chatId);

                        WebSocketService.onMessageReceived = (message) => {
                            setMessages((prevMessages) => [...prevMessages, message]);
                        };
                    } else {
                        alert("No chat room found. You can create a new one.");
                    }
                });
        }
    }, [userId, vetId]);

    const handleSendMessage = () => {
        if (messageInput.trim()) {
            WebSocketService.sendMessage(chatId, userId, vetId, messageInput, 'text');
            setMessages((prevMessages) => [
                ...prevMessages,
                { senderId: userId, message: messageInput, sentAt: new Date().toISOString() }
            ]);
            setMessageInput('');
        }
    };

    return (
        <div>
            <div>
                {messages.map((msg, idx) => (
                    <div key={idx}>
                        <p><strong>{msg.senderId}</strong>: {msg.message}</p>
                        <small>{new Date(msg.sentAt).toLocaleTimeString()}</small>
                    </div>
                ))}
            </div>
            <input
                type="text"
                value={messageInput}
                onChange={(e) => setMessageInput(e.target.value)}
                placeholder="Type a message"
            />
            <button onClick={handleSendMessage}>Send</button>
        </div>
    );
};

export default ChatRoom;