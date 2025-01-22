import React, { useContext } from 'react';
import './Clinic.css';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { UserContext } from '../../contexts/UserContext'; // 로그인 상태 확인용 Context

function Clinic() {
  const { user } = useContext(UserContext); // 로그인 상태 가져오기
  const navigate = useNavigate();
  const location = useLocation();
  const { analysisResult } = location.state || {}; // PhotoUpload에서 전달된 분석 결과

  const handleProtectedAccess = (path) => {
    if (!user) {
      alert('이 서비스는 로그인 후 이용할 수 있습니다.');
      navigate('/login'); // 로그인 페이지로 리다이렉트
    } else {
      navigate(path); // 서비스 페이지로 이동
    }
  };

  return (
    <div className="clinic-container">
                {/* 분석 결과가 있으면 출력 */}
                {analysisResult ? (
                    <div className="analysis-result">
                        <h3>진단 결과</h3>
                        <p><strong>질병명:</strong> {analysisResult.predictedDisease}</p>
                        {/* originImagePath 또는 processedImagePath를 사용 */}
                        <img
                          src={`${process.env.REACT_APP_API_BASE_URL}${analysisResult.originImagePath}`}
                          alt="분석된 이미지"
                          style={{
                            width: "300px",
                            height: "300px",
                            objectFit: "cover",
                            borderRadius: "8px",
                          }}
                        />
                    </div>
                ) : (
                    <p>분석된 결과가 없습니다. 다시 시도해 주세요.</p>
                )}

                <h2>클리닉</h2>
                <div className="options">
                    {/* 수의사 상담 */}
                    <div className="option">
                        <img src="/images/vet_consult.png" alt="수의사 상담" className="option-image" />
                        <button className="option-button" onClick={() => handleProtectedAccess('/clinic/vetselect')}>
                            수의사 상담
                        </button>
                    </div>

                    {/* GPT 상담 */}
                    <div className="option">
                        <img src="/images/gpt_consult.png" alt="GPT 상담" className="option-image" />
                        <button className="option-button" onClick={() => handleProtectedAccess('/clinic/chat')}>
                            GPT 상담
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    export default Clinic;
