import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import './SignupRanking.css';  // 스타일 파일 import

function SignupRanking() {
  const [userData, setUserData] = useState([]);  // 월별 유저 데이터
  const [loading, setLoading] = useState(true);  // 로딩 상태
  const [error, setError] = useState(null);  // 오류 상태

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user-ranking/by-month`);

        if (!response.ok) {
          throw new Error('데이터를 가져오는데 실패했습니다.');
        }

        const data = await response.json();
        setUserData(data);  // 데이터 저장
      } catch (err) {
        setError(`오류 발생: ${err.message}`);
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, []);

  if (loading) return <div className="loading">로딩 중...</div>;
  if (error) return <div className="error">오류 발생: {error}</div>;

  return (
    <div className="signup-ranking-container">
      <h1>월별 유저 가입 수</h1>
      {userData.length > 0 ? (
        <ResponsiveContainer width="100%" height={400}>
          <BarChart data={userData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="month" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="userCount" fill="#82ca9d" name="가입자 수" />
          </BarChart>
        </ResponsiveContainer>
      ) : (
        <p>데이터가 없습니다.</p>
      )}
    </div>
  );
}

export default SignupRanking;
