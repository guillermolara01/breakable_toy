import ProductList from '../../components/products/productList/ProductList';
import ProductStats from '../../components/products/productsStats/ProductStats';
import SearchBar from '../../components/products/SearchBar/SearchBar';
import type ICategory from '../../interfaces/Category/ICategory';

export default function Products() {
  const categories: ICategory[] = [
    {
      id: 1,
      name: 'Food',
    },
    {
      id: 2,
      name: 'Cleaning',
    },
    {
      id: 3,
      name: 'Pharmacy',
    },
    {
      id: 4,
      name: 'Chem Supplies',
    },
  ];

  return (
    <>
      <SearchBar categories={categories}></SearchBar>
      <ProductList></ProductList>
      <ProductStats></ProductStats>
    </>
  );
}
