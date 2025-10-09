import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import OfferPackage from './offer-package';
import OfferPackageDetail from './offer-package-detail';
import OfferPackageUpdate from './offer-package-update';
import OfferPackageDeleteDialog from './offer-package-delete-dialog';

const OfferPackageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<OfferPackage />} />
    <Route path="new" element={<OfferPackageUpdate />} />
    <Route path=":id">
      <Route index element={<OfferPackageDetail />} />
      <Route path="edit" element={<OfferPackageUpdate />} />
      <Route path="delete" element={<OfferPackageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OfferPackageRoutes;
