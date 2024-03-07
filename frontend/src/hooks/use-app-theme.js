import { createTheme } from '@mui/material';
import { useEffect, useState } from 'react';

export const useAppTheme = () => {
  const persistedMode = localStorage.getItem('mode') === 'dark';
  const [mode, setMode] = useState(persistedMode);

  useEffect(() => {
    localStorage.setItem('mode', mode ? 'dark' : 'light');
  }, [mode]);

  const handleChange = () => {
    setMode((prevMode) => {
      const newMode = !prevMode;
      localStorage.setItem('mode', newMode ? 'dark' : 'light');
      return newMode;
    });
  };

  const appTheme = createTheme({
    palette: {
      mode: mode ? 'dark' : 'light',
      primary: {
        main: '#0052ff',
      },
      secondary: {
        main: '#66a3ff',
      },
      ...(mode
        ? {
            background: {
              default: '#0a0b0e',
              paper: '#13171b',
            },
            text: {
              primary: '#ffff',
              disabled: '#acacac',
            },
          }
        : {
            background: {
              default: '#fff',
              paper: '#f5f6f8',
            },
            text: {
              primary: '#13171b',
              disabled: '#acacac',
            },
          }),
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            color: '#fff',
            textTransform: 'capitalize',
            fontSize: '0.9rem',
            borderRadius: '8px',
            boxShadow: 'none',
            '&:hover': {
              boxShadow: 'none',
            },
          },
          outlined: {
            color: !mode ? '#0052ff' : '#fff',
            borderColor: '#0052ff',
          },
        },
      },
      MuiIconButton: {
        styleOverrides: {
          root: {
            color: !mode && '#13171b',
          },
        },
      },
      MuiLink: {
        styleOverrides: {
          root: {
            textDecoration: 'none',
            color: !mode ? '#13171b' : '#fff',
          },
        },
      },
      MuiList: {
        styleOverrides: {
          root: {
            padding: 0,
          },
        },
      },
      MuiMenuItem: {
        styleOverrides: {
          root: {
            color: mode && '#fff',
          },
        },
      },
      MuiOutlinedInput: {
        styleOverrides: {
          root: {
            borderRadius: '8px',
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            boxShadow: 'none',
          },
        },
      },
      MuiSelect: {
        styleOverrides: {
          root: {
            height: '2.5rem',
          },
        },
      },
      MuiTab: {
        styleOverrides: {
          root: {
            textTransform: 'capitalize',
          },
        },
      },
      MuiTabPanel: {
        styleOverrides: {
          root: {
            padding: '1rem 0rem 0rem 0rem',
          },
        },
      },
      MuiTableCell: {
        styleOverrides: {
          root: {
            backgroundColor: 'background.default',
            color: 'text.primary',
            fontSize: '1.2rem',
          },
        },
      },
      MuiTableRow: {
        styleOverrides: {
          root: {
            '&:last-child td, &:last-child th': {
              border: 0,
            },
          },
        },
      },
    },
    typography: {
      fontFamily: 'Raleway',
      fontWeightLight: 400,
      fontWeightRegular: 500,
      fontWeightMedium: 600,
      fontWeightBold: 700,
    },
  });

  return { appTheme, mode, handleChange };
};
