/* eslint-disable react/prop-types */
import { Link, MenuItem, Select } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import {
  useDeleteUser,
  useGetUsers,
  useUpdateUserRole,
} from '../lib/react-query/queries';
import { Link as RouterLink } from 'react-router-dom';
import { ROLES } from '../lib/utils';
import Loader from './Loader';
import { useEffect, useState } from 'react';
import { getCurrentUser } from '../services/auth.service';
import ConfirmationModal from './ConfirmationModal';
import { toast } from 'react-toastify';

const UsersDataGrid = () => {
  const [rows, setRows] = useState([]);
  const currentUser = getCurrentUser();
  const { data: users, isLoading, refetch } = useGetUsers();
  const { mutateAsync: deleteUser } = useDeleteUser();
  const { mutateAsync: updateUserRole } = useUpdateUserRole();

  const handleDeleteUser = async (id) => {
    await deleteUser(id);
  };

  useEffect(() => {
    if (users) {
      setRows(users.map((user) => ({ ...user, selectedRole: '' })));
    }
  }, [users]);

  const columns = [
    {
      field: 'username',
      headerName: 'Username',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      minWidth: 150,
      renderCell: (params) => (
        <Link component={RouterLink} to={`/users/${params.row.username}`}>
          {params.row.username}
        </Link>
      ),
    },
    {
      field: 'email',
      headerName: 'Email',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      minWidth: 150,
    },
    {
      field: 'add-role',
      headerName: 'Add Role',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      minWidth: 150,
      renderCell: (params) => {
        if (
          !params.row.roles.includes('ROLE_USER') ||
          !params.row.roles.includes('ROLE_ADMIN')
        ) {
          return (
            <Select
              value={params.row.selectedRole}
              onChange={async (e) => {
                await updateUserRole({
                  userId: params.row.id,
                  role: e.target.value,
                  addRole: true,
                });
                toast.success(
                  `Added role ${e.target.value} to user "${params.row.username}"`
                );
                refetch();
              }}
              displayEmpty={true}
            >
              <MenuItem value=''>Select Role</MenuItem>
              {ROLES.filter(
                (role) => !params.row.roles.includes(role.name)
              ).map((role) => (
                <MenuItem key={role.id} value={role.name}>
                  {role.name}
                </MenuItem>
              ))}
            </Select>
          );
        }
        return null;
      },
    },
    {
      field: 'remove-role',
      headerName: 'Remove Role',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      minWidth: 150,
      renderCell: (params) => {
        if (
          params.row?.roles.includes('ROLE_ADMIN') &&
          params.row.id !== currentUser?.id
        ) {
          return (
            <Select
              value={params.row.selectedRole}
              onChange={async (e) => {
                await updateUserRole({
                  userId: params.row.id,
                  role: e.target.value,
                  addRole: false,
                });
                toast.success(
                  `Removed role "${e.target.value}" to user "${params.row.username}"`
                );
                refetch();
              }}
              displayEmpty={true}
            >
              <MenuItem value=''>Select Role</MenuItem>
              {ROLES.filter((role) => role.name !== 'ROLE_USER').map((role) => (
                <MenuItem key={role.id} value={role.name}>
                  {role.name}
                </MenuItem>
              ))}
            </Select>
          );
        }
        return null;
      },
    },
    {
      field: 'actions',
      headerName: 'Delete',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      minWidth: 80,
      renderCell: (params) =>
        currentUser?.id !== params?.row.id ? (
          <ConfirmationModal
            icon
            title='Delete User'
            text={`Are you sure you want to delete the user "${params.row.username}"?`}
            onClick={() => handleDeleteUser(params.row.id)}
          />
        ) : null,
    },
  ];

  return (
    <>
      {isLoading ? (
        <Loader />
      ) : (
        <DataGrid
          rows={rows}
          columns={columns}
          initialState={{
            sorting: {
              sortModel: [{ field: 'username', sort: 'asc' }],
            },
          }}
          pageSizeOptions={[15, 20, 30, 50, 100]}
          getRowId={(row) => row.id}
        />
      )}
    </>
  );
};

export default UsersDataGrid;
