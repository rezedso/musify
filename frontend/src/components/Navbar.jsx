/* eslint-disable react/prop-types */
import {
  AppBar,
  Avatar,
  Button,
  Container,
  Divider,
  IconButton,
  Menu,
  MenuItem,
  Stack,
  Toolbar,
  Tooltip,
  Typography,
  Link,
} from '@mui/material';
import { useState } from 'react';
import { AccountCircle } from '@mui/icons-material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import Brightness3Icon from '@mui/icons-material/Brightness3';
import WbSunnyOutlinedIcon from '@mui/icons-material/WbSunnyOutlined';
import { removeUser } from '../services/token.service';
import { useTheme } from '@emotion/react';
import UpdatePasswordModal from './UpdatePasswordModal';
import UpdateUserModal from './UpdateUserModal';
import { getCurrentUser } from '../services/auth.service';
import MenuIcon from '@mui/icons-material/Menu';

const Navbar = ({ handleChange }) => {
  const user = getCurrentUser();
  const navigate = useNavigate();
  const theme = useTheme();
  const [anchorElNav, setAnchorElNav] = useState(null);
  const [anchorElUser, setAnchorElUser] = useState(null);

  function handleClick(event) {
    if (anchorElUser !== event.currentTarget) {
      setAnchorElUser(event.currentTarget);
    }
  }

  const handleOpenNavMenu = (event) => {
    setAnchorElNav(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const handleOpenUserMenu = (event) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  const handleLogout = () => {
    removeUser();
    navigate('/login');
  };

  const pages = [
    {
      id: 1,
      name: 'Artists',
      href: '/artists',
    },
    {
      id: 2,
      name: 'Albums',
      href: '/albums',
    },
    {
      id: 3,
      name: 'Users',
      href: '/users',
    },
  ];
  return (
    <AppBar key={theme} position='sticky' sx={{ mb: 2 }}>
      <Container maxWidth='xl'>
        <Toolbar sx={{ display: 'flex', justifyContent: 'end' }} disableGutters>
          <Typography
            to='/'
            sx={(theme) => ({
              mr: 2,
              fontSize: '1.8rem',
              [theme.breakpoints.down('sm')]: {
                fontSize: '1.2rem',
              },
            })}
            component={RouterLink}
          >
            <b>Musify</b>
          </Typography>
          {user && (
            <>
              {' '}
              <Stack
                sx={{
                  flexGrow: 1,
                  display: { xs: 'flex', sm: 'none' },
                  alignItems: 'start',
                }}
              >
                <IconButton
                  size='large'
                  aria-label='account of current user'
                  aria-controls='menu-appbar'
                  aria-haspopup='true'
                  onClick={handleOpenNavMenu}
                  sx={{ color: '#fff' }}
                >
                  <MenuIcon />
                </IconButton>
                <Menu
                  id='menu-appbar'
                  anchorEl={anchorElNav}
                  anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'left',
                  }}
                  keepMounted
                  transformOrigin={{
                    vertical: 'top',
                    horizontal: 'left',
                  }}
                  open={Boolean(anchorElNav)}
                  onClose={handleCloseNavMenu}
                  sx={{
                    display: { xs: 'block', sm: 'none' },
                  }}
                >
                  {pages
                    .filter(
                      (page) =>
                        user?.roles.includes('ROLE_ADMIN') ||
                        page.name !== 'Users'
                    )
                    .map((page) => (
                      <MenuItem
                        key={page.id}
                        component={RouterLink}
                        to={page.href}
                        onClick={handleCloseNavMenu}
                      >
                        <Typography textAlign='center'>{page.name}</Typography>
                      </MenuItem>
                    ))}
                </Menu>
              </Stack>
              <Stack
                direction='row'
                spacing={2}
                sx={{ display: { xs: 'none', sm: 'block' } }}
              >
                <Link
                  component={RouterLink}
                  to='/artists'
                  sx={{ color: '#fff' }}
                >
                  Artists
                </Link>
                <Link
                  component={RouterLink}
                  to='/albums'
                  sx={{ color: '#fff' }}
                >
                  Albums
                </Link>
                {user?.roles?.some((role) => role?.includes('ROLE_ADMIN')) && (
                  <Link
                    component={RouterLink}
                    to='/users'
                    sx={{ color: '#fff' }}
                  >
                    Users
                  </Link>
                )}
              </Stack>
            </>
          )}
          <Stack sx={{ ml: 'auto' }}>
            {theme.palette.mode === 'dark' ? (
              <IconButton onClick={handleChange}>
                <WbSunnyOutlinedIcon sx={{ width: 30, height: 30 }} />
              </IconButton>
            ) : (
              <IconButton onClick={handleChange}>
                <Brightness3Icon sx={{ width: 30, height: 30 }} />
              </IconButton>
            )}
          </Stack>
          {user ? (
            <Stack direction='row' spacing={1} sx={{ ml: 1 }}>
              <Tooltip title='Open settings'>
                <IconButton
                  onClick={handleOpenUserMenu}
                  onMouseOver={handleClick}
                  sx={{ p: 0 }}
                >
                  {user?.imageUrl !== null ? (
                    <Avatar
                      alt='Avatar'
                      src={user?.imageUrl}
                      sx={{ width: 35, height: 35 }}
                    />
                  ) : (
                    <AccountCircle sx={{ fontSize: '2.188rem' }} />
                  )}
                </IconButton>
              </Tooltip>
              <Menu
                sx={{ mt: '45px' }}
                id='menu-appbar'
                anchorEl={anchorElUser}
                anchorOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                keepMounted
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                open={Boolean(anchorElUser)}
                onClose={handleCloseUserMenu}
                MenuListProps={{ onMouseLeave: handleCloseUserMenu }}
                disableScrollLock
              >
                <Typography variant='subtitle1' sx={{ px: 2, mt: 1 }}>
                  <b> {user?.username}</b>
                </Typography>
                <Typography variant='subtitle2' sx={{ px: 2, mb: 1 }}>
                  {user?.email}
                </Typography>
                <Divider />
                <MenuItem
                  to='/'
                  sx={{ color: 'text.primary' }}
                  onClick={handleCloseUserMenu}
                  component={RouterLink}
                >
                  Home
                </MenuItem>
                <MenuItem
                  to={`/users/${user?.username}`}
                  sx={{ color: 'text.primary' }}
                  onClick={handleCloseUserMenu}
                  component={RouterLink}
                >
                  Profile
                </MenuItem>
                <UpdateUserModal onClick={handleCloseUserMenu} />
                <UpdatePasswordModal />
                <MenuItem
                  onClick={() => {
                    handleCloseUserMenu();
                    handleLogout();
                  }}
                >
                  Log out
                </MenuItem>
              </Menu>
            </Stack>
          ) : (
            <Stack direction='row' spacing={2} sx={{ ml: 1 }}>
              <Button
                component={RouterLink}
                to='/login'
                variant='contained'
                color={theme.palette.mode === 'dark' ? 'primary' : 'secondary'}
                sx={(theme) => ({
                  [theme.breakpoints.down('sm')]: {
                    p: 0,
                  },
                })}
              >
                Log In
              </Button>
              <Button
                component={RouterLink}
                to='/register'
                variant='contained'
                color={theme.palette.mode === 'dark' ? 'primary' : 'secondary'}
              >
                Register
              </Button>
            </Stack>
          )}
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Navbar;
