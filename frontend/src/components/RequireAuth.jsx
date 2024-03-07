/* eslint-disable react/prop-types */
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { getCurrentUser } from '../services/auth.service';
import Navbar from './Navbar';

const RequireAuth = ({ allowedRoles, handleChange }) => {
  const user = getCurrentUser();
  const location = useLocation();

  return user?.roles?.find((role) => allowedRoles?.includes(role)) ? (
    <main>
      <Navbar handleChange={handleChange} user={user} />
      <section>
        <Outlet />
      </section>
    </main>
  ) : user ? (
    <Navigate to='/unauthorized' state={{ from: location }} replace />
  ) : (
    <Navigate to='/login' state={{ from: location }} replace />
  );
};

export default RequireAuth;
