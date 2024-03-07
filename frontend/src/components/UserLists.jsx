/* eslint-disable react/prop-types */
import {
  Button,
  IconButton,
  Menu,
  MenuItem,
  Stack,
  useMediaQuery,
} from '@mui/material';
import { useState } from 'react';
import { useAddAlbumToList } from '../lib/react-query/queries';
import AddCircleOutlinedIcon from '@mui/icons-material/AddCircleOutlined';

const UserLists = ({ lists, albumId, card, artistPage, id }) => {
  const [anchorElList, setAnchorElList] = useState(null);
  const { mutateAsync: addAlbumToList } = useAddAlbumToList();

  const isBelowMd = useMediaQuery((theme) => theme.breakpoints.down('md'));

  const handleOpenListMenu = (event) => {
    setAnchorElList(event.currentTarget);
  };

  const handleCloseListMenu = () => {
    setAnchorElList(null);
  };

  const handleAddAlbumToList = async (listId, albumId, id) => {
    await addAlbumToList({ listId, albumId, id });
  };

  return (
    <>
      {lists?.length > 0 && (
        <Stack
          direction='row'
          spacing={2}
          sx={{
            ml: !card ? 1 : 0,
            alignSelf: 'start',
          }}
        >
          {artistPage && isBelowMd ? (
            <IconButton
              onClick={handleOpenListMenu}
              sx={{ color: 'text.primary' }}
            >
              <AddCircleOutlinedIcon />
            </IconButton>
          ) : (
            <Button variant='contained' onClick={handleOpenListMenu}>
              Add to list
            </Button>
          )}
          <Menu
            sx={{ mt: '37px' }}
            id='menu-appbar'
            anchorEl={anchorElList}
            anchorOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            keepMounted
            transformOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            open={Boolean(anchorElList)}
            onClose={handleCloseListMenu}
          >
            {lists?.map((list) => (
              <MenuItem
                key={list.id}
                sx={{ p: 1 }}
                onClick={() => {
                  handleAddAlbumToList(list.id, albumId, id);
                  handleCloseListMenu();
                }}
              >
                {list.name}
              </MenuItem>
            ))}
          </Menu>
        </Stack>
      )}
    </>
  );
};

export default UserLists;
