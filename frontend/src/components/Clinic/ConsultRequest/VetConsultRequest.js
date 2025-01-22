import React, { useEffect, useState } from "react";
import { Modal, Button } from "react-bootstrap";
import WebSocketService from "../../../services/WebSocketService";
import "./VetConsultRequest.css";

const VetConsultRequest = () => {
    const [userId, setUserId] = useState("");
    const [vetId, setVetId] = useState("");
    const [selectedVetId, setSelectedVetId] = useState("");
    const [chatId, setChatId] = useState(null);
    const [requests, setRequests] = useState([]);
    const [selectedRequest, setSelectedRequest] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);

    const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

    // Fetch the user ID
    useEffect(() => {
        const fetchUserId = async () => {
            try {
                const response = await fetch(`${API_BASE_URL}/consultations/get-user-id`, {
                    credentials: "include",
                });
                if (response.ok) {
                    const data = await response.json();
                    if (data) {
                        setUserId(data.userId);
                    } else {
                        console.error("Empty response body");
                    }
                } else {
                    console.error("Failed to fetch user ID:", response.status);
                }
            } catch (error) {
                console.error("Failed to fetch user ID:", error);
            }
        };

        fetchUserId();
    }, [API_BASE_URL]);

    // Fetch the vet ID based on the user ID
    useEffect(() => {
        const fetchVetId = async () => {
            if (userId) {
                try {
                    const response = await fetch(`${API_BASE_URL}/consultations/get-vet-id?userId=${userId}`);
                    if (response.ok) {
                        const data = await response.json();
                        if (!data) {
                            console.error("No data received");
                        } else {
                            setVetId(data.vetId);
                            setSelectedVetId(data.vetId);
                        }
                    } else {
                        console.error("Failed to fetch vet ID:", response.status);
                    }
                } catch (error) {
                    console.error("Error fetching vet ID:", error);
                }
            }
        };

        fetchVetId();
    }, [userId, API_BASE_URL]);

    // Fetch consultation requests
    useEffect(() => {
        const fetchRequests = async () => {
            if (vetId) {
                setIsLoading(true);
                try {
                    const response = await fetch(`${API_BASE_URL}/consultations/${vetId}/detailed-requests`);
                    if (response.ok) {
                        const data = await response.json();
                        setRequests(data || []);
                    } else {
                        console.error("Failed to fetch consultation requests:", response.status);
                    }
                } catch (error) {
                    console.error("Failed to fetch consultation requests:", error);
                } finally {
                    setIsLoading(false);
                }
            }
        };

        fetchRequests();
    }, [vetId, API_BASE_URL]);

    // WebSocket connection
    useEffect(() => {
        if (userId && vetId) {
            const fetchChatId = async () => {
                try {
                    const response = await fetch(`${API_BASE_URL}/chat/rooms?userId=${userId}&vetId=${vetId}`);
                    if (response.ok) {
                        const data = await response.json();
                        if (data && data.chatId) {
                            setChatId(data.chatId);
                            WebSocketService.connect(data.chatId);

                            WebSocketService.onMessageReceived = (message) => {
                                if (message.type === "newConsultationRequest") {
                                    setRequests((prevRequests) => [...prevRequests, message]);
                                } else if (message.type === "consultationStatusUpdate") {
                                    setRequests((prevRequests) =>
                                        prevRequests.map((req) =>
                                            req.request_id === message.request_id ? { ...req, status: message.status } : req,
                                        ),
                                    );
                                }
                            };

                            return () => {
                                WebSocketService.disconnect();
                            };
                        } else {
                            alert("No chat room found. You can create a new one.");
                        }
                    } else {
                        console.error("Failed to fetch chat room:", response.status);
                        alert("Error fetching chat room. Please try again.");
                    }
                } catch (error) {
                    console.error("Failed to fetch chat room:", error);
                    alert("Error fetching chat room. Please try again.");
                }
            };
            fetchChatId();
        }
    }, [userId, vetId, API_BASE_URL]);


    // Show modal for selected request
    const handleShowModal = (request) => {
        setSelectedRequest(request);
        setShowModal(true);

        // 채팅방을 열 때 customerId를 사용하여 WebSocket 연결
        if (request.user_id && vetId) {
            const chatId = request.user_id + "_" + vetId;
            WebSocketService.connect(chatId);

            WebSocketService.onMessageReceived = (message) => {
                if (message.type === "newConsultationRequest") {
                    setRequests((prevRequests) => [...prevRequests, message]);
                } else if (message.type === "consultationStatusUpdate") {
                    setRequests((prevRequests) =>
                        prevRequests.map((req) =>
                            req.request_id === message.request_id ? { ...req, status: message.status } : req,
                        ),
                    );
                }
            };

            // 선택 사항: Modal이 닫힐 때 WebSocket 연결 해제
            // return () => {
            //     WebSocketService.disconnect();
            // };
        }
    };


    // Handle approval of the request
    const handleApprove = async () => {
        if (selectedRequest && selectedRequest.request_id && selectedVetId) {
            try {
                const response = await fetch(
                    `${API_BASE_URL}/consultations/${selectedRequest.request_id}/accept?vetId=${selectedVetId}`,
                    {
                        method: "POST",
                        credentials: "include",
                    },
                );
                if (response.ok) {
                    console.log("Request Approved");
                    setRequests((prevRequests) =>
                        prevRequests.map((req) =>
                            req.request_id === selectedRequest.request_id ? { ...req, status: "ACCEPTED" } : req,
                        ),
                    );
                    setShowModal(false);
                } else {
                    console.error("Failed to approve request:", response.status);
                }
            } catch (error) {
                console.error("Error approving request:", error);
            }
        } else {
            console.error("Invalid request or vet ID");
        }
    };

    // Handle rejection of the request
    const handleReject = async () => {
        if (selectedRequest && selectedRequest.request_id) {
            try {
                const response = await fetch(`${API_BASE_URL}/consultations/${selectedRequest.request_id}/reject`, {
                    method: "POST",
                });
                if (response.ok) {
                    console.log("Request Rejected");
                    setRequests((prevRequests) =>
                        prevRequests.map((req) =>
                            req.request_id === selectedRequest.request_id ? { ...req, status: "REJECTED" } : req,
                        ),
                    );
                    setShowModal(false);
                } else {
                    console.error("Failed to reject request:", response.status);
                }
            } catch (error) {
                console.error("Error rejecting request:", error);
            }
        }
    };

    return (
        <div>
            <h2>상담 요청</h2>
            {isLoading ? (
                <div className="loader">로딩 중...</div>
            ) : requests.length === 0 ? (
                <p>요청이 없습니다.</p>
            ) : (
                <table>
                    <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Status</th>
                        <th>Created At</th>
                        <th>Details</th>
                    </tr>
                    </thead>
                    <tbody>
                    {requests.map((request, index) => (
                        <tr key={request.request_id || index}>
                            <td>{request.user_id}</td>
                            <td>{request.status}</td>
                            <td>{request.created_at ? new Date(request.created_at).toLocaleString() : "Invalid Date"}</td>
                            <td>
                                <button onClick={() => handleShowModal(request)}>보기</button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}

            {selectedRequest && (
                <Modal show={showModal} onHide={() => setShowModal(false)} dialogClassName="custom-modal" scrollable>
                    <Modal.Header>
                        <Modal.Title>상담 요청 세부 정보</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <div style={{ textAlign: "center" }}>
                            <p>
                                <strong>질병명:</strong> {selectedRequest.predicted_disease || "정보 없음"}
                            </p>
                            {selectedRequest.processed_image_path && (
                                <img
                                    src={selectedRequest.processed_image_path}
                                    alt="질병 이미지"
                                    style={{
                                        maxWidth: "100%",
                                        maxHeight: "300px",
                                        objectFit: "contain",
                                        margin: "10px auto",
                                        border: "1px solid #ccc",
                                        borderRadius: "5px",
                                    }}
                                />
                            )}
                        </div>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button variant="success" onClick={handleApprove} style={{ width: "auto" }}>
                            수락
                        </Button>
                        <Button variant="danger" onClick={handleReject} style={{ width: "auto" }}>
                            거절
                        </Button>
                        <Button variant="secondary" onClick={() => setShowModal(false)} style={{ width: "auto" }}>
                            닫기
                        </Button>
                    </Modal.Footer>
                </Modal>
            )}
        </div>
    );
};

export default VetConsultRequest;