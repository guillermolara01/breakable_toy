import { useEffect, useState } from 'react';
import ProductList from '../../components/products/productList/ProductList';
import ProductStats from '../../components/products/productsStats/ProductStats';
import SearchBar from '../../components/products/SearchBar/SearchBar';
import { getCategories } from '../../service/categoryService';

export default function Products() {
  const [categories, setCategories] = useState([]);
  const [showModal, setShowModal] = useState<boolean[]>([false, false]);

  const handleToggleModal = (value: boolean, editMode:boolean) => {
    setShowModal([value, editMode]);
  };
  useEffect(() => {
    getCategories().then((data) => {
      setCategories(data);
    });
  }, []);
  return (
    <>
        
      <SearchBar categories={categories}></SearchBar>
      <ProductList categories={categories} handleToggleModal={handleToggleModal}></ProductList>
      <ProductStats></ProductStats>
    </>
  );
}
