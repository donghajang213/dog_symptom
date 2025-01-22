import React, { useState } from "react";

const ChatApp = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");

  const handleSend = async () => {
    if (!input.trim()) return;

    // 사용자 입력 메시지 추가
    setMessages([...messages, { sender: "user", text: input }]);

    try {
      // Ollama 서버 호출
      const response = await fetch("http://localhost:11434/api/generate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          model: "llama2", // 사용 중인 모델
          prompt: input,  // 사용자 입력
        }),
      });

      if (!response.ok) {
        // 상태 코드와 메시지 출력
        const errorText = await response.text();
        throw new Error(
          `HTTP error! status: ${response.status}, message: ${errorText}`
        );
      }

      const data = await response.json();

      // Ollama 응답 메시지 추가
      setMessages((prev) => [...prev, { sender: "bot", text: data.response }]);
    } catch (error) {
      console.error("Error occurred while fetching response:", error.message);
      setMessages((prev) => [
        ...prev,
        { sender: "bot", text: "Error occurred while fetching response." },
      ]);
    }

    setInput(""); // 입력 초기화
  };

  return (
    <div style={{ maxWidth: "600px", margin: "0 auto", padding: "20px" }}>
      <h1>Chat with AI</h1>
      <div
        style={{
          border: "1px solid #ddd",
          padding: "10px",
          height: "400px",
          overflowY: "scroll",
          marginBottom: "10px",
        }}
      >
        {messages.map((msg, index) => (
          <div
            key={index}
            style={{
              textAlign: msg.sender === "user" ? "right" : "left",
              marginBottom: "10px",
            }}
          >
            <strong>{msg.sender === "user" ? "You" : "AI"}:</strong>
            <p>{msg.text}</p>
          </div>
        ))}
      </div>
      <div style={{ display: "flex", gap: "10px" }}>
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Type your message here..."
          style={{ flex: 1, padding: "10px", border: "1px solid #ddd" }}
        />
        <button onClick={handleSend} style={{ padding: "10px 15px" }}>
          Send
        </button>
      </div>
    </div>
  );
};

export default ChatApp;
