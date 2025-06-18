import { useState, useEffect, useCallback } from 'react';
import { getAllProducts } from '../service/productService';
import type IProduct from '../interfaces/Product/IProduct';
import type  IFilterParams from '../interfaces/FilteringParams/IFilterParams';

export default function useProducts(initialParams: IFilterParams = {}) {
  const [products, setProducts] = useState<IProduct[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [params, setParams] = useState<IFilterParams>(initialParams);

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await getAllProducts(params);
      setProducts(result);
    } catch (error) {
        const e = error as Error;
        setError(e.message || 'Error fetching products');
    } finally {
      setLoading(false);
    }
  }, [params]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const updateParams = (newParams: Partial<IFilterParams>) => {
    setParams(prev => ({ ...prev, ...newParams }));
  };

  const refresh = () => {
    fetchProducts();
  };

  return {
    products,
    loading,
    error,
    params,
    updateParams,
    refresh,
  };
}
