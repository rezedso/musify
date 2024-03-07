import { useState } from 'react';
import { getCurrentUser } from '../../services/auth.service';
import {
  useGetUser,
  useGetUserAlbumRatings,
  useGetUserFollowedArtists,
  useGetUserGenreOverview,
  useGetUserListsCount,
  useGetUserListsSummary,
  useGetUserReviews,
} from '../../lib/react-query/queries';
import { calculateRatingsData } from '../../lib/utils';
import InfiniteScrollReviewCards from '../InfiniteScrollReviewCards';
import {
  Button,
  Container,
  Grid,
  Link,
  Stack,
  Tab,
  Typography,
} from '@mui/material';
import { TabContext, TabList, TabPanel } from '@mui/lab';
import RatingsDataGrid from '../RatingsDataGrid';
import AlbumDataGrid from '../AlbumDataGrid';
import Loader from '../Loader';
import CreateOrUpdateListModal from '../CreateOrUpdateListModal';
import { Link as RouterLink, useNavigate, useParams } from 'react-router-dom';
import GenreOverview from '../GenreOverview';
import Lists from '../Lists';
import FollowedArtists from '../FollowedArtists';
import NoResults from '../NoResults';

const ProfilePage = () => {
  const [value, setValue] = useState('1');
  const currentUser = getCurrentUser();
  const navigate = useNavigate();
  const params = useParams();
  const { data: user } = useGetUser(params?.username);

  const { data: ratings, isLoading: isLoadingRatings } = useGetUserAlbumRatings(
    user?.id
  );

  const { data: listsCount, isLoading: isLoadingLists } = useGetUserListsCount(
    user?.username
  );

  const { data: genreOverview, isLoading: isLoadingGenreOverview } =
    useGetUserGenreOverview(user?.username);

  const { data: listsData } = useGetUserListsSummary(params?.username);

  const { data: userFollowedArtists, isLoading: isLoadingUserFollowedArtists } =
    useGetUserFollowedArtists(params?.username);

  const {
    data: reviews,
    isLoading: isLoadingReviews,
    fetchNextPage: fetchNextPageReviews,
    hasNextPage: hasNextPageReviews,
  } = useGetUserReviews();

  const ratingsData = calculateRatingsData(ratings);
  const totalReviews = reviews?.pages?.map((el) => el.totalElements);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const totalEntries = genreOverview?.reduce((a, c) => a + c.albumCount, 0);

  return (
    <Container maxWidth='xl' sx={{ pb: 2 }}>
      {isLoadingReviews ||
      isLoadingRatings ||
      isLoadingLists ||
      isLoadingGenreOverview ||
      isLoadingUserFollowedArtists ? (
        <Loader />
      ) : (
        <Grid container spacing={2}>
          <Grid item xs={12} md={9}>
            <Stack spacing={2}>
              <Stack
                direction='row'
                alignItems={{ xs: 'start' }}
                spacing={2}
                sx={{ mb: 2 }}
              >
                <Button
                  to='..'
                  sx={{ alignSelf: 'start', mb: 2 }}
                  component={RouterLink}
                  variant='contained'
                  onClick={(e) => {
                    e.preventDefault();
                    navigate(-1);
                  }}
                >
                  Go Back
                </Button>
                {currentUser?.id === user?.id && <CreateOrUpdateListModal />}
              </Stack>
              {genreOverview?.length > 0 && (
                <GenreOverview
                  genreOverview={genreOverview}
                  totalEntries={totalEntries}
                />
              )}
              <Stack sx={{ width: '100%' }}>
                <TabContext value={value}>
                  <Stack sx={{ borderBottom: 1, borderColor: 'divider' }}>
                    <TabList onChange={handleChange} aria-label='tabs'>
                      <Tab label='Recent' value='1' />
                      <Tab label='Ratings' value='2' />
                      <Tab label='Reviews' value='3' />
                    </TabList>
                  </Stack>
                  <TabPanel value='1'>
                    <AlbumDataGrid albums={ratings} />
                  </TabPanel>
                  <TabPanel value='2'>
                    <Stack
                      sx={(theme) => ({
                        width: 850,
                        [theme.breakpoints.down('lg')]: {
                          width: 650,
                        },
                        [theme.breakpoints.down('sm')]: {
                          width: 350,
                        },
                      })}
                    >
                      <Typography variant='h6'>
                        <b>Ratings: </b>
                        {ratings?.length}
                      </Typography>
                      {ratings?.length > 0 ? (
                        <RatingsDataGrid
                          ratingsData={ratingsData}
                          username={user?.username}
                        />
                      ) : (
                        <>
                          <NoResults text='There are no rated albums yet.' />
                          {currentUser?.username === params?.username && (
                            <Link
                              to='/artists'
                              component={RouterLink}
                              sx={{
                                color: 'primary.main',
                              }}
                            >
                              Try rating some albums.
                            </Link>
                          )}
                        </>
                      )}
                    </Stack>
                  </TabPanel>
                  <TabPanel value='3'>
                    <Typography variant='h6' sx={{ mb: 2 }}>
                      <b>Reviews: </b>
                      {totalReviews}
                    </Typography>
                    <InfiniteScrollReviewCards
                      data={reviews}
                      fetchNextPage={fetchNextPageReviews}
                      hasNextPage={hasNextPageReviews}
                    />
                  </TabPanel>
                </TabContext>
              </Stack>
            </Stack>
          </Grid>
          <Grid item xs={12} md={3}>
            <Stack direction='column' spacing={2}>
              <Lists
                listsCount={listsCount}
                listsData={listsData}
                username={params?.username}
              />
              {userFollowedArtists?.length > 0 && (
                <FollowedArtists userFollowedArtists={userFollowedArtists} />
              )}
            </Stack>
          </Grid>
        </Grid>
      )}
    </Container>
  );
};

export default ProfilePage;
