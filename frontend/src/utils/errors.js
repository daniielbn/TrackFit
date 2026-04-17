export function getErrorMessage(error) {
  const response = error?.response?.data;
  if (response?.validationErrors && Object.keys(response.validationErrors).length > 0) {
    return Object.values(response.validationErrors).join('. ');
  }
  return response?.message || 'Ha ocurrido un error inesperado';
}
