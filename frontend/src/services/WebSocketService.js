import React, { useState, useEffect } from 'react';

const WebSocketService = {
    socket: null,
    reconnectInterval: null, // 재연결 인터벌 ID

    connect(chatId) {
        this.socket = new WebSocket(`ws://localhost:8080/chat/message/${chatId}`);

        this.socket.onopen = () => {
            console.log("WebSocket connection established.");
            clearInterval(this.reconnectInterval); // 연결 성공 시 재연결 인터벌 해제
        };

        this.socket.onmessage = (event) => {
            const message = JSON.parse(event.data);
            // 받은 메시지 처리
        };

        this.socket.onclose = (event) => {
            console.log(`WebSocket connection closed. Code: ${event.code}, Reason: ${event.reason}`);
            this.reconnect(chatId); // 연결 종료 시 재연결 시도
        };

        this.socket.onerror = (error) => {
            console.error("WebSocket error:", error);
            this.reconnect(chatId); // 오류 발생 시 재연결 시도
        };
    },

    sendMessage(chatId, senderId, vetId, message, type) {
        const messageData = {
            chatId: chatId,
            senderId: senderId,
            vetId: vetId,
            message: message,
            type: type,
            sentAt: new Date().toISOString(),
        };

        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify(messageData));
        } else {
            console.error("WebSocket is not open. Message not sent.");
        }
    },

    disconnect() {
        clearInterval(this.reconnectInterval); // 연결 해제 시 재연결 인터벌 해제
        if (this.socket) {
            this.socket.close();
            this.socket = null; // 소켓 객체 null 처리
            console.log("WebSocket connection disconnected.");
        }
    },

    reconnect(chatId) {
        clearInterval(this.reconnectInterval); // 기존 재연결 인터벌 해제
        this.reconnectInterval = setInterval(() => {
            console.log("Attempting to reconnect...");
            this.connect(chatId);
        }, 5000); // 5초 간격으로 재연결 시도
    },
};

export default WebSocketService;