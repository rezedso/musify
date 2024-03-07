import { Button, Container, Stack, Typography } from '@mui/material';
import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import {
  useGetAlbumsByGenre,
  useGetUserListsSummary,
} from '../../lib/react-query/queries';
import Loader from '../Loader';
import InfiniteScrollAlbums from '../InfiniteScrollAlbums';
import { useEffect } from 'react';
import { formatSlug } from '../../lib/utils';
import { getCurrentUser } from '../../services/auth.service';

const AlbumGenrePage = () => {
  const user = getCurrentUser();
  const navigate = useNavigate();
  const params = useParams();
  const {
    data: albums,
    isLoading: isLoadingAlbums,
    fetchNextPage: fetchNextPageAlbums,
    hasNextPage: hasNextPageAlbums,
    refetch,
  } = useGetAlbumsByGenre(params?.slug);

  const { data: lists } = useGetUserListsSummary(user?.username);

  useEffect(() => {
    refetch();
    window.scrollTo(0, 0);
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
        <Typography variant='h4'>
          Top {formatSlug(params?.slug)} albums of all time
        </Typography>
        {isLoadingAlbums ? (
          <Loader />
        ) : (
          <InfiniteScrollAlbums
            data={albums}
            fetchNextPage={fetchNextPageAlbums}
            hasNextPage={hasNextPageAlbums}
            genrePage={true}
            lists={lists}
          />
        )}
      </Stack>
    </Container>
  );
};

export default AlbumGenrePage;
