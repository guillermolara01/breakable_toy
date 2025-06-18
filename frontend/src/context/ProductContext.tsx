import { createContext, useContext } from "react";
import type { ReactNode } from "react";
import type { IProductContext } from "../interfaces/Product/IProductContext";
import useProducts from "../hooks/useProducts";

const ProductContext = createContext<IProductContext | undefined>(undefined);

export const ProductsProvider = ({ children }: { children: ReactNode }) => {
  const { products, loading, error, params, updateParams, refresh } = useProducts();

  return (
    <ProductContext.Provider
      value={{
        products,
        loading,
        error,
        params,
        updateParams,
        refresh
      }}
    >
      {children}
    </ProductContext.Provider>
  );
};

export const useProductContext = () => {
  const context = useContext(ProductContext);
  if (!context) {
    throw new Error('useProductContext must be used within a ProductsProvider');
  }
  return context;
};
