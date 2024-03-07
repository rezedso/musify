import { Button, Container, Typography } from '@mui/material';
import UsersDataGrid from '../UsersDataGrid';
import { Link as RouterLink, useNavigate } from 'react-router-dom';

const UsersPage = () => {
  const navigate = useNavigate();

  return (
    <Container maxWidth='xl'>
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
      <Typography variant='h5' sx={{ mb: 2 }}>
        Users
      </Typography>
      <UsersDataGrid />
    </Container>
  );
};

export default UsersPage;
