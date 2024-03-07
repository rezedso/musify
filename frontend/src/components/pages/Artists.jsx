import { Button, Container, Stack } from '@mui/material';
import { useGetArtists } from '../../lib/react-query/queries';
import InfiniteScrollArtists from '../InfiniteScrollArtists';
import Loader from '../Loader';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { getCurrentUser } from '../../services/auth.service';
import CreateOrUpdateArtistModal from '../CreateOrUpdateArtistModal';

const ArtistsPage = () => {
  const user = getCurrentUser();
  const navigate = useNavigate();
  const {
    data: artists,
    isLoading: isLoadingArtists,
    fetchNextPage,
    hasNextPage,
  } = useGetArtists();

  return (
    <Container maxWidth='xl' sx={{ pb: 2 }}>
      {isLoadingArtists ? (
        <Loader />
      ) : (
        <>
          <Stack direction='row' spacing={2} sx={{ mb: 2 }}>
            <Button
              to='..'
              sx={{ mb: 2 }}
              component={RouterLink}
              variant='contained'
              onClick={(e) => {
                e.preventDefault();
                navigate(-1);
              }}
            >
              Go Back
            </Button>
            {user?.roles?.some((role) => role.includes('ROLE_ADMIN')) && (
              <CreateOrUpdateArtistModal />
            )}
          </Stack>
          <Stack spacing={2}>
            <InfiniteScrollArtists
              data={artists}
              fetchNextPage={fetchNextPage}
              hasNextPage={hasNextPage}
            />
          </Stack>
        </>
      )}
    </Container>
  );
};

export default ArtistsPage;
