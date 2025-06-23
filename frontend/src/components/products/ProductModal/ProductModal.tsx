import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
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
    category: { id: 0, name: '' },
    unitPrice: 0,
    expirationDate: '',
    stock: 0,
  });

  const handleSubmit = async () => {
    if (props.editMode) {
      console.log('update');
    } else {
      console.log('create');
    }

    refresh();
  };

  useEffect(() => {
    if (props.editMode && props.initialData) {
      setProductData(props.initialData);
    }
  }, [props.editMode]);

  return (
    <>
      <Dialog
        open={props.open}
        onClose={props.handleClose}>
        <DialogTitle>
          {props.editMode ? 'Update Product ' : 'Create New Product'}
        </DialogTitle>
        <DialogContent></DialogContent>
        <DialogActions></DialogActions>
      </Dialog>
    </>
  );
}
