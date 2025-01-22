import React, { useEffect, useState, useContext } from 'react';
import { Map, MapMarker, CustomOverlayMap } from 'react-kakao-maps-sdk';
import { UserContext } from '../../contexts/UserContext';
import { useNavigate } from 'react-router-dom';

function Hospital() {
  const { user } = useContext(UserContext);
  const navigate = useNavigate();

  const [currentPosition, setCurrentPosition] = useState({
    lat: 37.5665,
    lng: 126.978,
  });
  const [hospitals, setHospitals] = useState([]);
  const [isKakaoLoaded, setIsKakaoLoaded] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(null);

  // 로그인 확인
  useEffect(() => {
    if (!user) {
      alert('로그인 후 이용 가능합니다.');
      navigate('/login');
    }
  }, [user, navigate]);

  // Kakao Maps SDK 로드 확인
  useEffect(() => {
    if (window.kakao && window.kakao.maps) {
      setIsKakaoLoaded(true);
    }
  }, []);

  // 사용자 위치 가져오기
  useEffect(() => {
    if (!isKakaoLoaded) return;

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords;
          setCurrentPosition({ lat: latitude, lng: longitude });
        },
        () => {
          console.error('위치 정보를 가져올 수 없습니다.');
        },
        { enableHighAccuracy: true }
      );
    }
  }, [isKakaoLoaded]);

  // 주변 동물 병원 검색
  useEffect(() => {
    if (!isKakaoLoaded) return;

    const { kakao } = window;
    const places = new kakao.maps.services.Places();

    const callback = (result, status) => {
      if (status === kakao.maps.services.Status.OK) {
        const hospitalsWithDistance = result.map((hospital) => ({
          ...hospital,
          distance: calculateDistance(
            currentPosition.lat,
            currentPosition.lng,
            parseFloat(hospital.y),
            parseFloat(hospital.x)
          ),
        }));
        hospitalsWithDistance.sort((a, b) => a.distance - b.distance);
        setHospitals(hospitalsWithDistance);
      }
    };

    places.keywordSearch('동물병원', callback, {
      location: new kakao.maps.LatLng(currentPosition.lat, currentPosition.lng),
      radius: 2000,
    });
  }, [isKakaoLoaded, currentPosition]);

  // 거리 계산 함수
  const calculateDistance = (lat1, lng1, lat2, lng2) => {
    const R = 6371; // 지구 반경 (km)
    const dLat = ((lat2 - lat1) * Math.PI) / 180;
    const dLng = ((lng2 - lng1) * Math.PI) / 180;
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos((lat1 * Math.PI) / 180) *
        Math.cos((lat2 * Math.PI) / 180) *
        Math.sin(dLng / 2) *
        Math.sin(dLng / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return (R * c).toFixed(2);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'row', height: '100vh' }}>
      {/* 리스트 영역 */}
      <div style={{ flex: 1, padding: '20px', overflowY: 'scroll' }}>
        <h3>근처 동물 병원</h3>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {hospitals.map((hospital, index) => (
            <li
              key={index}
              style={{
                borderBottom: '1px solid #ccc',
                padding: '10px 0',
                cursor: 'pointer',
              }}
              onMouseEnter={() => setHighlightedIndex(index)}
              onMouseLeave={() => setHighlightedIndex(null)}
              onClick={() =>
                window.open(
                  `https://search.naver.com/search.naver?query=${encodeURIComponent(
                    hospital.place_name
                  )}`,
                  '_blank'
                )
              }
            >
              <strong>{hospital.place_name}</strong>
              <br />
              <span style={{ fontSize: '13px', color: 'gray' }}>
                {hospital.road_address_name || hospital.address_name}
              </span>
              <br />
              <span style={{ fontSize: '15px', color: 'blue' }}>
                {`현 위치에서 약 ${hospital.distance} km`}
              </span>
            </li>
          ))}
        </ul>
      </div>

      {/* 지도 영역 */}
      <div style={{ flex: 2, height: '100%' }}>
        <Map
          center={currentPosition}
          level={3}
          style={{ width: '100%', height: '100%' }}
        >
          {/* 현재 위치 표시 */}
          <CustomOverlayMap position={currentPosition}>
            <div
              style={{
                position: 'relative',
                textAlign: 'center',
              }}
            >
              <img
                src="https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png"
                alt="현위치"
                style={{
                  width: '30px',
                  height: '45px',
                  filter: 'hue-rotate(240deg)',
                }}
              />
              <div
                style={{
                  marginTop: '5px',
                  backgroundColor: 'white',
                  border: '1px solid #ccc',
                  borderRadius: '5px',
                  padding: '5px',
                  fontSize: '12px',
                  color: 'black',
                  fontWeight: 'bold',
                }}
              >
                현 위치
              </div>
            </div>
          </CustomOverlayMap>

          {/* 동물 병원 표시 */}
          {hospitals.map((hospital, index) => (
            <div key={index}>
              <MapMarker
                position={{
                  lat: hospital.y,
                  lng: hospital.x,
                }}
                image={{
                  src:
                    highlightedIndex === index
                      ? 'https://cdn-icons-png.flaticon.com/512/684/684908.png'
                      : 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png',
                  size: {
                    width: highlightedIndex === index ? 50 : 40,
                    height: highlightedIndex === index ? 70 : 60,
                  },
                }}
                onMouseOver={() => setHighlightedIndex(index)}
                onMouseOut={() => setHighlightedIndex(null)}
                onClick={() =>
                  window.open(
                    `https://search.naver.com/search.naver?query=${encodeURIComponent(
                      hospital.place_name
                    )}`,
                    '_blank'
                  )
                }
              />
              {highlightedIndex === index && (
                <CustomOverlayMap position={{ lat: hospital.y, lng: hospital.x }}>
                  <div
                    style={{
                      backgroundColor: 'white',
                      border: '1px solid #ccc',
                      padding: '5px',
                      borderRadius: '5px',
                      fontSize: '13px',
                      textAlign: 'center',
                      whiteSpace: 'nowrap',
                    }}
                  >
                    {hospital.place_name}
                  </div>
                </CustomOverlayMap>
              )}
            </div>
          ))}
        </Map>
      </div>
    </div>
  );
}

export default Hospital;