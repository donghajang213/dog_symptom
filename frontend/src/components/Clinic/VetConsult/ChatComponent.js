import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const ChatComponent = ({ roomId, userId, vetId }) => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [client, setClient] = useState(null);

    useEffect(() => {
        const stompClient = new Client({
            brokerURL: 'ws://localhost:8080/chat',
            connectHeaders: {
                login: 'user',
                passcode: 'password',
            },
            debug: function (str) {
                console.log(str);
            },
            onConnect: () => {
                // 서버로부터 메시지 받기
                stompClient.subscribe(`/topic/messages/${roomId}`, (message) => {
                    const chatMessage = JSON.parse(message.body);
                    setMessages((prevMessages) => [...prevMessages, chatMessage]);
                });
            },
        });

        stompClient.activate();
        setClient(stompClient);

        return () => {
            stompClient.deactivate();
        };
    }, [roomId]);

    const sendMessage = () => {
        if (client && newMessage.trim()) {
            const message = {
                roomId,
                userId,
                vetId,
                chatInput: newMessage,
                chatOutput: '',  // 초기에는 수의사의 응답이 없으므로 빈 문자열
                chatTime: new Date().toISOString(),
            };

            // 메시지 전송
            client.publish({
                destination: '/app/sendMessage',
                body: JSON.stringify(message),
            });

            setNewMessage('');
        }
    };

    return (
        <div>
            <div>
                {messages.map((message, index) => (
                    <div key={index}>
                        <strong>{message.userId}:</strong> {message.chatInput}
                        <br />
                        <strong>Vet:</strong> {message.chatOutput}
                    </div>
                ))}
            </div>
            <input
                type="text"
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                placeholder="Type a message"
            />
            <button onClick={sendMessage}>Send</button>
        </div>
    );
};

export default ChatComponent;
