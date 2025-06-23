import type IProduct from "../../../interfaces/Product/IProduct";
import type ICategory from "../../../interfaces/Category/ICategory";

export default interface IProductModalProps {
  open: boolean;
  editMode: boolean;
  initialData?: IProduct;
  categories: ICategory[];
  handleClose: () => void;
}
