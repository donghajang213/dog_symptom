import React, { useState, useEffect } from 'react';
import './PermissionManagement.css';

function PermissionManagement() {
  const [requests, setRequests] = useState([]);

  useEffect(() => {
    // 권한 요청 데이터 가져오기
    async function fetchRequests() {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/permissions`);
      const data = await response.json();
      setRequests(data);
    }
    fetchRequests();
  }, []);

  const handleApprove = async (userId, role) => {
    console.log("승인 요청 보내기: ", userId, role); // 로그 추가
    await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/approve/${userId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ role }),
    });
    setRequests(requests.map((req) => (req.userId === userId ? { ...req, userRole: role } : req)));
  };

  const handleReject = async (userId) => {
    await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/rejectRole/${userId}`, {
      method: 'PUT',
    });
    setRequests(requests.map((req) => (req.userId === userId ? { ...req, userRole: 'REJECTED' } : req)));
  };

  return (
      <div className="permission-management">
        <h2>권한 정보 관리</h2>
        <table>
          <thead>
          <tr>
            <th>이름</th>
            <th>역할</th>
            <th>인증번호</th>
            <th>상태</th>
            <th>작업</th>
          </tr>
          </thead>
          <tbody>
          {requests.map((request) => (
              <tr key={request.userId}>
                <td>{request.name}</td>
                <td>{request.userRole}</td>
                <td>{request.userRole === 'VET' ? request.vetLicense : request.businessNumber}</td>
                <td>{request.userRole}</td>
                <td>
                  {request.userRole === 'PENDING' && (
                      <>
                        <button onClick={() => handleApprove(request.userId, 'VET')}>승인</button>
                        <button onClick={() => handleReject(request.userId)}>거부</button>
                      </>
                  )}
                </td>
              </tr>
          ))}
          </tbody>
        </table>
      </div>
  );
}

export default PermissionManagement;
