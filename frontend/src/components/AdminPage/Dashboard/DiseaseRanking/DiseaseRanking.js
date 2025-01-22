import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import './DiseaseRanking.css';  // 스타일 파일 import

function DiseaseRanking() {
  const [diseaseData, setDiseaseData] = useState([]);  // 질병별 데이터
  const [loading, setLoading] = useState(true);  // 로딩 상태
  const [error, setError] = useState(null);  // 오류 상태

  useEffect(() => {
    const fetchDiseaseData = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/disease-ranking/by-disease`);

        if (!response.ok) {
          throw new Error('데이터를 가져오는데 실패했습니다.');
        }

        const data = await response.json();
        setDiseaseData(data);  // 데이터 저장
      } catch (err) {
        setError(`오류 발생: ${err.message}`);
      } finally {
        setLoading(false);
      }
    };

    fetchDiseaseData();
  }, []);

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div>오류 발생: {error}</div>;

  return (
    <div className="disease-ranking-container">
      <h1>질병별 발생 빈도</h1>
      {diseaseData.length > 0 ? (
        <ResponsiveContainer width="100%" height={400}>
          <BarChart data={diseaseData} margin={{ top: 30, right: 30, left: 30, bottom: 30 }}>
            <CartesianGrid strokeDasharray="5 5" stroke="#ddd" />
            <XAxis dataKey="predictedDisease" tick={{ fill: '#4e4e4e' }} />
            <YAxis tick={{ fill: '#4e4e4e' }} />
            <Tooltip contentStyle={{ backgroundColor: '#f5f5f5', borderRadius: '5px' }} />
            <Bar dataKey="casesCount" fill="#82ca9d" radius={[5, 5, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      ) : (
        <p>데이터가 없습니다.</p>
      )}
    </div>
  );
}

export default DiseaseRanking;
