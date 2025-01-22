import React, { useEffect, useState } from 'react';
import './Event.css';

function Event() {
  const [events, setEvents] = useState({ ongoing: [], ended: [] });

  useEffect(() => {
      const fetchEvents = async () => {
          try {
              const ongoingResponse = await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/events/ongoing`);
              const endedResponse = await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/events/ended`);

              if (!ongoingResponse.ok || !endedResponse.ok) {
                  throw new Error("이벤트 데이터를 가져오는 데 실패했습니다.");
              }

              const ongoingData = await ongoingResponse.json();
              const endedData = await endedResponse.json();

              setEvents({ ongoing: ongoingData, ended: endedData }); // 데이터를 상태에 저장
          } catch (error) {
              console.error("이벤트 데이터를 가져오는 중 오류 발생:", error.message);
          }
      };

      fetchEvents();
  }, []);

  return (
    <div className="event-container">
      <h2>진행 중인 이벤트</h2>
      <div className="event-grid">
        {events.ongoing?.map(event => (
          <div key={event.eventId} className="event-card">
            <img src={event.imageUrl} alt={event.eventTitle} className="event-image" />
            <h3>{event.eventTitle}</h3>
            <p>{event.eventDesc}</p>
          </div>
        ))}
      </div>

      <h2>종료된 이벤트</h2>
      <div className="event-grid">
        {events.ended?.map(event => (
          <div key={event.eventId} className="event-card">
            <img src={event.imageUrl} alt={event.eventTitle} className="event-image" />
            <h3>{event.eventTitle}</h3>
            <p>{event.eventDesc}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Event;
