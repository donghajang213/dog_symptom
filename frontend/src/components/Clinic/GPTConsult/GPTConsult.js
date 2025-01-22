import React, { useContext, useState } from 'react';
import { UserContext } from '../../../contexts/UserContext';
import { useNavigate } from 'react-router-dom';
import './GPTConsult.css';

function GPTConsult() {
  const { user } = useContext(UserContext); // 로그인 상태 확인
  const navigate = useNavigate();
  const [uploadedImage, setUploadedImage] = useState(null);
  const [messages, setMessages] = useState([
    { sender: 'system', text: '이미지를 업로드하여 반려동물에 대한 상담을 시작하세요.' },
  ]);
  const [input, setInput] = useState('');

  // 로그인 확인
  if (!user) {
    alert('이 서비스는 로그인 후 이용할 수 있습니다.');
    navigate('/login');
    return null;
  }

  // 이미지 업로드 처리
  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        setUploadedImage(reader.result);
        setMessages([
          ...messages,
          { sender: 'user', text: '이미지를 업로드했습니다.' },
          { sender: 'system', text: '이미지를 분석 중입니다. 잠시만 기다려주세요...' },
        ]);
        // GPT 모델과 통신 (모의 API 호출)
        setTimeout(() => {
          setMessages((prevMessages) => [
            ...prevMessages,
            { sender: 'gpt', text: '이미지 분석 결과: 반려동물의 피부 상태가 정상입니다.' },
          ]);
        }, 2000); // 2초 후 분석 결과 표시
      };
      reader.readAsDataURL(file);
    }
  };

  // 메시지 전송 처리
  const handleSendMessage = () => {
    if (!input.trim()) return;

    setMessages([...messages, { sender: 'user', text: input }]);
    setInput('');

    // GPT와 대화 (모의 API 호출)
    setTimeout(() => {
      setMessages((prevMessages) => [
        ...prevMessages,
        { sender: 'gpt', text: `GPT 응답: "${input}"에 대한 답변입니다.` },
      ]);
    }, 1000); // 1초 후 GPT 응답
  };

  return (
    <div className="gpt-consult-container">
      {/* 이미지 업로드 및 분석 섹션 */}
      <div className="upload-section">
        <h2>GPT 상담</h2>
        <p>이미지를 업로드하여 반려동물의 상태를 분석하고 상담을 시작하세요.</p>
        <input
          type="file"
          accept="image/*"
          onChange={handleImageUpload}
          className="image-upload-input"
        />
        {uploadedImage && (
          <img
            src={uploadedImage}
            alt="미리보기"
            className="uploaded-image"
          />
        )}
      </div>

      {/* 대화창 */}
      <div className="message-container">
        {messages.map((message, index) => (
          <div
            key={index}
            className={`message ${message.sender === 'user' ? 'user-message' : 'gpt-message'}`}
          >
            <div className={`message-bubble ${message.sender}`}>
              {message.text.split('\n').map((line, idx) => (
                <p key={idx}>{line}</p>
              ))}
            </div>
          </div>
        ))}
      </div>

      {/* 메시지 입력창 */}
      <div className="message-input-container">
        <input
          type="text"
          placeholder="메시지를 입력해주세요."
          value={input}
          onChange={(e) => setInput(e.target.value)}
          className="message-input"
        />
        <button onClick={handleSendMessage} className="send-button">
          ➤
        </button>
      </div>
    </div>
  );
}

export default GPTConsult;
