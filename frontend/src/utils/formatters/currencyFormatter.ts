export function formatCurrency(value: number, locale: string = "En-US", currency: string = "USD"){
    return Intl.NumberFormat(locale, {style:  'currency', currency: currency}).format(value);
}