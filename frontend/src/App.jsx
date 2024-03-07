import { CssBaseline, ThemeProvider } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Route, Routes } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import { useAppTheme } from './hooks/use-app-theme';
import RootLayout from './components/RootLayout';
import RegisterUserPage from './components/pages/RegisterUser';
import LoginPage from './components/pages/Login';
import HomePage from './components/pages/Home';
import Unauthorized from './components/pages/Unauthorized';
import ArtistsPage from './components/pages/Artists';
import ArtistPage from './components/pages/Artist';
import ListPage from './components/pages/List';
import AlbumPage from './components/pages/Album';
import AlbumGenrePage from './components/pages/AlbumGenre';
import ArtistGenrePage from './components/pages/ArtistGenre';
import CollectionPage from './components/pages/Collection';
import ProfilePage from './components/pages/Profile';
import RequireAuth from './components/RequireAuth';
import UsersPage from './components/pages/Users';
import AlbumsPage from './components/pages/Albums';

function App() {
  const queryClient = new QueryClient();
  const { appTheme, handleChange } = useAppTheme();

  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={appTheme}>
        <CssBaseline />
        <ToastContainer
          position='top-center'
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          limit={2}
        />
        <Routes>
          <Route element={<RootLayout handleChange={handleChange} />}>
            <Route path='/register' element={<RegisterUserPage />} />
            <Route path='/login' element={<LoginPage />} />
            <Route path='/unauthorized' element={<Unauthorized />} />
          </Route>

          <Route
            element={
              <RequireAuth
                allowedRoles={['ROLE_USER', 'ROLE_ADMIN']}
                handleChange={handleChange}
              />
            }
          >
            <Route path='/' element={<HomePage />} />
            <Route path='/artists' element={<ArtistsPage />} />
            <Route path='/artists/:artistSlug' element={<ArtistPage />} />
            <Route
              path='/artists/:artistSlug/:albumSlug'
              element={<AlbumPage />}
            />
            <Route path='/albums' element={<AlbumsPage />} />
            <Route path='/genres/albums/:slug' element={<AlbumGenrePage />} />
            <Route path='/genres/artists/:slug' element={<ArtistGenrePage />} />
            <Route path='/lists/:username/:listName' element={<ListPage />} />
            <Route path='/users/:username' element={<ProfilePage />} />
            <Route
              path='/collections/:username/:rating'
              element={<CollectionPage />}
            />
          </Route>

          <Route
            element={
              <RequireAuth
                allowedRoles={['ROLE_ADMIN']}
                handleChange={handleChange}
              />
            }
          >
            <Route path='/users' element={<UsersPage />} />
          </Route>
        </Routes>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;
