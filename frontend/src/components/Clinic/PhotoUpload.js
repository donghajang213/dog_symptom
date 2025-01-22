import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";

function PhotoUpload() {
    const [selectedFile, setSelectedFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [loading, setLoading] = useState(false);
    const [analysisResult, setAnalysisResult] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();

    // 선택된 반려동물 데이터를 location에서 가져옴
    const selectedPet = location.state?.selectedPet;

    const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB 제한

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            if (file.size > MAX_FILE_SIZE) {
                alert("파일 크기가 너무 큽니다. 5MB 이하의 파일만 업로드 가능합니다.");
                return;
            }
            if (file.type.startsWith("image/")) {
                setSelectedFile(file);

                const reader = new FileReader();
                reader.onloadend = () => {
                    setPreview(reader.result);
                };
                reader.readAsDataURL(file);
            } else {
                alert("이미지 파일만 업로드할 수 있습니다.");
            }
        }
    };

    const handleUpload = () => {
        if (!selectedFile) {
            alert("사진을 선택해주세요!");
            return;
        }

        if (!selectedPet) {
            alert("반려동물이 선택되지 않았습니다. 다시 시도해주세요.");
            navigate("/clinic/select-pet");
            return;
        }

        const formData = new FormData();
        formData.append("file", selectedFile);
        formData.append("petId", selectedPet.petId); // 선택된 반려동물의 petId 추가

        const apiUrl = `${process.env.REACT_APP_API_BASE_URL}/api/analyze`;
        console.log("API 요청 URL:", apiUrl);
        console.log("전송할 Pet ID:", selectedPet.petId);

        setLoading(true);

        fetch(apiUrl, {
            method: "POST",
            body: formData,
            credentials: "include", // 세션 쿠키 포함
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP 오류! 상태 코드: ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                setLoading(false);
                setAnalysisResult(data);
                alert("분석이 완료되었습니다.");
                navigate("/clinic", { state: { analysisResult: data, selectedPet } });
            })
            .catch((error) => {
                console.error("업로드 실패:", error.message || error);
                setLoading(false);
                alert(`업로드 실패: ${error.message || "네트워크 오류가 발생했습니다. 서버를 확인해주세요."}`);
            });
    };

    const buttonStyle = {
        marginTop: "20px",
        padding: "10px 20px",
        borderRadius: "5px",
        border: "none",
        color: "#fff",
        cursor: "pointer",
    };

    if (!selectedPet) {
        return (
            <div style={{ textAlign: "center", padding: "20px" }}>
                <h1>선택된 반려동물이 없습니다.</h1>
                <button
                    onClick={() => navigate("/clinic/select-pet")}
                    style={{ ...buttonStyle, backgroundColor: "#007BFF" }}
                >
                    반려동물 선택하기
                </button>
            </div>
        );
    }

    return (
        <div style={{ textAlign: "center", padding: "20px" }}>
            <h1>클리닉 - 사진 업로드</h1>

            {selectedPet && (
                <div>
                    <h3>선택된 반려동물: {selectedPet.name}</h3>
                    <p>품종: {selectedPet.breed || "정보 없음"}</p>
                    <p>생일: {selectedPet.birthDate || "정보 없음"}</p>
                </div>
            )}

            {preview && (
                <div style={{ marginBottom: "20px" }}>
                    <img
                        src={preview}
                        alt="사진 미리보기"
                        style={{
                            width: "300px",
                            height: "300px",
                            objectFit: "cover",
                            borderRadius: "8px",
                        }}
                    />
                </div>
            )}

            {loading && <p>사진 업로드 및 분석 중입니다. 잠시만 기다려주세요...</p>}

            {!loading && !analysisResult && (
                <div>
                    <input type="file" accept="image/*" onChange={handleFileChange} />
                    <button
                        onClick={handleUpload}
                        disabled={!selectedFile}
                        style={{
                            ...buttonStyle,
                            backgroundColor: selectedFile ? "#007BFF" : "#ccc",
                            cursor: selectedFile ? "pointer" : "not-allowed",
                        }}
                    >
                        업로드
                    </button>
                </div>
            )}

            {!loading && analysisResult && (
                <button
                    onClick={() => navigate("/clinic", { state: { analysisResult, selectedPet } })}
                    style={{ ...buttonStyle, backgroundColor: "#28a745" }}
                >
                    결과 확인
                </button>
            )}
        </div>
    );
}

export default PhotoUpload;
