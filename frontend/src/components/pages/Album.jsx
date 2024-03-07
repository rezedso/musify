import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import {
  useDeleteAlbum,
  useExistsReview,
  useGetAlbum,
  useGetAlbumReviews,
  useGetUserListsSummary,
} from '../../lib/react-query/queries';
import { Button, Container, Grid, Stack } from '@mui/material';
import Loader from '../Loader';

import ReviewForm from '../ReviewForm';
import InfiniteScrollReviews from '../InfiniteScrollReviews';
import CreateOrUpdateAlbumModal from '../CreateOrUpdateAlbumModal';
import { useState } from 'react';
import AlbumData from '../AlbumData';
import { getCurrentUser } from '../../services/auth.service';
import ConfirmationModal from '../ConfirmationModal';

const AlbumPage = () => {
  const user = getCurrentUser();
  const params = useParams();
  const navigate = useNavigate();
  const { data: album, isLoading: isLoadingAlbum } = useGetAlbum(
    params?.artistSlug,
    params?.albumSlug
  );

  const [showReviewForm, setShowReviewForm] = useState(false);

  const {
    data: albumReviews,
    fetchNextPage,
    hasNextPage,
    isLoading: isLoadingAlbumReviews,
  } = useGetAlbumReviews(album?.id);
  const { data: existsReview } = useExistsReview(album?.id);
  const { data: lists } = useGetUserListsSummary(user?.username);

  const { mutateAsync: deleteAlbum } = useDeleteAlbum();

  const totalPages = albumReviews?.pages?.flatMap((pages) => pages.totalPages);

  const handleDeleteAlbum = async (albumId) => {
    await deleteAlbum(albumId);
    navigate(-1);
  };

  return (
    <Container maxWidth='xl'>
      <Grid container spacing={2}>
        {isLoadingAlbum || isLoadingAlbumReviews ? (
          <Loader />
        ) : (
          <>
            <Grid item xs={12} md={totalPages > 0 ? 5 : 12}>
              <Stack
                direction={{ xs: 'column', sm: 'row' }}
                alignItems={{ xs: 'start' }}
                spacing={2}
                sx={{ mb: 2 }}
              >
                <Button
                  to='..'
                  component={RouterLink}
                  variant='contained'
                  onClick={(e) => {
                    e.preventDefault();
                    navigate(-1);
                  }}
                >
                  Go Back
                </Button>

                {user?.roles.some((role) => role.includes('ROLE_ADMIN')) && (
                  <>
                    <CreateOrUpdateAlbumModal album={album} />
                    <ConfirmationModal
                      text={`Are you sure you want to delete the album "${album?.title}"?`}
                      title='Delete Album'
                      onClick={() => handleDeleteAlbum(album?.id)}
                    />
                  </>
                )}
              </Stack>
              <AlbumData
                album={album}
                setShowReviewForm={setShowReviewForm}
                lists={lists}
                id={user?.id}
              />
              {!existsReview && (
                <>
                  <Button
                    variant='contained'
                    sx={{ alignSelf: 'start', mt: 2 }}
                    onClick={() => setShowReviewForm((prevState) => !prevState)}
                  >
                    Review
                  </Button>
                  {showReviewForm && (
                    <ReviewForm
                      albumId={album?.id}
                      showReviewForm={showReviewForm}
                      setShowReviewForm={setShowReviewForm}
                    />
                  )}
                </>
              )}
            </Grid>
            <Grid item xs={12} md={7}>
              {totalPages?.length > 0 && (
                <InfiniteScrollReviews
                  data={albumReviews}
                  fetchNextPage={fetchNextPage}
                  hasNextPage={hasNextPage}
                />
              )}
            </Grid>
          </>
        )}
      </Grid>
    </Container>
  );
};

export default AlbumPage;
