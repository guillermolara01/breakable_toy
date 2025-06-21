import axios from 'axios';

export async function getGeneralMetrics() {
  const response = await axios.get(`${import.meta.env.VITE_API_URL}products/metrics`);
  const metrics = response.data;
  return metrics;
}
