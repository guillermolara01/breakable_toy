import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  MenuItem,
  Button,
  TextField,
} from '@mui/material';
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

  const handleFieldUpdate = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    let parsed: string | number;
    parsed = value;
    if (name == 'unitPrice') {
      parsed = parseFloat(value);
    } else if (name == 'stock') {
      parsed = parseInt(value);
    }
    setProductData({ ...productData, [name]: parsed });
  };

  const handleSubmit = async () => {
    if (props.editMode) {
      await updateProduct(productData);
    } else {
      await createProduct(productData);
    }
    refresh();
    props.handleClose();
  };

  const handleCategoryChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedId = parseInt(e.target.value);
    const selectedCategory = props.categories.find((c) => c.id === selectedId);
    if (selectedCategory) {
      setProductData({ ...productData, category: selectedCategory });
    }
  };

  useEffect(() => {
    if (props.editMode && props.initialData) {
      setProductData(props.initialData);
    }
  }, [props.editMode, props.initialData]);

  return (
    <>
      <Dialog
        open={props.open}
        onClose={props.handleClose}
        fullWidth
        maxWidth='md'
        >
        <DialogTitle>
          {props.editMode
            ? 'Update Product ' + productData.name
            : 'Create New Product'}
        </DialogTitle>
        <DialogContent
          sx={{ display: 'flex', flexDirection: 'column', gap: 2, m: 2 }}>
          <TextField
            name='name'
            label='Product Name'
            autoComplete='false'
            fullWidth
            value={productData.name}
            onChange={handleFieldUpdate}
          />

          <TextField
            name='category'
            select
            label='Category'
            fullWidth
            value={productData.category.id}
            onChange={handleCategoryChange}>
            {props.categories.map((category) => (
              <MenuItem
                key={category.id}
                value={category.id}>
                {category.name}
              </MenuItem>
            ))}
          </TextField>

          <TextField
            name='stock'
            label='Stock'
            type='number'
            fullWidth
            value={productData.stock}
            onChange={handleFieldUpdate}
          />

          <TextField
            name='unitPrice'
            label='Unit Price'
            type='number'
            fullWidth
            value={productData.unitPrice}
            onChange={handleFieldUpdate}
          />

          <TextField
            name='expirationDate'
            label='Expiration Date'
            type='date'
            fullWidth
            value={productData.expirationDate}
            onChange={handleFieldUpdate}
            InputLabelProps={{ shrink: true }}
          />
        </DialogContent>

        <DialogActions>
          <Button onClick={props.handleClose}>Cancel</Button>
          <Button
            variant='contained'
            color='primary'
            onClick={handleSubmit}>
            Save
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
