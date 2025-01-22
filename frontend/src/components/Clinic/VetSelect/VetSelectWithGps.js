import React, { useState, useEffect } from 'react';
import './VetSelectWithGps.css';
import Modal from './Modal';  // 모달 컴포넌트 임포트

const VetSelectWithGps = ({ userId }) => {  // userId를 props로 받음
    const [location, setLocation] = useState({ latitude: null, longitude: null });
    const [vets, setVets] = useState([]);
    const [selectedVet, setSelectedVet] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [sortCriteria, setSortCriteria] = useState('distance'); // 기본 정렬 기준은 거리
    const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태 추가

    useEffect(() => {
        if ("geolocation" in navigator) {
            navigator.geolocation.getCurrentPosition((position) => {
                setLocation({
                    latitude: position.coords.latitude,
                    longitude: position.coords.longitude,
                });
            });
        } else {
            console.error("Geolocation is not available");
        }
    }, []);

    useEffect(() => {
        const fetchVets = async () => {
            if (location.latitude && location.longitude) { // 위치 정보가 있는 경우에만 요청
                try {
                    const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/vets?userLat=${location.latitude}&userLon=${location.longitude}&sortCriteria=${sortCriteria}`);
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    const data = await response.json();
                    // 거리 계산하여 vets 데이터에 추가
                    const updatedVets = data.map(vet => ({
                        ...vet,
                        distance: calculateDistance(location.latitude, location.longitude, vet.latitude, vet.longitude)
                    }));
                    setVets(updatedVets);
                } catch (error) {
                    console.error("Error fetching vets:", error);
                }
            }
        };

        fetchVets();
    }, [location, sortCriteria]);

    const handleSelect = async (vet) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/vets/${vet.vetId}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const vetData = await response.json();
            setSelectedVet(vetData);
            setIsModalOpen(true); // 수의사 선택 시 모달 열기
        } catch (error) {
            console.error("Error fetching selected vet data:", error);
        }
    };

const handleConsultationRequest = async (vetId) => {
    try {
        console.log("상담 요청 vetId: ", vetId);
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/consultations/${vetId}/request`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include', // 세션 정보를 포함
            body: JSON.stringify({ userId }), //userId를 요청 바디에 포함
        });
        console.log("상담 응답 요청: ", response);
        if (!response.ok) {
            throw new Error("네트워크 응답 X");
        }
        const result = await response.text(); // 응답을 텍스트로 처리
        console.log("상담 요청 결과: ", result);
        alert('상담 요청이 성공적으로 접수되었습니다!');
    } catch (error) {
        console.error("응답 에러", error);
        alert("상담 요청 중 오류가 발생했습니다.");
    }
};

    const calculateDistance = (lat1, lon1, lat2, lon2) => {
        const toRad = (value) => (value * Math.PI) / 180;
        const R = 6371; // 지구 반경 (킬로미터)
        const dLat = toRad(lat2 - lat1);
        const dLon = toRad(lon1 - lon2);
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 결과 거리 (킬로미터)
    };

    const filteredVets = vets
        .filter(vet => vet.name.toLowerCase().includes(searchTerm.toLowerCase())) // 이름 검색 필터
        .sort((a, b) => {
            if (sortCriteria === 'distance') {
                return a.distance - b.distance;
            } else if (sortCriteria === 'rating') {
                return b.vetRating - a.vetRating;
            } else if (sortCriteria === 'reviews') {
                return b.reviewCount - a.reviewCount;
            } else if (sortCriteria === 'consultations') {
                return b.consultationCount - a.consultationCount;
            }
            return 0;
        });

    return (
        <div className="container">
            <h1>수의사 선택 및 GPS 위치</h1>
            <div className="gps-coordinates">
                <h2>GPS 위치</h2>
                {location.latitude && location.longitude ? (
                    <p>위도: {location.latitude}, 경도: {location.longitude}</p>
                ) : (
                    <p>위치 정보를 가져오는 중...</p>
                )}
            </div>

            <div className="search-bar">
                <input
                    type="text"
                    placeholder="수의사 이름 검색"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />

                <select value={sortCriteria} onChange={(e) => setSortCriteria(e.target.value)}>
                    <option value="distance">거리순</option>
                    <option value="rating">평점순</option>
                    <option value="reviews">리뷰순</option>
                    <option value="consultations">상담건수순</option>
                </select>
            </div>

            <div className="vet-list">
                {filteredVets.map(vet => {
                    const imageURL = `${process.env.REACT_APP_API_BASE_URL}/images/${vet.vetImage}`;
                    return (
                        <div className="vet-item" key={vet.vetId}>
                            <img
                                src={imageURL}
                                alt={`${vet.name} 이미지`}
                                onClick={() => handleSelect(vet)}
                                style={{ cursor: 'pointer' }}
                            />
                            <h3>{vet.name}</h3>
                            <p>{vet.address}</p>
                            <button onClick={() => handleConsultationRequest(vet.vetId)}>
                                상담 요청
                            </button>
                        </div>
                    );
                })}
            </div>

            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
                {selectedVet && (
                    <div className="vet-details">
                        <h2>선택한 수의사</h2>
                        <img src={`${process.env.REACT_APP_API_BASE_URL}/images/${selectedVet.vetImage}`} alt="수의사 이미지" className="vet-details-image" />
                        <p><strong>이름:</strong> {selectedVet.name}</p>
                        <p><strong>전화번호:</strong> {selectedVet.phoneNumber}</p>
                        <p><strong>이메일:</strong> {selectedVet.email}</p>
                        <p><strong>주소:</strong> {selectedVet.address}</p>
                        <p><strong>리뷰:</strong> {selectedVet.reviewCount}</p>
                        <p><strong>상담 건수:</strong> {selectedVet.consultationCount}</p>
                        <p><strong>평점:</strong> {selectedVet.vetRating}</p>
                    </div>
                )}
            </Modal>
        </div>
    );
};

export default VetSelectWithGps;