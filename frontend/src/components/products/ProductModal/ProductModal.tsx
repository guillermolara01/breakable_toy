import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  MenuItem,
  Button,
  TextField,
  Snackbar,
  CircularProgress,
  FormControl,
  Typography,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CategoryIcon from '@mui/icons-material/Category';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import PriceCheckIcon from '@mui/icons-material/PriceCheck';
import InventoryIcon from '@mui/icons-material/Inventory';
import type IProduct from '../../../interfaces/Product/IProduct';
import type IProductModalProps from './IProductModalProps';
import { useEffect, useState } from 'react';
import { useProductContext } from '../../../context/ProductContext';
import { createProduct, updateProduct } from '../../../service/productService';

export default function ProductModal(props: IProductModalProps) {
  const { refresh } = useProductContext();

  const [productData, setProductData] = useState<IProduct>({
    id: 0,
    name: '',
    category: { id: 0, name: 'Select a Category' },
    unitPrice: 0,
    expirationDate: '',
    stock: 0,
  });

  const [loading, setLoading] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [errors, setErrors] = useState<{ [key: string]: string }>({});

  const handleFieldUpdate = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    let parsed: string | number = value;
    if (name === 'unitPrice') {
      parsed = parseFloat(value);
    } else if (name === 'stock') {
      parsed = parseInt(value);
    }
    setProductData({ ...productData, [name]: parsed });
    setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const validateFields = () => {
    const newErrors: { [key: string]: string } = {};
    if (!productData.name.trim()) newErrors.name = 'Product name is required';
    if (productData.category.id === 0) newErrors.category = 'Select a category';
    if (productData.unitPrice <= 0)
      newErrors.unitPrice = 'Price must be positive';
    if (productData.stock < 0) newErrors.stock = 'Stock cannot be negative';

    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const expDate = new Date(productData.expirationDate);
    if (
      productData.expirationDate &&
      expDate < new Date(tomorrow.toDateString())
    ) {
      newErrors.expirationDate = 'Expiration must be at least tomorrow';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateFields()) return;
    setLoading(true);
    if (props.editMode) {
      await updateProduct(productData);
    } else {
      await createProduct(productData);
    }
    refresh();
    setLoading(false);
    setSnackbarOpen(true);
    props.handleClose();
  };

  const handleCategoryChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedId = parseInt(e.target.value);
    const selectedCategory = props.categories.find((c) => c.id === selectedId);
    if (selectedCategory) {
      setProductData({ ...productData, category: selectedCategory });
      setErrors((prev) => ({ ...prev, category: '' }));
    }
  };

  useEffect(() => {
    if (props.editMode && props.initialData) {
      setProductData(props.initialData);
    }
  }, [props.editMode, props.initialData]);

  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const minDateStr = tomorrow.toISOString().split('T')[0];

  return (
    <>
      <Dialog
        open={props.open}
        onClose={props.handleClose}
        fullWidth
        maxWidth='md'>
        <DialogTitle>
          <Typography fontSize={25} textAlign={'center'} fontWeight={600}>
            {props.editMode
              ? 'Update Product ' + productData.name
              : 'Create New Product'}
          </Typography>
        </DialogTitle>
        <DialogContent
          sx={{
            display: 'flex',
            flexDirection: 'column',
            gap: 1,
            m: 2,
            p: 2,
          }}>
          <FormControl sx={{ padding: 2 }}>
            <TextField
              variant='outlined'
              name='name'
              required
              label='Product Name'
              fullWidth
              value={productData.name}
              onChange={handleFieldUpdate}
              error={!!errors.name}
              helperText={errors.name}
              InputProps={{
                startAdornment: (
                  <CheckCircleIcon
                    color='action'
                    sx={{ mr: 1 }}
                  />
                ),
              }}
            />
          </FormControl>
          <FormControl sx={{ padding: 2 }}>
            <TextField
              variant='outlined'
              name='category'
              select
              required
              label='Category'
              fullWidth
              value={productData.category.id}
              onChange={handleCategoryChange}
              error={!!errors.category}
              helperText={errors.category}
              InputProps={{
                startAdornment: (
                  <CategoryIcon
                    color='action'
                    sx={{ mr: 1 }}
                  />
                ),
              }}>
              <MenuItem
                value={0}
                key={0}>
                Select Category
              </MenuItem>
              {props.categories.map((category) => (
                <MenuItem
                  key={category.id}
                  value={category.id}>
                  {category.name}
                </MenuItem>
              ))}
            </TextField>
          </FormControl>
          <FormControl sx={{ padding: 2 }}>
            <TextField
              variant='outlined'
              name='stock'
              label='Stock'
              type='number'
              fullWidth
              value={productData.stock}
              onChange={handleFieldUpdate}
              error={!!errors.stock}
              helperText={errors.stock}
              InputProps={{
                startAdornment: (
                  <InventoryIcon
                    color='action'
                    sx={{ mr: 1 }}
                  />
                ),
              }}
            />
          </FormControl>
          <FormControl sx={{ padding: 2 }}>
            <TextField
              variant='outlined'
              name='unitPrice'
              required
              label='Unit Price'
              type='number'
              fullWidth
              value={productData.unitPrice}
              onChange={handleFieldUpdate}
              error={!!errors.unitPrice}
              helperText={errors.unitPrice}
              InputProps={{
                startAdornment: (
                  <PriceCheckIcon
                    color='action'
                    sx={{ mr: 1 }}
                  />
                ),
              }}
            />
          </FormControl>
          <FormControl sx={{ padding: 2 }}>
            <TextField
              variant='outlined'
              name='expirationDate'
              label='Expiration Date'
              type='date'
              fullWidth
              value={productData.expirationDate}
              onChange={handleFieldUpdate}
              inputProps={{ min: minDateStr }}
              error={!!errors.expirationDate}
              helperText={errors.expirationDate}
              InputProps={{
                startAdornment: (
                  <CalendarTodayIcon
                    color='action'
                    sx={{ mr: 1 }}
                  />
                ),
              }}
            />
          </FormControl>
        </DialogContent>

        <DialogActions sx={{p: 2}}>
          <Button
            onClick={props.handleClose}
            disabled={loading}>
            Cancel
          </Button>
          <Button
            variant='contained'
            color='primary'
            onClick={handleSubmit}
            disabled={loading}
            startIcon={
              loading ? (
                <CircularProgress
                  size={20}
                  color='inherit'
                />
              ) : undefined
            }>
            Save
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={() => setSnackbarOpen(false)}
        message={`Product ${
          props.editMode ? 'updated' : 'created'
        } successfully`}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      />
    </>
  );
}
