import { Button, Container, Stack, Typography } from '@mui/material';
import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import { useGetArtistsByGenre } from '../../lib/react-query/queries';
import Loader from '../Loader';
import { useEffect } from 'react';
import { formatSlug } from '../../lib/utils';
import InfiniteScrollArtists from '../InfiniteScrollArtists';

const ArtistGenrePage = () => {
  const navigate = useNavigate();
  const params = useParams();
  const {
    data: artists,
    isLoading,
    fetchNextPage,
    hasNextPage,
    refetch,
  } = useGetArtistsByGenre(params?.slug);

  useEffect(() => {
    refetch();
  }, [params?.slug, refetch]);

  return (
    <Container maxWidth='xl'>
      <Stack spacing={2}>
        <Button
          to='..'
          component={RouterLink}
          variant='contained'
          onClick={(e) => {
            e.preventDefault();
            navigate(-1);
          }}
          sx={{ alignSelf: 'start' }}
        >
          Go Back
        </Button>
        <Typography variant='h4'>{formatSlug(params?.slug)} artists</Typography>
        {isLoading ? (
          <Loader />
        ) : (
          <InfiniteScrollArtists
            data={artists}
            fetchNextPage={fetchNextPage}
            hasNextPage={hasNextPage}
            genrePage={true}
          />
        )}
      </Stack>
    </Container>
  );
};

export default ArtistGenrePage;
