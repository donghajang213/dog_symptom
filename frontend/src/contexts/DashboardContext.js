import React, { createContext, useState, useEffect } from 'react';

export const DashboardContext = createContext();

export const DashboardProvider = ({ children }) => {
  const [dashboardData, setDashboardData] = useState([]); // 초기 데이터를 빈 배열로 설정
  const [loading, setLoading] = useState(true); // 로딩 상태 추가
  const [error, setError] = useState(null); // 에러 상태 추가

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/dashboard`);
        if (!response.ok) {
          throw new Error('데이터를 가져오는 데 실패했습니다.');
        }
        const data = await response.json();
        setDashboardData(data); // API에서 가져온 데이터 설정
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []); // 컴포넌트가 마운트될 때 한 번만 호출

  return (
    <DashboardContext.Provider value={{ dashboardData, loading, error }}>
      {children}
    </DashboardContext.Provider>
  );
};
