import React, { useEffect, useState } from 'react';
import './Dashboard.css'; // 스타일 파일
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { Link } from "react-router-dom"; // Link 추가
import { FaUserMd, FaDisease, FaDog, FaBox, FaIndustry } from 'react-icons/fa';

function Dashboard() {
  const [data, setData] = useState({
    vetRanking: null,
    diseaseRanking: null,
    breedRanking: null,
    signupRanking: null,
    productRanking: null,
    originRanking: null,
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const responses = await Promise.all([
          fetch(`${process.env.REACT_APP_API_BASE_URL}/ranking/vet`),
          fetch(`${process.env.REACT_APP_API_BASE_URL}/ranking/disease`),
          fetch(`${process.env.REACT_APP_API_BASE_URL}/ranking/breed`),
          fetch(`${process.env.REACT_APP_API_BASE_URL}/ranking/signup`),
          fetch(`${process.env.REACT_APP_API_BASE_URL}/ranking/product`),
          fetch(`${process.env.REACT_APP_API_BASE_URL}/ranking/origin`),
        ]);

        const data = await Promise.all(responses.map((res) => res.json()));

        setData({
          vetRanking: data[0][0],
          diseaseRanking: data[1][0],
          breedRanking: data[2][0],
          signupRanking: data[3][0],
          productRanking: data[4][0],
          originRanking: data[5][0],
        });
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div>오류 발생: {error}</div>;

  return (
    <div className="dashboard">
      <h1 className="dashboard-title">대시보드</h1>
      <div className="dashboard-grid">
        {/* 수의사 랭킹 카드 */}
        <Link to="/admin/dashboard/vet" className="dashboard-card">
          <FaUserMd className="card-icon" />
          <h2>수의사 랭킹</h2>
          <p>
            {data.vetRanking?.vetName || '정보 없음'} -{' '}
            {data.vetRanking?.vetRating?.toFixed(1) || '정보 없음'}
          </p>
        </Link>

        {/* 질병 랭킹 카드 */}
        <Link to="/admin/dashboard/disease" className="dashboard-card">
          <FaDisease className="card-icon" />
          <h2>질병 랭킹</h2>
          <p>
            {data.diseaseRanking?.diseaseName || '정보 없음'} -{' '}
            {data.diseaseRanking?.casesCount || 0}건
          </p>
        </Link>

        {/* 품종 랭킹 카드 */}
        <Link to="/admin/dashboard/breed" className="dashboard-card">
          <FaDog className="card-icon" />
          <h2>품종 랭킹</h2>
          <p>
            {data.breedRanking?.dogCategory || '정보 없음'} -{' '}
            {data.breedRanking?.casesCount || 0}건
          </p>
        </Link>

        {/* 회원가입 랭킹 카드 */}
        <Link to="/admin/dashboard/signup" className="dashboard-card">
          <FaUserMd className="card-icon" />
          <h2>회원가입 랭킹</h2>
          <p>
            {data.signupRanking?.month || '정보 없음'} -{' '}
            {data.signupRanking?.signupCount || 0}명
          </p>
        </Link>

        {/* 상품 랭킹 카드 */}
        <Link to="/admin/dashboard/product" className="dashboard-card">
          <FaBox className="card-icon" />
          <h2>상품 랭킹</h2>
          <p>
            {data.productRanking?.productName || '정보 없음'} -{' '}
            {data.productRanking?.productSales || 0}개
          </p>
        </Link>

        {/* 제조사 랭킹 카드 */}
        <Link to="/admin/dashboard/origin" className="dashboard-card">
          <FaIndustry className="card-icon" />
          <h2>제조사 랭킹</h2>
          <p>
            {data.originRanking?.originName || '정보 없음'} -{' '}
            {data.originRanking?.originSales || 0}개
          </p>
        </Link>

        {/* 예제 그래프 */}
        <div className="dashboard-graph">
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={[data.vetRanking, data.diseaseRanking]} margin={{ top: 20 }}>
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="rating" fill="#8884d8" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
