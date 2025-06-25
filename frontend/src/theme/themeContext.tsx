import { createContext, useContext, useMemo, useState } from 'react';
import { ThemeProvider, createTheme, CssBaseline } from '@mui/material';
import type { PaletteMode } from '@mui/material';

interface ThemeContextProps {
  mode: PaletteMode;
  toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextProps | undefined>(undefined);

const commonPalette = {
   primary: {
    main: '#978BC8', 
  },
  secondary: {
    main: '#574F92', 
  },

}

const lightTheme = createTheme({
  palette: {
    mode: 'light',
    ...commonPalette,
  },
});

const darkTheme = createTheme({
  palette: {
    mode: 'dark',
    ...commonPalette,
  },
});


export const useThemeContext = () => {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error("ThemeContext not found");
  return ctx;
};

export const ThemeContextProvider = ({ children }: { children: React.ReactNode }) => {
  const [mode, setMode] = useState<PaletteMode>('dark');

  const toggleTheme = () => {
    setMode((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  // const theme = useMemo(() =>
  //   createTheme({
  //     palette: {
  //       mode,
  //     },
  //     components: {
  //       MuiButton: {
  //         styleOverrides: {
  //           root: {
  //             borderRadius: 8,
  //           },
  //         },
  //       },
  //     },
  //   }), [mode]);

  const theme = useMemo(() => (mode === 'light' ? lightTheme : darkTheme), [mode]);


  return (
    <ThemeContext.Provider value={{ mode, toggleTheme }}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </ThemeContext.Provider>
  );
};
