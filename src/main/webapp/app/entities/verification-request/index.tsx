import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import VerificationRequest from './verification-request';
import VerificationRequestDetail from './verification-request-detail';
import VerificationRequestUpdate from './verification-request-update';
import VerificationRequestDeleteDialog from './verification-request-delete-dialog';

const VerificationRequestRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<VerificationRequest />} />
    <Route path="new" element={<VerificationRequestUpdate />} />
    <Route path=":id">
      <Route index element={<VerificationRequestDetail />} />
      <Route path="edit" element={<VerificationRequestUpdate />} />
      <Route path="delete" element={<VerificationRequestDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VerificationRequestRoutes;
