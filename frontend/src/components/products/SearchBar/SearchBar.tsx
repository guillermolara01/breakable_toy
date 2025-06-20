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
} from '@mui/material';
import { useProductContext } from '../../../context/ProductContext';
import type ISearchBarProps from '../../../interfaces/FilteringParams/ISearchBarProps';
import type ICategory from '../../../interfaces/Category/ICategory';

const SearchBar = ({ categories }: ISearchBarProps) => {
  const { updateParams } = useProductContext();
  const [name, setName] = useState('');
  const [selectedCategories, setSelectedCategories] = useState<ICategory[]>([]);
  const [availability, setAvailability] = useState<string>(' ');
  const theme = useTheme();
  const handleSearch = () => {
    console.log(
      selectedCategories
        .map((selectedCategory) => selectedCategory.id)
        .join('-')
    );
    updateParams({
      name,
      category: selectedCategories
        .map((selectedCategory) => selectedCategory.id)
        .join('-'),
      available: availability === ' ' ? undefined : availability === 'true',
    });
  };

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        gap: 1.5,
        mb: 2,
        py: 2,
        px: 2,
        width: '100%',
        maxWidth: '1220px',
        border: `1px solid ${theme.palette.divider}`,
        borderRadius: 2,
        backgroundColor: theme.palette.background.paper,
      }}>
      <TextField
        size='small'
        label='Search by name'
        variant='outlined'
        value={name}
        onChange={(e) => setName(e.target.value)}
      />

      <FormControl size='small'>
        <InputLabel>Categories</InputLabel>
        <Select
          multiple
          value={selectedCategories.map((cat) => cat.id)}
          onChange={(e) => {
            const selectedIds = e.target.value as number[];
            const selected = categories.filter((cat) =>
              selectedIds.includes(cat.id)
            );
            setSelectedCategories(selected);
          }}
          input={<OutlinedInput label='Categories' />}
          renderValue={() => (
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
              {selectedCategories.map((selectedCat) => (
                <Chip
                  key={selectedCat.id}
                  label={selectedCat.name}
                />
              ))}
            </Box>
          )}>
          {categories.map((category) => (
            <MenuItem
              key={category.id}
              value={category.id}>
              {category.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl size='small'>
        <InputLabel>Availability</InputLabel>
        <Select
          value={availability}
          onChange={(e) => setAvailability(e.target.value)}
          label='Availability'>
          <MenuItem value=' '>All</MenuItem>
          <MenuItem value='true'>In Stock</MenuItem>
          <MenuItem value='false'>Out of Stock</MenuItem>
        </Select>
      </FormControl>

      <Box width={'16vw'}>
        <Button
          variant='outlined'
          size='small'
          onClick={handleSearch}>
          Search
        </Button>
      </Box>
    </Box>
  );
};

export default SearchBar;
