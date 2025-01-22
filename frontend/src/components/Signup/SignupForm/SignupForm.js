import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import DaumPostcode from 'react-daum-postcode'; // 다음 주소 검색 라이브러리
import '../Signup.css';

function SignupForm({ role, title }) {
  const [formData, setFormData] = useState({
    userId: '',
    password: '',
    name: '',
    email: '',
    phoneNumber: '',
    address: '', // 기본 주소
    detailedAddress: '', // 상세 주소
    gender: '', // 성별
    birthDate: '', // 생년월일
    vetImage: '', //수의사 이미지
    vetLicense: '', // 수의사 면허
    bankAccount: '', // 은행 계좌
    businessNumber: '', // 사업자 번호
    userRole: role,
  });

  const [isAddressModalOpen, setIsAddressModalOpen] = useState(false); // 주소 모달 상태
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [isUserIdAvailable, setIsUserIdAvailable] = useState(true); // 아이디 중복 체크 상태

   // 정규식 패턴
    const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/; // 비밀번호: 영문, 숫자, 특수문자 포함 8~20자


  // 파일 입력 처리 함수
  const handleFileChange = (event) => {
    const file = event.target.files[0];
    setFormData({
      ...formData,
      vetImage: file,
    });
  };
  // 입력값 변경 핸들러
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

 const checkUserIdAvailability = async () => {
   try {
     const response = await fetch(
       `${process.env.REACT_APP_API_BASE_URL}/user/checkUserId?userId=${formData.userId}`
     );

     if (response.ok) {
       const data = await response.json(); // 서버에서 반환한 데이터를 JSON으로 변환
       console.log(data); // 반환된 데이터 확인
       if (data.available) {
         setIsUserIdAvailable(true);
         setErrorMessage('');
       } else {
         setIsUserIdAvailable(false);
         setErrorMessage('이미 사용 중인 아이디입니다.');
       }
     } else {
       console.error('서버 응답 상태 코드:', response.status);
       setIsUserIdAvailable(false);
       setErrorMessage('아이디 중복 체크에 실패했습니다.');
     }
   } catch (error) {
     console.error('아이디 중복 체크 중 오류:', error);
     setErrorMessage('서버 오류로 아이디 확인에 실패했습니다.');
   }
 };


  // 주소 선택 핸들러
  const handleCompleteAddress = (data) => {
    setFormData({ ...formData, address: data.address });
    setIsAddressModalOpen(false); // 모달 닫기
  };

  // 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const formDataToSend = new FormData();

      // vetImage를 제외한 나머지 폼 데이터를 JSON으로 변환하여 "user"필드로 추가
      const { vetImage, ...userData } = formData; // vetImage만 제외
      formDataToSend.append("user", new Blob([JSON.stringify(userData)], { type: 'application/json' })); // user 필드 추가

      // vetImage 파일 추가
      if (formData.vetImage) {
        formDataToSend.append("vetImage", formData.vetImage);
      }
      if (!isUserIdAvailable) {
            setErrorMessage('사용할 수 없는 아이디입니다.');
            return;
          }

          if (!passwordPattern.test(formData.password)) {
            setErrorMessage(
              '비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.'
            );
            return;
          }

      // 로그 추가
      for (let pair of formDataToSend.entries()){
        console.log(pair[0] + ', ' + pair[1]);
      }

      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/user/signup`, {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
        },
        body: formDataToSend, // FormData 전송
      });

      if (response.ok) {
        setSuccessMessage('회원가입 성공! 로그인 페이지로 이동합니다.');
        setTimeout(() => navigate('/login'), 2000);
      } else {
        const error = await response.text();
        console.error("서버 응답 에러: ", error)
        setErrorMessage(error.message || '회원가입 실패.');
      }
    } catch (error) {
      console.error("catch 블록 에러: ", error)
      setErrorMessage('서버 오류가 발생했습니다.');
    }
  };

  return (
      <div className="signup-container">
        <h2>{title}</h2>
        <form onSubmit={handleSubmit}>
          {/* 기본 입력 필드 */}
          <label>
            아이디:
            <input
              type="text"
              name="userId"
              value={formData.userId}
              onChange={handleChange}
              onBlur={checkUserIdAvailability} // 포커스가 벗어날 때 중복 체크
              required
            />
            {!isUserIdAvailable && <span className="error-message">이미 사용 중인 아이디입니다.</span>}
          </label>

          <label>
            비밀번호:
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            이름:
            <input type="text" name="name" value={formData.name} onChange={handleChange} required />
          </label>
          <label>
            이메일:
            <input type="email" name="email" value={formData.email} onChange={handleChange} required />
          </label>
          <label>
            전화번호:
            <input
                type="text"
                name="phoneNumber"
                value={formData.phoneNumber}
                onChange={handleChange}
                placeholder="010-1234-5678"
                pattern="^010[-]?[0-9]{4}[-]?[0-9]{4}$"
                required
            />
          </label>

          <label>
            생년월일:
            <input
                type="date"
                name="birthDate"
                value={formData.birthDate}
                onChange={handleChange}
                required
            />
          </label>

          {/* 주소 입력 필드 */}
          <label>
            주소:
            <input
                type="text"
                name="address"
                value={formData.address}
                placeholder="주소를 검색하세요"
                readOnly
                onClick={() => setIsAddressModalOpen(true)} // 모달 열기
                required
            />
          </label>

          {isAddressModalOpen && (
              <div className="address-modal">
                <DaumPostcode onComplete={handleCompleteAddress} />
                <button onClick={() => setIsAddressModalOpen(false)}>닫기</button>
              </div>
          )}

          <label>
            상세 주소:
            <input
                type="text"
                name="detailedAddress"
                value={formData.detailedAddress}
                placeholder="상세 주소를 입력하세요 (선택)"
                onChange={handleChange}
            />
          </label>


          {/* 성별 입력 필드 */}
          <div className="gender-field">
            <label>
              성별:
              <select
                  name="gender"
                  value={formData.gender}
                  onChange={handleChange}
                  required
              >
                <option value="">선택하세요</option>
                <option value="남">남</option>
                <option value="여">여</option>
              </select>
            </label>
          </div>


          {/* 수의사 가입 추가 필드 */}
          {role === 'vet' && (
              <>
                <label>
                  수의사 인증번호:
                  <input type="text" name="vetLicense" value={formData.vetLicense} onChange={handleChange} required />
                </label>
                <label>
                  수의사 사진
                  <input type="file" name="vetImage" onChange={handleFileChange} />
                </label>
                <label>
                  계좌번호:
                  <input type="text" name="bankAccount" value={formData.bankAccount} onChange={handleChange} required />
                </label>
              </>
          )}

          {/* 판매자 가입 추가 필드 */}
          {role === 'seller' && (
              <label>
                사업자 번호:
                <input type="text" name="businessNumber" value={formData.businessNumber} onChange={handleChange} required />
              </label>
          )}

          <button type="submit">회원가입</button>
        </form>

        {errorMessage && <p className="error-message error-below-button">{errorMessage}</p>}
        {successMessage && <p className="success-message">{successMessage}</p>}
      </div>
  );
}

export default SignupForm;
