import React from 'react';
import { Navigate } from 'react-router-dom';
import { useContext } from 'react';
import { UserContext } from '../contexts/UserContext';

function ProtectedRoute({ element }) {
  const { user } = useContext(UserContext);

  if (!user) {
    return <Navigate to="/login" />;
  }

  if (user.userRole !== 'admin') {
    return <Navigate to="/" />;
  }

  return element;
}

export default ProtectedRoute;
