import React, { useState, useEffect } from "react";
import "./EventManagement.css";

function EventManagement() {
  // 상태 관리
  const [events, setEvents] = useState([]); // 서버에서 가져올 이벤트 데이터
  const [newEvent, setNewEvent] = useState({
    eventTitle: "",
    eventDesc: "",
    startDate: "",
    endDate: "",
    imageFile: null,
    status: "진행중",
  });
  const [previewImage, setPreviewImage] = useState(null);
  const [editingEventId, setEditingEventId] = useState(null); // 수정 중인 이벤트 ID

  // useEffect: 서버에서 이벤트 데이터 가져오기
  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/events`);
        if (!response.ok) {
          throw new Error("이벤트 데이터를 가져오는 데 실패했습니다.");
        }
        const data = await response.json();
        setEvents(data); // 서버에서 가져온 데이터로 상태 업데이트
      } catch (error) {
        console.error("이벤트 데이터를 가져오는 중 오류 발생:", error.message);
      }
    };

    fetchEvents();
  }, []);

  // 입력 필드 값 변경 시 상태 업데이트
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewEvent((prevEvent) => ({ ...prevEvent, [name]: value }));
  };

  // 파일 업로드 시 상태 업데이트 및 미리보기 설정
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setNewEvent((prevEvent) => ({ ...prevEvent, imageFile: file }));
      const reader = new FileReader();
      reader.onload = () => setPreviewImage(reader.result);
      reader.readAsDataURL(file);
    }
  };

  // 새로운 이벤트 추가 또는 수정 핸들러
  const handleSubmitEvent = async (e) => {
    e.preventDefault();

    try {
      const formData = new FormData();
      formData.append("eventTitle", newEvent.eventTitle);
      formData.append("eventDesc", newEvent.eventDesc);
      formData.append("startDate", newEvent.startDate);
      formData.append("endDate", newEvent.endDate);
      if (newEvent.imageFile) {
        formData.append("imageFile", newEvent.imageFile); // 이미지 파일 추가
      }

      let response;
      if (editingEventId) {
        // 수정 API 호출
        response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/events/${editingEventId}`, {
          method: "PUT",
          body: formData,
        });
      } else {
        // 추가 API 호출
        response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/events`, {
          method: "POST",
          body: formData,
        });
      }

      if (!response.ok) {
        throw new Error("이벤트 저장에 실패했습니다.");
      }

      const updatedEvent = await response.json();
      setEvents((prevEvents) =>
        editingEventId
          ? prevEvents.map((event) => (event.eventId === editingEventId ? updatedEvent : event))
          : [...prevEvents, updatedEvent]
      );

      // 상태 초기화
      setEditingEventId(null);
      setNewEvent({
        eventTitle: "",
        eventDesc: "",
        startDate: "",
        endDate: "",
        imageFile: null,
        status: "진행중",
      });
      setPreviewImage(null);
    } catch (error) {
      console.error("이벤트 저장 중 오류 발생:", error.message);
    }
  };

  // 이벤트 삭제 핸들러
  const handleDeleteEvent = async (eventId) => {
    if (!window.confirm("이벤트를 삭제하시겠습니까?")) return;

    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/admin/events/${eventId}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        throw new Error("이벤트 삭제에 실패했습니다.");
      }

      setEvents((prevEvents) => prevEvents.filter((event) => event.eventId !== eventId));
    } catch (error) {
      console.error("이벤트 삭제 중 오류 발생:", error.message);
    }
  };

  // 이벤트 수정 핸들러
  const handleEditEvent = (eventId) => {
    const eventToEdit = events.find((event) => event.eventId === eventId);
    if (eventToEdit) {
      setEditingEventId(eventId);
      setNewEvent({
        eventTitle: eventToEdit.eventTitle,
        eventDesc: eventToEdit.eventDesc,
        startDate: eventToEdit.startDate,
        endDate: eventToEdit.endDate,
        imageFile: null,
        status: eventToEdit.status,
      });
      setPreviewImage(eventToEdit.imageUrl);
    }
  };

  return (
    <div className="event-management">
      <h3>이벤트 관리</h3>

      {/* 이벤트 추가/수정 폼 */}
      <form className="event-form" onSubmit={handleSubmitEvent}>
        <label>
          이벤트 제목:
          <input
            type="text"
            name="eventTitle"
            value={newEvent.eventTitle}
            onChange={handleInputChange}
            required
          />
        </label>

        <label>
          이벤트 설명:
          <textarea
            name="eventDesc"
            value={newEvent.eventDesc}
            onChange={handleInputChange}
            required
          ></textarea>
        </label>

        <label>
          시작 날짜:
          <input
            type="date"
            name="startDate"
            value={newEvent.startDate}
            onChange={handleInputChange}
            required
          />
        </label>

        <label>
          종료 날짜:
          <input
            type="date"
            name="endDate"
            value={newEvent.endDate}
            onChange={handleInputChange}
            required
          />
        </label>

        <label>
          이미지 업로드:
          <input type="file" onChange={handleFileChange} />
        </label>

        <button type="submit">{editingEventId ? "이벤트 수정" : "이벤트 추가"}</button>
      </form>

      {/* 이벤트 미리보기 */}
      {previewImage && (
        <div className="event-preview">
          <h4>미리보기</h4>
          <div className="event-card">
            <img src={previewImage} alt="미리보기 이미지" />
            <h4>{newEvent.eventTitle}</h4>
            <p>{newEvent.eventDesc}</p>
            <p>
              {newEvent.startDate} ~ {newEvent.endDate}
            </p>
          </div>
        </div>
      )}

      {/* 이벤트 목록 */}
      <div className="event-list">
        {events.map((event) => (
          <div key={event.eventId} className="event-card">
            <img src={event.imageUrl} alt="이벤트 이미지" />
            <h4>{event.eventTitle}</h4>
            <p>{event.eventDesc}</p>
            <p>
              {event.startDate} ~ {event.endDate}
            </p>
            <button onClick={() => handleEditEvent(event.eventId)}>수정</button>
            <button onClick={() => handleDeleteEvent(event.eventId)}>삭제</button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default EventManagement;