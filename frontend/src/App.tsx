import './App.css';
import { ProductsProvider } from './context/ProductContext';
import Products from './views/Products/Products';
import { useThemeContext } from './theme/themeContext';
import { IconButton } from '@mui/material';
import { Brightness4, Brightness7 } from '@mui/icons-material';

function App() {
  const { mode, toggleTheme } = useThemeContext();

  return (
    <>
      <ProductsProvider>
        <div style={{ display: 'flex', justifyContent: 'flex-end', padding: '1rem' }}>
          <IconButton onClick={toggleTheme} color="inherit">
            {mode === 'dark' ? <Brightness7 /> : <Brightness4 />}
          </IconButton>
        </div>
        <Products />
      </ProductsProvider>
    </>
  );
}

export default App;
