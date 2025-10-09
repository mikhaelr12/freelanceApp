import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Requirement from './requirement';
import RequirementDetail from './requirement-detail';
import RequirementUpdate from './requirement-update';
import RequirementDeleteDialog from './requirement-delete-dialog';

const RequirementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Requirement />} />
    <Route path="new" element={<RequirementUpdate />} />
    <Route path=":id">
      <Route index element={<RequirementDetail />} />
      <Route path="edit" element={<RequirementUpdate />} />
      <Route path="delete" element={<RequirementDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RequirementRoutes;
