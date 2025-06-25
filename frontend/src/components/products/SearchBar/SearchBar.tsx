import { useState } from 'react';
import {
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
  Button,
  OutlinedInput,
  Chip,
  useTheme,
  Typography,
} from '@mui/material';
import { useProductContext } from '../../../context/ProductContext';
import type ISearchBarProps from './ISearchBarProps';
import type ICategory from '../../../interfaces/Category/ICategory';

const SearchBar = ({ categories }: ISearchBarProps) => {
  const { updateParams } = useProductContext();
  const [name, setName] = useState('');
  const [selectedCategories, setSelectedCategories] = useState<ICategory[]>([]);
  const [availability, setAvailability] = useState<string>(' ');
  const theme = useTheme();

  const handleSearch = () => {
    updateParams({
      name,
      category: selectedCategories.map((c) => c.id).join('-'),
      available: availability === ' ' ? undefined : availability === 'true',
    });
  };

  const handleClear = () => {
    setName('');
    setSelectedCategories([]);
    setAvailability(' ');
    updateParams({
      name: undefined,
      category: undefined,
      available: undefined,
    });
  };

  const handleRemoveCategory = (id: number) => {
    const updated = selectedCategories.filter((c) => c.id !== id);
    setSelectedCategories(updated);
  };

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        gap: 2,
        mb: 2,
        py: 2,
        px: 2,
        width: '100%',
        maxWidth: '1220px',
        border: `1px solid ${theme.palette.divider}`,
        borderRadius: 2,
        backgroundColor: theme.palette.background.paper,
      }}>
      <Typography sx={{fontSize: '1.2rem', textAlign: 'center', fontWeight: 600}}>Search and Filter Products</Typography>
      {/* Product Name */}
      <Box
        display='flex'
        alignItems='center'
        gap={2}>
        <Typography width={130}>Product Name:</Typography>
        <FormControl size='small'>
          <TextField
            size='small'
            label='Search by name'
            variant='outlined'
            fullWidth
            sx={{ width: 500 }}
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </FormControl>
      </Box>
      <Box
        display='flex'
        alignItems='center'
        gap={2}>
        <Typography
          width={130}
          pt={1}>
          Categories:
        </Typography>
        <Box
          flex={1}
          mt={2}>
          <FormControl
            fullWidth
            size='small'>
            <Select
              multiple
              value={selectedCategories.map((cat) => cat.id)}
              onChange={(e) => {
                const ids = e.target.value as number[];
                const selected = categories.filter((cat) =>
                  ids.includes(cat.id)
                );
                setSelectedCategories(selected);
              }}
              input={<OutlinedInput />}>
              {categories.map((category) => (
                <MenuItem
                  key={category.id}
                  value={category.id}>
                  {category.name}
                </MenuItem>
              ))}
            </Select>
            {/* Chips */}
          </FormControl>
        </Box>
        <Box flex={1}>
          {selectedCategories.length > 0 && (
            <Box>
              <InputLabel>Selected Categories</InputLabel>
              <Box
                display='flex'
                flexWrap='wrap'
                gap={1}>
                {selectedCategories.map((cat) => (
                  <Chip
                    key={cat.id}
                    label={cat.name}
                    onClick={() => handleRemoveCategory(cat.id)}
                    onDelete={() => handleRemoveCategory(cat.id)}
                  />
                ))}
              </Box>
            </Box>
          )}
        </Box>
      </Box>
      <Box
        display='flex'
        alignItems='center'
        gap={2}>
        <Typography width={130}>Availability:</Typography>
        <FormControl size='small'>
          <Select
            sx={{ width: 300 }}
            value={availability}
            onChange={(e) => setAvailability(e.target.value)}>
            <MenuItem value=' '>All</MenuItem>
            <MenuItem value='true'>In Stock</MenuItem>
            <MenuItem value='false'>Out of Stock</MenuItem>
          </Select>
        </FormControl>
      </Box>

      {/* Action Buttons */}
      <Box
        display='flex'
        justifyContent='flex-end'
        gap={2}>
        <Button
          variant='contained'
          size='small'
          onClick={handleSearch}>
          Search
        </Button>
        <Button
          variant='outlined'
          size='small'
          onClick={handleClear}>
          Clear Filters
        </Button>
      </Box>
    </Box>
  );
};

export default SearchBar;
