import { Link as RouterLink, useNavigate, useParams } from 'react-router-dom';
import {
  useGetAlbumsByArtist,
  useGetArtist,
  useDeleteArtist,
  useGetUserListsSummary,
  useFollowArtist,
  useIsUserFollowing,
  useUnFollowArtist,
  useGetArtistFollowersCount,
} from '../../lib/react-query/queries';
import { Container, Stack, Typography, Button } from '@mui/material';
import Loader from '../Loader';
import { formatDate } from '../../lib/utils';
import { getCurrentUser } from '../../services/auth.service';
import CreateOrUpdateAlbumModal from '../CreateOrUpdateAlbumModal';
import CreateOrUpdateArtistModal from '../CreateOrUpdateArtistModal';
import UserLists from '../UserLists';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import ConfirmationModal from '../ConfirmationModal';

const ArtistPage = () => {
  const params = useParams();
  const navigate = useNavigate();
  const user = getCurrentUser();

  const { data: albums, isLoading: isLoadingAlbums } = useGetAlbumsByArtist(
    params?.artistSlug
  );
  const { data: artist, isLoading: isLoadingArtist } = useGetArtist(
    params?.artistSlug
  );
  const { data: lists } = useGetUserListsSummary(user?.username);
  const { data: isFollowing, refetch } = useIsUserFollowing(artist?.id);
  const { data: artistFollowersCount, refetch: refetchCount } =
    useGetArtistFollowersCount(artist?.id);

  const { mutateAsync: followArtist } = useFollowArtist();
  const { mutateAsync: unFollowArtist } = useUnFollowArtist();
  const { mutateAsync: deleteArtist } = useDeleteArtist();

  const handleFollowing = async () => {
    if (isFollowing) {
      await unFollowArtist(artist?.id);
    } else {
      await followArtist(artist?.id);
    }
    refetch();
    refetchCount();
  };

  const handleDeleteArtist = async () => {
    await deleteArtist(artist?.id);
    navigate('/');
  };

  return (
    <Container maxWidth='xl'>
      {isLoadingAlbums || isLoadingArtist ? (
        <Loader />
      ) : (
        <>
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            alignItems={{ xs: 'start', sm: 'center' }}
            spacing={2}
            sx={{
              mb: 2,
            }}
          >
            <Button
              to='..'
              sx={{ m: 0 }}
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
                <CreateOrUpdateArtistModal artist={artist} />
                <CreateOrUpdateAlbumModal artistName={artist?.name} />
                <ConfirmationModal
                  title='Delete Artist'
                  text={`Are you sure you want to delete the artist "${artist?.name}"?`}
                  onClick={() => handleDeleteArtist(artist.id)}
                />
              </>
            )}
          </Stack>
          <Stack direction='column'>
            <Stack sx={{ mb: 2 }}>
              {!artist?.image ? (
                <ImageOutlinedIcon sx={{ width: 350, height: 350 }} />
              ) : (
                <img src={artist.image} width={350} height={350} />
              )}
            </Stack>
            <Typography variant='h4' sx={{ mb: 1, fontWeight: 'bold' }}>
              {artist?.name}
            </Typography>
            <Stack
              direction='row'
              sx={{ mb: 2, alignItems: 'end' }}
              spacing={2}
            >
              <Button
                variant='outlined'
                sx={{ alignSelf: 'start' }}
                onClick={handleFollowing}
              >
                {isFollowing ? 'Following' : 'Follow'}
              </Button>
              <Typography variant='subtitle1' sx={{ fontSize: '1.1rem' }}>
                {artistFollowersCount === 1
                  ? '1 follower'
                  : `${artistFollowersCount} followers`}
              </Typography>
            </Stack>
            <Typography variant='subtitle2' sx={{ color: 'text.disabled' }}>
              Formed
            </Typography>
            <Typography>
              {artist?.formedYear}, {artist?.originCountry}.
            </Typography>
            <Typography
              variant='subtitle2'
              sx={{ color: 'text.disabled', mt: 1 }}
            >
              Genres
            </Typography>
            <Stack direction='row' spacing={1}>
              {artist?.artistGenres?.map((genre, i) => (
                <Typography
                  key={genre.id}
                  to={`/genres/artists/${genre.slug}`}
                  component={RouterLink}
                  sx={{ color: 'text.primary' }}
                >
                  {i === artist?.artistGenres.length - 1
                    ? `${genre.name}.`
                    : `${genre.name} ,`}
                </Typography>
              ))}
            </Stack>
            <Typography
              variant='subtitle2'
              sx={{ color: 'text.disabled', my: 1 }}
            >
              Albums
            </Typography>
            <Stack spacing={2} sx={{ mb: 2 }}>
              {albums?.map((album) => (
                <Stack
                  key={album.id}
                  direction='row'
                  sx={{ alignItems: 'center', mb: 1 }}
                  spacing={2}
                >
                  <Stack
                    direction='row'
                    spacing={2}
                    sx={{ alignItems: 'center' }}
                  >
                    <Stack
                      component={RouterLink}
                      to={`/artists/${album.artistSlug}/${album.slug}`}
                      sx={{ color: 'text.primary' }}
                    >
                      {!album?.albumImage ? (
                        <ImageOutlinedIcon sx={{ width: 50, height: 50 }} />
                      ) : (
                        <img src={album.albumImage} width={50} height={50} />
                      )}
                    </Stack>
                    <Stack direction='column'>
                      <Typography
                        to={`/artists/${artist.slug}/${album.slug}`}
                        component={RouterLink}
                        sx={{ color: 'text.primary' }}
                      >
                        {album.title}
                      </Typography>
                      <Typography>{formatDate(album.releaseDate)}</Typography>
                    </Stack>
                    <UserLists
                      lists={lists}
                      albumId={album?.id}
                      artistPage
                      id={user?.id}
                    />
                  </Stack>
                </Stack>
              ))}
            </Stack>
          </Stack>
        </>
      )}
    </Container>
  );
};

export default ArtistPage;
