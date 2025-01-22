import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from 'recharts';

function VetRanking() {
  const [vetData, setVetData] = useState([]); // 수의사 데이터를 저장
  const [loading, setLoading] = useState(true); // 로딩 상태
  const [error, setError] = useState(null); // 에러 상태

  useEffect(() => {
    const fetchVetData = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/ranking/vet`);
        if (!response.ok) {
          throw new Error('수의사 데이터를 가져오는데 실패했습니다.');
        }
        const data = await response.json();
        setVetData(data); // 데이터 저장
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchVetData();
  }, []);

  if (loading) return <div>로딩 중...</div>; // 로딩 상태 표시
  if (error) return <div>오류 발생: {error}</div>; // 에러 상태 표시

  return (
    <div>
      <h1>수의사 랭킹</h1>
      <ResponsiveContainer width="100%" height={400}>
        <BarChart data={vetData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="vetName" label={{ value: "수의사 이름", position: "insideBottom", offset: -5 }} />
          <YAxis label={{ value: "평점", angle: -90, position: "insideLeft" }} />
          <Tooltip />
          <Bar dataKey="vetRating" fill="#82ca9d" name="평점" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default VetRanking;
