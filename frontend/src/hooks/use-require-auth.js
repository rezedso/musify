import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { getCurrentUser } from '../services/auth.service';

const useRequireAuth = (allowedRoles) => {
  const user = getCurrentUser();
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate('/login', { state: { from: location }, replace: true });
    }

    if (user && !user?.roles.some((role) => allowedRoles.includes(role))) {
      navigate('/unauthorized', { state: { from: location }, replace: true });
    }
  }, [allowedRoles, navigate, user, location]);

  return user;
};

export default useRequireAuth;
