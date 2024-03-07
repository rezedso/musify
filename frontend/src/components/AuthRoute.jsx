/* eslint-disable react/prop-types */
import { Navigate } from 'react-router-dom';
import { getCurrentUser } from '../services/auth.service';

const AuthRoute = ({ element, roles }) => {
  const user = getCurrentUser();

  const hasRequiredRoles = roles.some((role) => user.roles.includes(role));

  if (!user || !hasRequiredRoles) {
    return <Navigate to='/' />;
  }

  return element;
};

export default AuthRoute;
