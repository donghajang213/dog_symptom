import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { UserContext } from '../../contexts/UserContext';
import './Login.css';

function Login() {
  const [formData, setFormData] = useState({ userId: '', password: '' });
  const [errorMessage, setErrorMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const { setUser } = useContext(UserContext);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage('');
    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
        credentials: 'include',
      });

      if (response.ok) {
        const user = await response.json();
        setUser(user);
        navigate('/');
      } else {
        setErrorMessage('아이디 또는 비밀번호가 잘못되었습니다.');
      }
    } catch (error) {
      console.error('로그인 오류:', error);
      setErrorMessage('서버 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <h2 className="login-title">로그인</h2>
      <form onSubmit={handleSubmit} className="login-form">
        <div className="input-wrapper">
          <input
            type="text"
            name="userId"
            value={formData.userId}
            onChange={handleChange}
            placeholder="아이디"
            className="login-input"
            required
          />
        </div>
        <div className="input-wrapper">
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="비밀번호"
            className="login-input"
            required
          />
        </div>
        {errorMessage && <p className="error-message">{errorMessage}</p>}
        <button type="submit" className="login-submit" disabled={loading}>
          {loading ? '로딩 중...' : '로그인'}
        </button>
      </form>
      <div className="login-links">
        <a href="/find-id">아이디 찾기</a> | <a href="/find-password">비밀번호 찾기</a> |{' '}
        <a href="/signup">회원가입</a>
      </div>
    </div>
  );
}

export default Login;
