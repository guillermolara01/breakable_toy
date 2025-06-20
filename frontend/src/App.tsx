import './App.css';
import { ProductsProvider } from './context/ProductContext';
import Products from './views/Products/Products';
import { useThemeContext } from './theme/themeContext';
import { IconButton, useTheme, Box } from '@mui/material';
import { Brightness4, Brightness7 } from '@mui/icons-material';

function App() {
  const { mode, toggleTheme } = useThemeContext();
  const theme = useTheme();

  return (
    <ProductsProvider>
      <Box
        sx={{
          position: 'fixed',
          bottom: 16,
          right: 16,
          backgroundColor: theme.palette.background.paper,
          border: `1px solid ${theme.palette.divider}`,
          borderRadius: '50%',
          boxShadow: 3,
          zIndex: 1300, // stay above most components
          transition: 'all 0.3s ease',
          '&:hover': {
            boxShadow: 6,
            transform: 'scale(1.05)',
          },
        }}
      >
        <IconButton onClick={toggleTheme} color="inherit" size="large">
          {mode === 'dark' ? <Brightness7 /> : <Brightness4 />}
        </IconButton>
      </Box>

      <Products />
    </ProductsProvider>
  );
}

export default App;
