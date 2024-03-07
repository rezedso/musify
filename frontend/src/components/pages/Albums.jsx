import { Button, Container, Stack } from '@mui/material';
import { useGetAlbums } from '../../lib/react-query/queries';
import Loader from '../Loader';
import InfiniteScrollAlbums from '../InfiniteScrollAlbums';
import { useNavigate, Link as RouterLink } from 'react-router-dom';

const AlbumsPage = () => {
  const navigate = useNavigate();
  const {
    data: albums,
    isLoading: isLoadingAlbums,
    fetchNextPage,
    hasNextPage,
  } = useGetAlbums();

  return (
    <Container maxWidth='xl' sx={{ pb: 2 }}>
      {isLoadingAlbums ? (
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
          </Stack>
          <Stack spacing={2}>
            <InfiniteScrollAlbums
              data={albums}
              fetchNextPage={fetchNextPage}
              hasNextPage={hasNextPage}
            />
          </Stack>
        </>
      )}
    </Container>
  );
};

export default AlbumsPage;
