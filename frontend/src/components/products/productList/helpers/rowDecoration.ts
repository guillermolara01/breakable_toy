type ThemeMode = 'light' | 'dark';

const colors: Record<ThemeMode, {
  nearExpire: string;
  middleExpire: string;
  farExpire: string;
  warningStock: string;
  lowStock: string;
}> = {
  dark: {
    nearExpire: '#632929',
    middleExpire: '#69632B',
    farExpire: '#244B23',
    warningStock: '#BE824A',
    lowStock: '#9B4343',
  },
  light: {
    nearExpire: '#FAC3C3',
    middleExpire: '#F5EFB7',
    farExpire: '#B7F5BD',
    warningStock: '#F9BB82',
    lowStock: '#D96B6B',
  },
};

export function getColorByExpirationDate(theme: ThemeMode, expirationDate?: string) {
  if (!expirationDate) return {};
  try {
    const currentDate = new Date();
    const expDate = new Date(expirationDate);
    const daysLeft =
      (expDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);

    const themeColors = colors[theme];
    const style = {
      backgroundColor: themeColors.farExpire,
    };
    if (daysLeft < 7) {
      style.backgroundColor = themeColors.nearExpire;
    } else if (daysLeft < 14) {
      style.backgroundColor = themeColors.middleExpire;
    }
    return style;
  } catch (error) {
    console.log(error);
    return {};
  }
}

export function getStockCellStyle(stock: number, theme: ThemeMode) {
  if (stock === 0) {
    return {
      backgroundColor: colors[theme].lowStock,
      color: theme === 'dark' ? '#f1fff' : '#1B1313',
      fontWeight: 'bold',
    };
  }
  if (stock < 5) return { backgroundColor: colors[theme].lowStock };
  if (stock < 11) return { backgroundColor: colors[theme].warningStock };
  return undefined;
}
