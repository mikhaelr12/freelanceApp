import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Dispute from './dispute';
import DisputeDetail from './dispute-detail';
import DisputeUpdate from './dispute-update';
import DisputeDeleteDialog from './dispute-delete-dialog';

const DisputeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Dispute />} />
    <Route path="new" element={<DisputeUpdate />} />
    <Route path=":id">
      <Route index element={<DisputeDetail />} />
      <Route path="edit" element={<DisputeUpdate />} />
      <Route path="delete" element={<DisputeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DisputeRoutes;
