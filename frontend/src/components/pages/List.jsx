import { Container, Stack, Link, Button, Typography } from '@mui/material';
import { useDeleteList, useGetList } from '../../lib/react-query/queries';
import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import NoResults from '../NoResults';
import Loader from '../Loader';
import CreateOrUpdateListModal from '../CreateOrUpdateListModal';
import AlbumDataGrid from '../AlbumDataGrid';
import { getCurrentUser } from '../../services/auth.service';

const ListPage = () => {
  const currentUser = getCurrentUser();
  const params = useParams();
  const navigate = useNavigate();
  const { data: list, isLoading } = useGetList(
    params?.listName,
    params?.username
  );
  const { mutateAsync: deleteList } = useDeleteList();

  const handleDeleteList = async (listId, id) => {
    await deleteList({ listId, id });
    navigate(-1);
  };

  return (
    <Container maxWidth='xl'>
      <Stack direction='column' spacing={2} sx={{ mb: 2 }}>
        <Stack direction='row' spacing={2} sx={{ alignItems: 'start', mb: 2 }}>
          <Button
            to={`/lists/${list?.username}`}
            component={RouterLink}
            variant='contained'
            sx={(theme) => ({
              [theme.breakpoints.down('sm')]: {
                p: '.5rem',
              },
            })}
            onClick={(e) => {
              e.preventDefault();
              navigate(-1);
            }}
          >
            Go Back
          </Button>
          {(currentUser?.roles.some((role) => role.includes('ROLE_ADMIN')) ||
            currentUser?.id === list?.userId) && (
            <>
              <CreateOrUpdateListModal list={list} />
              <Button
                variant='contained'
                color='error'
                onClick={() => handleDeleteList(list?.id, list?.userId)}
                sx={(theme) => ({
                  [theme.breakpoints.down('sm')]: {
                    p: '.5rem',
                  },
                })}
              >
                Delete List
              </Button>
            </>
          )}
        </Stack>
        <Typography variant='h5'>
          <b>{list?.name}</b>
        </Typography>
      </Stack>
      {isLoading ? (
        <Loader />
      ) : (
        <>
          {list?.albums?.length > 0 ? (
            <AlbumDataGrid albums={list?.albums} list={list} />
          ) : (
            <>
              <NoResults text='There are no albums in this list yet.' />
              {currentUser?.id === list?.userId && (
                <Link
                  to='/albums'
                  component={RouterLink}
                  sx={{ color: 'primary.main', textAlign: 'center' }}
                >
                  Try adding some albums.
                </Link>
              )}
            </>
          )}
        </>
      )}
    </Container>
  );
};

export default ListPage;
