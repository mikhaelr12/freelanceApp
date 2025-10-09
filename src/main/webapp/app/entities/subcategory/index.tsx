import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Subcategory from './subcategory';
import SubcategoryDetail from './subcategory-detail';
import SubcategoryUpdate from './subcategory-update';
import SubcategoryDeleteDialog from './subcategory-delete-dialog';

const SubcategoryRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Subcategory />} />
    <Route path="new" element={<SubcategoryUpdate />} />
    <Route path=":id">
      <Route index element={<SubcategoryDetail />} />
      <Route path="edit" element={<SubcategoryUpdate />} />
      <Route path="delete" element={<SubcategoryDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SubcategoryRoutes;
