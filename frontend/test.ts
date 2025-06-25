import { getColorByExpirationDate, getStockCellStyle } from "./src/components/products/productList/helpers/rowDecoration";


describe('getColorByExpirationDate', () => {
  const today = new Date();

  function getDateString(daysToAdd: number) {
    const date = new Date();
    date.setDate(today.getDate() + daysToAdd);
    return date.toISOString().split('T')[0]; // format as yyyy-mm-dd
  }

  it('returns nearExpire color for <7 days', () => {
    const result = getColorByExpirationDate('light', getDateString(3));
    expect(result).toEqual({ backgroundColor: '#FAC3C3' });
  });

  it('returns middleExpire color for 7-13 days', () => {
    const result = getColorByExpirationDate('light', getDateString(10));
    expect(result).toEqual({ backgroundColor: '#F5EFB7' });
  });

  it('returns farExpire color for 14+ days', () => {
    const result = getColorByExpirationDate('light', getDateString(20));
    expect(result).toEqual({ backgroundColor: '#B7F5BD' });
  });

  it('returns empty object if expirationDate is undefined', () => {
    expect(getColorByExpirationDate('light')).toEqual({});
  });

});

describe('getStockCellStyle', () => {
  it('returns lowStock + styling if stock is 0', () => {
    expect(getStockCellStyle(0, 'dark')).toEqual({
      backgroundColor: '#9B4343',
      color: '#f1fff',
      fontWeight: 'bold',
    });
  });

  it('returns lowStock if stock < 5', () => {
    expect(getStockCellStyle(3, 'light')).toEqual({
      backgroundColor: '#D96B6B',
    });
  });

  it('returns warningStock if stock < 11', () => {
    expect(getStockCellStyle(8, 'light')).toEqual({
      backgroundColor: '#F9BB82',
    });
  });

  it('returns undefined if stock > 10', () => {
    expect(getStockCellStyle(12, 'light')).toBeUndefined();
  });
});
