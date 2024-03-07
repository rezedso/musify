import { Container, Grid, Paper, Stack, Typography } from '@mui/material';
import {
  useGetMostRecentAlbums,
  useGetMostRecentArtists,
  useGetRecentAlbumRatings,
  useGetRecentReviews,
} from '../../lib/react-query/queries';
import Loader from '../Loader';
import RecentItem from '../RecentItem';
import ReviewCard from '../ReviewCard';
import RecentArtist from '../RecentArtist';

const HomePage = () => {
  const { data: recentReviews, isLoading: isLoadingRecentReviews } =
    useGetRecentReviews();
  const { data: recentRatings, isLoading: isLoadingRecentRatings } =
    useGetRecentAlbumRatings();
  const { data: recentArtists, isLoading: isLoadingRecentArtists } =
    useGetMostRecentArtists();
  const { data: recentAlbums, isLoading: isLoadingRecentAlbums } =
    useGetMostRecentAlbums();

  return (
    <Container maxWidth='xl' sx={{ mt: 2 }}>
      {isLoadingRecentReviews ||
      isLoadingRecentRatings ||
      isLoadingRecentArtists ||
      isLoadingRecentAlbums ? (
        <Loader />
      ) : (
        <Grid container spacing={2}>
          <Grid item xs={12} md={8}>
            {recentReviews?.length > 0 && (
              <>
                <Typography variant='h5' sx={{ mb: 2 }}>
                  Recent Reviews
                </Typography>
                {recentReviews?.map((review) => (
                  <ReviewCard key={review.id} review={review} />
                ))}
              </>
            )}
          </Grid>
          <Grid item xs={12} md={4}>
            <Stack direction='column' spacing={2} sx={{ mb: 2 }}>
              {recentRatings?.length > 0 && (
                <>
                  <Typography variant='h5'>Latest Ratings</Typography>
                  <Stack component={Paper} sx={{ pt: 2 }}>
                    {recentRatings?.map((rating) => (
                      <RecentItem key={rating.id} data={rating} />
                    ))}
                  </Stack>
                </>
              )}
              {recentArtists?.length > 0 && (
                <>
                  <Typography variant='h5'>Newly Added Artists</Typography>
                  <Stack component={Paper} sx={{ pt: 2 }}>
                    {recentArtists?.map((artist) => (
                      <RecentArtist key={artist.id} artist={artist} />
                    ))}
                  </Stack>
                </>
              )}
              {recentAlbums?.length > 0 && (
                <>
                  <Typography variant='h5'>Newly Added Albums</Typography>
                  <Stack component={Paper} sx={{ pt: 2 }}>
                    {recentAlbums?.map((album) => (
                      <RecentItem key={album.id} data={album} album />
                    ))}
                  </Stack>
                </>
              )}
            </Stack>
          </Grid>
        </Grid>
      )}
    </Container>
  );
};

export default HomePage;
