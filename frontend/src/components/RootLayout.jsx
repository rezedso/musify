/* eslint-disable react/prop-types */
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';

const RootLayout = ({ handleChange }) => {
  return (
    <main>
      <Navbar handleChange={handleChange} />
      <Outlet />
    </main>
  );
};

export default RootLayout;
