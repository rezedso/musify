import {
  Button,
  Container,
  FormLabel,
  Stack,
  TextField,
  Typography,
  Link,
} from '@mui/material';
import { useRef, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import ImageOutlinedIcon from '@mui/icons-material/ImageOutlined';
import { useRegisterUser } from '../../lib/react-query/queries';
import { useEffect } from 'react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useTheme } from '@emotion/react';
import { registerUserSchema } from '../../lib/zod/validations';

const RegisterUserPage = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const fileRef = useRef(null);
  const theme = useTheme();
  const navigate = useNavigate();

  const { mutateAsync: registerUser, isSuccess, isPending } = useRegisterUser();

  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(registerUserSchema),
  });

  const onSubmit = async (data) => {
    const formData = new FormData();
    const user = {
      username: data.username,
      email: data.email,
      password: data.password,
    };

    const userBlob = new Blob([JSON.stringify(user)], {
      type: 'application/json',
    });

    formData.append('user', userBlob);

    if (data.image) {
      formData.append('image', data.image);
    }

    registerUser(formData);
  };

  useEffect(() => {
    if (isSuccess) {
      navigate('/login');
    }
  }, [isSuccess, navigate]);

  return (
    <Container maxWidth='lg'>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Stack spacing={2} width={350} sx={{ m: 'auto', p: 2 }}>
          <Typography variant='h5' textAlign='center'>
            Register
          </Typography>
          <FormLabel>Username</FormLabel>
          <TextField
            {...register('username')}
            size='small'
            fullWidth
            variant='outlined'
          />
          {errors.username && (
            <Typography
              variant='subtitle2'
              sx={{ color: theme.palette.error.main }}
            >
              {errors.username.message}
            </Typography>
          )}
          <FormLabel>Email</FormLabel>
          <TextField
            {...register('email')}
            size='small'
            fullWidth
            variant='outlined'
          />
          {errors.email && (
            <Typography
              variant='subtitle2'
              sx={{ color: theme.palette.error.main }}
            >
              {errors.email.message}
            </Typography>
          )}
          <FormLabel>Password</FormLabel>
          <TextField
            {...register('password')}
            size='small'
            fullWidth
            type='password'
            variant='outlined'
          />
          {errors.password && (
            <Typography
              variant='subtitle2'
              sx={{ color: theme.palette.error.main }}
            >
              {errors.password.message}
            </Typography>
          )}
          <FormLabel>Profile image (optional)</FormLabel>
          <Button
            direction='row'
            variant='contained'
            onClick={() => fileRef.current.click()}
            sx={{
              fontSize: '0.9rem',
            }}
          >
            <ImageOutlinedIcon />
            <Typography variant='subtitle2' sx={{ ml: 1 }}>
              Select File
            </Typography>
          </Button>
          <Controller
            control={control}
            name='image'
            render={({ field: { value, onChange, ref, ...field } }) => {
              return (
                <input
                  {...field}
                  value={value?.fileName}
                  onChange={(e) => {
                    onChange(e.target.files[0]);
                    setSelectedFile(e.target.files[0]);
                  }}
                  ref={(e) => {
                    ref(e);
                    fileRef.current = e;
                  }}
                  hidden
                  type='file'
                />
              );
            }}
          />
          {selectedFile && (
            <img
              src={URL.createObjectURL(selectedFile)}
              width={200}
              height={200}
            />
          )}
          <Button type='submit' variant='contained' disabled={isPending}>
            {isPending ? 'Loading...' : 'Register'}
          </Button>
          <Stack direction='row' spacing={2}>
            <Typography>Already have an account?</Typography>
            <Link
              to='/login'
              component={RouterLink}
              sx={{ color: 'primary.main' }}
            >
              Log In
            </Link>
          </Stack>
        </Stack>
      </form>
    </Container>
  );
};

export default RegisterUserPage;
