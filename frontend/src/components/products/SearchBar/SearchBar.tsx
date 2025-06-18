import { useState } from 'react';
import { TextField, FormControl, InputLabel, Select, MenuItem, Box, Button, OutlinedInput, Chip } from '@mui/material';
import { useProductContext } from '../../../context/ProductContext';
import type ISearchBarProps from '../../../interfaces/FilteringParams/ISearchBarProps';

const SearchBar = ({ categories }: ISearchBarProps) => {
   const { updateParams } = useProductContext();

  const [name, setName] = useState('');
  const [selectedCategories, setSelectedCategories] = useState<number[]>([]);
  const [availability, setAvailability] = useState<string>('');

  const handleSearch = () => {
    updateParams({
      name,
      category: selectedCategories.join(','), // assume backend supports comma-separated values
      available: availability === '' ? undefined : availability === 'true'
    });
  };

  return (
    <Box display="flex" flexDirection="column" gap={2} mb={3}>
      <TextField
        label="Buscar por nombre"
        variant="outlined"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />

      <FormControl>
        <InputLabel>Categorías</InputLabel>
        <Select
          multiple
          value={selectedCategories}
          onChange={(e) => setSelectedCategories(e.target.value as number[])}
          input={<OutlinedInput label="Categorías" />}
          renderValue={(selected) => (
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
              {(selected as number[]).map(id => {
                const cat = categories.find(c => c.id === id);
                return cat && <Chip key={id} label={cat.name} />;
              })}
            </Box>
          )}
        >
          {categories.map((category) => (
            <MenuItem key={category.id} value={category.id}>
              {category.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl>
        <InputLabel>Disponibilidad</InputLabel>
        <Select
          value={availability}
          onChange={(e) => setAvailability(e.target.value)}
          label="Disponibilidad"
        >
          <MenuItem value="">Todas</MenuItem>
          <MenuItem value="true">En stock</MenuItem>
          <MenuItem value="false">Sin stock</MenuItem>
        </Select>
      </FormControl>

      <Button variant="contained" onClick={handleSearch}>
        Aplicar filtros
      </Button>
    </Box>
  );
};

export default SearchBar;