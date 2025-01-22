import React, { useState, useEffect } from "react";
import "./ContentManagement.css";

function ContentManagement() {
  const [contents, setContents] = useState({ banners: [], events: [] });

  useEffect(() => {
    const fetchContent = async () => {
      try {
        const bannerResponse = await fetch(`${process.env.REACT_APP_API_BASE_URL}/banners`);
        const eventResponse = await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/events`);

        if (!bannerResponse.ok || !eventResponse.ok) {
          throw new Error("데이터를 가져오는 데 실패했습니다.");
        }

        const banners = await bannerResponse.json();
        const events = await eventResponse.json();

        setContents({ banners, events });
      } catch (error) {
        console.error("콘텐츠 데이터를 가져오는 중 오류 발생:", error.message);
      }
    };

    fetchContent();
  }, []);

  const currentDate = new Date().toISOString().split("T")[0];

  const ongoingBanners = contents.banners.filter(
    (banner) => banner.startDate <= currentDate && banner.endDate >= currentDate
  );

  const ongoingEvents = contents.events.filter(
    (event) => event.startDate <= currentDate && event.endDate >= currentDate
  );

  return (
    <div className="content-management">
      <h2>콘텐츠 관리</h2>

      {/* 진행 중인 배너 */}
      <section>
        <h3>진행 중인 배너</h3>
        <div className="banner-slider">
          {ongoingBanners.length > 0 ? (
            ongoingBanners.map((banner) => (
              <div key={banner.bannerId} className="banner-card">
                <a href={banner.redirectUrl} target="_blank" rel="noopener noreferrer">
                  <img src={banner.imageUrl} alt="배너 이미지" />
                </a>
                <p>우선순위: {banner.priority}</p>
                <p>기간: {banner.startDate} ~ {banner.endDate}</p>
              </div>
            ))
          ) : (
            <p>진행 중인 배너가 없습니다.</p>
          )}
        </div>
      </section>

      {/* 진행 중인 이벤트 */}
      <section>
        <h3>진행 중인 이벤트</h3>
        <div className="event-list">
          {ongoingEvents.length > 0 ? (
            ongoingEvents.map((event) => (
              <div key={event.eventId} className="event-card">
                <img
                  src={event.imageUrl}
                  alt={event.eventTitle}
                  style={{ maxWidth: "100%", height: "auto" }}
                />
                <h4>{event.eventTitle}</h4>
                <p>{event.eventDesc}</p>
                <p>
                  기간: {event.startDate} ~ {event.endDate}
                </p>
              </div>
            ))
          ) : (
            <p>진행 중인 이벤트가 없습니다.</p>
          )}
        </div>
      </section>
    </div>
  );
}

export default ContentManagement;