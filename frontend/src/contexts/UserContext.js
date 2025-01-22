import React, { createContext, useState, useEffect, useContext } from 'react';

export const UserContext = createContext();

export function UserProvider({ children }) {
    const [user, setUser] = useState(null);


  useEffect(() => {
    // 세션을 통해 유저 정보를 가져옵니다.
    console.log("API URL:", `${process.env.REACT_APP_API_BASE_URL}/user/me`);

    fetch(`${process.env.REACT_APP_API_BASE_URL}/user/me`, {
      credentials: 'include', // 세션 쿠키를 자동으로 포함
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('유저 정보를 가져오지 못했습니다.');
        }
        return response.json();
      })
      .then((data) => {
        console.log("유저 정보:", data);
        setUser(data.user); // 유저 정보 저장
        localStorage.setItem("user", JSON.stringify(data.user));
        localStorage.setItem("pets", JSON.stringify(data.pets || []));
      })
      .catch((error) => console.error('유저 정보 가져오기 실패:', error));
  }, []);

    useEffect(() => {
        // 세션을 통해 유저 정보를 가져옵니다.
        console.log("API URL:", `${process.env.REACT_APP_API_BASE_URL}/user/me`);

        fetch(`${process.env.REACT_APP_API_BASE_URL}/user/me`, {
            credentials: 'include', // 세션 쿠키를 자동으로 포함
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('유저 정보를 가져오지 못했습니다.');
                }
                return response.json();
            })
            .then((data) => {
                console.log("유저 정보:", data);
                setUser(data.user); // 유저 정보 저장
            })
            .catch((error) => console.error('유저 정보 가져오기 실패:', error));
    }, []);

    return (
        <UserContext.Provider value={{ user, setUser }}>
            {children}
        </UserContext.Provider>
    );
}
export const useUser = () => useContext(UserContext);