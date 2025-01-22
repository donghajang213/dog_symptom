import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import './BreedRanking.css';  // 스타일 파일 임포트

function PetRanking() {
  const [petData, setPetData] = useState([]);  // 품종별 강아지 데이터
  const [loading, setLoading] = useState(true);  // 로딩 상태
  const [error, setError] = useState(null);  // 오류 상태

  useEffect(() => {
    const fetchPetData = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/pet-ranking/by-breed`);

        if (!response.ok) {
          throw new Error('데이터를 가져오는데 실패했습니다.');
        }

        const data = await response.json();
        setPetData(data);  // 데이터 저장
      } catch (err) {
        setError(`오류 발생: ${err.message}`);
      } finally {
        setLoading(false);
      }
    };

    fetchPetData();
  }, []);

  if (loading) return <div className="loading">로딩 중...</div>;
  if (error) return <div className="error">오류 발생: {error}</div>;

  return (
    <div className="pet-ranking-container">
      <h1>강아지 품종별 개수</h1>
      {petData.length > 0 ? (
        <ResponsiveContainer width="100%" height={400}>
          <BarChart data={petData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="breed" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="count" fill="#82ca9d" name="강아지 수" />
          </BarChart>
        </ResponsiveContainer>
      ) : (
        <p>데이터가 없습니다.</p>
      )}
    </div>
  );
}

export default PetRanking;
