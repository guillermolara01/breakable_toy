import { useState, useEffect, useCallback } from 'react';
import { getAllProducts } from '../service/productService';
import type  IFilterParams from '../interfaces/FilteringParams/IFilterParams';
import type IPaginatedProducts from '../interfaces/Product/IPaginatedProducts';
import type IMetric from '../interfaces/Product/IMetric';
import { getGeneralMetrics } from '../service/metricsService';
export default function useProducts(initialParams: IFilterParams = {}) {
  const [paginatedProducts, setProducts] = useState<IPaginatedProducts>({} as IPaginatedProducts);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [params, setParams] = useState<IFilterParams>(initialParams);
  const [metrics, setMetrics] = useState<IMetric[]>([]);
  
  
  
  const fetchProducts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await getAllProducts(params);
      setProducts(result);
       const metrics = await getGeneralMetrics();
      setMetrics(metrics);
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
    paginatedProducts,
    loading,
    error,
    params,
    updateParams,
    refresh,
    metrics
  };
}
