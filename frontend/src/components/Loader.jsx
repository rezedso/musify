import { Stack } from '@mui/material';
import { TailSpin } from 'react-loader-spinner';

const Loader = () => {
  return (
    <Stack
      sx={{
        display: 'flex',
        alignItems: 'center',
        my: 4,
        width: '100%',
      }}
    >
      <TailSpin
        height='80'
        width='80'
        color='#0052ff'
        ariaLabel='tail-spin-loading'
        radius='1'
        visible={true}
      />
    </Stack>
  );
};

export default Loader;
